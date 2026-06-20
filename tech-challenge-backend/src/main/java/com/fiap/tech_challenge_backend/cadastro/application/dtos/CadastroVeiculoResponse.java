package com.fiap.tech_challenge_backend.cadastro.application.dtos;

import java.util.UUID;

public record CadastroVeiculoResponse(
        UUID id,
        String placa,
        String modelo,
        String cpfCnpj
) {
}
