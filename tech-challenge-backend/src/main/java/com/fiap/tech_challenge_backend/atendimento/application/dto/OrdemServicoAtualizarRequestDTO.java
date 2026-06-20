package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record OrdemServicoAtualizarRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        UUID clienteId,

        @NotNull(message = "O veículo é obrigatório")
        UUID veiculoId,

        UUID mecanicoId,

        @NotNull(message = "O status é obrigatório")
        StatusOrdemServico status,

        @NotNull(message = "O valor total é obrigatório")
        @PositiveOrZero(message = "O valor total não pode ser negativo")
        BigDecimal valorTotal,

        LocalDateTime dataInicioExecucao,

        LocalDateTime dataFinalizacao,

        @NotNull(message = "A flag de urgência é obrigatória")
        Boolean urgente
) {
}

