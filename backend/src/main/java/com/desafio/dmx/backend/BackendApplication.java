package com.desafio.dmx.backend;

import com.desafio.dmx.backend.Services.IndicatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    @Bean
    CommandLineRunner loadCsv(IndicatorService indicatorService) {
        return args -> {
            indicatorService.carregarCSV(
                    "Data/chuva.csv",
                    "Data/ndvi.csv"
            );
        };
    }

}
