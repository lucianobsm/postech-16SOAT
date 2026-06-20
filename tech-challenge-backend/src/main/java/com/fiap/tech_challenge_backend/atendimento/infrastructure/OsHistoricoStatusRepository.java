package com.fiap.tech_challenge_backend.atendimento.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OsHistoricoStatusRepository extends JpaRepository<OsHistoricoStatus, UUID> {

    List<OsHistoricoStatus> findByOrdemServicoIdOrderByDataMudancaAsc(UUID ordemServicoId);
}

