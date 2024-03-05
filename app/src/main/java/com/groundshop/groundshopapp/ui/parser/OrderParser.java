package com.groundshop.groundshopapp.ui.parser;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderParser {

    public List<Order> parseOrders(String response) {
        List<Order> orders = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String phone = jsonObject.getString("phone");
                String comment = jsonObject.getString("comment");
                orders.add(new Order(id, name, phone, comment));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orders;
    }
}