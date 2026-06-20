package com.fiap.tech_challenge_backend.atendimento.application.dto;

import java.util.Map;
import java.util.UUID;


public record RelatorioOrdemServicoResponseDTO(
        UUID id,
        String clienteNome,
        String statusAtual,
        Boolean urgente,
        String tempoTotalAtendimento,
        Map<String, String> tempoPorStatus
) {
}

