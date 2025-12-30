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
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            // Asegúrate de que el nombre del XML (R.layout.item_problem_layout) sea correcto
            convertView = inflater.inflate(R.layout.item_historial_fallos, parent, false);

            holder = new ViewHolder();
            holder.tvTema = convertView.findViewById(R.id.tvId);
            holder.tvRespuesta = convertView.findViewById(R.id.tvRespuestaCorrecta);
            holder.tvEnunciado = convertView.findViewById(R.id.tvEnunciado);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ErrorReto er = datos.get(i);
        if (er != null) {
            String tema = er.tema != null ? er.tema : "Desconocido";
            String pregunta = er.pregunta != null ? er.pregunta : "Desconocido";
            String respuesta = er.respuestaCorrecta != null ? er.respuestaCorrecta : "-";

            holder.tvTema.setText("Tema: " + tema);
            holder.tvEnunciado.setText(pregunta);
            holder.tvRespuesta.setText("Correcta: " + respuesta);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvTema;
        TextView tvRespuesta;
        TextView tvEnunciado;
    }
}