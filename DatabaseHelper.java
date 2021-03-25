package com.trees.locationrestaurants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Location.db";
    private static final String TABLE_USER = "user";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_CONTACT = "contact";
    private static final String COLUMN_PASSWORD = "password";


    private static final String CREATE_TABLE_USERS = "create table " + TABLE_USER + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email Text, contact TEXT, password Text)";

    private String createTableOthers;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public Cursor getData(String username, String password){
        Cursor cursor ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE name LIKE '%" + username + "%' AND  password LIKE '%" + password + "%'", null);
        Log.d("asasdasd", "SELECT * FROM " + TABLE_USER + " WHERE name LIKE '%" + username + "%' AND  password LIKE '%" + password + "%'");
        return cursor;
    }

    public Long insertData(String name, String contact, String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_CONTACT, contact);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);

        Long result = sqLiteDatabase.insert(TABLE_USER , null, contentValues);
        return result;
    }
}
