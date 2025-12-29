package com.example.pmd_proyecto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class Home extends AppCompatActivity {
    private Fragment homeF, retosF, problemasF, yoF;
    private Fragment active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- LÓGICA DE SESIÓN  ---
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        boolean isLogged = prefs.getBoolean("logged", false);
        boolean remember = prefs.getBoolean("remember", false);
        boolean fromLogin = prefs.getBoolean("from_login", false);

        // Si no marcó "Recordar" y no viene directamente del login (es un reinicio de app)
        if (isLogged && !remember && !fromLogin) {
            prefs.edit().clear().apply();
            isLogged = false;
        }
        // ---------------------------------------

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
        bottomNav.setSelectedItemId(fromLogin ? R.id.nav_yo : R.id.nav_home);

        // Reseteamos el flag de login para la siguiente vez
        prefs.edit().putBoolean("from_login", false).apply();

        // Notificaciones
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Carga de retos inicial automatica
        new Thread(() -> {
            DBHelper.getInstance(Home.this).reabastecerRetos();
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
                programarNotificacionDiaria();
            } else {
                Toast.makeText(this, "No recibiras avisos para practicar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void programarNotificacionDiaria() {
        // Configurando la hora
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Creamos el PendingIntent
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, flags);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            // Cancelamos cualquier alarma previa que tuviera este mismo PendingIntent
            alarmManager.cancel(pendingIntent);

            // Establecemos la nueva alarma
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }
}