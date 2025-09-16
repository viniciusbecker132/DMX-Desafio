package com.desafio.dmx.backend.Integration;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class PropertiesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PropertiesRepository repository;

    private Properties testProperty;

    @BeforeEach
    void setup() {
        // Limpa a base antes de cada teste
        repository.deleteAll();

        // Inicializa MockMvc com segurança
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Cria propriedade de teste com todos os campos obrigatórios
        testProperty = new Properties();
        testProperty.setName("Teste");
        testProperty.setArea(BigDecimal.valueOf(100.0));
        testProperty.setCity("São Paulo");
        testProperty.setUf("SP");
        testProperty = repository.save(testProperty);
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void testCreateProperty() throws Exception {
        String json = """
                {
                    "name":"Nova Propriedade",
                    "area":150.0,
                    "city":"Rio de Janeiro",
                    "uf":"RJ"
                }
                """;

        mockMvc.perform(post("/propriedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void testGetAllProperties() throws Exception {
        mockMvc.perform(get("/propriedades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void testGetPropertyById() throws Exception {
        mockMvc.perform(get("/propriedades/{id}", testProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void testUpdateProperty() throws Exception {
        String json = """
                {
                    "name":"Atualizada",
                    "area":200.0,
                    "city":"São Paulo",
                    "uf":"SP"
                }
                """;

        mockMvc.perform(put("/propriedades/{id}", testProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void testDeleteProperty() throws Exception {
        mockMvc.perform(delete("/propriedades/{id}", testProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
