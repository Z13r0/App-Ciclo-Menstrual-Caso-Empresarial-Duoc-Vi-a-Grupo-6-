package com.myciclo.api;

/**
 * Modelo de una guía educativa.
 * Los nombres deben coincidir con lo que espera la app Android.
 */
public class Guia {

    private int id;
    private String titulo;
    private String contenido;
    private String categoria;

    public Guia() {
    }

    public Guia(int id, String titulo, String contenido, String categoria) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}