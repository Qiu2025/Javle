package com.example.pmd_proyecto;

import android.app.Activity;
import android.content.Context;

import com.example.pmd_proyecto.model.Problem;

import java.util.List;

public class ConsultarProblemasThread implements Runnable{
    Context ctx;
    ProblemasFragment fragment;
    public ConsultarProblemasThread(Context ctx, ProblemasFragment fragment){
        this.ctx = ctx;
        this.fragment = fragment;
    }
    @Override
    public void run() {
        try{
            List<Problem> problemas = NetUtils.ConsultarProblemas();
            ((Activity)ctx).runOnUiThread(new Runnable() {
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
