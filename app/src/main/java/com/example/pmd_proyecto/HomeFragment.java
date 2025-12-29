package com.example.pmd_proyecto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Referencia al botón
        View btnStart = view.findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.menu);
            bottomNav.setSelectedItemId(R.id.nav_retos);
        });

        return view;
    }
}