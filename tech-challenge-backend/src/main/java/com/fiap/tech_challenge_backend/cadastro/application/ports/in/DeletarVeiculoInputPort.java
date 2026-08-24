package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

/**
 * Porta de entrada para a exclusão (lógica) de um veículo.
 * Contexto Delimitado: cadastro
 */
public interface DeletarVeiculoInputPort {

    void execute(String placa);
}
