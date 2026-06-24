package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AdicionarServicoRequestDTO(
        @NotNull(message = "O servico e obrigatorio") UUID servicoId
) {
}
