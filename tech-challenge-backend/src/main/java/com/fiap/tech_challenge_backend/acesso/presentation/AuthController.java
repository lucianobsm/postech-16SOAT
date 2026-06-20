package com.fiap.tech_challenge_backend.acesso.presentation;

import com.fiap.tech_challenge_backend.acesso.application.dtos.LoginRequest;
import com.fiap.tech_challenge_backend.acesso.application.usecases.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsável pela autenticação de usuários.
 * Contexto Delimitado: acesso
 * Camada: Presentation
 */
@RestController
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/auth/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUseCase.execute(request.email(), request.password());
        return Map.of("accessToken", token);
    }
}