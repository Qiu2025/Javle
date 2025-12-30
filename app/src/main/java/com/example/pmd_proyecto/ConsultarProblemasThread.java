package com.example.pmd_proyecto;

import android.app.Activity;

import com.example.pmd_proyecto.model.Problem;

import java.util.List;

public class ConsultarProblemasThread implements Runnable {
    ProblemasFragment fragment;
    DBHelper db;
    List<Problem> problemas;

    public ConsultarProblemasThread(ProblemasFragment fragment){
        this.fragment = fragment;
        Activity ctx = fragment.getActivity();
        if (ctx != null) {
            db = DBHelper.getInstance(ctx);
        }
    }

    @Override
    public void run() {
        try{
//            Primero intentamos obtenerlo de la base de datos
            if (db != null) problemas = db.obtenerProblemas();

            if (problemas == null) {
//                Si no lo encuentra, lo consultamos a la API
                problemas = NetUtils.ConsultarProblemas();
                if (db != null && problemas != null) db.guardarProblemas(problemas);
            }

            Activity ctx = fragment.getActivity();
            if (ctx == null) return;
            ctx.runOnUiThread(() -> fragment.mostrarProblemas(problemas));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
