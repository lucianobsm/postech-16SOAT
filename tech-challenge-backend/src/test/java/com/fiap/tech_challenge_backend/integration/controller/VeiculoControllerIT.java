package com.fiap.tech_challenge_backend.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.ClienteVeiculoJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.VeiculoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para VeiculoController
 * Cobre o fluxo completo de CRUD de veículo com banco de dados real
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("VeiculoControllerIT - Testes de Integração")
class VeiculoControllerIT {

    @DynamicPropertySource
    static void overrideDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VeiculoJpaRepository veiculoJpaRepository;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private ClienteVeiculoJpaRepository clienteVeiculoJpaRepository;

    @BeforeEach
    void setUp() {
        clienteVeiculoJpaRepository.deleteAll();
        veiculoJpaRepository.deleteAll();
        clienteJpaRepository.deleteAll();
    }

    private String criarClientePreRequisito(String cpf) throws Exception {
        Map<String, String> cliente = new HashMap<>();
        cliente.put("nome", "Cliente " + UUID.randomUUID().toString().substring(0, 8));
        cliente.put("cpfCnpj", cpf);
        cliente.put("email", cpf.substring(0, 5) + "@example.com");
        cliente.put("telefone", "11999999999");
        cliente.put("senha", "senha123");
        cliente.put("cep", "01310100");
        cliente.put("rua", "Av. Paulista");
        cliente.put("numero", "1000");
        cliente.put("cidade", "São Paulo");
        cliente.put("estado", "SP");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated());

        return cpf;
    }

    private Map<String, String> criarVeiculoRequest(String placa, String cpf) {
        Map<String, String> veiculo = new HashMap<>();
        veiculo.put("placa", placa);
        veiculo.put("marca", "Toyota");
        veiculo.put("modelo", "Corolla");
        veiculo.put("ano", "2020");
        veiculo.put("cpfCnpj", cpf);
        veiculo.put("cor", "Preto");
        return veiculo;
    }

    private String gerarPlacaDinamica() {
        // Formato brasileiro: 3 letras + 4 números (ex: ABC1234)
        long nanoTime = System.nanoTime();
        int numero = (int) (nanoTime % 10000);
        String numeroStr = String.format("%04d", numero);

        // Gera 3 letras aleatórias
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder placa = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            placa.append(letras.charAt((int) ((nanoTime >> (8 + i * 8)) % 26)));
        }
        placa.append(numeroStr);
        return placa.toString();
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void testCriarVeiculoComSucesso() throws Exception {
        String cpf = "12345678901";
        String placa = gerarPlacaDinamica();

        criarClientePreRequisito(cpf);
        Map<String, String> veiculo = criarVeiculoRequest(placa, cpf);
        String requestBody = objectMapper.writeValueAsString(veiculo);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value(placa))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar veículo com placa duplicada")
    void testCriarVeiculoComPlacaDuplicada() throws Exception {
        String cpf = "98765432101";
        String placa = gerarPlacaDinamica();

        criarClientePreRequisito(cpf);
        Map<String, String> veiculo = criarVeiculoRequest(placa, cpf);
        String requestBody = objectMapper.writeValueAsString(veiculo);

        // Criar primeiro veículo
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        // Tentar criar com mesma placa
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve buscar veículo por placa com sucesso")
    void testBuscarVeiculoPorPlaca() throws Exception {
        String cpf = "11122233344";
        String placa = gerarPlacaDinamica();

        criarClientePreRequisito(cpf);
        Map<String, String> veiculo = criarVeiculoRequest(placa, cpf);
        String requestBody = objectMapper.writeValueAsString(veiculo);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/veiculos/{placa}", placa)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value(placa))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar veículo inexistente")
    void testBuscarVeiculoInexistente() throws Exception {
        String placaNaoExistente = gerarPlacaDinamica();
        mockMvc.perform(get("/veiculos/{placa}", placaNaoExistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar veículos com sucesso")
    void testListarVeiculos() throws Exception {
        String cpf = "55566677788";
        String placa1 = gerarPlacaDinamica();
        String placa2 = gerarPlacaDinamica();

        criarClientePreRequisito(cpf);

        Map<String, String> veiculo1 = criarVeiculoRequest(placa1, cpf);
        String requestBody1 = objectMapper.writeValueAsString(veiculo1);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody1))
                .andExpect(status().isCreated());

        Map<String, String> veiculo2 = criarVeiculoRequest(placa2, cpf);
        veiculo2.put("marca", "Honda");
        veiculo2.put("modelo", "Civic");
        veiculo2.put("ano", "2021");
        veiculo2.put("cor", "Branco");

        String requestBody2 = objectMapper.writeValueAsString(veiculo2);
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody2))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("Deve validar campo placa obrigatório")
    void testValidarPlacaObrigatoria() throws Exception {
        String cpf = "99988877766";
        criarClientePreRequisito(cpf);

        Map<String, String> veiculoInvalido = new HashMap<>();
        veiculoInvalido.put("placa", ""); // Placa vazia
        veiculoInvalido.put("marca", "Toyota");
        veiculoInvalido.put("modelo", "Corolla");
        veiculoInvalido.put("ano", "2020");
        veiculoInvalido.put("cpfCnpj", cpf);
        veiculoInvalido.put("cor", "Preto");

        String requestBody = objectMapper.writeValueAsString(veiculoInvalido);
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
