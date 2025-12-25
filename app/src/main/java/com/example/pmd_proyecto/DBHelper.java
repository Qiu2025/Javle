package com.example.pmd_proyecto;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE usuarios (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "avatar TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE usuarios ADD COLUMN avatar TEXT");
        }
    }
}
