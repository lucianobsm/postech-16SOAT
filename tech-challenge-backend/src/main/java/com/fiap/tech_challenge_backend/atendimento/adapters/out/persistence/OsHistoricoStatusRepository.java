package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OsHistoricoStatusRepository extends JpaRepository<OsHistoricoStatus, UUID> {

    List<OsHistoricoStatus> findByOrdemServicoIdOrderByDataMudancaAsc(UUID ordemServicoId);

    @Query("""
            SELECT h
            FROM OsHistoricoStatus h
            LEFT JOIN FETCH h.usuario
            WHERE h.ordemServico.id IN :ordemIds
            ORDER BY h.ordemServico.id ASC, h.dataMudanca ASC
            """)
    List<OsHistoricoStatus> findByOrdemServicoIdsOrderedWithUsuario(@Param("ordemIds") Collection<UUID> ordemIds);
}


