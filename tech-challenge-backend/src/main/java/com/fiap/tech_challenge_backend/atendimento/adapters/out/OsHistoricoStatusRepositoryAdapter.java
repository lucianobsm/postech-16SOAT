package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusRepository;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
public class OsHistoricoStatusRepositoryAdapter implements OsHistoricoStatusRepositoryPort {

    private final OsHistoricoStatusRepository repository;

    public OsHistoricoStatusRepositoryAdapter(OsHistoricoStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public OsHistoricoStatus salvar(OsHistoricoStatus historicoStatus) {
        return repository.save(historicoStatus);
    }

    @Override
    public List<OsHistoricoStatus> buscarPorOrdensServicoOrdenado(List<UUID> ordemIds) {
        if (ordemIds == null || ordemIds.isEmpty()) {
            return List.of();
        }
        return repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }
}

