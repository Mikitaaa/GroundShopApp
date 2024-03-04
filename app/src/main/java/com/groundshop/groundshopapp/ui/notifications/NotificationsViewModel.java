package com.groundshop.groundshopapp.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
        sendGetRequest("https://groundshop.vercel.app/api/route");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private void sendGetRequest(final String endpoint) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Log the response
                    Log.d("HTTP_RESPONSE", "Response: " + response.toString());

                    // Update mText with the response
                    mText.postValue(response.toString());

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
            }
        }).start();
    }

}