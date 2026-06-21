package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Email email;

    @BeforeEach
    void setUp() {
        email = new Email("joao@email.com");
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email(email)
                .senha("$2a$10$hashedSenha")
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build();
    }

    @Nested
    @DisplayName("authenticate")
    class Authenticate {

        @Test
        @DisplayName("deve retornar usuário quando credenciais são válidas")
        void deveRetornarUsuarioComCredenciaisValidas() {
            when(usuarioRepository.procuraPorEmail(email)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("senha123", usuario.getSenha())).thenReturn(true);

            Optional<Usuario> resultado = authService.authenticate(email, "senha123");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNome()).isEqualTo("João Silva");
            assertThat(resultado.get().getPerfil()).isEqualTo(PerfilUsuario.CLIENTE);
        }

        @Test
        @DisplayName("deve retornar Optional vazio quando e-mail não encontrado")
        void deveRetornarVazioQuandoEmailNaoEncontrado() {
            when(usuarioRepository.procuraPorEmail(email)).thenReturn(Optional.empty());

            Optional<Usuario> resultado = authService.authenticate(email, "senha123");

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar Optional vazio quando senha não confere")
        void deveRetornarVazioQuandoSenhaErrada() {
            when(usuarioRepository.procuraPorEmail(email)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("senhaErrada", usuario.getSenha())).thenReturn(false);

            Optional<Usuario> resultado = authService.authenticate(email, "senhaErrada");

            assertThat(resultado).isEmpty();
        }
    }
}
