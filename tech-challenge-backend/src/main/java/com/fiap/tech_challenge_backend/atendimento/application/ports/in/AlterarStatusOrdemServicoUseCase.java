package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.util.UUID;


public interface AlterarStatusOrdemServicoUseCase {

    /**
     * Altera o status de uma OS, registrando o evento no histórico.
     *
     * @param ordemServicoId ID da OS a ser atualizada
     * @param novoStatus     novo status desejado
     * @param usuarioEmail   e-mail do usuário autenticado (extraído do JWT)
     * @return OS atualizada como DTO
     */
    OrdemServicoResponseDTO alterarStatus(UUID ordemServicoId,
                                          StatusOrdemServico novoStatus,
                                          String usuarioEmail);
}


