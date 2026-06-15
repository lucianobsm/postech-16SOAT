package com.fiap.tech_challenge_backend.acesso.application;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável pela autenticação de usuários.
 * Contexto Delimitado: acesso
 * Camada: Application
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Usuario> authenticate(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getSenha()));
    }
}
