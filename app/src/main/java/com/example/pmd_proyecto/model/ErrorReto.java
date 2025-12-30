package com.example.pmd_proyecto.model;

public class ErrorReto {
    public String tema;
    public String pregunta;
    public String respuestaCorrecta;
    public long fecha;

    public ErrorReto(String tema, String pregunta, String respuestaCorrecta, long fecha) {
        this.tema = tema;
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
        this.fecha = fecha;
    }
}