package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaInsumoResponseDTO(
        UUID id,
        String nome,
        String descricao,
        BigDecimal precoVenda,
        BigDecimal precoCompra,
        String quantidadePorUnidade,
        Integer quantidadeEstoque,
        Integer quantidadeMinima,
        boolean abaixoDoMinimo
) {
    public static PecaInsumoResponseDTO from(PecaInsumo p) {
        return new PecaInsumoResponseDTO(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getPrecoVenda(),
                p.getPrecoCompra(),
                p.getQuantidadePorUnidade(),
                p.getQuantidadeEstoque(),
                p.getQuantidadeMinima(),
                p.estoqueAbaixoDoMinimo()
        );
    }
}
