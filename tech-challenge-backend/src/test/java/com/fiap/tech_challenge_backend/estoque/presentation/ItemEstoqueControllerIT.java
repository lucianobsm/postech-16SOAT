package com.fiap.tech_challenge_backend.estoque.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
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
@DisplayName("ItemEstoqueController - Integração")
class ItemEstoqueControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void overrideDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PecaInsumoRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBase() {
        try {
            jdbcTemplate.execute("DELETE FROM os_pecas");
            jdbcTemplate.execute("DELETE FROM peca_insumo");
        } catch (Exception e) {
            // Ignorar se não houver dados
        }
    }

    private PecaInsumoRequestDTO requestFiltroOleo() {
        return new PecaInsumoRequestDTO(
                "Filtro de óleo", "Filtro para motor 1.0",
                new BigDecimal("35.00"), new BigDecimal("25.00"),
                "Unidade", 10, 3
        );
    }

    @Test
    @WithMockUser
    @DisplayName("POST /estoque/itens - deve cadastrar e retornar 201")
    void deveCadastrarItem() throws Exception {
        mockMvc.perform(post("/estoque/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFiltroOleo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Filtro de óleo"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(10))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens - deve listar todos os itens")
    void deveListarItens() throws Exception {
        mockMvc.perform(post("/estoque/itens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestFiltroOleo())));

        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Filtro de óleo"));
    }

    @Test
    @WithMockUser
    @DisplayName("Fluxo completo: cadastrar → entrada → saída → verificar abaixo do mínimo")
    void deveExecutarFluxoCompleto() throws Exception {
        // Cadastrar com estoque 5 e mínimo 8
        var requestEstoqueBaixo = new PecaInsumoRequestDTO(
                "Vela de ignição", null,
                new BigDecimal("20.00"), new BigDecimal("12.00"),
                "Jogo c/4", 5, 8
        );

        String body = mockMvc.perform(post("/estoque/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEstoqueBaixo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.abaixoDoMinimo").value(true))
                .andReturn().getResponse().getContentAsString();

        String itemId = objectMapper.readTree(body).get("id").asText();

        // Entrada de 10 → estoque = 15 (acima do mínimo 8)
        mockMvc.perform(patch("/estoque/itens/{id}/entrada", itemId)
                        .param("quantidade", "10")
                        .param("observacao", "Compra mensal"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/itens/{id}", itemId))
                .andExpect(jsonPath("$.quantidadeEstoque").value(15))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(false));

        // Saída de 9 → estoque = 6 (abaixo do mínimo 8)
        mockMvc.perform(patch("/estoque/itens/{id}/saida", itemId)
                        .param("quantidade", "9")
                        .param("observacao", "Uso em OSs"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/itens/{id}", itemId))
                .andExpect(jsonPath("$.quantidadeEstoque").value(6))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(true));

        // Verificar alerta de estoque baixo
        mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados", hasSize(1)));

        // Verificar movimentações (2 registros: 1 entrada + 1 saída)
        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /estoque/itens/{id}/saida - deve retornar 400 quando estoque insuficiente")
    void deveRejeitarSaidaComEstoqueInsuficiente() throws Exception {
        String body = mockMvc.perform(post("/estoque/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFiltroOleo())))
                .andReturn().getResponse().getContentAsString();

        String itemId = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(patch("/estoque/itens/{id}/saida", itemId)
                        .param("quantidade", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /estoque/itens - deve retornar 401 sem autenticação")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isUnauthorized());
    }
}
