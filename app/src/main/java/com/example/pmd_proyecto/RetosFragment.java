package com.example.pmd_proyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pmd_proyecto.model.RetoProgramacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.highlight.ColorTheme;

public class RetosFragment extends Fragment {
    private TextView txtPregunta;
    private CodeView codeView;
    private Button btnGenerar;
    private Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    private List<Button> botonesOpcion;
    private RetoProgramacion retoActual;

    public RetosFragment() {
        // Required empty public constructor
    }

    public static RetosFragment newInstance() {
        return new RetosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGenerar = view.findViewById(R.id.retos_button_generar);
        txtPregunta = view.findViewById(R.id.retos_textview_pregunta);
        codeView = view.findViewById(R.id.retos_code_view);
        btnOpt1 = view.findViewById(R.id.retos_button_opt1);
        btnOpt2 = view.findViewById(R.id.retos_button_opt2);
        btnOpt3 = view.findViewById(R.id.retos_button_opt3);
        btnOpt4 = view.findViewById(R.id.retos_button_opt4);
        botonesOpcion = new ArrayList<>(Arrays.asList(btnOpt1, btnOpt2, btnOpt3, btnOpt4));

        // 2. Asignar listeners
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarRetoThread task = new GenerarRetoThread(requireActivity(), RetosFragment.this);
                new Thread(task).start();
            }
        });
        btnOpt1.setOnClickListener(v -> comprobarRespuesta("A", v));
        btnOpt2.setOnClickListener(v -> comprobarRespuesta("B", v));
        btnOpt3.setOnClickListener(v -> comprobarRespuesta("C", v));
        btnOpt4.setOnClickListener(v -> comprobarRespuesta("D", v));

        // 3. Establecer el estado inicial de la UI
        mostrarReto(null); // Muestra un estado de bienvenida/vacío al inicio
    }

    public void mostrarReto(RetoProgramacion reto) {
        if (getView() == null || getContext() == null) return; // Asegurarse de que el fragmento está activo

        this.retoActual = reto;
        resetearBotones();

        if (reto == null || reto.pregunta == null) {
            // Estado inicial o de error en la generación
            txtPregunta.setText("Pulsa el botón 'Generar Reto' para empezar.");
            codeView.setVisibility(View.GONE);
            for (Button btn : botonesOpcion) {
                btn.setVisibility(View.GONE);
            }
            return;
        }

        // Poblar la UI con el nuevo reto
        txtPregunta.setText(reto.pregunta);

        // Configurar y mostrar el bloque de código si existe
        if (reto.codigo != null && !reto.codigo.isEmpty()) {
            codeView.setVisibility(View.VISIBLE);
            codeView.setOptions(io.github.kbiakov.codeview.adapters.Options.Default.get(getContext())
                    .withLanguage("java")
                    .withTheme(ColorTheme.DEFAULT));
            codeView.setCode(reto.codigo);
        } else {
            codeView.setVisibility(View.GONE);
        }

        // Configurar y mostrar los botones de opción
        if (reto.opciones != null && !reto.opciones.isEmpty()) {
            for (int i = 0; i < botonesOpcion.size(); i++) {
                if (i < reto.opciones.size()) {
                    botonesOpcion.get(i).setVisibility(View.VISIBLE);
                    botonesOpcion.get(i).setText(reto.opciones.get(i));
                } else {
                    botonesOpcion.get(i).setVisibility(View.GONE);
                }
            }
        } else {
            // Ocultar todos los botones si no hay opciones
            for (Button btn : botonesOpcion) {
                btn.setVisibility(View.GONE);
            }
        }
    }

    private void resetearBotones() {
        for (Button b : botonesOpcion) {
            b.setEnabled(true);
            b.setBackgroundResource(android.R.drawable.btn_default); // Restablece el fondo por defecto
        }
    }

    private void comprobarRespuesta(String letraSeleccionada, View botonPulsado) {
        if (retoActual == null || retoActual.respuestaCorrecta == null || getContext() == null) return;

        // Limpia la respuesta correcta (ej: "[A]" -> "A")
        String respuestaLimpia = retoActual.respuestaCorrecta.replaceAll("[\\[\\]]", "").trim();

        if (letraSeleccionada.equals(respuestaLimpia)) {
            botonPulsado.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
            Toast.makeText(getContext(), "¡Correcto!", Toast.LENGTH_SHORT).show();
        } else {
            botonPulsado.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
            Toast.makeText(getContext(), "Incorrecto. La respuesta era " + respuestaLimpia, Toast.LENGTH_SHORT).show();
        }

        // Deshabilitar todos los botones para que no sigan pulsando
        for (Button btn : botonesOpcion) {
            btn.setEnabled(false);
        }
    }
}
