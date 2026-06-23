package com.fiap.tech_challenge_backend.ordemservico.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, UUID> {

    List<OrdemServico> findByStatus(StatusOrdemServico status);

    @EntityGraph(attributePaths = {"cliente", "veiculo", "mecanico", "pecas", "pecas.peca", "servicos", "servicos.servico"})
    Optional<OrdemServico> findWithDetailsById(UUID id);

    @Query("SELECT o FROM OrdemServico o WHERE o.cliente.id = :clienteId")
    @EntityGraph(attributePaths = {"cliente", "veiculo", "mecanico", "pecas", "pecas.peca", "servicos", "servicos.servico"})
    List<OrdemServico> findByClienteIdWithDetails(@Param("clienteId") UUID clienteId);

    @Query("SELECT o FROM OrdemServico o WHERE o.id = :id AND o.cliente.id = :clienteId")
    @EntityGraph(attributePaths = {"cliente", "veiculo", "mecanico", "pecas", "pecas.peca", "servicos", "servicos.servico"})
    Optional<OrdemServico> findByIdAndClienteId(@Param("id") UUID id, @Param("clienteId") UUID clienteId);
}
