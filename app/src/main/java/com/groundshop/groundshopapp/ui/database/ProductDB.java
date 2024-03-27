package com.groundshop.groundshopapp.ui.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_VOLUME = "volume";

    public ProductDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_VOLUME + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void saveProduct(int id, String name, String price, String volume) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_VOLUME, volume);
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public void updateProductPrice(int id, String newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRICE, newPrice);
        db.update(TABLE_PRODUCTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUCTS, null, null, null, null, null, null);
    }

    @SuppressLint("Range")
    public String getProductNameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NAME};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        String productName = null;
        if (cursor != null && cursor.moveToFirst()) {
            productName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            cursor.close();
        }

        db.close();
        return productName;
    }

    @SuppressLint("Range")
    public String getProductPriceById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PRICE};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        String productPrice = null;
        if (cursor != null && cursor.moveToFirst()) {
            productPrice = cursor.getString(cursor.getColumnIndex(COLUMN_PRICE));
            cursor.close();
        }

        db.close();
        return productPrice;
    }

    @SuppressLint("Range")
    public String getProductVolumeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_VOLUME};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        String productName = null;
        if (cursor != null && cursor.moveToFirst()) {
            productName = cursor.getString(cursor.getColumnIndex(COLUMN_VOLUME));
            cursor.close();
        }

        db.close();
        return productName;
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}

