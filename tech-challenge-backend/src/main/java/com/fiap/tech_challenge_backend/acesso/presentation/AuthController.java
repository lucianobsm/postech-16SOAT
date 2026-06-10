package com.fiap.tech_challenge_backend.acesso.presentation;

import com.fiap.tech_challenge_backend.acesso.application.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsável pela autenticação de usuários.
 * Contexto Delimitado: acesso
 * Camada: Presentation
 */
@RestController
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        if (!"admin@email.com".equals(request.email()) || !"123456".equals(request.password())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(
                request.email(),
                Map.of("role", "ADMIN")
        );

        return Map.of("accessToken", token);
    }

    public record LoginRequest(String email, String password) {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidCredentialsException extends RuntimeException {
    }
}

