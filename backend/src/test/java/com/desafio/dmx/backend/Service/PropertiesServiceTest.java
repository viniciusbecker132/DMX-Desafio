package com.desafio.dmx.backend.Service;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import com.desafio.dmx.backend.Services.PropertiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertiesServiceTest {

    @Mock
    private PropertiesRepository repository;

    @InjectMocks
    private PropertiesService service;

    private Properties property;

    @BeforeEach
    void setUp() {
        property = Properties.builder()
                .id(1L)
                .name("Fazenda Boa Vista")
                .city("Curitiba")
                .uf("PR")
                .area(new BigDecimal("120.50"))
                .build();
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(property));

        List<Properties> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("Fazenda Boa Vista", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(property));

        Optional<Properties> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Curitiba", result.get().getCity());
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Properties> result = service.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreate_Success() {
        when(repository.save(any(Properties.class))).thenReturn(property);

        Properties created = service.create(property);

        assertNotNull(created);
        assertEquals("Fazenda Boa Vista", created.getName());
        verify(repository, times(1)).save(any(Properties.class));
    }

    @Test
    void testCreate_InvalidArea() {
        property.setArea(BigDecimal.ZERO);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(property)
        );

        assertEquals("A área deve ser maior que zero.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(property));
        when(repository.save(any(Properties.class))).thenReturn(property);

        property.setCity("São Paulo");
        Properties updated = service.update(1L, property);

        assertNotNull(updated);
        assertEquals("São Paulo", updated.getCity());
        verify(repository, times(1)).save(any(Properties.class));
    }

    @Test
    void testUpdate_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.update(99L, property)
        );

        assertEquals("Propriedade não encontrada", ex.getMessage());
    }

    @Test
    void testDelete() {
        doNothing().when(repository).deleteById(anyLong());

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
