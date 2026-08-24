package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarClienteResponse;

import java.util.List;

/**
 * Porta de entrada para a listagem de todos os clientes.
 * Contexto Delimitado: cadastro
 */
public interface ListarClientesInputPort {

    List<BuscarClienteResponse> execute();
}
