package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;

import java.math.BigDecimal;
import java.util.UUID;

public record OsServicoResponseDTO(
        UUID id,
        UUID servicoId,
        String servicoNome,
        BigDecimal precoMaoDeObraAplicado
) {
    public static OsServicoResponseDTO from(OsServico os) {
        return new OsServicoResponseDTO(
                os.getId(),
                os.getServico().getId(),
                os.getServico().getNome(),
                os.getPrecoMaoDeObraAplicado()
        );
    }
}
