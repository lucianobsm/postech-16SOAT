package com.fiap.tech_challenge_backend.atendimento.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída para persistência de Ordem de Serviço.
 * Isola o domínio de qualquer detalhe de infraestrutura (JPA, etc.).
 * Camada: Ports/Out (Hexagonal Architecture)
 */
public interface OrdemServicoRepositoryPort {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarPorId(UUID id);

    List<OrdemServico> listarTodos();

    void remover(UUID id);

    boolean existePorId(UUID id);
}

