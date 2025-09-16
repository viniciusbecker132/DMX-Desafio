package com.desafio.dmx.backend.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NdviDataDTO {
    private Long talhaoId;
    private LocalDate data;
    private BigDecimal valor;

    public NdviDataDTO(Long talhaoId, LocalDate data, BigDecimal valor) {
        this.talhaoId = talhaoId;
        this.data = data;
        this.valor = valor;
    }

    public Long getTalhaoId() { return talhaoId; }
    public LocalDate getData() { return data; }
    public BigDecimal getValor() { return valor; }
}
