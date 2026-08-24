package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoResponse;

/**
 * Porta de entrada para o cadastro de um novo veículo.
 * Contexto Delimitado: cadastro
 */
public interface CadastrarVeiculoInputPort {

    CadastroVeiculoResponse execute(CadastroVeiculoRequest request);
}
