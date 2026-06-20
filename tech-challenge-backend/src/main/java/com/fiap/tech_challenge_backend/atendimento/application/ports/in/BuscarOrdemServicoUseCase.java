package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

import java.util.List;
import java.util.UUID;


public interface BuscarOrdemServicoUseCase {

    OrdemServicoResponseDTO buscarPorId(UUID id);

    List<OrdemServicoResponseDTO> listarTodos();
}


