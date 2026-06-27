package com.fiap.tech_challenge_backend.acesso.presentation;

import com.fiap.tech_challenge_backend.acesso.application.dtos.LoginRequest;
import com.fiap.tech_challenge_backend.acesso.application.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController - Testes Unitários")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private String email;
    private String senha;
    private String token;

    @BeforeEach
    void setUp() {
        email = "mecanico@oficina.com.br";
        senha = "senha123";
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0";
    }

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar token")
    void testLoginSucesso() throws Exception {
        LoginRequest request = new LoginRequest(email, senha);

        when(authService.autenticar(email, senha))
                .thenReturn(Map.of("accessToken", token));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token));

        verify(authService, times(1)).autenticar(email, senha);
    }

    @Test
    @DisplayName("Deve retornar 401 com mensagem de erro quando credenciais inválidas")
    void testLoginComSenhaInvalida() throws Exception {
        LoginRequest request = new LoginRequest(email, senha);

        when(authService.autenticar(email, senha))
                .thenThrow(new IllegalArgumentException("Senha inválida"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Senha inválida"));

        verify(authService, times(1)).autenticar(email, senha);
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuário não encontrado")
    void testLoginUsuarioNaoEncontrado() throws Exception {
        LoginRequest request = new LoginRequest(email, senha);

        when(authService.autenticar(email, senha))
                .thenThrow(new IllegalArgumentException("Usuário não encontrado: " + email));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Usuário não encontrado: " + email));

        verify(authService, times(1)).autenticar(email, senha);
    }
}
