package com.example.pmd_proyecto;

import android.content.Intent;
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

// Para la alarma
import android.app.AlarmManager;
import android.app.PendingIntent;
import java.util.Calendar;

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Preferencias
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        emailUsuario = prefs.getString("email", null);

        // Botones
        Button btnSiguiente = findViewById(R.id.retos_button_siguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarRetoThread task = new MostrarRetoThread(RetosActivity.this);
                new Thread(task).start();
            }
        });

        btnOpt1 = findViewById(R.id.retos_button_opt1);
        btnOpt1.setOnClickListener(v -> comprobarRespuesta("A", v, retoActual));

        btnOpt2 = findViewById(R.id.retos_button_opt2);
        btnOpt2.setOnClickListener(v -> comprobarRespuesta("B", v, retoActual));

        btnOpt3 = findViewById(R.id.retos_button_opt3);
        btnOpt3.setOnClickListener(v -> comprobarRespuesta("C", v, retoActual));

        btnOpt4 = findViewById(R.id.retos_button_opt4);
        btnOpt4.setOnClickListener(v -> comprobarRespuesta("D", v, retoActual));

        // Textos
        tvTema = findViewById(R.id.retos_textview_tema);
        tvPregunta = findViewById(R.id.retos_textview_pregunta);
        tvCode = findViewById(R.id.retos_textview_code);

        // Carga automatica del primer reto
        MostrarRetoThread task = new MostrarRetoThread(RetosActivity.this);
        new Thread(task).start();

        resetearBotones();
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

    private void resetearBotones() {
        Button[] botones = {btnOpt1, btnOpt2, btnOpt3, btnOpt4};
        for (Button b : botones) {
            b.setEnabled(true);
            b.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    private void comprobarRespuesta(String letraSeleccionada, View botonPulsado, RetoProgramacion reto) {
        if (reto == null) return;

        DBHelper dbHelper = DBHelper.getInstance(this);

        if (letraSeleccionada.equals(reto.respuestaCorrecta)) {
            botonPulsado.setBackgroundColor(getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Correcto!", Toast.LENGTH_SHORT).show();

            dbHelper.sumarAcierto(emailUsuario);
        } else {
            botonPulsado.setBackgroundColor(getColor(android.R.color.holo_red_light));
            Toast.makeText(this, "Incorrecto!", Toast.LENGTH_SHORT).show();

            dbHelper.sumarFallo(emailUsuario);
            dbHelper.guardarError(emailUsuario, reto);
        }

        // Deshabilitar botones
        btnOpt1.setEnabled(false);
        btnOpt2.setEnabled(false);
        btnOpt3.setEnabled(false);
        btnOpt4.setEnabled(false);
    }
}