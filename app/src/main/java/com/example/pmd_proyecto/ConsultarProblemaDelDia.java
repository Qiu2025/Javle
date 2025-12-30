package com.example.pmd_proyecto;

import android.app.Activity;

import com.example.pmd_proyecto.model.EnunciadoProblema;

public class ConsultarProblemaDelDia implements Runnable{
    HomeFragment fragment;
    EnunciadoProblema enunciado;
    public ConsultarProblemaDelDia(HomeFragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void run() {
        try{
            enunciado = NetUtils.ConsultarDailyProblem();
            Activity ctx = fragment.getActivity();
            if (ctx == null) return;
            ctx.runOnUiThread(() -> fragment.mostrarProblema(enunciado));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
