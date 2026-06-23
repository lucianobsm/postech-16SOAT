package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrdemServicoResponseDTO(
        UUID id,
        UUID clienteId,
        String clienteNome,
        UUID veiculoId,
        String veiculoPlaca,
        UUID mecanicoId,
        String mecanicoNome,
        StatusOrdemServico status,
        BigDecimal valorTotal,
        LocalDateTime dataCriacao,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataFinalizacao,
        List<OsPecaResponseDTO> pecas,
        List<OsServicoResponseDTO> servicos
) {
    public static OrdemServicoResponseDTO from(OrdemServico os) {
        return new OrdemServicoResponseDTO(
                os.getId(),
                os.getCliente().getId(),
                os.getCliente().getNome(),
                os.getVeiculo().getId(),
                os.getVeiculo().getPlaca().toString(),
                os.getMecanico() != null ? os.getMecanico().getId() : null,
                os.getMecanico() != null ? os.getMecanico().getNome() : null,
                os.getStatus(),
                os.getValorTotal(),
                os.getDataCriacao(),
                os.getDataInicioExecucao(),
                os.getDataFinalizacao(),
                os.getPecas().stream().map(OsPecaResponseDTO::from).toList(),
                os.getServicos().stream().map(OsServicoResponseDTO::from).toList()
        );
    }
}
