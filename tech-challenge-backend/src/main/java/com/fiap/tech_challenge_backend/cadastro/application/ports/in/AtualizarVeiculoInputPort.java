package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;

/**
 * Porta de entrada para a atualização de dados de um veículo.
 * Contexto Delimitado: cadastro
 */
public interface AtualizarVeiculoInputPort {

    BuscarVeiculoResponse execute(String placa, AtualizarVeiculoRequest request);
}
