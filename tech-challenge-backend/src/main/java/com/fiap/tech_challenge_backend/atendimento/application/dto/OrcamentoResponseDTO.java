package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrcamentoResponseDTO(
        Long id,
        TipoOrcamento tipo,
        StatusOrcamento status,
        BigDecimal valorTotal,
        LocalDateTime prazoEstipulado,
        LocalDateTime dataCriacao,
        List<ServicoDTO> servicos,
        List<PecaDTO> pecas
) {
    public static OrcamentoResponseDTO from(OsOrcamento orcamento) {
        return new OrcamentoResponseDTO(
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
