package com.groundshop.groundshopapp.ui;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.room.Room;

import com.groundshop.groundshopapp.ui.parser.Order;

@Database(entities = {Order.class}, version = 1, exportSchema = false)
public abstract class OrderDatabase extends RoomDatabase {
    private static OrderDatabase instance;
    public abstract orderDao orderDao();

    public static synchronized OrderDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            OrderDatabase.class, "order_database")
                    .build();
        }
        return instance;
    }
}
