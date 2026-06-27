package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;


public interface CriarOrdemServicoUseCase {

    OrdemServicoResponseDTO criar(OrdemServicoRequestDTO request);
}


