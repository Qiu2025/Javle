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
        try {
            RetoProgramacion reto = NetUtils.generarReto();

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {((RetosActivity)ctx).mostrarReto(reto);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
