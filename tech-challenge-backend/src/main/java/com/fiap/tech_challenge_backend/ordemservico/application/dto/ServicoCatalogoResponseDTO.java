package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoCatalogoResponseDTO(
        UUID id,
        String nome,
        String descricao,
        BigDecimal precoMaoDeObra
) {
    public static ServicoCatalogoResponseDTO from(ServicoCatalogo s) {
        return new ServicoCatalogoResponseDTO(s.getId(), s.getNome(), s.getDescricao(), s.getPrecoMaoDeObra());
    }
}
