package com.desafio.dmx.backend.Integration;

import com.desafio.dmx.backend.Controllers.SafraController;
import com.desafio.dmx.backend.Entities.Safra;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Services.SafraService;
import com.desafio.dmx.backend.Services.TalhaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SafraControllerIntegrationTest {

    private MockMvc mockMvc;
    private SafraService safraService;
    private TalhaoService talhaoService;

    private Talhao talhao;
    private Safra safra;

    @BeforeEach
    void setUp() {
        // Cria os mocks dos serviços
        safraService = Mockito.mock(SafraService.class);
        talhaoService = Mockito.mock(TalhaoService.class);

        // Cria o controller passando os serviços mockados
        SafraController safraController = new SafraController(safraService, talhaoService);

        // Configura MockMvc standalone com Spring Security habilitado
        mockMvc = MockMvcBuilders.standaloneSetup(safraController)
                .build();

        // Cria dados de teste
        talhao = Talhao.builder()
                .id(1L)
                .name("Talhao Teste")
                .area(java.math.BigDecimal.valueOf(10.5))
                .build();

        safra = Safra.builder()
                .id(1L)
                .cultivation("Milho")
                .year(2025)
                .planting(LocalDate.now())
                .harvest(LocalDate.now().plusMonths(4))
                .talhao(talhao)
                .build();
    }

    @Test
    void testGetAllSafras() throws Exception {
        Mockito.when(safraService.getAll()).thenReturn(List.of(safra));

        mockMvc.perform(get("/safra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cultivation").value("Milho"));
    }

    @Test
    void testGetSafraById() throws Exception {
        Mockito.when(safraService.getAll()).thenReturn(List.of(safra));

        mockMvc.perform(get("/safra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cultivation").value("Milho"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testCreateSafra() throws Exception {
        Mockito.when(talhaoService.findById(1L)).thenReturn(Optional.of(talhao));
        Mockito.when(safraService.create(any(Safra.class))).thenReturn(safra);

        String json = """
                {
                    "cultivation": "Milho",
                    "year": 2025,
                    "planting": "%s",
                    "harvest": "%s",
                    "talhao": {"id": 1}
                }
                """.formatted(LocalDate.now(), LocalDate.now().plusMonths(4));

        mockMvc.perform(post("/safra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cultivation").value("Milho"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testUpdateSafra() throws Exception {
        Safra updated = Safra.builder()
                .id(1L)
                .cultivation("Trigo")
                .year(2026)
                .planting(LocalDate.now())
                .harvest(LocalDate.now().plusMonths(6))
                .talhao(talhao)
                .build();

        Mockito.when(talhaoService.findById(1L)).thenReturn(Optional.of(talhao));
        Mockito.when(safraService.update(eq(1L), any(Safra.class))).thenReturn(updated);

        String json = """
                {
                    "cultivation": "Trigo",
                    "year": 2026,
                    "planting": "%s",
                    "harvest": "%s",
                    "talhao": {"id": 1}
                }
                """.formatted(LocalDate.now(), LocalDate.now().plusMonths(6));

        mockMvc.perform(put("/safra/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cultivation").value("Trigo"))
                .andExpect(jsonPath("$.year").value(2026));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testDeleteSafra() throws Exception {
        Mockito.doNothing().when(safraService).delete(1L);

        mockMvc.perform(delete("/safra/1"))
                .andExpect(status().isNoContent());
    }
}
