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
 * Testes de Integração para ClienteController
 * Cobre o fluxo completo de CRUD de cliente com banco de dados real
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ClienteControllerIT - Testes de Integração")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> criarClienteRequest() {
        Map<String, String> cliente = new HashMap<>();
        cliente.put("nome", "João Silva");
        cliente.put("cpfCnpj", "12345678901");
        cliente.put("email", "joao@example.com");
        cliente.put("telefone", "11999999999");
        return cliente;
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void testCriarClienteComSucesso() throws Exception {
        Map<String, String> cliente = criarClienteRequest();
        String requestBody = objectMapper.writeValueAsString(cliente);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar cliente com CPF duplicado")
    void testCriarClienteComCpfDuplicado() throws Exception {
        Map<String, String> cliente = criarClienteRequest();
        String requestBody = objectMapper.writeValueAsString(cliente);

        // Criar primeiro cliente
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        // Tentar criar com mesmo CPF
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void testBuscarClientePorCpf() throws Exception {
        Map<String, String> cliente = criarClienteRequest();
        String requestBody = objectMapper.writeValueAsString(cliente);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clientes/{cpfCnpj}", "12345678901")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente")
    void testBuscarClienteInexistente() throws Exception {
        mockMvc.perform(get("/clientes/{cpfCnpj}", "99999999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar clientes com sucesso")
    void testListarClientes() throws Exception {
        Map<String, String> cliente1 = criarClienteRequest();
        String requestBody1 = objectMapper.writeValueAsString(cliente1);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody1))
                .andExpect(status().isCreated());

        Map<String, String> cliente2 = new HashMap<>();
        cliente2.put("nome", "Maria Silva");
        cliente2.put("cpfCnpj", "98765432100");
        cliente2.put("email", "maria@example.com");
        cliente2.put("telefone", "11988888888");

        String requestBody2 = objectMapper.writeValueAsString(cliente2);
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody2))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios na criação")
    void testValidarCamposObrigatorios() throws Exception {
        Map<String, String> clienteInvalido = new HashMap<>();
        clienteInvalido.put("nome", ""); // Nome vazio
        clienteInvalido.put("cpfCnpj", "12345678901");

        String requestBody = objectMapper.writeValueAsString(clienteInvalido);
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
