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

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RetosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RetosFragment extends Fragment {

    private TextView tvAciertos;
    private TextView tvFallos;
    private String emailUsuario;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RetosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RetosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RetosFragment newInstance(String param1, String param2) {
        RetosFragment fragment = new RetosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retos, container, false);

        tvAciertos = view.findViewById(R.id.tv_aciertos);
        tvFallos = view.findViewById(R.id.tv_fallos);

        // Para leer los email del usuario en sesión
        SharedPreferences prefs =
                requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        emailUsuario = prefs.getString("email", null);

        // Cargar progreso inicial
        cargarProgreso();

        Button btnEmpezar = view.findViewById(R.id.fretos_btn_empezar);
        btnEmpezar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RetosActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void cargarProgreso() {
        if (emailUsuario == null) return;

        DBHelper dbHelper = new DBHelper(requireContext());
        int[] progreso = dbHelper.obtenerProgreso(emailUsuario);

        tvAciertos.setText(String.valueOf(progreso[0]));
        tvFallos.setText(String.valueOf(progreso[1]));
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarProgreso();
    }

}