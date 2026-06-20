package com.fiap.tech_challenge_backend.acesso.infrastructure.repositories;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
    public boolean existePorEmail(Email email) {
        return usuarioJpaRepository.existsByEmailValor(email.valor());
    }

    @Override
    public boolean existePorCpfCnpj(CpfCnpj cpfCnpj) {
        return usuarioJpaRepository.existsByCpfCnpjValor(cpfCnpj.valor());
    }

    @Override
    public Optional<Usuario> procuraPorEmail(Email email) {
        return usuarioJpaRepository.findByEmailValor(email.valor());
    }

    @Override
    public Optional<Usuario> procuraPorCpfCnpj(CpfCnpj cpfCnpj) {
        return usuarioJpaRepository.findByCpfCnpjValor(cpfCnpj.valor());
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioJpaRepository.findById(id);
    }

    @Override
    public void deletar(UUID id) {
        this.usuarioJpaRepository.deleteById(id);
    }
}
