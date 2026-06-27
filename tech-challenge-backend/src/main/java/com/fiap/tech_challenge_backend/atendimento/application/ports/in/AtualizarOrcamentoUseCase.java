package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;

public interface AtualizarOrcamentoUseCase {

    OrcamentoResponseDTO atualizar(Long ordemServicoId, Long orcamentoId, CriarOrcamentoRequestDTO request);
}
