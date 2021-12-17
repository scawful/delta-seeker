package org.halext.deltaseeker.service;

import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javax.net.ssl.HttpsURLConnection;

public class Client {

    static public String TD_API_KEY = "<INSERT-API-KEY>";
    static public String TD_REFRESH_TOKEN = "<INSERT-REFRESH-TOKEN>";

    static public String AccessToken;

    public static void retrieveKeyFile() throws FileNotFoundException, IOException {
        BufferedReader apiReader = new BufferedReader( new FileReader("C:/Users/starw/iCloudDrive/Documents/Java/deltaseeker/src/api.txt") );
        TD_API_KEY = apiReader.readLine();
        TD_REFRESH_TOKEN = apiReader.readLine();
        apiReader.close();
    }
    
    public static void postAccessToken() throws IOException, ParseException {
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

    public static JSONObject getPriceHistory( String ticker ) throws IOException, ParseException {
        String baseUrl = "https://api.tdameritrade.com/v1/marketdata/{ticker}/pricehistory?apikey=" + TD_API_KEY  + "&periodType=ytd&period=1&frequencyType=daily&frequency=1&needExtendedHoursData=true";
        String newUrl = baseUrl.replace("{ticker}", ticker);
        URL u = new URL(newUrl);

        System.out.println(newUrl);

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
}
