package com.fiap.tech_challenge_backend.acesso.application.usecases;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.CredenciaisInvalidasException;
import com.fiap.tech_challenge_backend.acesso.application.ports.TokenGenerator;
import com.fiap.tech_challenge_backend.acesso.application.services.AuthService;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Use Case responsável pelo fluxo de autenticação e geração de token.
 * Contexto Delimitado: acesso
 * Camada: Application
 */
@Service
public class LoginUseCase {

    private final AuthService authService;
    private final TokenGenerator tokenGenerator;

    public LoginUseCase(AuthService authService, TokenGenerator tokenGenerator) {
        this.authService = authService;
        this.tokenGenerator = tokenGenerator;
    }

    public String execute(String email, String password) {
        Usuario usuario = authService.authenticate(new Email(email), password)
                .orElseThrow(CredenciaisInvalidasException::new);

        return tokenGenerator.generateToken(
                usuario.getEmail().valor(),
                Map.of("role", usuario.getPerfil().name())
        );
    }
}
