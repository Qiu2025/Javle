package com.example.pmd_proyecto;

import android.app.Activity;

import com.example.pmd_proyecto.model.Problem;

import java.util.List;

public class ConsultarProblemasThread implements Runnable{
    ProblemasFragment fragment;
    public ConsultarProblemasThread(ProblemasFragment fragment){
        this.fragment = fragment;
    }
    @Override
    public void run() {
        try{
            List<Problem> problemas = NetUtils.ConsultarProblemas();
            Activity ctx = fragment.getActivity();
            if(ctx == null) return;
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.mostrarProblemas(problemas);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
