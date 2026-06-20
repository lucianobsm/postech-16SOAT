package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Domain Service responsável pela validação de credenciais de usuários.
 * Contexto Delimitado: acesso
 * Camada: Application
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> authenticate(Email email, String password) {
        return usuarioRepository.procuraPorEmail(email)
                .filter(usuario -> passwordEncoder.matches(password, usuario.getSenha()));
    }
}
