package com.fiap.tech_challenge_backend.cadastro.application.ports;

public record CriarUsuarioClienteCommand(
        String nome,
        String email,
        String telefone,
        String cpfCnpj
) {
}
