package com.example.pmd_proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public YoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoFragment newInstance(String param1, String param2) {
        YoFragment fragment = new YoFragment();
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

        SharedPreferences prefs =
                requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);

        boolean logged = prefs.getBoolean("logged", false);

        if (logged) {
            return cargarVistaPerfil(inflater, container, prefs);
        } else {
            return cargarVistaInvitado(inflater, container);
        }
    }

    private View cargarVistaInvitado(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragmento_yo_guest, container, false);

        view.findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LoginActivity.class))
        );

        view.findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RegisterActivity.class))
        );

        return view;
    }

    private View cargarVistaPerfil(LayoutInflater inflater,
                                   ViewGroup container,
                                   SharedPreferences prefs) {

        View view = inflater.inflate(R.layout.fragment_yo, container, false);
        TextView tvEmail = view.findViewById(R.id.tvEmailPerfil);
        TextView tvNombre = view.findViewById(R.id.tvNombrePerfil);

        String email = prefs.getString("email", "usuario");
        tvEmail.setText(email);

        String nombre = email.contains("@")
                ? email.substring(0, email.indexOf("@"))
                : email;

        tvNombre.setText(nombre);




        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new YoFragment())
                    .commit();
        });

        return view;
    }

}