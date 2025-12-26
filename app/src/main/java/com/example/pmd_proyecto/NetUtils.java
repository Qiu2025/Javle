package com.example.pmd_proyecto;

import android.util.Log;

import com.example.pmd_proyecto.model.GeminiResponse;
import com.example.pmd_proyecto.model.Problem;
import com.example.pmd_proyecto.model.RetoProgramacion;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetUtils {
    private static final String apiKey = BuildConfig.GEMINI_API_KEY;
    private static final String[] temas = {
            "Diferencia entre == y .equals()",
            "Polimorfismo",
            "Herencia",
            "Interfaces",
            "Clases abstractas",
            "Excepciones",
            "Modificadores de acceso",
            "Uso de la palabra clave static",
            "Paso por valor vs por referencia",
    };

    private static String obtenerJsonPrompt() {
        String temaAzar = temas[(int) (Math.random() * temas.length)];
        long seed = System.currentTimeMillis();
        String template = """
        {
          "contents": [{
            "parts": [{
              "text": "Genera un reto técnico nivel facil en JAVA sobre el tema: %s. Debe ser corto
                       ID: %d. Responde ÚNICAMENTE con este formato, sin texto extra:
                       
                       [PREGUNTA]
                       ... (Pocas palabras que clasifique el tema del problema)
                       
                       [CODIGO]
                       (maximo 15 lineas, NO TE PASES DE ESTE LIMITE)
                       
                       [OPCIONES] (maximo una linea por cada opcion)
                       [A]...
                       [B]...
                       [C]...
                       [D]...
                       
                       [CORRECTA]
                       (solo la letra correcta, nada extra)"
            }]
          }],
          "generationConfig": {
            "temperature": 1.0,
            "topP": 0.95,
            "maxOutputTokens": 1024
          }
        }
        """;

        return String.format(template, temaAzar, seed);
    }

    // Metodo public para generar un reto
    public static RetoProgramacion generarReto() {
        return parsearRespuesta(llamarAPIGemini());
    }

    public static String generarRetoSinParsear() {
        return llamarAPIGemini();
    }

    // Envia el prompt a Gemini y devuelve la respuesta json
    private static String llamarAPIGemini() {
        StringBuilder response = new StringBuilder();

        try {
            // Sin limite, pero lento
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemma-3-27b-it:generateContent?key=" + apiKey);

            // El mas rapido, a veces no sigue el formato
//            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey);

            // Con limite pero mas rapido que gemma
//            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + apiKey);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Enviar prompt
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(obtenerJsonPrompt());
            writer.flush();
            writer.close();

            // Leer la respuesta
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    // Recibe la respuesta json, la parsea y genera un objeto RetoProgramacion
    private static RetoProgramacion parsearRespuesta(String respuestaJson) {
        Gson gson = new Gson();
        RetoProgramacion reto = new RetoProgramacion();

        try {
            GeminiResponse objetoFull = gson.fromJson(respuestaJson, GeminiResponse.class);

            // Verificamos que haya candidatos antes de acceder
            if (objetoFull != null && objetoFull.candidates != null && !objetoFull.candidates.isEmpty()) {
                String textoBruto = objetoFull.candidates.get(0).content.parts.get(0).text;
                Log.d("DEBUG_IA", "Respuesta real de la IA:\n" + textoBruto);

                // Dividimos el texto en cuatro partes usando las etiquetas
                String[] partes = textoBruto.split("\\[PREGUNTA\\]|\\[CODIGO\\]|\\[OPCIONES\\]|\\[CORRECTA\\]");

                // partes[0] suele estar vacío porque el texto empieza con [PREGUNTA]
                // partes[1] -> Pregunta
                // partes[2] -> Código
                // partes[3] -> Opciones
                // partes[4] -> Correcta

                if (partes.length >= 5) {
                    // Pregunta
                    reto.pregunta = partes[1].trim();

                    // Codigo
                    String codigo = partes[2].replaceAll("```java|```", "").trim();
                    reto.codigo = codigo;

                    // Opciones
                    String bloqueOpciones = partes[3].trim();
                    String[] arrayOpciones = bloqueOpciones.split("(?=\\[[A-D]\\])");
                    reto.opciones = new ArrayList<>();
                    for (String opt : arrayOpciones) {
                        if (!opt.trim().isEmpty()) reto.opciones.add(opt.trim());
                    }

                    // Respuesta
                    reto.respuestaCorrecta = partes[4].trim();
                } else {
                    reto.pregunta = "Error: El formato de la IA no fue el esperado.";
                    Log.e("Parseo", "Texto recibido: " + textoBruto);
                }
            }

        } catch (Exception e) {
            reto.pregunta = "Error crítico en el parseo.";
            e.printStackTrace();
        }

        return reto;
    }

    public static List<Problem> ConsultarProblemas(){
        try {
            URL url = new URL("https://leetcode-api-pied.vercel.app/problems");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Leer la respuesta
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Gson gson = new Gson();
            return Arrays.asList(gson.fromJson(response.toString(), Problem[].class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}