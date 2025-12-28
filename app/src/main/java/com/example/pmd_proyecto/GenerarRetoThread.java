package com.example.pmd_proyecto;

import android.app.Activity;
import android.app.Notification; // Import necesario
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
            int cantidadActual = db.contarRetosDisponibles();
            int nuevosDescargados = 0;

            // Solo nos ponemos a trabajar si el stock baja de 5 (el disparador)
            // Pero si trabajamos, rellenamos hasta 10 (el objetivo)
            if (cantidadActual < 5) {

                while (db.contarRetosDisponibles() < 10) {
                    RetoProgramacion nuevoReto = NetUtils.generarReto();

                    if (nuevoReto != null && nuevoReto.pregunta != null) {
                        db.guardarReto(nuevoReto);
                        nuevosDescargados++;
                    } else {
                        break;
                    }
                }
            }

            //Si se han descargado retos nuevos, lanzamos la notificación
            if (nuevosDescargados > 0) {
                NotificationHandler handler = new NotificationHandler(ctx);
                Notification.Builder nb = handler.createNotification(
                        "Stock recargado",
                        "Hemos añadido " + nuevosDescargados + " nuevos retos para ti."
                );
                // ID 1 para la notificación
                handler.getManager().notify(1, nb.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}