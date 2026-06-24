package com.fiap.tech_challenge_backend.estoque.presentation;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.infrastructure.MovimentacaoRepository;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("MovimentacaoController - Integração")
class MovimentacaoControllerIT {

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
    @Autowired private PecaInsumoRepository pecaRepository;
    @Autowired private MovimentacaoRepository movimentacaoRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBase() {
        jdbcTemplate.execute("DELETE FROM os_pecas");
        movimentacaoRepository.deleteAll();
        pecaRepository.deleteAll();
    }

    private PecaInsumo salvarPeca() {
        return pecaRepository.save(PecaInsumo.builder()
                .nome("Filtro de óleo")
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadeEstoque(20)
                .quantidadeMinima(5)
                .tipo(TipoPecaInsumo.PECA)
                .build());
    }

    private MovimentacaoEstoque salvarMovimentacao(PecaInsumo peca, TipoMovimentacao tipo, int quantidade, String obs) {
        var mov = MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(tipo)
                .quantidade(quantidade)
                .observacao(obs)
                .build();
        mov.prePersist();
        return movimentacaoRepository.save(mov);
    }

    // ─────────────────────────────────────────────
    // GET /estoque/movimentacoes/item/{pecaInsumoId}
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar lista de movimentações ordenada por data desc")
    void deveListarMovimentacoesOrdenadas() throws Exception {
        var peca = salvarPeca();
        salvarMovimentacao(peca, TipoMovimentacao.ENTRADA, 10, "Compra NF-001");
        salvarMovimentacao(peca, TipoMovimentacao.VENDA, 3, "Venda OS-001");
        salvarMovimentacao(peca, TipoMovimentacao.RESERVA, 2, "Reserva OS-002");

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("RESERVA"))
                .andExpect(jsonPath("$[1].tipoMovimentacao").value("VENDA"))
                .andExpect(jsonPath("$[2].tipoMovimentacao").value("ENTRADA"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar lista vazia quando não há movimentações")
    void deveRetornarListaVaziaQuandoSemMovimentacoes() throws Exception {
        var peca = salvarPeca();

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar 404 quando peça não existe")
    void deveRetornar404QuandoPecaNaoExiste() throws Exception {
        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar campos corretos em cada movimentação")
    void deveRetornarCamposCorretos() throws Exception {
        var peca = salvarPeca();
        salvarMovimentacao(peca, TipoMovimentacao.ENTRADA, 10, "Compra NF-001");

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pecaInsumoId").value(peca.getId().toString()))
                .andExpect(jsonPath("$[0].pecaNome").value("Filtro de óleo"))
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("ENTRADA"))
                .andExpect(jsonPath("$[0].quantidade").value(10))
                .andExpect(jsonPath("$[0].observacao").value("Compra NF-001"))
                .andExpect(jsonPath("$[0].criadoEm").isNotEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar movimentação sem observação")
    void deveRetornarMovimentacaoSemObservacao() throws Exception {
        var peca = salvarPeca();
        salvarMovimentacao(peca, TipoMovimentacao.VENDA, 5, null);

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].observacao").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar apenas movimentações da peça solicitada")
    void deveRetornarApenasMovimentacoesDaPecaSolicitada() throws Exception {
        var peca1 = salvarPeca();
        var peca2 = pecaRepository.save(PecaInsumo.builder()
                .nome("Pastilha de freio")
                .precoVenda(new BigDecimal("80.00"))
                .precoCompra(new BigDecimal("50.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .tipo(TipoPecaInsumo.PECA)
                .build());

        salvarMovimentacao(peca1, TipoMovimentacao.ENTRADA, 10, "Peca 1");
        salvarMovimentacao(peca2, TipoMovimentacao.ENTRADA, 5, "Peca 2");
        salvarMovimentacao(peca2, TipoMovimentacao.VENDA, 2, "Peca 2 venda");

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pecaNome").value("Filtro de óleo"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /item/{id} - deve retornar todos os tipos de movimentação")
    void deveRetornarTodosOsTiposDeMovimentacao() throws Exception {
        var peca = salvarPeca();
        salvarMovimentacao(peca, TipoMovimentacao.ENTRADA, 50, null);
        salvarMovimentacao(peca, TipoMovimentacao.SAIDA,   5,  null);
        salvarMovimentacao(peca, TipoMovimentacao.VENDA,   3,  null);
        salvarMovimentacao(peca, TipoMovimentacao.RESERVA, 2,  null);
        salvarMovimentacao(peca, TipoMovimentacao.AJUSTE,  1,  null);

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].tipoMovimentacao",
                        containsInAnyOrder("ENTRADA", "SAIDA", "VENDA", "RESERVA", "AJUSTE")));
    }

    @Test
    @DisplayName("GET /item/{id} - deve retornar 401 sem autenticação")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}
