package com.example.pmd_proyecto;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.pmd_proyecto.model.Problem;

import java.util.ArrayList;
import java.util.List;

public class ProblemasFragment extends Fragment {
    ListView lv;
    ProblemAdapter adapter;
    List<Problem> problemas;
    List<Problem> problemasMostrados;
    static int nMostrados;

    public ProblemasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_problemas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.lista_problemas);

        nMostrados = 0;
        problemasMostrados = new ArrayList<>();
        problemas = new ArrayList<>();

        ConsultarProblemasThread task = new ConsultarProblemasThread(ProblemasFragment.this);
        new Thread(task).start();

    }

    public void mostrarProblemas(List<Problem> problemas){
//        Mostrar los primeros problemas
        if (!isAdded() || getActivity() == null || lv == null) return;
        this.problemas = problemas;
        for(int i = 0; i<problemas.size() && i<20;i++){
            problemasMostrados.add(this.problemas.get(i));
            nMostrados++;
        }
        adapter = new ProblemAdapter(requireActivity(), problemasMostrados);
        lv.setAdapter(adapter);

//        Abrir enunciado al hacer click en un problema
        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), EnunciadoActivity.class);
            String idString = ""+ (problemas.get(position).frontend_id);
            intent.putExtra("ID", idString);
            startActivity(intent);
        });

//        Cargar más problemas al llegar al final de la lista
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    mostrarSiguientesProblemas();
                }
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
        });
    }

//    Mostrar los siguientes problemas
    public void mostrarSiguientesProblemas(){
        for(int i = 0; nMostrados<problemas.size() && i<20;i++){
            problemasMostrados.add(problemas.get(nMostrados));
            nMostrados++;
        }
        adapter.notifyDataSetChanged();
    }
}