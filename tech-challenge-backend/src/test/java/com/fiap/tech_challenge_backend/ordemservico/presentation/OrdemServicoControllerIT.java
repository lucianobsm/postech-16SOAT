package com.fiap.tech_challenge_backend.ordemservico.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.AdicionarPecaRequestDTO;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.AdicionarServicoRequestDTO;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.AtribuirMecanicoRequestDTO;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.CriarOrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.ordemservico.infrastructure.OrdemServicoRepository;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("OrdemServicoController - Integracao")
class OrdemServicoControllerIT {

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
    @Autowired private OrdemServicoRepository osRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private static final String CLIENTE_ID  = "44444444-4444-4444-8444-444444444441";
    private static final String VEICULO_ID  = "55555555-5555-4555-8555-555555555551";
    private static final String MECANICO_ID = "22222222-2222-4222-8222-222222222221";
    private static final String PECA_ID     = "66666666-6666-4666-8666-666666666661";
    private static final String SERVICO_ID  = "77777777-7777-4777-8777-777777777771";

    private static final String OS_RECEBIDA_ID = "88888888-8888-4888-8888-888888888881";
    private static final String OS_ENTREGUE_ID = "88888888-8888-4888-8888-888888888884";

    @BeforeEach
    void resetarEstado() {
        // os_pecas e os_servicos: preserva apenas os itens das OS que foram seeded com itens (882, 883)
        jdbcTemplate.execute("DELETE FROM os_pecas WHERE ordem_servico_id NOT IN (" +
            "'88888888-8888-4888-8888-888888888882'," +
            "'88888888-8888-4888-8888-888888888883'" +
            ")");
        jdbcTemplate.execute("DELETE FROM os_servicos WHERE ordem_servico_id NOT IN (" +
            "'88888888-8888-4888-8888-888888888882'," +
            "'88888888-8888-4888-8888-888888888883'" +
            ")");
        // remove OS criadas durante testes
        jdbcTemplate.execute("DELETE FROM ordens_servico WHERE id NOT IN (" +
            "'88888888-8888-4888-8888-888888888881'," +
            "'88888888-8888-4888-8888-888888888882'," +
            "'88888888-8888-4888-8888-888888888883'," +
            "'88888888-8888-4888-8888-888888888884'" +
            ")");
        // restaura estado inicial da OS 881: RECEBIDA, sem mecanico
        jdbcTemplate.execute("UPDATE ordens_servico SET status = 'RECEBIDA', mecanico_id = NULL, valor_total = 0 " +
            "WHERE id = '88888888-8888-4888-8888-888888888881'");
        // restaura estoque da peca usada nos testes
        jdbcTemplate.execute("UPDATE peca_insumo SET quantidade_estoque = 40 " +
            "WHERE id = '66666666-6666-4666-8666-666666666661'");
    }

    @Test
    @WithMockUser
    @DisplayName("POST / - deve criar OS com status RECEBIDA")
    void deveCriarOs() throws Exception {
        var request = new CriarOrdemServicoRequestDTO(
                UUID.fromString(CLIENTE_ID), UUID.fromString(VEICULO_ID), null);

        mockMvc.perform(post("/atendimento/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("RECEBIDA"))
                .andExpect(jsonPath("$.valorTotal").value(0.00))
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.mecanicoId").value(nullValue()));
    }

    @Test
    @WithMockUser
    @DisplayName("POST / - deve criar OS com mecanico quando informado")
    void deveCriarOsComMecanico() throws Exception {
        var request = new CriarOrdemServicoRequestDTO(
                UUID.fromString(CLIENTE_ID), UUID.fromString(VEICULO_ID), UUID.fromString(MECANICO_ID));

        mockMvc.perform(post("/atendimento/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mecanicoId").value(MECANICO_ID));
    }

    @Test
    @WithMockUser
    @DisplayName("POST / - deve retornar 404 quando cliente nao existe")
    void deveRetornar404ClienteInexistente() throws Exception {
        var request = new CriarOrdemServicoRequestDTO(
                UUID.randomUUID(), UUID.fromString(VEICULO_ID), null);

        mockMvc.perform(post("/atendimento/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST / - deve exigir autenticacao")
    void deveExigirAutenticacao() throws Exception {
        var request = new CriarOrdemServicoRequestDTO(
                UUID.fromString(CLIENTE_ID), UUID.fromString(VEICULO_ID), null);

        mockMvc.perform(post("/atendimento/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET / - deve listar todas as OS")
    void deveListarTodasAsOs() throws Exception {
        mockMvc.perform(get("/atendimento/ordens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /?status=RECEBIDA - deve filtrar por status")
    void deveFiltrarOsPorStatus() throws Exception {
        mockMvc.perform(get("/atendimento/ordens").param("status", "RECEBIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(is("RECEBIDA"))));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /?status=ENTREGUE - deve retornar OS entregues")
    void deveFiltrarOsEntregues() throws Exception {
        mockMvc.perform(get("/atendimento/ordens").param("status", "ENTREGUE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(OS_ENTREGUE_ID)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id} - deve retornar OS seeded com pecas e servicos")
    void deveRetornarOsPorId() throws Exception {
        mockMvc.perform(get("/atendimento/ordens/{id}", "88888888-8888-4888-8888-888888888882"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("88888888-8888-4888-8888-888888888882"))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"))
                .andExpect(jsonPath("$.mecanicoId").value(MECANICO_ID))
                .andExpect(jsonPath("$.pecas", hasSize(1)))
                .andExpect(jsonPath("$.servicos", hasSize(1)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id} - deve retornar 404 quando OS nao existe")
    void deveRetornar404QuandoNaoEncontrada() throws Exception {
        mockMvc.perform(get("/atendimento/ordens/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /{id}/mecanico - deve atribuir mecanico a OS sem mecanico")
    void deveAtribuirMecanico() throws Exception {
        var request = new AtribuirMecanicoRequestDTO(UUID.fromString(MECANICO_ID));

        mockMvc.perform(patch("/atendimento/ordens/{id}/mecanico", OS_RECEBIDA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mecanicoId").value(MECANICO_ID));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /{id}/status/avancar - deve avancar de RECEBIDA para EM_DIAGNOSTICO")
    void deveAvancarStatusDeRecebida() throws Exception {
        mockMvc.perform(patch("/atendimento/ordens/{id}/status/avancar", OS_RECEBIDA_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /{id}/status/avancar - deve retornar 400 ao avancar OS ENTREGUE")
    void deveRetornar400AoAvancarEntregue() throws Exception {
        mockMvc.perform(patch("/atendimento/ordens/{id}/status/avancar", OS_ENTREGUE_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /{id}/pecas - deve adicionar peca a OS e reduzir estoque")
    void deveAdicionarPecaNaOs() throws Exception {
        var request = new AdicionarPecaRequestDTO(UUID.fromString(PECA_ID), 2);

        mockMvc.perform(post("/atendimento/ordens/{id}/pecas", OS_RECEBIDA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pecas", hasSize(1)))
                .andExpect(jsonPath("$.pecas[0].pecaId").value(PECA_ID))
                .andExpect(jsonPath("$.pecas[0].quantidade").value(2))
                .andExpect(jsonPath("$.valorTotal").value(greaterThan(0.0)));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /{id}/pecas - deve retornar 400 ao adicionar peca em OS ENTREGUE")
    void deveRetornar400AoAdicionarPecaEmOsEntregue() throws Exception {
        var request = new AdicionarPecaRequestDTO(UUID.fromString(PECA_ID), 1);

        mockMvc.perform(post("/atendimento/ordens/{id}/pecas", OS_ENTREGUE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /{id}/pecas/{osPecaId} - deve remover peca da OS e restaurar estoque")
    void deveRemoverPecaDaOs() throws Exception {
        var addRequest = new AdicionarPecaRequestDTO(UUID.fromString(PECA_ID), 3);

        var addResult = mockMvc.perform(post("/atendimento/ordens/{id}/pecas", OS_RECEBIDA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var body     = objectMapper.readTree(addResult.getResponse().getContentAsString());
        var osPecaId = body.at("/pecas/0/id").asText();

        mockMvc.perform(delete("/atendimento/ordens/{id}/pecas/{osPecaId}", OS_RECEBIDA_ID, osPecaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pecas").isEmpty())
                .andExpect(jsonPath("$.valorTotal").value(0.0));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /{id}/servicos - deve adicionar servico a OS")
    void deveAdicionarServico() throws Exception {
        var request = new AdicionarServicoRequestDTO(UUID.fromString(SERVICO_ID));

        mockMvc.perform(post("/atendimento/ordens/{id}/servicos", OS_RECEBIDA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicos", hasSize(1)))
                .andExpect(jsonPath("$.servicos[0].servicoId").value(SERVICO_ID))
                .andExpect(jsonPath("$.valorTotal").value(120.00));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /{id}/servicos/{osServicoId} - deve remover servico da OS")
    void deveRemoverServicoDaOs() throws Exception {
        var addRequest = new AdicionarServicoRequestDTO(UUID.fromString(SERVICO_ID));

        var addResult = mockMvc.perform(post("/atendimento/ordens/{id}/servicos", OS_RECEBIDA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var body        = objectMapper.readTree(addResult.getResponse().getContentAsString());
        var osServicoId = body.at("/servicos/0/id").asText();

        mockMvc.perform(delete("/atendimento/ordens/{id}/servicos/{osServicoId}", OS_RECEBIDA_ID, osServicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicos").isEmpty())
                .andExpect(jsonPath("$.valorTotal").value(0.0));
    }
}
