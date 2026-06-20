package com.fiap.tech_challenge_backend.cadastro.application.ports;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;

import java.util.UUID;

public interface ClienteVeiculoRepository {

    ClienteVeiculo salvar(ClienteVeiculo clienteVeiculo);

    boolean existeVinculoAtivo(UUID clienteId, UUID veiculoId);

    void deletarPorVeiculoId(UUID veiculoId);
}
