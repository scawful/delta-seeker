package org.halext.deltaseeker.service;

import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javax.net.ssl.HttpsURLConnection;

public class Client {

    public Client() {
        
    }

    private static String CONST_TICKER = "{ticker}";
    private String TD_API_KEY = "<INSERT-API-KEY>";
    private String TD_REFRESH_TOKEN = "<INSERT-REFRESH-TOKEN>";

    public String AccessToken;

    public void retrieveKeyFile() throws IOException {
        String apiFileLocation = "/Users/scawful/Code/Java/delta-seeker/src/api.txt";
        try ( BufferedReader apiReader = new BufferedReader( new FileReader(apiFileLocation) )) {
            TD_API_KEY = apiReader.readLine();
            TD_REFRESH_TOKEN = apiReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
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
}
