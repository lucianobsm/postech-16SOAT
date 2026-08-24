package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    boolean existsByCpfCnpj(String cpfCnpj);

    Optional<ClienteJpaEntity> findByCpfCnpj(String cpfCnpj);
}
