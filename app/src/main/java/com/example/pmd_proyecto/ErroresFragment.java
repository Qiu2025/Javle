package com.example.pmd_proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pmd_proyecto.model.ErrorReto;

import java.util.ArrayList;
import java.util.List;

public class ErroresFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_errores, container, false);

        ListView listView = view.findViewById(R.id.lista_fallos);

        SharedPreferences prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);

        List<ErrorReto> errores = DBHelper.getInstance(requireContext()).obtenerErrores(email);
        if (errores == null) {
            errores = new ArrayList<>();
        }

        ArrayAdapter<ErrorReto> adapter = new ArrayAdapter<ErrorReto>(
                requireContext(),
                R.layout.item_historial_fallos,
                errores
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View item = convertView;

                if (item == null) {
                    item = LayoutInflater.from(getContext()).inflate(R.layout.item_historial_fallos, parent, false);
                }

                ErrorReto errorActual = getItem(position);

                TextView tvTema = item.findViewById(R.id.tvId);
                TextView tvRespuesta = item.findViewById(R.id.tvRespuestaCorrecta);
                TextView tvEnunciado = item.findViewById(R.id.tvEnunciado);

                if (errorActual != null) {
                    tvTema.setText("Tema: " + errorActual.tema);
                    tvRespuesta.setText("Respuesta: " + errorActual.respuestaCorrecta);
                    tvEnunciado.setText(errorActual.pregunta);
                }

                return item;
            }
        };

        listView.setAdapter(adapter);

        return view;
    }
}