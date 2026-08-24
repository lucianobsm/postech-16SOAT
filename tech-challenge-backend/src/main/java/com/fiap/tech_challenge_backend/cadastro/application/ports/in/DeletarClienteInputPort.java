package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

/**
 * Porta de entrada para a exclusão de um cliente.
 * Contexto Delimitado: cadastro
 */
public interface DeletarClienteInputPort {

    void execute(String cpfCnpj);
}
