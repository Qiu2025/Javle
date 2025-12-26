package com.example.pmd_proyecto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.text.TextUtils;

import com.example.pmd_proyecto.model.RetoProgramacion;

import java.util.ArrayList;
import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 7; // Incrementamos versión

    // Tabla Retos
    public static final String TABLE_RETOS = "retos";
    public static final String COL_ID = "_id";
    public static final String COL_PREGUNTA = "pregunta";
    public static final String COL_CODIGO = "codigo";
    public static final String COL_OPCIONES = "opciones"; // Guardado como texto separado
    public static final String COL_RESPUESTA = "respuesta_correcta";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla usuarios existente
        db.execSQL(
                "CREATE TABLE usuarios (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "avatar TEXT)"
        );
        // Nueva tabla retos
        crearTablaRetos(db);
    }

    private void crearTablaRetos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RETOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PREGUNTA + " TEXT, " +
                COL_CODIGO + " TEXT, " +
                COL_OPCIONES + " TEXT, " +
                COL_RESPUESTA + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE usuarios ADD COLUMN avatar TEXT");
        }
        if (oldVersion < 3) {
            crearTablaRetos(db);
        }
    }

    // --- MÉTODOS PARA GESTIÓN DE RETOS ---

    public void guardarReto(RetoProgramacion reto) {
        if (reto == null || reto.pregunta == null) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PREGUNTA, reto.pregunta);
        values.put(COL_CODIGO, reto.codigo);
        // Convertir lista a string separado por ##
        String opcionesStr = "";
        if (reto.opciones != null) {
            opcionesStr = TextUtils.join("##", reto.opciones);
        }
        values.put(COL_OPCIONES, opcionesStr);
        values.put(COL_RESPUESTA, reto.respuestaCorrecta);
        db.insert(TABLE_RETOS, null, values);
    }

    public RetoProgramacion obtenerSiguienteReto() {
        SQLiteDatabase db = this.getWritableDatabase();
        RetoProgramacion reto = null;
        // Obtenemos el más antiguo (FIFO)
        Cursor cursor = db.query(TABLE_RETOS, null, null, null, null, null, COL_ID + " ASC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            reto = new RetoProgramacion();
            reto.pregunta = cursor.getString(cursor.getColumnIndexOrThrow(COL_PREGUNTA));
            reto.codigo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CODIGO));
            reto.respuestaCorrecta = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESPUESTA));

            String opcRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_OPCIONES));
            if (opcRaw != null && !opcRaw.isEmpty()) {
                reto.opciones = new ArrayList<>(Arrays.asList(opcRaw.split("##")));
            }

            // Borramos el reto entregado para no repetirlo
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            db.delete(TABLE_RETOS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        }
        if (cursor != null) cursor.close();
        return reto;
    }

    public int contarRetosDisponibles() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RETOS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}