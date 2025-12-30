package com.example.pmd_proyecto;

import android.app.Activity;

import com.example.pmd_proyecto.model.EnunciadoProblema;

public class ConsultarProblemaDelDia implements Runnable {
    HomeFragment fragment;
    DBHelper db;
    EnunciadoProblema enunciado;

    public ConsultarProblemaDelDia(HomeFragment fragment){
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
            if (db != null) enunciado = db.obtenerProblemaDia();

            if (enunciado == null) {
//                Si no lo encuentra, lo consultamos a la API
                enunciado = NetUtils.ConsultarDailyProblem();
                if (db != null && enunciado != null) db.guardarProblemaDia(enunciado);
            }

            Activity ctx = fragment.getActivity();
            if (ctx == null) return;
            ctx.runOnUiThread(() -> fragment.mostrarProblema(enunciado));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
