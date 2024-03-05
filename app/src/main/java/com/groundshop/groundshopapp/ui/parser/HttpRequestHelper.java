package com.groundshop.groundshopapp.ui.parser;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestHelper {
    public static String sendGetRequest(final String endpoint) {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Adding headers
            connection.setRequestProperty("Content-Type", "application/json");
            // Add Basic authentication header
            String auth = "GroundShopMobileApp" + ":" + "7n9w!P.=E4Hh.)-N4^([g29zs,VZ&!";
            String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Log the response
            Log.d("HTTP_RESPONSE", "Response: " + response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("HTTP_REQUEST", "Error occurred during HTTP request: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("HTTP_REQUEST", "Error occurred while closing reader: " + e.getMessage());
                }
            }
        }

        return response.toString();
    }
}
