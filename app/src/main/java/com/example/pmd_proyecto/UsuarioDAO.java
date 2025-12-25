package com.example.pmd_proyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDAO {

    private DBHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean login(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                "usuarios",
                new String[]{ "_id" },
                "email = ? AND password = ?",
                new String[]{ email, password },
                null, null, null
        );

        boolean existe = c.moveToFirst();
        c.close();

        return existe;
    }

    public boolean registrar(String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("usuarios", null, values);

        return result != -1;
    }


}

