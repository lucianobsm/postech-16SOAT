package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;
    private String email;
    private String senha;
    private String tokenGerado;

    @BeforeEach
    void setUp() {
        email = "mecanico@oficina.com.br";
        senha = "senha123";
        tokenGerado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

        usuarioMock = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Mecânico")
                .email(new Email(email))
                .senha("hash_da_senha")
                .build();
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso")
    void testAutenticarSucesso() {
        when(usuarioRepository.procuraPorEmail(new Email(email)))
                .thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(senha, usuarioMock.getSenha()))
                .thenReturn(true);
        when(jwtTokenProvider.gerarToken(usuarioMock))
                .thenReturn(tokenGerado);

        Map<String, String> resultado = authService.autenticar(email, senha);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey("accessToken"));
        assertEquals(tokenGerado, resultado.get("accessToken"));
        verify(usuarioRepository, times(1)).procuraPorEmail(new Email(email));
        verify(passwordEncoder, times(1)).matches(senha, usuarioMock.getSenha());
        verify(jwtTokenProvider, times(1)).gerarToken(usuarioMock);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void testAutenticarUsuarioNaoEncontrado() {
        when(usuarioRepository.procuraPorEmail(new Email(email)))
                .thenReturn(Optional.empty());

        IllegalArgumentException excepcao = assertThrows(IllegalArgumentException.class,
            () -> authService.autenticar(email, senha));

        assertEquals("Usuário não encontrado: " + email, excepcao.getMessage());
        verify(usuarioRepository, times(1)).procuraPorEmail(new Email(email));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha inválida")
    void testAutenticarSenhaInvalida() {
        when(usuarioRepository.procuraPorEmail(new Email(email)))
                .thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(senha, usuarioMock.getSenha()))
                .thenReturn(false);

        IllegalArgumentException excepcao = assertThrows(IllegalArgumentException.class,
            () -> authService.autenticar(email, senha));

        assertEquals("Senha inválida", excepcao.getMessage());
        verify(usuarioRepository, times(1)).procuraPorEmail(new Email(email));
        verify(passwordEncoder, times(1)).matches(senha, usuarioMock.getSenha());
        verify(jwtTokenProvider, never()).gerarToken(any());
    }
}
