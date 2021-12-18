package org.halext.deltaseeker.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.websocket.DeploymentException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client {

    private JSONObject userPrincipals;

    public Client() {
      // TODO document why this constructor is empty
    }

    private static String CONST_TICKER = "{ticker}";
    private String TD_API_KEY = "<INSERT-API-KEY>";
    private String TD_REFRESH_TOKEN = "<INSERT-REFRESH-TOKEN>";

    public String AccessToken;

    /**
     * Opens local file containing TDA api key and refresh token
     * @throws IOException
     */
    public void retrieveKeyFile() throws IOException {
        String apiFileLocation = "/Users/scawful/Code/Java/delta-seeker/src/api.txt";
        try ( BufferedReader apiReader = new BufferedReader( new FileReader(apiFileLocation) )) {
            TD_API_KEY = apiReader.readLine();
            TD_REFRESH_TOKEN = apiReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * POST new access token using refresh token 
     * @throws IOException
     * @throws ParseException
     */
    public void postAccessToken() throws IOException, ParseException {
        String url = "https://api.tdameritrade.com/v1/oauth2/token";
        URL u = new URL(url);

        String query = "grant_type=refresh_token&refresh_token=" + URLEncoder.encode(TD_REFRESH_TOKEN,"UTF-8") + "&client_id=" + TD_API_KEY;

        HttpsURLConnection connection = (HttpsURLConnection) u.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Transfer-Encoding", "chunked");
        connection.setDoOutput(true); 
        connection.setDoInput(true); 

        DataOutputStream output = new DataOutputStream(connection.getOutputStream());  
        output.writeBytes(query);
        output.close();

        try {
            System.out.println("Response Code: " + connection.getResponseCode() );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        Object response = new JSONParser().parse(in);
        JSONObject jo = (JSONObject) response;
          
        AccessToken = (String) jo.get("access_token");
        System.out.println(AccessToken);

        in.close();

    }

    /**
     * GET user principals (streaming, )
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private void getUserPrincipals() throws IOException, ParseException {
        String url = "https://api.tdameritrade.com/v1/userprincipals?fields=streamerSubscriptionKeys,streamerConnectionInfo";
        URL u = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Delta Seeker");
        connection.setRequestProperty("Accept-Language", "en-US,en");
        connection.addRequestProperty("Authorization", "Bearer " + AccessToken);

        try {
            System.out.println("Response Code: " + connection.getResponseCode() );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Object response = new JSONParser().parse(in);
        userPrincipals = (JSONObject) response;
        in.close();
    }

    private JSONObject downloadData( String url ) throws IOException, ParseException {
        URL u = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Delta Seeker");
        connection.setRequestProperty("Accept-Language", "en-US,en");

        try {
            System.out.println("Response Code: " + connection.getResponseCode() );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Object response = new JSONParser().parse(in);
        JSONObject jo = (JSONObject) response;
        in.close();
        return jo;
    }

    public JSONObject getPriceHistory( String ticker, String pType, String p, String fType, String f ) throws IOException, ParseException {
        // https://api.tdameritrade.com/v1/marketdata/{ticker}/pricehistory?apikey=" + TD_API_KEY  + "&periodType=ytd&period=1&frequencyType=daily&frequency=1&needExtendedHoursData=true
        String baseUrl = "https://api.tdameritrade.com/v1/marketdata/{ticker}/pricehistory?apikey=" + TD_API_KEY;
        String newUrl = baseUrl.replace(CONST_TICKER, ticker);

        if ( pType != null )
            newUrl += "&periodType=" + pType;

        if ( p != null )
            newUrl += "&period=" + p;

        if ( fType != null )
            newUrl += "&frequencyType=" + fType;

        if ( f != null )
            newUrl += "&frequency=" + f;

        return downloadData(newUrl);
    }

    public JSONObject getQuote( String ticker ) throws IOException, ParseException {
        String baseUrl = "https://api.tdameritrade.com/v1/marketdata/TLT/quotes?apikey=" + TD_API_KEY;
        String newUrl = baseUrl.replace(CONST_TICKER, ticker);
        return downloadData(newUrl);
    }

    public JSONObject getInstrument( String ticker ) throws IOException, ParseException { 
        String baseUrl = "https://api.tdameritrade.com/v1/instruments?apikey=" + TD_API_KEY  + "&symbol={ticker}&projection=fundamental";
        String newUrl = baseUrl.replace(CONST_TICKER, ticker);
        return downloadData(newUrl);
    }

    private String createLoginRequest() throws java.text.ParseException {
        HashMap<String, Object> credentials = new HashMap<>();
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> requests = new HashMap<>();


        JSONArray accounts = (JSONArray) userPrincipals.get("accounts");
        JSONObject accountElements = (JSONObject) accounts.get(0);

        credentials.put("userid", accountElements.get("accountId"));
        JSONObject streamerInfo = (JSONObject) userPrincipals.get("streamerInfo");
        credentials.put("token", streamerInfo.get("token"));
        credentials.put("company", accountElements.get("company"));
        credentials.put("segment", accountElements.get("segment"));
        credentials.put("cddomain", accountElements.get("cddomain"));
        credentials.put("usergroup", accountElements.get("usergroup"));
        credentials.put("accesslevel", accountElements.get("accesslevel"));
        credentials.put("authorized", "Y");

        requests.put("service", "ADMIN");
        requests.put("command", "LOGIN");
        requests.put("requestid", 0);
        requests.put("account", accountElements.get("accountId"));
        requests.put("source", streamerInfo.get("appId"));

        parameters.put("token", streamerInfo.get("token"));
        parameters.put("version", "1.0");

        // timestamp 
        // 2021-12-18T04:29:21+0000
        String timestampString = streamerInfo.get("tokenTimestamp").toString().replace('T', ' ');
        String replaceTimestamp = timestampString.replace("+0000", "");
        Date tokenTimeStampAsDateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(replaceTimestamp);
        long tokenTimeStampAsMs = tokenTimeStampAsDateObj.getTime();
        credentials.put("timestamp", Long.toString(tokenTimeStampAsMs));
        credentials.put("appid", accountElements.get("appid"));
        credentials.put("acl", accountElements.get("acl"));

        // turn credentials into string 
        JSONObject credentialsJSON = new JSONObject(credentials);
        String temp = "";
        for (Object key : credentialsJSON.keySet()) {
            temp += key + "%3D" + credentialsJSON.get(key) + "%26";
        }
        String credentialsString = temp.substring(0, temp.length() - 3);
        parameters.put("credential", credentialsString);

        JSONObject parametersJSON = new JSONObject(parameters);

        requests.put("parameters", parametersJSON);

        // JSONArray requestsArray = new JSONArray();
        // requestsArray.add(requests);
        JSONObject requestsJSON = new JSONObject(requests);
        // requestsJSON.put("requests", requestsArray);

        System.out.println(requestsJSON.toJSONString().replace("\\", ""));
        return requestsJSON.toJSONString().replace("\\", "");
    }

    private String createLogoutRequest() { 
        HashMap<String, Object> requests = new HashMap<>();

        JSONArray accounts = (JSONArray) userPrincipals.get("accounts");
        JSONObject accountElements = (JSONObject) accounts.get(0);
        JSONObject streamerInfo = (JSONObject) userPrincipals.get("streamerInfo");

        requests.put("service", "ADMIN");
        requests.put("requestid", 1);
        requests.put("command", "LOGOUT");
        requests.put("account", accountElements.get("accountId") );
        requests.put("source", streamerInfo.get("appId") );

        requests.put( "parameters", new JSONObject() );

        JSONObject requestsJSON = new JSONObject(requests);
        System.out.println(requestsJSON.toJSONString().replace("\\", ""));
        return requestsJSON.toJSONString().replace("\\", "");
    }

    public void openStream() throws IOException, ParseException, DeploymentException, java.text.ParseException {
        getUserPrincipals();
        try {
            // open websocket
            JSONObject streamerInfo = (JSONObject) userPrincipals.get("streamerInfo");
            final Stream clientEndPoint = new Stream(new URI("wss://" + (String) streamerInfo.get("streamerSocketUrl") + "/ws"));

            // add listener
            clientEndPoint.addMessageHandler(new Stream.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage(createLoginRequest());
            clientEndPoint.sendMessage(createLogoutRequest());

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
