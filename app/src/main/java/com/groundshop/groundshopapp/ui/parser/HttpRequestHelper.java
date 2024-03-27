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

    public static String sendGetRequest(final String endpoint, String auth) {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Content-Type", "application/json");

            String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

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
