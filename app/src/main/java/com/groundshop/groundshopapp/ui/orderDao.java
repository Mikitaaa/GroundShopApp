package com.groundshop.groundshopapp.ui;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.groundshop.groundshopapp.ui.parser.Order;

import java.util.List;

@Dao
public interface orderDao {
    @Query("DELETE FROM orders WHERE id = :orderId")
    void deleteOrder(int orderId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<Order> orders);

    @Query("SELECT * FROM orders")
    LiveData<List<Order>> getOrders();
}
