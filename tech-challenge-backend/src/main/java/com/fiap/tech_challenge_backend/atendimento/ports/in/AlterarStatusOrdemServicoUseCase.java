package com.fiap.tech_challenge_backend.atendimento.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.util.UUID;

/**
 * Porta de entrada (Use Case) para alteração de status de Ordem de Serviço.
 * Registra automaticamente o histórico de status.
 * Camada: Ports/In (Hexagonal Architecture)
 */
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

