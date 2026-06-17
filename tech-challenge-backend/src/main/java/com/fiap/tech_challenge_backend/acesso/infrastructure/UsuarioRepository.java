package com.fiap.tech_challenge_backend.acesso.infrastructure;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
}
