package com.example.pmd_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn = findViewById(R.id.main_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarAPI();
            }
        });

        // Inicio parte para borrar: solo es para ir a la Home

        /*
        Intent i = new Intent(MainActivity.this, Home.class);
        startActivity(i);
        */

        // Fin parte para borrar

    }
    // CLIENTE REST, donde el servidor es el API de GEMINI, y el cliente es la aplicación
    private void llamarAPI() {
        OkHttpClient client = new OkHttpClient();

        String apiKey = "AIzaSyD-ee5eQwyZMqWjZjyhcRq9m_JZPMd_1T8";

        String json = """
        {
          "contents": [{
            "parts": [{
              "text": "Genera un reto de programación tipo test en Java, que sea corto.
                       Formato obligatorio:
                       - Pregunta: [texto]
                       - Opciones: [A] ..., [B] ..., [C] ..., [D] ...
                       - Respuesta correcta: [letra de la opción correcta]
                       No escribas explicaciones ni código adicional. Solo devuelve el texto siguiendo exactamente este formato."
            }]
          }]
        }
        """;
        // PETICIÓN HTTP ENVIANDO EL JSON
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        // PROTOCOLO HTTPS, usamos post para acceder enviar el prompt al API de gemini
        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();



                Log.i("MainActivity", "Response: " + responseBody);

                EditText txt = findViewById(R.id.main_text);
                runOnUiThread(() -> {
                    txt.setText(responseBody);
                });
            }
        });
    }
}


