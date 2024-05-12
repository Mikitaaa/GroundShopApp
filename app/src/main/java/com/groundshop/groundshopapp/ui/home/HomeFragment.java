package com.groundshop.groundshopapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.groundshop.groundshopapp.databinding.FragmentHomeBinding;
import com.groundshop.groundshopapp.R;
import com.groundshop.groundshopapp.ui.database.ProductDB;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Button;

import com.groundshop.groundshopapp.ui.parser.HttpRequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductDB dataBase;
    private String auth = null;
    private GridLayout gridLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gridLayout = root.findViewById(R.id.gridLayout);

        Context context = getContext();
        dataBase = new ProductDB(context);
        SQLiteDatabase db = dataBase.getWritableDatabase();
        dataBase.initDBifNotExist(db);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.auth);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            auth = new String(buffer);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        auth = auth.trim();

        int childCount = gridLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = gridLayout.getChildAt(i);
            final int index = i+1;

            String productName = dataBase.getProductNameById(index);

            TextView productNameView = childView.findViewById(R.id.name1);
            productNameView.setText(productName);

            TextView productPriceView = childView.findViewById(R.id.price1);

            String photoName;
            if(index>6){
                photoName = "photo_250";
            }else{ photoName = "photo" + (index); }

            ImageView productPhotoView = childView.findViewById(R.id.photo1);
            int resId = getResources().getIdentifier(photoName, "drawable", requireContext().getPackageName());
            productPhotoView.setImageResource(resId);

            productPriceView.setOnClickListener(v -> {
                showProductDetailsDialog(index, productName, productPriceView);
            });
        }
        loadPricesFromServer();

        return root;
    }

    private void loadPricesFromServer() {
        if (isNetworkAvailable()) {
            new GetRequestTask(auth).execute("https://groundshop.by/api/prices");
        } else {
            openDialog("Отсутствует подключение к сети");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetRequestTask extends AsyncTask<String, Void, String[]> {
        private final String auth;

        public GetRequestTask(String auth) {
            this.auth = auth;
        }

        @Override
        protected String[] doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null || !isNetworkAvailable()) {
                openDialog("Ошибка получения цен");
                return null;
            }

            return HttpRequestHelper.sendGetRequest(urls[0], auth);
        }

        @Override
        protected void onPostExecute(String[] resultAndStatus) {
            if (resultAndStatus != null && resultAndStatus.length == 2) {
                String result = resultAndStatus[0];
                int status = Integer.parseInt(resultAndStatus[1]);

                if (status == HttpURLConnection.HTTP_OK) {
                    Map<String, String> pricesMap = convertJsonStringToMap(result);
                    if (pricesMap != null && !pricesMap.isEmpty()) {
                        for (Map.Entry<String, String> entry : pricesMap.entrySet()) {
                            int productID = Integer.parseInt(entry.getKey());
                            String productPrice = entry.getValue();
                            dataBase.updateProductPrice(productID, productPrice);

                            View childView = gridLayout.getChildAt(productID-1);
                            if (childView != null) {
                                TextView productPriceView = childView.findViewById(R.id.price1);
                                productPriceView.setText(productPrice+ " руб.");
                            }
                        }
                    }
                }else { openDialog("Ошибка получения цен с сайта"); }
            }else { openDialog("Ошибка подключения к сайту"); }
        }

    }

    private Map<String, String> convertJsonStringToMap(String result) {
        Map<String, String> pricesMap = new HashMap<>();
        try {
            JSONObject jsonResponse = new JSONObject(result);
            Iterator<String> keys = jsonResponse.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonResponse.getString(key);
                pricesMap.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pricesMap;
    }


    private void showProductDetailsDialog(int index, String productName, TextView productPriceView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View customLayout = getLayoutInflater().inflate(R.layout.change_price_window, null);
        builder.setView(customLayout);

        TextView dialogTitle = customLayout.findViewById(R.id.changePriceTitle);
        EditText dialogInput = customLayout.findViewById(R.id.changePriceInput);
        Button closeButton = customLayout.findViewById(R.id.closeButton);
        Button setButton = customLayout.findViewById(R.id.setButton);

        String volume = dataBase.getProductVolumeById(index);
        String Price = dataBase.getProductPriceById(index);

        dialogInput.setText(Price);
        dialogTitle.setText(productName+" ("+volume+")");

        AlertDialog dialog = builder.create();

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.change_price_background);
        }

        setButton.setOnClickListener(v -> {
            String newPrice = dialogInput.getText().toString();

            if (!newPrice.isEmpty()) {
                if (isNetworkAvailable()) {
                    new PostRequestTask(auth, index, newPrice, productPriceView).execute("https://groundshop.by/api/prices");
                } else {
                    openDialog("Отсутствует подключение к сети");
                }
            } else {
                openDialog("Введите цену");
            }
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class PostRequestTask extends AsyncTask<String, Void, String> {
        private final String auth;
        private final int index;
        private final String newPrice;
        private final TextView productPriceView;

        public PostRequestTask(String auth, int index, String newPrice, TextView productPriceView) {
            this.auth = auth;
            this.index = index;
            this.newPrice = newPrice;
            this.productPriceView = productPriceView;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null || !isNetworkAvailable()) {
                openDialog("Ошибка отправки цены");
                return null;
            }
            String jsonData = "{\"" + index + "\": \"" + newPrice + "\"}";
            return HttpRequestHelper.sendPostRequest(urls[0], auth, jsonData);
        }

        @Override
        protected void onPostExecute(String Status) {
            if (Status != null) {
                int status = Integer.parseInt(Status);

                if (status == HttpURLConnection.HTTP_OK) {
                    dataBase.updateProductPrice(index, newPrice);
                    openDialog("Цена обновлена");
                    productPriceView.setText(newPrice + " руб.");
                } else {
                    openDialog("Ошибка отправки цены");
                }
            } else {
                openDialog("Сервер недоступен");
            }
        }
    }

    public void openDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View customView = LayoutInflater.from(requireActivity()).inflate(R.layout.alert_window, null);
        builder.setView(customView);

        TextView titleTextView = customView.findViewById(R.id.alert_title);
        titleTextView.setText(title);

        AlertDialog dialog = builder.create();

        Button cancelButton = customView.findViewById(R.id.dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable._bg_alert_window);
        }

        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
