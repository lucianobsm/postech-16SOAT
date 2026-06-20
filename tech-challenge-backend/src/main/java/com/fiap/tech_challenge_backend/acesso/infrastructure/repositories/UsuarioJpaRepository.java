package com.fiap.tech_challenge_backend.acesso.infrastructure.repositories;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {

    boolean existsByEmailValor(String email);

    boolean existsByCpfCnpjValor(String cpfCnpj);

    Optional<Usuario> findByEmailValor(String email);

    Optional<Usuario> findByCpfCnpjValor(String cpfCnpj);
}
