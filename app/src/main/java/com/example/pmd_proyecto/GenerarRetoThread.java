package com.example.pmd_proyecto;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.pmd_proyecto.model.RetoProgramacion;

public class GenerarRetoThread implements Runnable {
    private Context ctx;
    private RetosFragment rf;

    public GenerarRetoThread(Context ctx, RetosFragment rf) {
        this.ctx = ctx;
        this.rf = rf;
    }

    @Override
    public void run() {
        DBHelper db = new DBHelper(ctx);
        RetoProgramacion retoParaMostrar = null;

        try {
            // 1. Intentamos sacar de la caché local primero (Offline mode)
            // Asegúrate de tener implementado 'obtenerSiguienteReto()' en tu DBHelper
            retoParaMostrar = db.obtenerSiguienteReto();

            // 2. Si no hay nada en base de datos, llamamos a la API (Online mode)
            if (retoParaMostrar == null) {
                Log.d("GenerarReto", "Cache vacía. Llamando a API...");
                retoParaMostrar = NetUtils.generarReto();
            }

            // 3. Mostramos el reto en la UI (con seguridad para no crashear)
            final RetoProgramacion finalReto = retoParaMostrar;

            // Verificamos que el fragmento sigue activo antes de tocar la UI
            if (rf.getActivity() != null && rf.isAdded()) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rf.mostrarReto(finalReto);
                    }
                });
            }

            // 4. Lógica de relleno en segundo plano (opcional pero recomendado)
            // Si la base de datos se está vaciando, bajamos más retos para la próxima
            int retosDisponibles = db.contarRetosDisponibles(); // Necesitas este método en DBHelper
            if (retosDisponibles < 6) {
                Log.d("GenerarReto", "Rellenando caché en background...");
                RetoProgramacion nuevoReto = NetUtils.generarReto();

                // Solo guardamos si el reto es válido
                if (nuevoReto != null && nuevoReto.pregunta != null) {
                    db.guardarReto(nuevoReto); // Necesitas este método en DBHelper
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerramos la base de datos para evitar fugas de memoria
            db.close();
        }
    }
}