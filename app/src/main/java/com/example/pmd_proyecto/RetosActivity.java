package com.example.pmd_proyecto;

import android.os.Bundle;
import android.util.Log;
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

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.classifier.CodeProcessor;
import io.github.kbiakov.codeview.highlight.ColorTheme;

public class RetosActivity extends AppCompatActivity {
    private Button btn;
    private TextView txtTema;
    private CodeView codeView;
    private Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    private RetoProgramacion retoActual;
    private String emailUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_retos);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        emailUsuario = prefs.getString("email", null);

        Log.d("RETOS", "Usuario en sesión: " + emailUsuario);

        CodeProcessor.init(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Carga automatica del primer reto
        GenerarRetoThread task = new GenerarRetoThread(RetosActivity.this);
        new Thread(task).start();

        btn = findViewById(R.id.main_button_generar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarRetoThread task = new GenerarRetoThread(RetosActivity.this);
                new Thread(task).start();
            }
        });

        txtTema = findViewById(R.id.main_textview_tema);
        codeView = findViewById(R.id.main_code_view);
        btnOpt1 = findViewById(R.id.main_button_opt1);
        btnOpt2 = findViewById(R.id.main_button_opt2);
        btnOpt3 = findViewById(R.id.main_button_opt3);
        btnOpt4 = findViewById(R.id.main_button_opt4);

        btnOpt1.setOnClickListener(v -> comprobarRespuesta("A", v, retoActual));
        btnOpt2.setOnClickListener(v -> comprobarRespuesta("B", v, retoActual));
        btnOpt3.setOnClickListener(v -> comprobarRespuesta("C", v, retoActual));
        btnOpt4.setOnClickListener(v -> comprobarRespuesta("D", v, retoActual));

        resetearBotones();
    }

    public void mostrarReto(RetoProgramacion reto) {
        // Asignar pregunta
        if (reto == null) {
            txtTema.setText("Error: No se pudo generar el reto.");
            return;
        }

        // Actualizar para tener el nuevo reto
        this.retoActual = reto;
        resetearBotones();

        txtTema.setText(reto.pregunta);

        if (reto.codigo != null) {
            codeView.setOptions(io.github.kbiakov.codeview.adapters.Options.Default.get(this)
                    .withLanguage("java")
                    .withTheme(ColorTheme.DEFAULT));
            codeView.setCode(reto.codigo);
        } else {
            codeView.setCode("Error: codigo no encontrado.");
        }

        // Asignar opciones
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

        DBHelper dbHelper = new DBHelper(this);

        if (letraSeleccionada.equals(reto.respuestaCorrecta)) {
            botonPulsado.setBackgroundColor(getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Correcto!", Toast.LENGTH_SHORT).show();

            dbHelper.sumarAcierto(emailUsuario);

        } else {
            botonPulsado.setBackgroundColor(getColor(android.R.color.holo_red_light));
            Toast.makeText(this, "Incorrecto!", Toast.LENGTH_SHORT).show();

            dbHelper.sumarFallo(emailUsuario);
        }

        // Deshabilitar botones
        btnOpt1.setEnabled(false);
        btnOpt2.setEnabled(false);
        btnOpt3.setEnabled(false);
        btnOpt4.setEnabled(false);
    }
}