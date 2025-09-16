package com.desafio.dmx.backend.Dto;

import java.math.BigDecimal;

public class IndicatorDTO {
    private BigDecimal acumuladoChuva;
    private BigDecimal mediaNDVI;

    public IndicatorDTO() {}

    public IndicatorDTO(BigDecimal acumuladoChuva, BigDecimal mediaNDVI) {
        this.acumuladoChuva = acumuladoChuva;
        this.mediaNDVI = mediaNDVI;
    }

    public BigDecimal getAcumuladoChuva() {
        return acumuladoChuva;
    }

    public void setAcumuladoChuva(BigDecimal acumuladoChuva) {
        this.acumuladoChuva = acumuladoChuva;
    }

    public BigDecimal getMediaNDVI() {
        return mediaNDVI;
    }

    public void setMediaNDVI(BigDecimal mediaNDVI) {
        this.mediaNDVI = mediaNDVI;
    }
}
