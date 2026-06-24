package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrcamentoDTO(
        UUID id,
        String tipo,
        String status,
        BigDecimal valorTotal,
        LocalDateTime prazoEstipulado,
        LocalDateTime dataCriacao,
        List<ServicoDTO> servicos,
        List<PecaDTO> pecas
) {
    public static OrcamentoDTO from(OsOrcamento orcamento) {
        return new OrcamentoDTO(
                orcamento.getId(),
                orcamento.getTipo(),
                orcamento.getStatus(),
                orcamento.getValorTotal(),
                orcamento.getPrazoEstipulado(),
                orcamento.getDataCriacao(),
                orcamento.getServicos().stream()
                        .map(ServicoDTO::from)
                        .toList(),
                orcamento.getPecas().stream()
                        .map(PecaDTO::from)
                        .toList()
        );
    }
}
