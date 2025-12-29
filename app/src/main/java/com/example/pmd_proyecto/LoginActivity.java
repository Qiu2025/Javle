package com.example.pmd_proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvCreateAccount, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> hacerLogin());
        tvCreateAccount.setOnClickListener(v -> abrirRegistro());
    }

    private void hacerLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean correcto = UsuarioDAO.login(this, email, password);

        if (correcto) {
            Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show();
            guardarSesion(email);
            finish();
        } else {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarSesion(String email) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("logged", true)
                .putBoolean("from_login", true)
                .putString("email", email)
                .apply();

        DBHelper.getInstance(this).asegurarProgresoUsuario(email);

        Intent intent = new Intent(LoginActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    private void abrirRegistro() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
