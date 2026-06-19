package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteVeiculoJpaRepository extends JpaRepository<ClienteVeiculo, ClienteVeiculoId> {

    boolean existsByClienteIdAndVeiculoIdAndAtivoTrue(UUID clienteId, UUID veiculoId);

    void deleteByVeiculoId(UUID veiculoId);
}
