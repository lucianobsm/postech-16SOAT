package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteVeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ClienteVeiculoRepositoryAdapter implements ClienteVeiculoRepository {

    private final ClienteVeiculoJpaRepository clienteVeiculoJpaRepository;

    public ClienteVeiculoRepositoryAdapter(
            ClienteVeiculoJpaRepository clienteVeiculoJpaRepository
    ) {
        this.clienteVeiculoJpaRepository = clienteVeiculoJpaRepository;
    }

    @Override
    public ClienteVeiculo salvar(ClienteVeiculo clienteVeiculo) {
        return this.clienteVeiculoJpaRepository.save(clienteVeiculo);
    }

    @Override
    public boolean existeVinculoAtivo(UUID clienteId, UUID veiculoId) {
        return this.clienteVeiculoJpaRepository.existsByClienteIdAndVeiculoIdAndAtivoTrue(
                clienteId,
                veiculoId
        );
    }

    @Override
    public void deletarPorVeiculoId(UUID veiculoId) {
        this.clienteVeiculoJpaRepository.deleteByVeiculoId(veiculoId);
    }
}
