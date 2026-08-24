package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroClienteResponse;

/**
 * Porta de entrada para o cadastro de um novo cliente.
 * Contexto Delimitado: cadastro
 */
public interface CadastrarClienteInputPort {

    CadastroClienteResponse execute(CadastroClienteRequest request);
}
