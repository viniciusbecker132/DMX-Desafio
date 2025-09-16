package com.desafio.dmx.backend.Integration;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import com.desafio.dmx.backend.Repositories.TalhaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class TalhaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TalhaoRepository talhaoRepository;

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private Properties propriedade;
    private Talhao talhao;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Limpa DB antes de cada teste
        talhaoRepository.deleteAll();
        propertiesRepository.deleteAll();

        // Cria propriedade base
        propriedade = Properties.builder()
                .name("Propriedade Teste")
                .area(BigDecimal.valueOf(100.0))
                .city("São Paulo")
                .uf("SP")
                .build();
        propriedade = propertiesRepository.save(propriedade);

        // Cria talhão base
        talhao = Talhao.builder()
                .name("Talhão Teste")
                .area(new BigDecimal("50.0"))
                .properties(propriedade)
                .build();
        talhao = talhaoRepository.save(talhao);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateTalhao() throws Exception {
        Talhao novoTalhao = Talhao.builder()
                .name("Talhão Novo")
                .area(new BigDecimal("30.0"))
                .properties(propriedade)
                .build();

        mockMvc.perform(post("/talhao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoTalhao)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetAllTalhoes() throws Exception {
        mockMvc.perform(get("/talhao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetTalhaoById() throws Exception {
        mockMvc.perform(get("/talhao/{id}", talhao.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTalhao() throws Exception {
        talhao.setName("Talhão Atualizado");

        mockMvc.perform(put("/talhao/{id}", talhao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(talhao)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTalhao() throws Exception {
        mockMvc.perform(delete("/talhao/{id}", talhao.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
