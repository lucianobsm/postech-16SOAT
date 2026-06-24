package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Provedor de tokens JWT.
 * Gera e valida tokens para autenticação.
 * Contexto: acesso
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:uma-chave-local-de-desenvolvimento-com-32-caracteres}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Gera um token JWT para um usuário.
     *
     * @param usuario usuário para o qual gerar o token
     * @return token JWT
     */
    public String gerarToken(Usuario usuario) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(usuario.getEmail().toString())
                .claim("role", usuario.getPerfil().name())
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
