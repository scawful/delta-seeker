package org.halext.deltaseeker.service;

import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;

public class TDClient {

    static public String TDApiKey = "<INSERT-API-KEY>";
    static public String TDRefreshToken = "<INSERT-REFRESH-TOKEN>";

    static public String AccessToken;
    
    static public void postAccessToken() throws IOException, Exception {
        String url = "https://api.tdameritrade.com/v1/oauth2/token";
        URL u = new URL(url);

        String query = "grant_type=refresh_token&refresh_token=" + URLEncoder.encode(TDRefreshToken,"UTF-8") + "&client_id=" + TDApiKey;

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
}
