package com.desafio.dmx.backend.Service;

import com.desafio.dmx.backend.Entities.Safra;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Repositories.SafraRepository;
import com.desafio.dmx.backend.Services.SafraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SafraServiceTest {

    private SafraRepository safraRepository;
    private SafraService safraService;

    @BeforeEach
    void setUp() {
        safraRepository = Mockito.mock(SafraRepository.class);
        safraService = new SafraService(safraRepository);
    }

    @Test
    void testGetAll() {
        Safra s1 = new Safra(1L, new Talhao(), "Milho", 2025,
                LocalDate.now(), LocalDate.now().plusMonths(4));
        Safra s2 = new Safra(2L, new Talhao(), "Soja", 2025,
                LocalDate.now(), LocalDate.now().plusMonths(5));

        when(safraRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Safra> result = safraService.getAll();
        assertEquals(2, result.size());
        verify(safraRepository, times(1)).findAll();
    }

    @Test
    void testCreate() {
        Safra safra = new Safra(null, new Talhao(), "Milho", 2025,
                LocalDate.now(), LocalDate.now().plusMonths(4));

        when(safraRepository.save(safra)).thenReturn(safra);

        Safra created = safraService.create(safra);
        assertEquals("Milho", created.getCultivation());
        verify(safraRepository, times(1)).save(safra);
    }

    @Test
    void testUpdate() {
        Safra existing = new Safra(1L, new Talhao(), "Soja", 2024,
                LocalDate.now(), LocalDate.now().plusMonths(5));
        Safra updateData = new Safra(null, new Talhao(), "Milho", 2025,
                LocalDate.now(), LocalDate.now().plusMonths(6));

        when(safraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(safraRepository.save(existing)).thenReturn(existing);

        Safra updated = safraService.update(1L, updateData);
        assertEquals("Milho", updated.getCultivation());
        assertEquals(2025, updated.getYear());
    }

    @Test
    void testDelete() {
        safraService.delete(1L);
        verify(safraRepository, times(1)).deleteById(1L);
    }
}

