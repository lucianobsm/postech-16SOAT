package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaDTO(
        UUID id,
        UUID pecaId,
        String nome,
        Integer quantidade,
        BigDecimal precoVendaAplicado
) {
    public static PecaDTO from(OsPeca osPeca) {
        return new PecaDTO(
                osPeca.getId(),
                osPeca.getPeca().getId(),
                osPeca.getPeca().getNome(),
                osPeca.getQuantidade(),
                osPeca.getPrecoVendaAplicado()
        );
    }
}
