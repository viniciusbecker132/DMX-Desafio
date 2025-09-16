package com.desafio.dmx.backend.Controllers;

import com.desafio.dmx.backend.Dto.IndicatorDTO;
import com.desafio.dmx.backend.Services.IndicatorService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/indicadores")
@CrossOrigin(origins = "http://localhost:3000")
public class IndicatorController {

    private final IndicatorService service;

    public IndicatorController(IndicatorService service) {
        this.service = service;
    }

    @GetMapping("/{talhaoId}")
    public IndicatorDTO getIndicadores(
            @PathVariable Long talhaoId,
            @RequestParam String dataPlantio,
            @RequestParam String dataColheita) {

        LocalDate plantio = LocalDate.parse(dataPlantio);
        LocalDate colheita = LocalDate.parse(dataColheita);

        return service.calcularIndicadores(talhaoId, plantio, colheita);
    }
}
