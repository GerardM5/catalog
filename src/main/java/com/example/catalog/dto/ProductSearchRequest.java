package com.example.catalog.dto;

import java.math.BigDecimal;

public class ProductSearchRequest {

    private String search;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String categoria;
    private String empresa;
    private String color;
    private String origenPdf;
    private Long minAncho;
    private Long maxAncho;
    private Long minLargo;
    private Long maxLargo;
    private Long minPeso;
    private Long maxPeso;
    private Long minVolumen;
    private Long maxVolumen;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getOrigenPdf() {
        return origenPdf;
    }

    public void setOrigenPdf(String origenPdf) {
        this.origenPdf = origenPdf;
    }

    public Long getMinAncho() {
        return minAncho;
    }

    public void setMinAncho(Long minAncho) {
        this.minAncho = minAncho;
    }

    public Long getMaxAncho() {
        return maxAncho;
    }

    public void setMaxAncho(Long maxAncho) {
        this.maxAncho = maxAncho;
    }

    public Long getMinLargo() {
        return minLargo;
    }

    public void setMinLargo(Long minLargo) {
        this.minLargo = minLargo;
    }

    public Long getMaxLargo() {
        return maxLargo;
    }

    public void setMaxLargo(Long maxLargo) {
        this.maxLargo = maxLargo;
    }

    public Long getMinPeso() {
        return minPeso;
    }

    public void setMinPeso(Long minPeso) {
        this.minPeso = minPeso;
    }

    public Long getMaxPeso() {
        return maxPeso;
    }

    public void setMaxPeso(Long maxPeso) {
        this.maxPeso = maxPeso;
    }

    public Long getMinVolumen() {
        return minVolumen;
    }

    public void setMinVolumen(Long minVolumen) {
        this.minVolumen = minVolumen;
    }

    public Long getMaxVolumen() {
        return maxVolumen;
    }

    public void setMaxVolumen(Long maxVolumen) {
        this.maxVolumen = maxVolumen;
    }
}
