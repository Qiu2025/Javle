package com.example.pmd_proyecto;

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

        // Añadir todas una vez, mostrando solo Home
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView, yoF, "YO").hide(yoF)
                .add(R.id.fragmentContainerView, problemasF, "PROB").hide(problemasF)
                .add(R.id.fragmentContainerView, retosF, "RETOS").hide(retosF)
                .add(R.id.fragmentContainerView, homeF, "HOME")
                .commit();

        active = homeF;

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
//        Fragmento nicial
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean logged = getSharedPreferences("session", MODE_PRIVATE)
                .getBoolean("logged", false);

        Fragment actual = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        if (logged && actual instanceof YoFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new YoFragment())
                    .commit();
        }
    }
}