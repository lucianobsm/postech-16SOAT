package com.fiap.tech_challenge_backend.atendimento.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Porta de entrada (Use Case) para consultas de Ordem de Serviço.
 * Camada: Ports/In (Hexagonal Architecture)
 */
public interface BuscarOrdemServicoUseCase {

    OrdemServicoResponseDTO buscarPorId(UUID id);

    List<OrdemServicoResponseDTO> listarTodos();
}

