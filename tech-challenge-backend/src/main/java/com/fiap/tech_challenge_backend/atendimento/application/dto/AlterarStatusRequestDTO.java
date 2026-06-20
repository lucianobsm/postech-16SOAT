package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;


public record AlterarStatusRequestDTO(

        @NotNull(message = "O novo status é obrigatório")
        StatusOrdemServico novoStatus
) {
}

