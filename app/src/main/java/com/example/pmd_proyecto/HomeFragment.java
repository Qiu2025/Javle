package com.example.pmd_proyecto;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmd_proyecto.model.EnunciadoProblema;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    TextView tvId, tvTitle, tvDifficulty;
    Button btnVerProblema;
    EnunciadoProblema problemaDia;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Referencia al botón
        View btnStart = view.findViewById(R.id.btn_empezarReto);

        btnStart.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.menu);
            bottomNav.setSelectedItemId(R.id.nav_retos);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias problema del día
        tvId = view.findViewById(R.id.tvIdDaily);
        tvTitle = view.findViewById(R.id.tvTitleDaily);
        tvDifficulty = view.findViewById(R.id.tvDifficultyDaily);

        // Botón para ver problema
        btnVerProblema = view.findViewById(R.id.btn_verProblema);
        btnVerProblema.setEnabled(false);
        btnVerProblema.setOnClickListener(v -> {
            if (problemaDia == null || problemaDia.questionFrontendId == null) return;
            Intent intent = new Intent(getActivity(), EnunciadoActivity.class);
            intent.putExtra("ID", problemaDia.questionFrontendId);
            startActivity(intent);
        });

        // Cargar problema del día
        ConsultarProblemaDelDia task = new ConsultarProblemaDelDia(HomeFragment.this);
        new Thread(task).start();
    }

    public void mostrarProblema(EnunciadoProblema enunciado) {
        if (!isAdded() || getView() == null) return;

        problemaDia = enunciado;

        if (enunciado == null) {
            tvId.setText("ID ?");
            tvTitle.setText("No se pudo cargar el problema del día");
            tvDifficulty.setText("");
            btnVerProblema.setEnabled(false);
            return;
        }

        tvId.setText("ID " + enunciado.questionFrontendId);
        tvTitle.setText(enunciado.title != null ? enunciado.title : "");
        tvDifficulty.setText(enunciado.difficulty != null ? enunciado.difficulty : "");
        btnVerProblema.setEnabled(enunciado.questionFrontendId != null);
    }

}