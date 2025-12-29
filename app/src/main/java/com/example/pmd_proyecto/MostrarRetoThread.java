package com.example.pmd_proyecto;

import android.app.Activity;
import android.app.Notification; // Import necesario
import android.content.Context;

import com.example.pmd_proyecto.model.RetoProgramacion;

import java.util.List;

public class MostrarRetoThread implements Runnable {
    private Context ctx;
    private DBHelper db;

    public MostrarRetoThread(Context ctx) {
        this.ctx = ctx;
        db = DBHelper.getInstance(ctx);
    }

    @Override
    public void run() {
        RetoProgramacion retoParaMostrar;

        try {
            // Intentamos obtener uno de la base de datos local
            retoParaMostrar = db.obtenerSiguienteReto();

            // Si es la primera ejecucion o BD vacia
            if (retoParaMostrar == null) {
                // Pedimos 10 de golpe
                List<RetoProgramacion> loteInicial = NetUtils.generarLoteRetos();

                if (!loteInicial.isEmpty()) {
                    // El primero lo usamos para mostrarlo ya
                    retoParaMostrar = loteInicial.get(0);
                }

                // Guardamos el resto en la BD para el futuro
                for (int i = 1; i < loteInicial.size(); i++) {
                    db.guardarReto(loteInicial.get(i));
                }
            }

            // Lo enviamos a la UI
            final RetoProgramacion finalReto = retoParaMostrar;
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((RetosActivity) ctx).mostrarReto(finalReto);
                }
            });

            // Logica para cargar nuevos retos en segundo plano
            int nuevosGuardados = db.reabastecerRetos();
            // Si se han descargado retos nuevos, lanzamos una notificación
            if (nuevosGuardados > 0) {
                NotificationHandler handler = new NotificationHandler(ctx);
                Notification.Builder nb = handler.createNotification(
                        "Stock recargado",
                        "Hemos añadido " + nuevosGuardados + " nuevos retos para ti."
                );
                // ID 1 para la notificación
                handler.getManager().notify(1, nb.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}