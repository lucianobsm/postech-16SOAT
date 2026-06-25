package com.fiap.tech_challenge_backend.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> criarVeiculoRequest() {
        Map<String, String> veiculo = new HashMap<>();
        veiculo.put("placa", "ABC1234");
        veiculo.put("marca", "Toyota");
        veiculo.put("modelo", "Corolla");
        veiculo.put("ano", "2020");
        return veiculo;
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void testCriarVeiculoComSucesso() throws Exception {
        Map<String, String> veiculo = criarVeiculoRequest();
        String requestBody = objectMapper.writeValueAsString(veiculo);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar veículo com placa duplicada")
    void testCriarVeiculoComPlacaDuplicada() throws Exception {
        Map<String, String> veiculo = criarVeiculoRequest();
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve buscar veículo por placa com sucesso")
    void testBuscarVeiculoPorPlaca() throws Exception {
        Map<String, String> veiculo = criarVeiculoRequest();
        String requestBody = objectMapper.writeValueAsString(veiculo);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/veiculos/{placa}", "ABC1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar veículo inexistente")
    void testBuscarVeiculoInexistente() throws Exception {
        mockMvc.perform(get("/veiculos/{placa}", "INVALIDO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar veículos com sucesso")
    void testListarVeiculos() throws Exception {
        Map<String, String> veiculo1 = criarVeiculoRequest();
        String requestBody1 = objectMapper.writeValueAsString(veiculo1);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody1))
                .andExpect(status().isCreated());

        Map<String, String> veiculo2 = new HashMap<>();
        veiculo2.put("placa", "XYZ9876");
        veiculo2.put("marca", "Honda");
        veiculo2.put("modelo", "Civic");
        veiculo2.put("ano", "2021");

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
        Map<String, String> veiculoInvalido = new HashMap<>();
        veiculoInvalido.put("placa", ""); // Placa vazia
        veiculoInvalido.put("marca", "Toyota");
        veiculoInvalido.put("modelo", "Corolla");
        veiculoInvalido.put("ano", "2020");

        String requestBody = objectMapper.writeValueAsString(veiculoInvalido);
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
