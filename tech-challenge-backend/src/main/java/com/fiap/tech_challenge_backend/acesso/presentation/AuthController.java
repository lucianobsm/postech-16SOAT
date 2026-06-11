package com.fiap.tech_challenge_backend.acesso.presentation;

import com.fiap.tech_challenge_backend.acesso.application.AuthService;
import com.fiap.tech_challenge_backend.acesso.application.JwtService;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = authService.authenticate(request.email(), request.password())
                .orElseThrow(InvalidCredentialsException::new);

        String token = jwtService.generateToken(
                usuario.getEmail(),
                Map.of("role", usuario.getPerfil().name())
        );

        return Map.of("accessToken", token);
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidCredentialsException extends RuntimeException {
    }
}

