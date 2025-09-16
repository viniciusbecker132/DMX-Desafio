package com.desafio.dmx.backend.Service;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import com.desafio.dmx.backend.Repositories.TalhaoRepository;
import com.desafio.dmx.backend.Services.TalhaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TalhaoServiceTest {

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private PropertiesRepository propertiesRepository;

    @InjectMocks
    private TalhaoService talhaoService;

    private Properties propriedade;
    private Talhao talhao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        propriedade = new Properties();
        propriedade.setId(1L);
        propriedade.setName("Propriedade 1");
        propriedade.setArea(BigDecimal.valueOf(100.0));
        propriedade.setCity("São Paulo");
        propriedade.setUf("SP");

        talhao = new Talhao();
        talhao.setId(1L);
        talhao.setName("Talhão 1");
        talhao.setArea(new BigDecimal("50.0"));
        talhao.setPropriedade(propriedade);
    }

    @Test
    void testFindAll() {
        when(talhaoRepository.findAll()).thenReturn(List.of(talhao));

        List<Talhao> result = talhaoService.findAll();

        assertEquals(1, result.size());
        verify(talhaoRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdFound() {
        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(talhao));

        Optional<Talhao> result = talhaoService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(talhao.getName(), result.get().getName());
        verify(talhaoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(talhaoRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Talhao> result = talhaoService.findById(2L);

        assertFalse(result.isPresent());
        verify(talhaoRepository, times(1)).findById(2L);
    }

    @Test
    void testCreateSuccess() {
        when(propertiesRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        when(talhaoRepository.save(any(Talhao.class))).thenReturn(talhao);

        Talhao result = talhaoService.create(talhao);

        assertNotNull(result);
        assertEquals("Talhão 1", result.getName());
        verify(talhaoRepository, times(1)).save(talhao);
    }

    @Test
    void testCreateAreaZeroOrNegative() {
        talhao.setArea(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> talhaoService.create(talhao));

        assertEquals("A área deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void testCreatePropriedadeNotFound() {
        when(propertiesRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> talhaoService.create(talhao));

        assertTrue(exception.getMessage().contains("Propriedade não encontrada"));
    }

    @Test
    void testUpdateSuccess() {
        Talhao updatedTalhao = new Talhao();
        updatedTalhao.setName("Talhão Atualizado");
        updatedTalhao.setArea(new BigDecimal("60.0"));
        updatedTalhao.setPropriedade(propriedade);

        when(talhaoRepository.findById(1L)).thenReturn(Optional.of(talhao));
        when(propertiesRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        when(talhaoRepository.save(any(Talhao.class))).thenReturn(updatedTalhao);

        Talhao result = talhaoService.update(1L, updatedTalhao);

        assertEquals("Talhão Atualizado", result.getName());
        assertEquals(new BigDecimal("60.0"), result.getArea());
    }

    @Test
    void testUpdateTalhaoNotFound() {
        when(talhaoRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> talhaoService.update(2L, talhao));

        assertTrue(exception.getMessage().contains("Talhão não encontrado"));
    }

    @Test
    void testDelete() {
        doNothing().when(talhaoRepository).deleteById(1L);

        talhaoService.delete(1L);

        verify(talhaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testImportCSV() throws Exception {
        String csvContent = "1;Talhao CSV;40.0\n";
        MockMultipartFile file = new MockMultipartFile("file", "talhoes.csv",
                "text/csv", csvContent.getBytes());

        when(propertiesRepository.findById(1L)).thenReturn(Optional.of(propriedade));
        when(talhaoRepository.save(any(Talhao.class))).thenReturn(talhao);

        talhaoService.importCSV(file);

        verify(talhaoRepository, times(1)).save(any(Talhao.class));
    }

    @Test
    void testImportCSVPropriedadeNaoEncontrada() {
        String csvContent = "2;Talhao CSV;40.0\n";
        MockMultipartFile file = new MockMultipartFile("file", "talhoes.csv",
                "text/csv", csvContent.getBytes());

        when(propertiesRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class,
                () -> talhaoService.importCSV(file));

        assertTrue(exception.getMessage().contains("Propriedade não encontrada"));
    }
}
