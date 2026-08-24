package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarClienteResponse;

/**
 * Porta de entrada para a atualização de dados de um cliente.
 * Contexto Delimitado: cadastro
 */
public interface AtualizarClienteInputPort {

    BuscarClienteResponse execute(String cpfCnpj, AtualizarClienteRequest request);
}
