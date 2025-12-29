package com.example.pmd_proyecto;

import android.util.Log;

import com.example.pmd_proyecto.model.EnunciadoProblema;
import com.example.pmd_proyecto.model.GeminiRequest;
import com.example.pmd_proyecto.model.GeminiResponse;
import com.example.pmd_proyecto.model.Problem;
import com.example.pmd_proyecto.model.RetoProgramacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

// Esta clase contiene metodos para comunicarse con las APIs (Gemini y Leetcode)
public class NetUtils {
    private static final int DEFAULT_CANTIDAD = 10;
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final int MAX_INTENTOS = 3;

    private static final String[] TEMAS = {
            "Diferencia entre == y .equals()", "Polimorfismo", "Herencia",
            "Interfaces", "Clases abstractas", "Excepciones", "Modificadores de acceso",
            "Uso de la palabra clave static", "Paso por valor vs por referencia",
            "Preguntas teoricas sobre conceptos basicos de Java", "POO", "Hilos",
            "Uso de try-catch-finally", "Tamaños de tipos primitivos",
            "Diferencias entre implementaciones de estructuras de datos",
            "Complejidades de estructuras de datos",
            "Valores por defecto de tipos", "Colecciones", "Arrays",
            "Casting de tipos", "Bucles for/while"
    };
    private static final String[] MODELOS = {
            "gemini-2.5-flash-lite",    // El mas rapido
            "gemini-3-flash-preview",   // Con limite pero mas rapido que gemma
            "gemini-2.5-flash",         // Otra opcion
            // "gemma-3-27b-it"         // ~ Sin limite, pero lento y no permite MimeType Json
    };

    // Devuelve el json a enviar a gemini
    private static String obtenerJsonPrompt() {
        // Concatenando todos los temas en un solo string
        String listaTemas = String.join(", ", TEMAS);

        String prompt = "Actua como un generador de exámenes de certificación Java. " +
                        "Genera un Array JSON con " + DEFAULT_CANTIDAD + " retos de programación.\n" +
                        "TEMAS DISPONIBLES: [" + listaTemas + "].\n\n" +

                        "REGLAS OBLIGATORIAS:\n" +
                        "1. VARIEDAD TOTAL: Elige aleatoriamente de la lista para cada pregunta, y si se repite, no preguntes lo mismo.\n" +
                        "2. Mezcla preguntas teoricas (sin código) y practicas (con código).\n" +
                        "3. FORMATO JSON PURO (Sin Markdown).\n" +
                        "4. Campo 'tema': Una linea que indique el tema del reto.\n" +
                        "5. Campo 'pregunta': Solo el enunciado en lenguaje natural.\n\n" +
                        "6. CAMPO 'codigo': Si es teorica escribe \"NO_CODE\". Si es practica, el codigo va AQUI y NUNCA en 'pregunta'. Usa \\n para saltos de línea.\n" +
                        "7. Campo 'opciones': Solo el texto en lenguaje natural, sin indicaciones de si es A,B,C o D" +
                        "8. Estructura:\n" +
                        "   [{ \"tema\": \"...\", \"pregunta\": \"...\", \"codigo\": \"...\", \"opciones\": [\"A\", \"B\", \"C\", \"D\"], \"respuestaCorrecta\": \"A\" }]";

        // Rellenando los campos del objeto
        GeminiRequest.Part part = new GeminiRequest.Part();
        part.text = prompt;

        GeminiRequest.Content content = new GeminiRequest.Content();
        content.parts.add(part);

        GeminiRequest request = new GeminiRequest();
        request.contents.add(content);
        request.generationConfig.temperature = 1.0;
        request.generationConfig.maxOutputTokens = 8192;
        request.generationConfig.responseMimeType = "application/json";

        // Pasar el objeto a Json
        return new Gson().toJson(request);
    }

    // Envia el prompt a Gemini y devuelve la respuesta json o vacio si error
    private static String llamarAPIGemini() {
        StringBuilder respuesta = new StringBuilder();

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            try {
                // Para elegir un modelo especifico
                //            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemma-3-27b-it:generateContent?key=" + API_KEY);
                //            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + API_KEY);
                //            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + API_KEY);
                //            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY);

                // Elegir aleatoriamente
                String modeloElegido;
                modeloElegido = MODELOS[(int) (Math.random() * MODELOS.length)];
                URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + modeloElegido + ":generateContent?key=" + API_KEY);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Enviar prompt
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(obtenerJsonPrompt());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        respuesta.append(inputLine);
                    }
                    in.close();
                    break;  // si hubo exito no realizamos mas intentos
                } else {
                    Log.e("NetUtils", "Error HTTP: " + responseCode);

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorMsg = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        errorMsg.append(line);
                    }
                    in.close();

                    Log.e("NetUtils", "Respuesta de error de Google: " + errorMsg.toString());
                    Log.e("NetUtils", "Se va a realizar otro intento a otro modelo...");
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("NetUtils", "Respuesta de la IA: " + respuesta.toString());
        return respuesta.toString();
    }

    // Recibe la respuesta json, la parsea y devuelve una lista de RetoProgramacion, o vacio si error
    private static List<RetoProgramacion> parsearRespuestaLote(String respuestaJson) {
        Gson gson = new Gson();
        List<RetoProgramacion> lista = new ArrayList<>();

        try {
            if (respuestaJson.isEmpty()) return lista;

            // Respuesta con metadatos
            GeminiResponse objetoFull = gson.fromJson(respuestaJson, GeminiResponse.class);

            if (objetoFull != null && !objetoFull.candidates.isEmpty()) {
                // Texto literal generado por la IA, es el Json de listado de retos
                String textoBruto = objetoFull.candidates.get(0).content.parts.get(0).text;

                // Limpieza por si la IA ha metido texto estilo "Claro!, aquí tienes..."
                int startIndex = textoBruto.indexOf("[");
                int endIndex = textoBruto.lastIndexOf("]");
                if (startIndex != -1 && endIndex != -1) {
                    textoBruto = textoBruto.substring(startIndex, endIndex + 1);
                }

                // Convertir json a lista de objetos java
                Type listType = new TypeToken<ArrayList<RetoProgramacion>>(){}.getType();
                lista = gson.fromJson(textoBruto, listType);
            }

        } catch (Exception e) {
            Log.e("NetUtils", "Error de parseo: " + e.getMessage());
        }

        return lista;
    }

    // Metodo publico para obtener DEFAULT_CANTIDAD retos en una llamada. Devuelve lista vacia si error
    public static List<RetoProgramacion> generarLoteRetos() {
        return parsearRespuestaLote(llamarAPIGemini());
    }

    // ----------------------------------------------------------------------------------------- //

    public static List<Problem> ConsultarProblemas() {
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

    public static EnunciadoProblema ConsultarEnunciado(String id) {
        try {
            URL url = new URL("https://leetcode-api-pied.vercel.app/problem/"+id);
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
            return gson.fromJson(response.toString(), EnunciadoProblema.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}