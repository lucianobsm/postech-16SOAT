package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<Cliente, UUID> {

    boolean existsByCpfCnpjValor(String cpfCnpj);

    Optional<Cliente> findByCpfCnpjValor(String cpfCnpj);
}