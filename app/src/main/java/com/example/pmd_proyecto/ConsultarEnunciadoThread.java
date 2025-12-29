package com.example.pmd_proyecto;

import android.app.Activity;
import android.content.Context;

import com.example.pmd_proyecto.model.EnunciadoProblema;

public class ConsultarEnunciadoThread implements Runnable{
    Context ctx;
    String id;
    public ConsultarEnunciadoThread(Context ctx, String id){
        this.ctx = ctx;
        this.id  = id;
    }

    @Override
    public void run() {
        try {
            EnunciadoProblema enunciado = NetUtils.ConsultarEnunciado(id);
            if (ctx == null) return;
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((EnunciadoActivity)ctx).mostrarEnunciado(enunciado);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
