package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

	@Query("""
			SELECT os
			FROM OrdemServico os
			JOIN FETCH os.cliente
			LEFT JOIN FETCH os.veiculo
			LEFT JOIN FETCH os.mecanico
			ORDER BY
				os.urgente DESC,
				COALESCE((
					SELECT MIN(
						CASE sc.categoria
							WHEN 'GARANTIA' THEN 1
							WHEN 'DIAGNOSTICO' THEN 2
							WHEN 'CORRETIVA' THEN 3
							WHEN 'PREVENTIVA' THEN 4
							ELSE 5
						END
					)
					FROM OsServico oss
					JOIN oss.orcamento orc
					JOIN oss.servico sc
					WHERE orc.ordemServico = os
				), 5) ASC,
				os.dataCriacao ASC
			""")
	List<OrdemServico> findAllPrioritized();

	@Query("""
			SELECT os
			FROM OrdemServico os
			JOIN FETCH os.cliente
			LEFT JOIN FETCH os.veiculo
			LEFT JOIN FETCH os.mecanico
			WHERE os.status = :status
			ORDER BY os.urgente DESC, os.dataCriacao ASC
			""")
	List<OrdemServico> findAllByStatusPrioritized(@Param("status") StatusOrdemServico status);

	@Query("""
			SELECT os
			FROM OrdemServico os
			JOIN FETCH os.cliente
			LEFT JOIN FETCH os.veiculo
			LEFT JOIN FETCH os.mecanico
			LEFT JOIN FETCH os.orcamentos
			WHERE EXISTS (
				SELECT 1 FROM OsOrcamento orc WHERE orc.ordemServico = os AND orc.id = :orcamentoId
			)
			""")
	Optional<OrdemServico> findByOrcamentoId(@Param("orcamentoId") Long orcamentoId);

	@EntityGraph(attributePaths = {
			"cliente",
			"veiculo",
			"mecanico",
			"orcamentos",
			"orcamentos.servicos",
			"orcamentos.servicos.servico",
			"orcamentos.pecas",
			"orcamentos.pecas.peca"
	})
	@Override
	Optional<OrdemServico> findById(Long id);

	@Query("""
			SELECT MAX(os.id)
			FROM OrdemServico os
			""")
	Long findMaxId();

	@Query("""
			SELECT MAX(orc.id)
			FROM OsOrcamento orc
			""")
	Long findMaxOrcamentoId();
}


