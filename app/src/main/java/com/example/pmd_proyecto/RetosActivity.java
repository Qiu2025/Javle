package com.example.pmd_proyecto;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pmd_proyecto.model.RetoProgramacion;

import java.util.ArrayList;

public class RetosActivity extends AppCompatActivity {
    private TextView tvTema, tvPregunta, tvCode;
    private Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    private RetoProgramacion retoActual;
    private String emailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_retos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializacion
        tvTema = findViewById(R.id.retos_textview_tema);
        tvPregunta = findViewById(R.id.retos_textview_pregunta);
        tvCode = findViewById(R.id.retos_textview_code);
        btnOpt1 = findViewById(R.id.retos_button_opt1);
        btnOpt2 = findViewById(R.id.retos_button_opt2);
        btnOpt3 = findViewById(R.id.retos_button_opt3);
        btnOpt4 = findViewById(R.id.retos_button_opt4);

        // Preferencias
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        emailUsuario = prefs.getString("email", null);

        // Modo revision
        boolean modoRevision = getIntent().getBooleanExtra("MODO_REVISION", false);

        if (modoRevision) {
            configurarRevision();
        } else {
            // Comportamiento normal
            Button btnSiguiente = findViewById(R.id.retos_button_siguiente);
            btnSiguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MostrarRetoThread task = new MostrarRetoThread(RetosActivity.this);
                    new Thread(task).start();
                }
            });

            configurarBoton(btnOpt1, "A");
            configurarBoton(btnOpt2, "B");
            configurarBoton(btnOpt3, "C");
            configurarBoton(btnOpt4, "D");

            MostrarRetoThread task = new MostrarRetoThread(RetosActivity.this);
            new Thread(task).start();
        }
    }

    private void configurarRevision() {
        Intent i = getIntent();
        tvTema.setText("Revisión: " + i.getStringExtra("TEMA"));
        tvPregunta.setText(i.getStringExtra("PREGUNTA"));

        String codigo = i.getStringExtra("CODIGO");
        if (codigo != null && !codigo.trim().isEmpty() && !codigo.equals("NO_CODE")) {
            tvCode.setVisibility(View.VISIBLE);
            tvCode.setText(codigo);
        } else {
            tvCode.setVisibility(View.GONE);
        }

        ArrayList<String> opciones = i.getStringArrayListExtra("OPCIONES");
        if (opciones != null && opciones.size() >= 4) {
            btnOpt1.setText(opciones.get(0));
            btnOpt2.setText(opciones.get(1));
            btnOpt3.setText(opciones.get(2));
            btnOpt4.setText(opciones.get(3));
        }

        String correcta = i.getStringExtra("CORRECTA");
        String usuario = i.getStringExtra("USUARIO");

        Button[] botones = {btnOpt1, btnOpt2, btnOpt3, btnOpt4};
        for (Button btn : botones) {
            btn.setEnabled(false);
            String texto = btn.getText().toString();

            if (texto.equals(correcta)) {
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                btn.setTextColor(Color.WHITE);
            } else if (texto.equals(usuario)) {
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                btn.setTextColor(Color.WHITE);
            } else {
                btn.setAlpha(0.5f);
            }
        }

        Button btnVolver = findViewById(R.id.retos_button_siguiente);
        btnVolver.setText("VOLVER");
        btnVolver.setOnClickListener(v -> finish());
    }

    public void mostrarReto(RetoProgramacion reto) {
        if (reto == null) {
            tvTema.setText("Error: No se pudo cargar el reto.");
            return;
        }
        this.retoActual = reto;

        tvTema.setText(reto.tema);
        tvPregunta.setText(reto.pregunta);
        resetearBotones();

        // Verificar si es una pregunta teorica o de codigo
        if (reto.codigo == null || reto.codigo.trim().equals("NO_CODE") || reto.codigo.trim().isEmpty()) {
            // Es teorica
            tvCode.setVisibility(View.GONE);
        } else {
            // Es practica
            tvCode.setVisibility(View.VISIBLE);
            tvCode.setText(reto.codigo);
        }

        // Asignar opciones con seguridad
        if (reto.opciones == null || reto.opciones.size() < 4) {
            btnOpt1.setText("Error: No se pudo generar las opciones.");
            btnOpt2.setText("Error: No se pudo generar las opciones.");
            btnOpt3.setText("Error: No se pudo generar las opciones.");
            btnOpt4.setText("Error: No se pudo generar las opciones.");
        } else {
            btnOpt1.setText(reto.opciones.get(0));
            btnOpt2.setText(reto.opciones.get(1));
            btnOpt3.setText(reto.opciones.get(2));
            btnOpt4.setText(reto.opciones.get(3));
        }
    }

    private void configurarBoton(Button btn, String letra) {
        btn.setOnClickListener(v -> {
            String texto = ((Button) v).getText().toString().toUpperCase().trim();
            comprobarRespuesta(v, letra, texto, retoActual);
        });
    }

    private void resetearBotones() {
        Button[] botones = {btnOpt1, btnOpt2, btnOpt3, btnOpt4};
        for (Button b : botones) {
            b.setEnabled(true);
            b.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        }
    }

    private void comprobarRespuesta(View botonPulsado, String letra, String texto, RetoProgramacion reto) {
        if (reto == null) return;

        DBHelper dbHelper = DBHelper.getInstance(this);

        String respuestaCorrecta = reto.respuestaCorrecta.toUpperCase().trim();
        String respuestaMarcada = ((Button) botonPulsado).getText().toString();

        if (letra.equals(respuestaCorrecta) || texto.equals(respuestaCorrecta)) {
            botonPulsado.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.holo_green_light)));
            Toast.makeText(this, "Correcto!", Toast.LENGTH_SHORT).show();

            dbHelper.sumarAcierto(emailUsuario);
        } else {
            botonPulsado.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.holo_red_light)));
            Toast.makeText(this, "Incorrecto!", Toast.LENGTH_SHORT).show();

            dbHelper.sumarFallo(emailUsuario);
            dbHelper.guardarError(emailUsuario, reto, respuestaMarcada);
        }

        // Deshabilitar botones
        btnOpt1.setEnabled(false);
        btnOpt2.setEnabled(false);
        btnOpt3.setEnabled(false);
        btnOpt4.setEnabled(false);
    }
}