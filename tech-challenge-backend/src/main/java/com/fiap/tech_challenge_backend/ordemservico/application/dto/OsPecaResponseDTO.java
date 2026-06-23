package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;

import java.math.BigDecimal;
import java.util.UUID;

public record OsPecaResponseDTO(
        UUID id,
        UUID pecaId,
        String pecaNome,
        TipoPecaInsumo tipoPeca,
        Integer quantidade,
        BigDecimal precoVendaAplicado,
        BigDecimal subtotal
) {
    public static OsPecaResponseDTO from(OsPeca op) {
        BigDecimal subtotal = op.getPrecoVendaAplicado()
                .multiply(BigDecimal.valueOf(op.getQuantidade()));
        return new OsPecaResponseDTO(
                op.getId(),
                op.getPeca().getId(),
                op.getPeca().getNome(),
                op.getPeca().getTipo(),
                op.getQuantidade(),
                op.getPrecoVendaAplicado(),
                subtotal
        );
    }
}
