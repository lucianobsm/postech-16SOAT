package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PecaInsumoRepositoryAdapter implements PecaInsumoCatalogoRepositoryPort {

    private final PecaInsumoRepository repository;

    public PecaInsumoRepositoryAdapter(PecaInsumoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PecaInsumo> buscarPorId(UUID id) {
        return repository.findById(id);
    }
}
