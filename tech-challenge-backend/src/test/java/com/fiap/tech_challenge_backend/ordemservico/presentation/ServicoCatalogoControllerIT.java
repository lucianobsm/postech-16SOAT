package com.fiap.tech_challenge_backend.ordemservico.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.ServicoCatalogoRequestDTO;
import com.fiap.tech_challenge_backend.ordemservico.infrastructure.ServicoCatalogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("ServicoCatalogoController - Integracao")
class ServicoCatalogoControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void overrideDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ServicoCatalogoRepository servicoRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private static final String SERVICO_TROCA_OLEO_ID = "77777777-7777-4777-8777-777777777771";

    @BeforeEach
    void limparServicosNaoSeeded() {
        jdbcTemplate.execute(
            "DELETE FROM servico_catalogo WHERE id NOT IN (" +
            "'77777777-7777-4777-8777-777777777771'," +
            "'77777777-7777-4777-8777-777777777772'," +
            "'77777777-7777-4777-8777-777777777773'," +
            "'77777777-7777-4777-8777-777777777774'" +
            ")"
        );
        jdbcTemplate.execute(
            "UPDATE servico_catalogo SET " +
            "nome = 'Troca de Oleo e Filtro', " +
            "descricao = 'Substituicao do oleo do motor, filtro de oleo e reset de indicador de manutencao.', " +
            "preco_mao_de_obra = 120.00 " +
            "WHERE id = '77777777-7777-4777-8777-777777777771'"
        );
    }

    @Test
    @WithMockUser
    @DisplayName("GET / - deve listar todos os servicos do catalogo")
    void deveListarServicos() throws Exception {
        mockMvc.perform(get("/atendimento/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$[*].nome", hasItem("Troca de Oleo e Filtro")));
    }

    @Test
    @DisplayName("GET / - deve exigir autenticacao")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/atendimento/servicos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id} - deve retornar servico seeded por ID")
    void deveRetornarServicoPorId() throws Exception {
        mockMvc.perform(get("/atendimento/servicos/{id}", SERVICO_TROCA_OLEO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SERVICO_TROCA_OLEO_ID))
                .andExpect(jsonPath("$.nome").value("Troca de Oleo e Filtro"))
                .andExpect(jsonPath("$.precoMaoDeObra").value(120.00));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id} - deve retornar 404 quando servico nao existe")
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/atendimento/servicos/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST / - deve cadastrar novo servico e retornar 201")
    void deveCadastrarServico() throws Exception {
        var request = new ServicoCatalogoRequestDTO("Revisao Completa", "Checklist completo", new BigDecimal("350.00"));

        mockMvc.perform(post("/atendimento/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Revisao Completa"))
                .andExpect(jsonPath("$.precoMaoDeObra").value(350.00));
    }

    @Test
    @WithMockUser
    @DisplayName("POST / - deve retornar 400 quando nome esta vazio")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        var request = new ServicoCatalogoRequestDTO("", null, new BigDecimal("100.00"));

        mockMvc.perform(post("/atendimento/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST / - deve retornar 400 quando preco e nulo")
    void deveRetornar400QuandoPrecoNulo() throws Exception {
        var request = new ServicoCatalogoRequestDTO("Servico X", null, null);

        mockMvc.perform(post("/atendimento/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /{id} - deve atualizar servico existente")
    void deveAtualizarServico() throws Exception {
        var request = new ServicoCatalogoRequestDTO("Troca de Oleo Atualizado", "Nova desc", new BigDecimal("130.00"));

        mockMvc.perform(put("/atendimento/servicos/{id}", SERVICO_TROCA_OLEO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Troca de Oleo Atualizado"))
                .andExpect(jsonPath("$.precoMaoDeObra").value(130.00));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /{id} - deve retornar 404 quando servico nao existe")
    void deveRetornar404AoAtualizarNaoExistente() throws Exception {
        var request = new ServicoCatalogoRequestDTO("Qualquer", null, new BigDecimal("100.00"));

        mockMvc.perform(put("/atendimento/servicos/{id}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
