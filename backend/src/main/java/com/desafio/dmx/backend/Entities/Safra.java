package com.desafio.dmx.backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "safras")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Safra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idtalhao", nullable = false)
    private Talhao talhao;

    @Column(nullable = false)
    private String cultivation;

    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate planting;

    @Column(nullable = false)
    private LocalDate harvest;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Talhao getTalhao() {
        return talhao;
    }

    public void setTalhao(Talhao talhao) {
        this.talhao = talhao;
    }

    public String getCultivation() {
        return cultivation;
    }

    public void setCultivation(String cultivation) {
        this.cultivation = cultivation;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getPlanting() {
        return planting;
    }

    public void setPlanting(LocalDate planting) {
        this.planting = planting;
    }

    public LocalDate getHarvest() {
        return harvest;
    }

    public void setHarvest(LocalDate harvest) {
        this.harvest = harvest;
    }
}
