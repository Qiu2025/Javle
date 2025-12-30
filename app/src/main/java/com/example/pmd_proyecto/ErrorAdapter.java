package com.example.pmd_proyecto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pmd_proyecto.model.ErrorReto;

import java.util.List;

public class ErrorAdapter extends BaseAdapter {
    Context ctx;
    List<ErrorReto> datos;

    public ErrorAdapter(Context ctx, List<ErrorReto> datos){
        this.ctx = ctx;
        this.datos = datos;
    }

    @Override
    public int getCount() {
        return (datos != null) ? datos.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.item_historial_fallos, parent, false);
        }

        TextView tvTema = convertView.findViewById(R.id.tvId);
        TextView tvRespuesta = convertView.findViewById(R.id.tvRespuestaCorrecta);
        TextView tvEnunciado = convertView.findViewById(R.id.tvEnunciado);

        ErrorReto er = datos.get(i);
        if (er != null) {
            String tema = er.tema != null ? er.tema : "Desconocido";
            String pregunta = er.pregunta != null ? er.pregunta : "Desconocido";
            String respuesta = er.respuestaCorrecta != null ? er.respuestaCorrecta : "-";

            tvTema.setText("Tema: " + tema);
            tvEnunciado.setText(pregunta);
            tvRespuesta.setText(respuesta);
        }

        return convertView;
    }
}