package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AdicionarPecaRequestDTO(
        @NotNull(message = "A peca/insumo e obrigatoria") UUID pecaId,
        @NotNull(message = "A quantidade e obrigatoria")
        @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade
) {
}
