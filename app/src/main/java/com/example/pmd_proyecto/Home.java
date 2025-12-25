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

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();

            if (id == R.id.nav_home) f = new HomeFragment();
            else if (id == R.id.nav_retos) f = new RetosFragment();
            else if (id == R.id.nav_problemas) f = new ProblemasFragment();
            else f = new YoFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, f)
                    .commit();

            return true;
        });
        // sección inicial
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