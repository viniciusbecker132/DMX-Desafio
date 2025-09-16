package com.desafio.dmx.backend.Services;

import com.desafio.dmx.backend.Entities.Safra;
import com.desafio.dmx.backend.Repositories.SafraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafraService {

    private final SafraRepository safraRepository;

    public SafraService(SafraRepository safraRepository) {
        this.safraRepository = safraRepository;
    }

    public List<Safra> getAll() {
        return safraRepository.findAll();
    }

    public Safra create(Safra safra) {
        return safraRepository.save(safra);
    }

    public Safra update(Long id, Safra safraData) {
        Safra safra = safraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Safra não encontrada"));
        safra.setCultivation(safraData.getCultivation());
        safra.setYear(safraData.getYear());
        safra.setPlanting(safraData.getPlanting());
        safra.setHarvest(safraData.getHarvest());
        safra.setTalhao(safraData.getTalhao());
        return safraRepository.save(safra);
    }

    public void delete(Long id) {
        safraRepository.deleteById(id);
    }
}
