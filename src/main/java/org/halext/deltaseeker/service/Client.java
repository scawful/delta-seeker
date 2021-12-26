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

/**
 * Client Class
 * 
 * Responsibility: GET data from the TDA API endpoint and pass it off to the Parser class 
 *                 POST to the TDA API endpoint for authentication 
 *                 CREATE a WebSocket client using the Stream class and pass messages to the API 
 */
public class Client {

    /**
     * Request Constants 
     */
    private static final String TOKEN = "token";
    private static final String STREAMER_INFO = "streamerInfo";
    private static final String ACCOUNTS_STR = "accounts";
    private static final String EN_US = "en-US,en";
    private static final String ACCEPT_LANGUAGE = "Accept-Language";
    private static final String DELTA_SEEKER = "Delta Seeker";
    private static final String USER_AGENT = "User-Agent";
    private static final String ACCOUNT_ID = "accountId";
    private static final String CONST_TICKER = "{ticker}";

    private JSONObject userPrincipals;
    private String TD_API_KEY;
    private String TD_REFRESH_TOKEN;
    private String ACCESS_TOKEN;

    public Client() {
        TD_API_KEY = "<INSERT-API-KEY>";
        TD_REFRESH_TOKEN = "<INSERT-REFRESH-TOKEN>";
        ACCESS_TOKEN = "<RETRIEVE-ACCESS-TOKEN>";
    }

    /**
     * Opens local file containing TDA api key and refresh token
     * @throws IOException
     */
    public void retrieveKeyFile() throws IOException {
        String apiFileLocation = "C:/Users/starw/Code/Java/deltaseeker/src/api.txt";
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
          
        ACCESS_TOKEN = (String) jo.get("access_token");
        in.close();
    }

    /**
     * GET user principals (streaming)
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private void getUserPrincipals() throws IOException, ParseException {
        if ( ACCESS_TOKEN.equals("<RETRIEVE-ACCESS-TOKEN>") ) {
            postAccessToken();
        }
        
        String url = "https://api.tdameritrade.com/v1/userprincipals?fields=streamerSubscriptionKeys,streamerConnectionInfo";
        URL u = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(USER_AGENT, DELTA_SEEKER);
        connection.setRequestProperty(ACCEPT_LANGUAGE, EN_US);
        connection.addRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);

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

    
    /**
     * GET json response from TDA API (no authentication required) 
     * @param url
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private JSONObject sendRequest( String url ) throws IOException, ParseException {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(USER_AGENT, DELTA_SEEKER);
        connection.setRequestProperty(ACCEPT_LANGUAGE, EN_US);

        try {
            System.out.println("Request Response Code: " + connection.getResponseCode() );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Object response = new JSONParser().parse(in);
        JSONObject jo = (JSONObject) response;
        in.close();
        return jo;
    }


    /**
     * GET request with use of access token 
     * 
     * @param url
     * @return JSONArray
     * @throws IOException
     * @throws ParseException
     */
    private JSONArray sendAuthorizedRequest( String url ) throws IOException, ParseException {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(USER_AGENT, DELTA_SEEKER);
        connection.setRequestProperty(ACCEPT_LANGUAGE, EN_US);
        connection.addRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);

        try {
            System.out.println("Authorized Request Response Code: " + connection.getResponseCode() );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Object response = new JSONParser().parse(in);
        JSONArray ja = (JSONArray) response;
        in.close();
        return ja;
    }

        /**
     * GET request with use of access token 
     * 
     * @param url
     * @return JSONObject
     * @throws IOException
     * @throws ParseException
     */
    private JSONObject sendAuthorizedObjectRequest( String url ) throws IOException, ParseException {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(USER_AGENT, DELTA_SEEKER);
        connection.setRequestProperty(ACCEPT_LANGUAGE, EN_US);
        connection.addRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);

        try {
            System.out.println("Authorized Request Response Code: " + connection.getResponseCode() );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Object response = new JSONParser().parse(in);
        JSONObject jo = (JSONObject) response;
        in.close();
        return jo;
    }


    /**
     * GET price history via given parameters where default is marked as asterisk
     * @param ticker
     * 
     * period type
     * @param pType     day, month, year, or ytd (year to date). 
     *                  Default is day.
     * 
     * period
     * @param p         day: 1, 2, 3, 4, 5, 10*
     *                  month: 1*, 2, 3, 6
     *                  year: 1*, 2, 3, 5, 10, 15, 20
     *                  ytd: 1*
     * 
     * frequency type
     * @param fType     day: minute*
     *                  month: daily, weekly*
     *                  year: daily, weekly, monthly*
     *                  ytd: daily, weekly*
     * 
     * frequency
     * @param f         minute: 1*, 5, 10, 15, 30
     *                  daily: 1*
     *                  weekly: 1*
     *                  monthly: 1*
     * 
     * extended hours
     * @param ext       true to return extended hours data, false for regular market hours only. Default is true
     * 
     * @return JSONObject
     * @throws IOException
     * @throws ParseException
     */
    public JSONObject getPriceHistory(String ticker, String pType, String p, String fType, String f, Boolean ext) throws IOException, ParseException {
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

        if ( Boolean.FALSE.equals(ext) )
            newUrl += "&needExtendedHoursData=false";

        return sendRequest(newUrl);
    }

    /**
     * GET price quote 
     * @param ticker
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public JSONObject getQuote( String ticker ) throws IOException, ParseException {
        String baseUrl = "https://api.tdameritrade.com/v1/marketdata/{ticker}/quotes?apikey=" + TD_API_KEY;
        String newUrl = baseUrl.replace(CONST_TICKER, ticker);
        return sendRequest(newUrl);
    }

    /**
     * GET instrument data including fundamentals
     * @param ticker
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public JSONObject getInstrument( String ticker ) throws IOException, ParseException { 
        String baseUrl = "https://api.tdameritrade.com/v1/instruments?apikey=" + TD_API_KEY  + "&symbol={ticker}&projection=fundamental";
        String newUrl = baseUrl.replace(CONST_TICKER, ticker);
        return sendRequest(newUrl);
    }

    /**
     * GET orders from main account by number of desired results (default timeframe 60 days)
     * 
     * @param maxResults
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public JSONArray getOrders(int maxResults) throws IOException, ParseException {
        JSONArray accounts = (JSONArray) userPrincipals.get(ACCOUNTS_STR);
        JSONObject accountElements = (JSONObject) accounts.get(0);
        String accountId = (String) accountElements.get(ACCOUNT_ID);
        String url = "https://api.tdameritrade.com/v1/accounts/" + accountId + "/orders?maxResults=" + maxResults;
        return sendAuthorizedRequest(url);
    }

    /**
     * GET watchlist for a single account
     * 
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public JSONArray getWatchlistSingleAccount() throws IOException, ParseException {
        getUserPrincipals();
        JSONArray accounts = (JSONArray) userPrincipals.get(ACCOUNTS_STR);
        JSONObject accountElements = (JSONObject) accounts.get(0);
        String accountId = (String) accountElements.get(ACCOUNT_ID);
        String url = "https://api.tdameritrade.com/v1/accounts/" + accountId + "/watchlists";
        return sendAuthorizedRequest(url);
    }

    public JSONObject getAccountData() throws IOException, ParseException {
        getUserPrincipals();
        JSONArray accounts = (JSONArray) userPrincipals.get(ACCOUNTS_STR);
        JSONObject accountElements = (JSONObject) accounts.get(0);
        String accountId = (String) accountElements.get(ACCOUNT_ID);
        String url = "https://api.tdameritrade.com/v1/accounts/" + accountId + "?fields=positions";
        return sendAuthorizedObjectRequest(url);
    }

    public JSONArray getMovers( String index, String direction, String change ) throws IOException, ParseException {
        String baseUrl = "https://api.tdameritrade.com/v1/marketdata/{index}/movers?apikey=" + TD_API_KEY + "&direction={direction}&change={change}";
        String newUrl = baseUrl.replace("{index}", index);
        newUrl = newUrl.replace("{direction}", direction);
        newUrl = newUrl.replace("{value}", change);
        return sendAuthorizedRequest(newUrl);
    }

    /**
     * WebSocket session login request for TDA API
     * 
     * @return
     * @throws java.text.ParseException
     */
    private String createLoginRequest() throws java.text.ParseException {
        HashMap<String, Object> credentials = new HashMap<>();
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> requests = new HashMap<>();

        JSONArray accounts = (JSONArray) userPrincipals.get(ACCOUNTS_STR);
        JSONObject accountElements = (JSONObject) accounts.get(0);

        credentials.put("userid", accountElements.get(ACCOUNT_ID));
        JSONObject streamerInfo = (JSONObject) userPrincipals.get(STREAMER_INFO);
        credentials.put(TOKEN, streamerInfo.get(TOKEN));
        credentials.put("company", accountElements.get("company"));
        credentials.put("segment", accountElements.get("segment"));
        credentials.put("cddomain", accountElements.get("cddomain"));
        credentials.put("usergroup", accountElements.get("usergroup"));
        credentials.put("accesslevel", accountElements.get("accesslevel"));
        credentials.put("authorized", "Y");

        requests.put("service", "ADMIN");
        requests.put("command", "LOGIN");
        requests.put("requestid", 0);
        requests.put("account", accountElements.get(ACCOUNT_ID));
        requests.put("source", streamerInfo.get("appId"));

        parameters.put(TOKEN, streamerInfo.get(TOKEN));
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
        JSONObject requestsJSON = new JSONObject(requests);

        System.out.println(requestsJSON.toJSONString().replace("\\", ""));
        return requestsJSON.toJSONString().replace("\\", "");
    }

    /**
     * WebSocket session logout request for TDA API
     * 
     * @return
     */
    private String createLogoutRequest() { 
        HashMap<String, Object> requests = new HashMap<>();

        JSONArray accounts = (JSONArray) userPrincipals.get(ACCOUNTS_STR);
        JSONObject accountElements = (JSONObject) accounts.get(0);
        JSONObject streamerInfo = (JSONObject) userPrincipals.get(STREAMER_INFO);

        requests.put("service", "ADMIN");
        requests.put("requestid", 1);
        requests.put("command", "LOGOUT");
        requests.put("account", accountElements.get(ACCOUNT_ID) );
        requests.put("source", streamerInfo.get("appId") );

        requests.put( "parameters", new JSONObject() );

        JSONObject requestsJSON = new JSONObject(requests);
        System.out.println(requestsJSON.toJSONString().replace("\\", ""));
        return requestsJSON.toJSONString().replace("\\", "");
    }

    /**
     * WebSocket stream testing grounds 
     * 
     * @throws IOException
     * @throws ParseException
     * @throws DeploymentException
     * @throws java.text.ParseException
     */
    public void openStream() throws IOException, ParseException, DeploymentException, java.text.ParseException {
        try {
            // open websocket
            JSONObject streamerInfo = (JSONObject) userPrincipals.get(STREAMER_INFO);
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
            // Thread sleep 

        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
