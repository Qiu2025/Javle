package com.example.pmd_proyecto.model;

import java.util.List;

public class ErrorReto {
    public String tema;
    public String pregunta;
    public String respuestaCorrecta;
    public String respuestaUsuario;
    public String codigo;
    public List<String> opciones;
    public long fecha;

    public ErrorReto(String tema, String pregunta, String respuestaCorrecta, String respuestaUsuario, String codigo, List<String> opciones, long fecha) {
        this.tema = tema;
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
        this.respuestaUsuario = respuestaUsuario;
        this.codigo = codigo;
        this.opciones = opciones;
        this.fecha = fecha;
    }
}