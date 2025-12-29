package com.example.pmd_proyecto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pmd_proyecto.model.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProblemasFragment extends Fragment {
    ListView lv;
    ProblemAdapter adapter;
    List<Problem> problemas;

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

        ConsultarProblemasThread task = new ConsultarProblemasThread(ProblemasFragment.this);
        new Thread(task).start();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EnunciadoActivity.class);
                intent.putExtra("ID", ""+(position+1));
                startActivity(intent);
            }
        });
    }

    public void mostrarProblemas(List<Problem> problemas){
        if (!isAdded() || getActivity() == null || lv == null) return;
        this.problemas = problemas;
        adapter = new ProblemAdapter(requireActivity(), this.problemas);
        lv.setAdapter(adapter);
    }
}