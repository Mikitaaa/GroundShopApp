package com.groundshop.groundshopapp.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;

import com.groundshop.groundshopapp.ui.parser.HttpRequestHelper;
import com.groundshop.groundshopapp.ui.parser.Order;
import com.groundshop.groundshopapp.ui.parser.OrderParser;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.groundshop.groundshopapp.ui.orderDao;
import com.groundshop.groundshopapp.ui.OrderDatabase;

import java.io.IOException;
import java.io.InputStream;
import com.groundshop.groundshopapp.R;
import androidx.annotation.NonNull;

import android.app.Application;


public class NotificationsViewModel extends AndroidViewModel {
    private LiveData<List<Order>> mOrders;
    private String auth = null;
    private final orderDao orderDao;

    public NotificationsViewModel(@NonNull Application application) {
        super(application);

        OrderDatabase database = OrderDatabase.getInstance(application);
        orderDao = database.orderDao();
        mOrders = orderDao.getOrders();

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

        loadOrdersFromServer();
    }

    private void loadOrdersFromServer() {
        new HttpRequestTask(auth).execute("https://groundshop.vercel.app/api/route");
    }

    public LiveData<List<Order>> getOrders() { return mOrders; }

    public void removeOrder(Order order) {
        new RemoveOrderAsyncTask(orderDao).execute(order);
    }

    private static class RemoveOrderAsyncTask extends AsyncTask<Order, Void, Void> {
        private final orderDao orderDao;

        public RemoveOrderAsyncTask(com.groundshop.groundshopapp.ui.orderDao orderDao) {
            this.orderDao = orderDao;
        }

        @Override
        protected Void doInBackground(Order... orders) {
            if (orders != null && orders.length > 0) {
                Order order = orders[0];
                orderDao.deleteOrder(order.getId());
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        private final String auth;

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
                mOrders = orderDao.getOrders();
                new InsertOrdersAsyncTask(orderDao).execute(parsedOrders);
            }
        }
    }
    private static class InsertOrdersAsyncTask extends AsyncTask<List<Order>, Void, Void> {
        private final orderDao orderDao;

        public InsertOrdersAsyncTask(orderDao orderDao) {
            this.orderDao = orderDao;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Order>... lists) {
            if (lists != null && lists.length > 0) {
                List<Order> orders = lists[0];
                orderDao.insertOrders(orders);
            }
            return null;
        }
    }
}
