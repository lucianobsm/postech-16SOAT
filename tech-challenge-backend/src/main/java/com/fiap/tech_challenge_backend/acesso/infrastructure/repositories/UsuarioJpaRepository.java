package com.fiap.tech_challenge_backend.acesso.infrastructure.repositories;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {

    boolean existsByEmail(String email);

    boolean existsByCpfCnpj(String cpfCnpj);
}
