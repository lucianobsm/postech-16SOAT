package com.fiap.tech_challenge_backend.cadastro.application.ports;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepository {

    Veiculo salvar(Veiculo veiculo);

    boolean existePorPlaca(Placa placa);

    Optional<Veiculo> buscarPorPlaca(Placa placa);

    List<Veiculo> listar();

    void deletar(UUID id);

}
