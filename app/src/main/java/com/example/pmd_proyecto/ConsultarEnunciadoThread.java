package com.example.pmd_proyecto;

import android.app.Activity;
import android.content.Context;

import com.example.pmd_proyecto.model.EnunciadoProblema;

public class ConsultarEnunciadoThread implements Runnable {
    Context ctx;
    String id;
    DBHelper db;
    EnunciadoProblema enunciado;

    public ConsultarEnunciadoThread(Context ctx, String id){
        this.ctx = ctx;
        this.id  = id;
        db = DBHelper.getInstance(this.ctx);
    }

    @Override
    public void run() {
        try {
//            Primero intentamos obtenerlo de la base de datos
            enunciado = db.obtenerEnunciado(id);
            if (enunciado == null) {
//                Si no lo encuentra, lo consultamos a la API
                enunciado = NetUtils.ConsultarEnunciado(id);
                if (enunciado != null) db.guardarEnunciado(enunciado);
            }

            if (ctx == null) return;
            ((Activity) ctx).runOnUiThread(() -> ((EnunciadoActivity)ctx).mostrarEnunciado(enunciado));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
