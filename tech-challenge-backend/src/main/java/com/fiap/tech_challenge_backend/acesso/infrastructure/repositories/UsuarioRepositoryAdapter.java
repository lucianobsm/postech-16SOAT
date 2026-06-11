package com.fiap.tech_challenge_backend.acesso.infrastructure.repositories;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return usuarioJpaRepository.save(usuario);
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existePorCpfCnpj(String cpfCnpj) {
        return usuarioJpaRepository.existsByCpfCnpj(cpfCnpj);
    }
}
