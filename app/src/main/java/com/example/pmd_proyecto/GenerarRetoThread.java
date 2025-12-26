package com.example.pmd_proyecto;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.pmd_proyecto.model.RetoProgramacion;

public class GenerarRetoThread implements Runnable {
    private Context ctx;

    public GenerarRetoThread(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        DBHelper db = new DBHelper(ctx);
        RetoProgramacion retoParaMostrar = null;

        try {
            // Intentamos sacar de la cache local (Offline mode)
            retoParaMostrar = db.obtenerSiguienteReto();

            // Si no hay nada en base de datos, llamamos a la API (Online mode)
            if (retoParaMostrar == null) {
                retoParaMostrar = NetUtils.generarReto();
            }

            final RetoProgramacion finalReto = retoParaMostrar;

            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((RetosActivity) ctx).mostrarReto(finalReto);
                }
            });

            // Logica de relleno en segundo plano
            int retosDisponibles = db.contarRetosDisponibles();
            if (retosDisponibles < 6) {
                RetoProgramacion nuevoReto = NetUtils.generarReto();

                // Solo guardamos si el reto es valido
                if (nuevoReto != null && nuevoReto.pregunta != null) {
                    db.guardarReto(nuevoReto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}