package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.util.List;


public interface RelatorioOrdensServicoUseCase {

    List<RelatorioOrdemServicoResponseDTO> listarRelatorio(String expand);

    List<RelatorioOrdemServicoResponseDTO> listarRelatorioPorStatus(StatusOrdemServico status, String expand);
}

