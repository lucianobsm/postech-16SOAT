package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<Cliente, UUID> {

    boolean existsByCpfCnpj(String cpfCnpj);
}