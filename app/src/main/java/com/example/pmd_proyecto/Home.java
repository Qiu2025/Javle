package com.example.pmd_proyecto;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private Fragment homeF, retosF, problemasF, yoF;
    private Fragment active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.menu);

        // Crear instancias una vez
        homeF = new HomeFragment();
        retosF = new RetosFragment();
        problemasF = new ProblemasFragment();
        yoF = new YoFragment();

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        boolean fromLogin = prefs.getBoolean("from_login", false);

        // Añadir todas una vez, mostrando solo Home
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView, homeF, "HOME")
                .add(R.id.fragmentContainerView, yoF, "YO")
                .add(R.id.fragmentContainerView, problemasF, "PROB")
                .add(R.id.fragmentContainerView, retosF, "RETOS")
                .hide(fromLogin ? homeF : yoF)
                .hide(problemasF)
                .hide(retosF)
                .commit();

        active = fromLogin ? yoF : homeF;

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment target;
            int id = item.getItemId();

            if (id == R.id.nav_home) target = homeF;
            else if (id == R.id.nav_retos) target = retosF;
            else if (id == R.id.nav_problemas) target = problemasF;
            else target = yoF;

            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(active)
                    .show(target)
                    .commit();
            active = target;
            return true;
        });

        // Fragmento inicial
        bottomNav.setSelectedItemId(
                fromLogin ? R.id.nav_yo : R.id.nav_home
        );
        prefs.edit().putBoolean("from_login", false).apply();

        // Carga de retos inicial automatica
        new Thread(() -> {
            DBHelper db = new DBHelper(Home.this);
            db.reabastecerRetos();
        }).start();
    }
}