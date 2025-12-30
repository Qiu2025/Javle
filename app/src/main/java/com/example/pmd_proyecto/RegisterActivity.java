package com.example.pmd_proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;


import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etPais;
    Button btnRegister;
    TextView tvLoginAccount;

    private static final int LOCATION_PERMISSION_CODE = 100;
    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        etPais = findViewById(R.id.etPais);
        tvLoginAccount = findViewById(R.id.tvLoginAccount);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        btnRegister.setOnClickListener(v -> hacerRegistro());

        tvLoginAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
        });

        pedirPermisoLocalizacion();

    }

    private void hacerRegistro() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String pais = etPais.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        boolean exito = UsuarioDAO.registrar(this, email, password, pais);
        if (exito) {
            Toast.makeText(this, "Cuenta creada, inicia sesión", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error: El usuario ya existe o hubo un fallo", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarSesion(String email) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("logged", true)
                .putString("email", email)
                .apply();
    }


    private void pedirPermisoLocalizacion() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
        } else {
            obtenerLocalizacion();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerLocalizacion();
        } else {
            etPais.setEnabled(true);
            Toast.makeText(this,
                    "No se pudo obtener la localización. Introduce el país manualmente.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerLocalizacion() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        obtenerPais(location.getLatitude(), location.getLongitude());
                    } else {
                        etPais.setEnabled(true);
                    }
                });
    }


    private void obtenerPais(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !addresses.isEmpty()) {
                String pais = addresses.get(0).getCountryName();
                etPais.setText(pais);
            } else {
                etPais.setEnabled(true);
            }
        } catch (Exception e) {
            etPais.setEnabled(true);
        }
    }




}
