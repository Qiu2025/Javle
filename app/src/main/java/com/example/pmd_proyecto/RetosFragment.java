package com.example.pmd_proyecto;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;

public class RetosFragment extends Fragment {

    private TextView tvAciertos, tvFallos;
    private String emailUsuario;

    public RetosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retos, container, false);

        // Para leer los emails del usuario en sesion
        SharedPreferences prefs =
                requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        emailUsuario = prefs.getString("email", null);

        // Cargar progreso inicial
        tvAciertos = view.findViewById(R.id.tv_aciertos);
        tvFallos = view.findViewById(R.id.tv_fallos);
        cargarProgreso();

        Button btnEmpezar = view.findViewById(R.id.fretos_btn_empezar);
        btnEmpezar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RetosActivity.class);
            startActivity(intent);
        });

        Button btnHistoryFailures = view.findViewById(R.id.btn_history_failures);
        btnHistoryFailures.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new ErroresFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void cargarProgreso() {
        if (emailUsuario == null) return;

        int[] progreso = DBHelper.getInstance(requireContext()).obtenerProgreso(emailUsuario);

        tvAciertos.setText(String.valueOf(progreso[0]));
        tvFallos.setText(String.valueOf(progreso[1]));
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarProgreso();
    }
}