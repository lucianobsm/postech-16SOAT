package com.fiap.tech_challenge_backend.acompanhamento.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("AcompanhamentoController - Integracao")
class AcompanhamentoControllerIT {

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

    private static final String CLIENTE_ID        = "44444444-4444-4444-8444-444444444441";
    private static final String OS_COM_ITENS_ID   = "88888888-8888-4888-8888-888888888882";
    private static final String OS_RECEBIDA_ID    = "88888888-8888-4888-8888-888888888881";

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve listar OS do cliente seeded")
    void deveListarOsDoCliente() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens", CLIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].veiculoPlaca", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].status", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].descricaoStatus", everyItem(notNullValue())));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve retornar lista vazia para cliente sem OS")
    void deveRetornarListaVaziaParaClienteSemOs() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /clientes/{clienteId}/ordens - deve exigir autenticacao")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens", CLIENTE_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar detalhe com servicos e pecas")
    void deveRetornarDetalheComItens() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", CLIENTE_ID, OS_COM_ITENS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OS_COM_ITENS_ID))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"))
                .andExpect(jsonPath("$.descricaoStatus").value("Aguardando aprovacao do orcamento"))
                .andExpect(jsonPath("$.veiculoPlaca").isNotEmpty())
                .andExpect(jsonPath("$.veiculoModelo").isNotEmpty())
                .andExpect(jsonPath("$.mecanicoNome").isNotEmpty())
                .andExpect(jsonPath("$.servicos", hasSize(1)))
                .andExpect(jsonPath("$.pecas", hasSize(1)))
                .andExpect(jsonPath("$.valorTotal").value(greaterThan(0.0)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar OS com status RECEBIDA sem mecanico")
    void deveRetornarOsRecebidaSemMecanico() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", CLIENTE_ID, OS_RECEBIDA_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEBIDA"))
                .andExpect(jsonPath("$.descricaoStatus").value("Recebida - aguardando diagnostico"))
                .andExpect(jsonPath("$.mecanicoNome").value(nullValue()))
                .andExpect(jsonPath("$.servicos", hasSize(0)))
                .andExpect(jsonPath("$.pecas", hasSize(0)))
                .andExpect(jsonPath("$.dataFinalizacao").value(nullValue()));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar 404 para OS de outro cliente")
    void deveRetornar404ParaOsDeOutroCliente() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", UUID.randomUUID(), OS_COM_ITENS_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar 404 quando OS nao existe")
    void deveRetornar404QuandoOsNaoExiste() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", CLIENTE_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
