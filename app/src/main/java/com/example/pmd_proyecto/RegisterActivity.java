package com.example.pmd_proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnRegister;
    TextView tvLoginAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginAccount = findViewById(R.id.tvLoginAccount);

        btnRegister.setOnClickListener(v -> hacerRegistro());

        tvLoginAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
        });
    }

    private void hacerRegistro() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Usamos tu UsuarioDAO existente
        boolean exito = UsuarioDAO.registrar(this, email, password);
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
}
