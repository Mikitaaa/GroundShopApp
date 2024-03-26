package com.groundshop.groundshopapp.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.groundshop.groundshopapp.ui.parser.HttpRequestHelper;
import com.groundshop.groundshopapp.ui.parser.Order;
import com.groundshop.groundshopapp.ui.parser.OrderParser;
import java.util.List;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import com.groundshop.groundshopapp.R;
import androidx.annotation.NonNull;
import android.app.Application;


public class NotificationsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Order>> mOrders;

    public NotificationsViewModel(@NonNull Application application) {
        super(application);

        mOrders = new MutableLiveData<>();

        String auth = null;
        try {
            InputStream inputStream = application.getResources().openRawResource(R.raw.auth);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            auth = new String(buffer);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        auth = auth.trim();
        new HttpRequestTask(auth).execute("https://groundshop.vercel.app/api/route");
    }

    public LiveData<List<Order>> getOrders() { return mOrders; }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        private String auth;

        public HttpRequestTask(String auth) {
            this.auth = auth;
        }
        @Override
        protected String doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return HttpRequestHelper.sendGetRequest(urls[0], auth);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                List<Order> parsedOrders = new OrderParser().parseOrders(result);
                mOrders.setValue(parsedOrders);
            }
            else {
                // Handle null result here
            }
        }
    }
}