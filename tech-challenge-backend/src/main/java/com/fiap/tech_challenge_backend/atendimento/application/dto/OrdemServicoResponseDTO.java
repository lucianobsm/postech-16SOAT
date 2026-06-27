package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record OrdemServicoResponseDTO(
        Long id,
        ClienteInfoDTO cliente,
        VeiculoInfoDTO veiculo,
        UUID mecanicoId,
        String mecanicoNome,
        StatusOrdemServico status,
        BigDecimal valorTotal,
        String queixaCliente,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataFinalizacao,
        Boolean urgente,
        List<OrcamentoDTO> orcamentos
) {
    public static OrdemServicoResponseDTO from(OrdemServico os) {
        // Calcula valorTotal a partir da soma de todos os orçamentos
        BigDecimal valorTotalCalculado = os.getOrcamentos() != null && !os.getOrcamentos().isEmpty()
                ? os.getOrcamentos().stream()
                    .map(orc -> orc.getValorTotal() != null ? orc.getValorTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        return new OrdemServicoResponseDTO(
                os.getId(),
                ClienteInfoDTO.from(os.getCliente()),
                VeiculoInfoDTO.from(os.getVeiculo()),
                os.getMecanico() != null ? os.getMecanico().getId() : null,
                os.getMecanico() != null ? os.getMecanico().getNome() : null,
                os.getStatus(),
                valorTotalCalculado,
                os.getQueixaCliente(),
                os.getObservacoes(),
                os.getDataCriacao(),
                os.getDataInicioExecucao(),
                os.getDataFinalizacao(),
                os.getUrgente(),
                os.getOrcamentos() != null
                        ? os.getOrcamentos().stream()
                        .map(OrcamentoDTO::from)
                        .toList()
                        : List.of()
        );
    }
}

