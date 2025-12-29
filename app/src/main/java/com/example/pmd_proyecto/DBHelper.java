package com.example.pmd_proyecto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.text.TextUtils;

import com.example.pmd_proyecto.model.ErrorReto;
import com.example.pmd_proyecto.model.RetoProgramacion;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {

    public static DBHelper instance;   // unica instancia en la app
    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 11;

    // Tablas
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String TABLE_RETOS = "retos";
    public static final String TABLE_PROGRESO = "progreso";
    public static final String TABLE_ERRORES = "errores";

    // Columnas TABLE_RETOS
    public static final String COL_ID = "_id";
    public static final String COL_TEMA = "tema";
    public static final String COL_PREGUNTA = "pregunta";
    public static final String COL_CODIGO = "codigo";
    public static final String COL_OPCIONES = "opciones"; // Guardado como texto separado
    public static final String COL_RESPUESTA = "respuesta_correcta";

    // Columnas TABLE_
    public static final String COL_EMAIL = "email";
    public static final String COL_ACIERTOS = "aciertos";
    public static final String COL_FALLOS = "fallos";
    public static final String COL_PREGUNTA_ERR = "pregunta";
    public static final String COL_RESPUESTA_ERR = "respuesta_correcta";
    public static final String COL_FECHA = "fecha";

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla usuarios existente
        db.execSQL(
                "CREATE TABLE " + TABLE_USUARIOS + "(" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "avatar TEXT)"
        );

        // Tabla para almacenar retos generados por Gemini
        db.execSQL(
                "CREATE TABLE " + TABLE_RETOS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_TEMA + " TEXT, " +
                        COL_PREGUNTA + " TEXT, " +
                        COL_CODIGO + " TEXT, " +
                        COL_OPCIONES + " TEXT, " +
                        COL_RESPUESTA + " TEXT)"
        );

        // Para almacenar aciertos y fallos en retos
        db.execSQL(
                "CREATE TABLE " + TABLE_PROGRESO + "(" +
                        "email TEXT PRIMARY KEY, " +
                        "aciertos INTEGER DEFAULT 0, " +
                        "fallos INTEGER DEFAULT 0, " +
                        "FOREIGN KEY(email) REFERENCES usuarios(email))"
        );

        // Para almacenar retos fallados
        db.execSQL(
                "CREATE TABLE " + TABLE_ERRORES + "(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT, " +
                        "pregunta TEXT, " +
                        "respuesta_correcta TEXT, " +
                        "fecha INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ERRORES);

        onCreate(db);
    }

    // Metodos para gestion de retos
    public void guardarReto(RetoProgramacion reto) {
        if (reto == null || reto.pregunta == null) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEMA, reto.tema);
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
            reto.tema = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEMA));
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

    // Metodo para cargar nuevos retos
    public int reabastecerRetos() {
        int cantidadActual = contarRetosDisponibles();
        int nuevosGuardados = 0;

        // Si quedan menos de 5 retos en la BD, pedimos...
        if (cantidadActual < 5) {
            // otro lote de 10
            List<RetoProgramacion> loteAdicional = NetUtils.generarLoteRetos();

            for (RetoProgramacion r : loteAdicional) {
                guardarReto(r);
                nuevosGuardados++;
            }
        }

        return nuevosGuardados;
    }

    public void asegurarProgresoUsuario(String email) {
        if (email == null) return;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(
                TABLE_PROGRESO,
                new String[]{COL_EMAIL},
                COL_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        );

        boolean existe = c.moveToFirst();
        c.close();

        if (!existe) {
            ContentValues values = new ContentValues();
            values.put(COL_EMAIL, email);
            values.put(COL_ACIERTOS, 0);
            values.put(COL_FALLOS, 0);
            db.insert(TABLE_PROGRESO, null, values);
        }
    }

    public void sumarAcierto(String email) {
        if (email == null) return;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "UPDATE " + TABLE_PROGRESO +
                        " SET " + COL_ACIERTOS + " = " + COL_ACIERTOS + " + 1 " +
                        " WHERE " + COL_EMAIL + " = ?",
                new Object[]{email}
        );
    }

    public void sumarFallo(String email) {
        if (email == null) return;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "UPDATE " + TABLE_PROGRESO +
                        " SET " + COL_FALLOS + " = " + COL_FALLOS + " + 1 " +
                        " WHERE " + COL_EMAIL + " = ?",
                new Object[]{email}
        );
    }

    public int[] obtenerProgreso(String email) {
        int[] resultado = new int[]{0, 0}; // [aciertos, fallos]
        if (email == null) return resultado;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(
                TABLE_PROGRESO,
                new String[]{COL_ACIERTOS, COL_FALLOS},
                COL_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        );

        if (c.moveToFirst()) {
            resultado[0] = c.getInt(0);
            resultado[1] = c.getInt(1);
        }
        c.close();
        return resultado;
    }

    public void guardarError(String email, RetoProgramacion reto) {
        if (email == null || reto == null) return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("email", email);
        values.put(COL_PREGUNTA_ERR, reto.pregunta);
        values.put(COL_RESPUESTA_ERR, reto.respuestaCorrecta);
        values.put(COL_FECHA, System.currentTimeMillis());

        db.insert(TABLE_ERRORES, null, values);
    }

    public List<ErrorReto> obtenerErrores(String email) {
        List<ErrorReto> lista = new ArrayList<>();

        if (email == null) return lista;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "errores",
                new String[]{"pregunta", "respuesta_correcta", "fecha"},
                "email = ?",
                new String[]{email},
                null,
                null,
                "fecha DESC"
        );

        while (cursor.moveToNext()) {
            String pregunta = cursor.getString(0);
            String respuesta = cursor.getString(1);
            long fecha = cursor.getLong(2);

            lista.add(new ErrorReto(pregunta, respuesta, fecha));
        }

        cursor.close();
        return lista;
    }
}