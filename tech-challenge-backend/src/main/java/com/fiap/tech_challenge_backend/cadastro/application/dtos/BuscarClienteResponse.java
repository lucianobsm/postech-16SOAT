package com.fiap.tech_challenge_backend.cadastro.application.dtos;

import java.util.UUID;

public record BuscarClienteResponse(
        UUID uuid,
        String nome,
        String cpfCnpj,
        String telefone,
        String cep,
        String rua,
        String numero,
        String complemento,
        String cidade,
        String estado
) {
}
