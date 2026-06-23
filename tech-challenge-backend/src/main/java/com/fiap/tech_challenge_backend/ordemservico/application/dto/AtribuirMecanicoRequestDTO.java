package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AtribuirMecanicoRequestDTO(
        @NotNull(message = "O mecanico e obrigatorio") UUID mecanicoId
) {
}
