package com.desafio.dmx.backend.Services;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import com.desafio.dmx.backend.Repositories.TalhaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class TalhaoService {

    private final TalhaoRepository talhaoRepository;
    private final PropertiesRepository propertiesRepository;

    public TalhaoService(TalhaoRepository talhaoRepository, PropertiesRepository propertiesRepository) {
        this.talhaoRepository = talhaoRepository;
        this.propertiesRepository = propertiesRepository;
    }

    public List<Talhao> findAll() {
        return talhaoRepository.findAll();
    }

    public Optional<Talhao> findById(Long id) {
        return talhaoRepository.findById(id);
    }

    public Talhao create(Talhao talhao) {
        if (talhao.getArea() == null || talhao.getArea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A área deve ser maior que zero.");
        }

        Long idPropriedade = talhao.getPropriedade().getId();
        Properties propriedade = propertiesRepository.findById(idPropriedade)
                .orElseThrow(() -> new IllegalArgumentException("Propriedade não encontrada: " + idPropriedade));

        talhao.setPropriedade(propriedade);

        return talhaoRepository.save(talhao);
    }

    public Talhao update(Long id, Talhao talhao) {
        Talhao existing = talhaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Talhão não encontrado: " + id));

        existing.setName(talhao.getName());
        existing.setArea(talhao.getArea());

        Long idPropriedade = talhao.getPropriedade().getId();
        Properties propriedade = propertiesRepository.findById(idPropriedade)
                .orElseThrow(() -> new IllegalArgumentException("Propriedade não encontrada: " + idPropriedade));
        existing.setPropriedade(propriedade);

        return talhaoRepository.save(existing);
    }

    public void delete(Long id) {
        talhaoRepository.deleteById(id);
    }

    public void importCSV(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo CSV está vazio.");
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // CSV: idPropriedade;name;area
                String[] fields = line.split(";");
                Long idPropriedade = Long.parseLong(fields[0]);
                String name = fields[1];
                BigDecimal area = new BigDecimal(fields[2]);

                Properties propriedade = propertiesRepository.findById(idPropriedade)
                        .orElseThrow(() -> new IllegalArgumentException("Propriedade não encontrada: " + idPropriedade));

                Talhao talhao = Talhao.builder()
                        .name(name)
                        .area(area)
                        .properties(propriedade)
                        .build();

                talhaoRepository.save(talhao);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao importar CSV: " + e.getMessage(), e);
        }
    }
}

