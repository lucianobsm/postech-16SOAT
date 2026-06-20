package com.fiap.tech_challenge_backend.atendimento.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

import java.util.UUID;

/**
 * Porta de entrada (Use Case) para atualização e remoção de Ordem de Serviço.
 * Camada: Ports/In (Hexagonal Architecture)
 */
public interface AtualizarOrdemServicoUseCase {

    OrdemServicoResponseDTO atualizar(UUID id, OrdemServicoAtualizarRequestDTO request);

    void remover(UUID id);
}

