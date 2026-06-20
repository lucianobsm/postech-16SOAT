package com.fiap.tech_challenge_backend.atendimento.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

/**
 * Porta de entrada (Use Case) para criação de Ordem de Serviço.
 * Camada: Ports/In (Hexagonal Architecture)
 */
public interface CriarOrdemServicoUseCase {

    OrdemServicoResponseDTO criar(OrdemServicoRequestDTO request);
}

