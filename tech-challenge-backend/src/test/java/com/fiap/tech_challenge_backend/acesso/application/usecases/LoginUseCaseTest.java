package com.fiap.tech_challenge_backend.acesso.application.usecases;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.CredenciaisInvalidasException;
import com.fiap.tech_challenge_backend.acesso.application.ports.TokenGenerator;
import com.fiap.tech_challenge_backend.acesso.application.services.AuthService;
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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase")
class LoginUseCaseTest {

    @Mock
    private AuthService authService;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email(new Email("joao@email.com"))
                .senha("$2a$10$hashedSenha")
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build();
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("deve retornar token quando credenciais são válidas")
        void deveRetornarTokenComCredenciaisValidas() {
            when(authService.authenticate(any(Email.class), eq("senha123")))
                    .thenReturn(Optional.of(usuario));
            when(tokenGenerator.generateToken(eq("joao@email.com"), any(Map.class)))
                    .thenReturn("jwt-token-gerado");

            String token = loginUseCase.execute("joao@email.com", "senha123");

            assertThat(token).isEqualTo("jwt-token-gerado");
            verify(tokenGenerator).generateToken(eq("joao@email.com"), any(Map.class));
        }

        @Test
        @DisplayName("deve incluir role do usuário no token")
        void deveIncluirRoleNoToken() {
            when(authService.authenticate(any(Email.class), anyString()))
                    .thenReturn(Optional.of(usuario));
            when(tokenGenerator.generateToken(anyString(), any(Map.class)))
                    .thenReturn("jwt-token");

            loginUseCase.execute("joao@email.com", "senha123");

            var captor = org.mockito.ArgumentCaptor.forClass(Map.class);
            verify(tokenGenerator).generateToken(anyString(), captor.capture());
            assertThat(captor.getValue()).containsEntry("role", PerfilUsuario.CLIENTE.name());
        }

        @Test
        @DisplayName("deve lançar CredenciaisInvalidasException quando credenciais inválidas")
        void deveLancarExcecaoComCredenciaisInvalidas() {
            when(authService.authenticate(any(Email.class), anyString()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> loginUseCase.execute("joao@email.com", "senhaErrada"))
                    .isInstanceOf(CredenciaisInvalidasException.class);

            verify(tokenGenerator, never()).generateToken(anyString(), any());
        }
    }
}
