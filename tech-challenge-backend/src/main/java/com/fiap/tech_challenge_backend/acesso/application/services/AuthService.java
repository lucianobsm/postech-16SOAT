package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Serviço de autenticação e geração de tokens JWT.
 * Contexto: acesso
 * Camada: Application
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UsuarioRepository usuarioRepository,
                     PasswordEncoder passwordEncoder,
                     JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Autentica um usuário e retorna um token JWT.
     *
     * @param email    e-mail do usuário
     * @param password senha do usuário
     * @return Map com o token JWT
     * @throws IllegalArgumentException se as credenciais forem inválidas
     */
    public Map<String, String> autenticar(String email, String password) {
        Usuario usuario = usuarioRepository.procuraPorEmail(new Email(email))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + email));

        if (!passwordEncoder.matches(password, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        String token = jwtTokenProvider.gerarToken(usuario);
        return Map.of("accessToken", token);
    }

    public Optional<Usuario> authenticate(Email email, String password) {
        return usuarioRepository.procuraPorEmail(email)
                .filter(usuario -> passwordEncoder.matches(password, usuario.getSenha()));
    }
}
