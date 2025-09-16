package com.desafio.dmx.backend.Services;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PropertiesService {

    private final PropertiesRepository repository;

    public PropertiesService(PropertiesRepository repository) {
        this.repository = repository;
    }

    public List<Properties> findAll() {
        return repository.findAll();
    }

    public Optional<Properties> findById(Long id) {
        return repository.findById(id);
    }

    public Properties create(Properties property) {
        if(property.getArea() == null || property.getArea().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("A área deve ser maior que zero.");
        }
        return repository.save(property);
    }

    public Properties update(Long id, Properties property) {
        return repository.findById(id).map(p -> {
            p.setName(property.getName());
            p.setCity(property.getCity());
            p.setUf(property.getUf());
            p.setArea(property.getArea());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Propriedade não encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}