package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CriarOrdemServicoRequestDTO(
        @NotNull(message = "O cliente e obrigatorio") UUID clienteId,
        @NotNull(message = "O veiculo e obrigatorio") UUID veiculoId,
        UUID mecanicoId
) {
}
