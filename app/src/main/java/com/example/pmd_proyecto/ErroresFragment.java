package com.example.pmd_proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pmd_proyecto.model.ErrorReto;

import java.util.List;

public class ErroresFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_errores, container, false);
        TextView tvErrores = view.findViewById(R.id.tvErrores);

        SharedPreferences prefs =
                requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);

        List<ErrorReto> errores = DBHelper.getInstance(requireContext()).obtenerErrores(email);

        if (errores.isEmpty()) {
            tvErrores.setText("No hay errores registrados.");
        } else {
            StringBuilder sb = new StringBuilder();

            for (ErrorReto e : errores) {
                sb.append("• Pregunta: ")
                        .append(e.pregunta)
                        .append("\n")
                        .append("  Respuesta correcta: ")
                        .append(e.respuestaCorrecta)
                        .append("\n\n");
            }

            tvErrores.setText(sb.toString());
        }

        return view;
    }
}
