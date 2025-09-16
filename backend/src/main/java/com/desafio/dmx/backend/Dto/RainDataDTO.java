package com.desafio.dmx.backend.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RainDataDTO {
    private LocalDate data;
    private String municipio;
    private String uf;
    private BigDecimal valor;

    public RainDataDTO(LocalDate data, String municipio, String uf, BigDecimal valor) {
        this.data = data;
        this.municipio = municipio;
        this.uf = uf;
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getUf() {
        return uf;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
