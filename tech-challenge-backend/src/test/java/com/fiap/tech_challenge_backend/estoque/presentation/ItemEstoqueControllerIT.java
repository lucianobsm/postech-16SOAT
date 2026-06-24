package com.fiap.tech_challenge_backend.estoque.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.infrastructure.MovimentacaoRepository;
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
import java.util.UUID;

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

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PecaInsumoRepository pecaRepository;
    @Autowired private MovimentacaoRepository movimentacaoRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBase() {
        jdbcTemplate.execute("DELETE FROM os_pecas");
        movimentacaoRepository.deleteAll();
        pecaRepository.deleteAll();
    }

    private PecaInsumo salvarPeca(int estoque, int minimo) {
        return salvarPeca("Filtro de óleo", estoque, minimo, TipoPecaInsumo.PECA);
    }

    private PecaInsumo salvarPeca(String nome, int estoque, int minimo, TipoPecaInsumo tipo) {
        return pecaRepository.save(PecaInsumo.builder()
                .nome(nome)
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadeEstoque(estoque)
                .quantidadeMinima(minimo)
                .tipo(tipo)
                .build());
    }

    private PecaInsumoRequestDTO requestAtualizar(String nome, int estoque, int minimo) {
        return new PecaInsumoRequestDTO(nome, null,
                new BigDecimal("35.00"), new BigDecimal("25.00"), null, estoque, minimo, TipoPecaInsumo.PECA);
    }

    private EntradaEstoqueRequestDTO entradaNova(String nome, int quantidade, int minimo) {
        return new EntradaEstoqueRequestDTO(null, nome, "Insumo novo",
                new BigDecimal("35.00"), new BigDecimal("25.00"), "Unidade", quantidade, minimo, "Compra inicial", TipoPecaInsumo.INSUMO);
    }

    private EntradaEstoqueRequestDTO entradaReposicao(UUID id, int quantidade, String observacao) {
        return new EntradaEstoqueRequestDTO(id, null, null, null, null, null, quantidade, null, observacao, null);
    }

    // ─────────────────────────────────────────────
    // GET /estoque/itens
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens - deve listar todos os itens")
    void deveListarTodos() throws Exception {
        salvarPeca(10, 3);

        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Filtro de óleo"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens - deve retornar lista vazia")
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens?tipo=PECA - deve retornar apenas peças")
    void deveListarFiltrandoPorTipoPeca() throws Exception {
        salvarPeca("Filtro de óleo", 10, 3, TipoPecaInsumo.PECA);
        salvarPeca("Óleo 5W30", 20, 5, TipoPecaInsumo.INSUMO);

        mockMvc.perform(get("/estoque/itens").param("tipo", "PECA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("PECA"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens?tipo=INSUMO - deve retornar apenas insumos")
    void deveListarFiltrandoPorTipoInsumo() throws Exception {
        salvarPeca("Filtro de óleo", 10, 3, TipoPecaInsumo.PECA);
        salvarPeca("Óleo 5W30", 20, 5, TipoPecaInsumo.INSUMO);

        mockMvc.perform(get("/estoque/itens").param("tipo", "INSUMO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("INSUMO"));
    }

    // ─────────────────────────────────────────────
    // GET /estoque/itens/{id}
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/{id} - deve retornar item")
    void deveBuscarPorId() throws Exception {
        var peca = salvarPeca(10, 3);

        mockMvc.perform(get("/estoque/itens/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(peca.getId().toString()))
                .andExpect(jsonPath("$.quantidadeEstoque").value(10))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/{id} - deve retornar 404 quando não encontrado")
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/estoque/itens/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // GET /estoque/itens/abaixo-do-minimo
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/abaixo-do-minimo - deve retornar itens com estoque baixo")
    void deveListarAbaixoDoMinimo() throws Exception {
        salvarPeca(2, 10);

        mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].abaixoDoMinimo").value(true));
    }

    // ─────────────────────────────────────────────
    // PUT /estoque/itens/{id}
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("PUT /estoque/itens/{id} - deve atualizar e gerar movimentação de AJUSTE")
    void deveAtualizarEGerarAjuste() throws Exception {
        var peca = salvarPeca(10, 3);

        mockMvc.perform(put("/estoque/itens/{id}", peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizar("Filtro Premium", 20, 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Filtro Premium"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(20));

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("AJUSTE"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /estoque/itens/{id} - não deve gerar movimentação quando estoque não muda")
    void naoDeveGerarAjusteQuandoEstoqueIgual() throws Exception {
        var peca = salvarPeca(10, 3);

        mockMvc.perform(put("/estoque/itens/{id}", peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizar("Filtro Premium", 10, 3))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /estoque/itens/{id} - deve retornar 404 quando não encontrado")
    void deveRetornar404AoAtualizarNaoEncontrado() throws Exception {
        mockMvc.perform(put("/estoque/itens/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizar("X", 5, 1))))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // POST /estoque/itens/entrada
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("POST /entrada - deve cadastrar peça nova e registrar ENTRADA")
    void deveCadastrarPecaNovaViaEntrada() throws Exception {
        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entradaNova("Vela de ignição", 40, 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Vela de ignição"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(40));

        mockMvc.perform(get("/estoque/itens"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /entrada - deve repor estoque de peça existente e registrar ENTRADA")
    void deveReporEstoqueViaEntrada() throws Exception {
        var peca = salvarPeca(10, 3);

        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entradaReposicao(peca.getId(), 5, "Compra NF-001"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque").value(15));

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("ENTRADA"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /entrada - deve retornar 400 quando peça nova sem nome")
    void deveRetornar400EntradaNovaSemNome() throws Exception {
        var semNome = new EntradaEstoqueRequestDTO(null, null, null,
                new BigDecimal("35.00"), new BigDecimal("25.00"), null, 10, 0, null, TipoPecaInsumo.PECA);

        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(semNome)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /entrada - deve retornar 400 quando tipo ausente em peça nova")
    void deveRetornar400EntradaNovaSemTipo() throws Exception {
        var semTipo = new EntradaEstoqueRequestDTO(null, "Vela", null,
                new BigDecimal("35.00"), new BigDecimal("25.00"), null, 10, 0, null, null);

        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(semTipo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /entrada - deve retornar 400 quando quantidade ausente")
    void deveRetornar400EntradaSemQuantidade() throws Exception {
        var semQtd = new EntradaEstoqueRequestDTO(null, "Vela", null,
                new BigDecimal("35.00"), new BigDecimal("25.00"), null, null, 0, null, TipoPecaInsumo.PECA);

        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(semQtd)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // PATCH /estoque/itens/{id}/venda
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("PATCH /venda - deve diminuir estoque e registrar VENDA")
    void deveRegistrarVenda() throws Exception {
        var peca = salvarPeca(10, 3);

        mockMvc.perform(patch("/estoque/itens/{id}/venda", peca.getId())
                        .param("quantidade", "4")
                        .param("observacao", "Venda OS-001"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/itens/{id}", peca.getId()))
                .andExpect(jsonPath("$.quantidadeEstoque").value(6));

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("VENDA"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /venda - deve retornar 400 quando estoque insuficiente")
    void deveRetornar400VendaInsuficiente() throws Exception {
        var peca = salvarPeca(5, 1);

        mockMvc.perform(patch("/estoque/itens/{id}/venda", peca.getId())
                        .param("quantidade", "999"))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // PATCH /estoque/itens/{id}/reserva
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("PATCH /reserva - deve diminuir estoque e registrar RESERVA")
    void deveRegistrarReserva() throws Exception {
        var peca = salvarPeca(10, 3);

        mockMvc.perform(patch("/estoque/itens/{id}/reserva", peca.getId())
                        .param("quantidade", "3")
                        .param("observacao", "Reserva OS-010"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/itens/{id}", peca.getId()))
                .andExpect(jsonPath("$.quantidadeEstoque").value(7));

        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", peca.getId()))
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("RESERVA"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /reserva - deve retornar 400 quando estoque insuficiente")
    void deveRetornar400ReservaInsuficiente() throws Exception {
        var peca = salvarPeca(2, 1);

        mockMvc.perform(patch("/estoque/itens/{id}/reserva", peca.getId())
                        .param("quantidade", "999"))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // Autenticação
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("GET /estoque/itens - deve retornar 401 sem autenticação")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isUnauthorized());
    }

    // ─────────────────────────────────────────────
    // Fluxo completo
    // ─────────────────────────────────────────────
    @Test
    @WithMockUser
    @DisplayName("Fluxo completo: entrada → reserva → venda → abaixo do mínimo → movimentações")
    void deveExecutarFluxoCompleto() throws Exception {
        var peca = salvarPeca(5, 8);
        String id = peca.getId().toString();

        // estoque inicial (5) já está abaixo do mínimo (8)
        mockMvc.perform(get("/estoque/itens/{id}", id))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(true));

        // entrada de 10 → estoque = 15
        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entradaReposicao(peca.getId(), 10, "Reposição"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/itens/{id}", id))
                .andExpect(jsonPath("$.quantidadeEstoque").value(15))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(false));

        // reserva de 5 → estoque = 10
        mockMvc.perform(patch("/estoque/itens/{id}/reserva", id)
                        .param("quantidade", "5").param("observacao", "Reserva OS-001"))
                .andExpect(status().isOk());

        // venda de 4 → estoque = 6 (abaixo do mínimo 8)
        mockMvc.perform(patch("/estoque/itens/{id}/venda", id)
                        .param("quantidade", "4").param("observacao", "Venda OS-001"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estoque/itens/{id}", id))
                .andExpect(jsonPath("$.quantidadeEstoque").value(6))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(true));

        // verificar alerta
        mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                .andExpect(jsonPath("$", hasSize(1)));

        // verificar 3 movimentações: ENTRADA + RESERVA + VENDA
        mockMvc.perform(get("/estoque/movimentacoes/item/{id}", id))
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
