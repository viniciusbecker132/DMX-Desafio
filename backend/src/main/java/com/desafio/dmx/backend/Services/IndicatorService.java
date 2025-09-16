package com.desafio.dmx.backend.Services;

import com.desafio.dmx.backend.Dto.IndicatorDTO;
import com.desafio.dmx.backend.Dto.NdviDataDTO;
import com.desafio.dmx.backend.Dto.RainDataDTO;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Repositories.TalhaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class IndicatorService {

    private List<RainDataDTO> chuvaData = new ArrayList<>();
    private List<NdviDataDTO> ndviData = new ArrayList<>();
    @Autowired
    private TalhaoRepository talhaoRepository;


    public void carregarCSV(String chuvaPath, String ndviPath) throws Exception {
        // Chuva
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(chuvaPath))
                )
        ))  {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                LocalDate data = LocalDate.parse(cols[0]);
                String municipio = cols[1];          // pega o município
                String uf = cols[2];                 // pega o estado
                BigDecimal valor = new BigDecimal(cols[3]); // chuva em mm
                chuvaData.add(new RainDataDTO(data, municipio, uf, valor));
            }
        }

        // NDVI
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(ndviPath))
                ))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                LocalDate data = LocalDate.parse(cols[0]);
                Long talhaoId = Long.parseLong(cols[1]);
                BigDecimal valor = new BigDecimal(cols[2]);
                ndviData.add(new NdviDataDTO(talhaoId, data, valor));
            }
        }
    }

    // Calcular indicadores
    public IndicatorDTO calcularIndicadores(Long talhaoId, LocalDate plantio, LocalDate colheita) {

        Talhao talhao = talhaoRepository.findById(talhaoId)
                .orElseThrow(() -> new IllegalArgumentException("Talhão não encontrado"));

        String municipioTalhao  = talhao.getPropriedade().getCity();


        BigDecimal chuvaTotal = chuvaData.stream()
                .filter(c -> c.getMunicipio().equalsIgnoreCase(municipioTalhao))
                .filter(c -> !c.getData().isBefore(plantio) && !c.getData().isAfter(colheita))
                .map(RainDataDTO::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BigDecimal> ndviFiltrado = ndviData.stream()
                .filter(n -> n.getTalhaoId().equals(talhaoId))
                .filter(n -> !n.getData().isBefore(plantio) && !n.getData().isAfter(colheita))
                .map(NdviDataDTO::getValor)
                .toList();

        BigDecimal mediaNDVI = ndviFiltrado.isEmpty() ? BigDecimal.ZERO :
                ndviFiltrado.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(ndviFiltrado.size()), 2, BigDecimal.ROUND_HALF_UP);

        return new IndicatorDTO(chuvaTotal, mediaNDVI);
    }
}
