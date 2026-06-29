package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenProvider")
class JwtTokenProviderTest {

    private static final String SECRET = "uma-chave-local-de-desenvolvimento-com-32-caracteres";
    private static final long EXPIRATION = 86400000L;

    private JwtTokenProvider provider;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(provider, "jwtExpiration", EXPIRATION);

        usuario = Usuario.builder()
                .email(new Email("mecanico@oficina.com"))
                .perfil(PerfilUsuario.FUNCIONARIO)
                .build();
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Nested
    @DisplayName("gerarToken")
    class GerarToken {

        @Test
        @DisplayName("retorna string nao nula e nao vazia")
        void retornaTokenNaoVazio() {
            String token = provider.gerarToken(usuario);

            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("subject do token e o email do usuario")
        void subjectEhEmailDoUsuario() {
            String token = provider.gerarToken(usuario);

            assertThat(parseToken(token).getSubject()).isEqualTo("mecanico@oficina.com");
        }

        @Test
        @DisplayName("claim 'role' e o nome do perfil do usuario")
        void claimRoleEhNomeDoPerfil() {
            String token = provider.gerarToken(usuario);

            assertThat(parseToken(token).get("role", String.class))
                    .isEqualTo(PerfilUsuario.FUNCIONARIO.name());
        }

        @Test
        @DisplayName("claim 'role' para perfil ADMIN")
        void claimRoleAdmin() {
            Usuario admin = Usuario.builder()
                    .email(new Email("admin@oficina.com"))
                    .perfil(PerfilUsuario.ADMIN)
                    .build();

            String token = provider.gerarToken(admin);

            assertThat(parseToken(token).get("role", String.class))
                    .isEqualTo(PerfilUsuario.ADMIN.name());
        }

        @Test
        @DisplayName("claim 'role' para perfil CLIENTE")
        void claimRoleCliente() {
            Usuario cliente = Usuario.builder()
                    .email(new Email("cliente@email.com"))
                    .perfil(PerfilUsuario.CLIENTE)
                    .build();

            String token = provider.gerarToken(cliente);

            assertThat(parseToken(token).get("role", String.class))
                    .isEqualTo(PerfilUsuario.CLIENTE.name());
        }

        @Test
        @DisplayName("expiration esta no futuro")
        void expirationEstaNoFuturo() {
            String token = provider.gerarToken(usuario);

            Date expiration = parseToken(token).getExpiration();

            assertThat(expiration).isAfter(new Date());
        }

        @Test
        @DisplayName("expiration e aproximadamente issuedAt mais jwtExpiration")
        void expirationEhIssuedAtMaisExpiracao() {
            String token = provider.gerarToken(usuario);
            Claims claims = parseToken(token);

            long diffMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

            assertThat(diffMs).isBetween(EXPIRATION - 1000, EXPIRATION + 1000);
        }

        @Test
        @DisplayName("token e verificavel com a chave secreta")
        void tokenVerificavelComChaveSecreta() {
            String token = provider.gerarToken(usuario);

            // parseToken throws if signature is invalid — success means the token is valid
            Claims claims = parseToken(token);

            assertThat(claims).isNotNull();
        }
    }
}
