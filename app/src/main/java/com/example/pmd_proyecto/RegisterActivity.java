package com.example.pmd_proyecto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnRegister;
    UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnLogin); // reutilizas el botón

        btnRegister.setText("Crear cuenta");

        usuarioDAO = new UsuarioDAO(this);

        btnRegister.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = usuarioDAO.registrar(email, password);

        if (ok) {
            guardarSesion(email);
            finish(); // vuelve al fragmento Yo
        } else {
            Toast.makeText(this, "Ese email ya existe", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarSesion(String email) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("logged", true)
                .putString("email", email)
                .apply();
    }
}
