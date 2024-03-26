package com.groundshop.groundshopapp.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.groundshop.groundshopapp.ui.parser.HttpRequestHelper;
import com.groundshop.groundshopapp.ui.parser.Order;
import com.groundshop.groundshopapp.ui.parser.OrderParser;
import java.util.List;
import android.os.AsyncTask;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<Order>> mOrders;

    public NotificationsViewModel() {
        mOrders = new MutableLiveData<>();

        new HttpRequestTask().execute("https://groundshop.vercel.app/api/route");
    }

    public LiveData<List<Order>> getOrders() { return mOrders; }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return HttpRequestHelper.sendGetRequest(urls[0]);
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