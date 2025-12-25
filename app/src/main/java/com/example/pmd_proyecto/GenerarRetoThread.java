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
        try {
            RetoProgramacion reto = NetUtils.generarReto();

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rf.mostrarReto(reto);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
