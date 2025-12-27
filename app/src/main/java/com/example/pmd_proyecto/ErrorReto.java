package com.example.pmd_proyecto.model;

public class ErrorReto {

    public String pregunta;
    public String respuestaCorrecta;
    public long fecha;

    public ErrorReto(String pregunta, String respuestaCorrecta, long fecha) {
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
        this.fecha = fecha;
    }
}