package com.example.pmd_proyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDAO {

    public static boolean login(Context context, String email, String password) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();

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

    public static boolean registrar(Context context, String email, String password) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("usuarios", null, values);

        return result != -1;
    }
}

