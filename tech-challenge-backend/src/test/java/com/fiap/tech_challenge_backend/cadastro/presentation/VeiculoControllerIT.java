package com.fiap.tech_challenge_backend.cadastro.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.acesso.infrastructure.repositories.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.ClienteVeiculoJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.VeiculoJpaRepository;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("VeiculoController - Integração")
class VeiculoControllerIT {

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
    private ClienteVeiculoJpaRepository clienteVeiculoJpaRepository;

    @Autowired
    private VeiculoJpaRepository veiculoJpaRepository;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        clienteVeiculoJpaRepository.deleteAll();
        veiculoJpaRepository.deleteAll();
        clienteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();

        Usuario usuario = usuarioJpaRepository.save(Usuario.builder()
                .nome("João Silva")
                .email(new Email("joao@email.com"))
                .senha(passwordEncoder.encode("senha123"))
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build());

        clienteJpaRepository.save(Cliente.builder()
                .usuario(usuario)
                .nome("João Silva")
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build());
    }

    private CadastroVeiculoRequest requestUno() {
        return new CadastroVeiculoRequest("ABC1234", "Fiat", "Uno", 2020, "Branco", "12345678901");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve cadastrar e retornar 201")
    void deveCadastrarVeiculo() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUno())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.modelo").value("Fiat Uno"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve retornar 409 quando placa já cadastrada")
    void deveRetornar409QuandoPlacaJaCadastrada() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUno())));

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUno())))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve retornar 404 quando cliente não existe")
    void deveRetornar404QuandoClienteNaoExiste() throws Exception {
        var request = new CadastroVeiculoRequest("XYZ9999", "Honda", "Civic", 2021, "Preto", "99999999999");

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Fluxo completo: cadastrar → buscar → atualizar modelo → listar → deletar")
    void deveExecutarFluxoCompleto() throws Exception {
        // Cadastrar
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUno())))
                .andExpect(status().isCreated());

        // Buscar por placa
        mockMvc.perform(get("/veiculos/ABC1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.modelo").value("Fiat Uno"));

        // Atualizar modelo
        mockMvc.perform(put("/veiculos/ABC1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"modelo\": \"Fiat Uno 1.4\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Fiat Uno 1.4"));

        // Listar
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].modelo").value("Fiat Uno 1.4"));

        // Deletar
        mockMvc.perform(delete("/veiculos/ABC1234"))
                .andExpect(status().isNoContent());

        // Confirmar remoção
        mockMvc.perform(get("/veiculos"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos/{placa} - deve retornar 404 quando placa não existe")
    void deveRetornar404QuandoPlacaNaoExiste() throws Exception {
        mockMvc.perform(get("/veiculos/ZZZ9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /veiculos - deve retornar 401 sem autenticação")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isUnauthorized());
    }
}
