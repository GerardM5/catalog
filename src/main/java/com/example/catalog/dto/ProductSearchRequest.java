package com.example.catalog.dto;

import java.math.BigDecimal;

public class ProductSearchRequest {

    private Long id;
    private String referencia;
    private String nombre;
    private String descripcion;
    private Long peso;
    private Long volumen;
    private String color;
    private Long ancho;
    private Long largo;
    private Long alto;
    private BigDecimal precio;
    private String empresa;
    private String categoria;
    private String origenPdf;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getPeso() {
        return peso;
    }

    public void setPeso(Long peso) {
        this.peso = peso;
    }

    public Long getVolumen() {
        return volumen;
    }

    public void setVolumen(Long volumen) {
        this.volumen = volumen;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getAncho() {
        return ancho;
    }

    public void setAncho(Long ancho) {
        this.ancho = ancho;
    }

    public Long getLargo() {
        return largo;
    }

    public void setLargo(Long largo) {
        this.largo = largo;
    }

    public Long getAlto() {
        return alto;
    }

    public void setAlto(Long alto) {
        this.alto = alto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getOrigenPdf() {
        return origenPdf;
    }

    public void setOrigenPdf(String origenPdf) {
        this.origenPdf = origenPdf;
    }
}
