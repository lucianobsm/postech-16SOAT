package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class OrdemServicoRepositoryAdapter implements OrdemServicoRepositoryPort {

    private final OrdemServicoRepository repository;

    public OrdemServicoRepositoryAdapter(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        return repository.save(ordemServico);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    @Override
    public List<OrdemServico> listarPriorizadas() {
        return repository.findAllPrioritized();
    }

    @Override
    public List<OrdemServico> listarPorStatus(StatusOrdemServico status) {
        return repository.findAllByStatusPrioritized(status);
    }

    @Override
    public void remover(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<OrdemServico> buscarPorOrcamentoId(UUID orcamentoId) {
        return repository.findByOrcamentoId(orcamentoId);
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }
}

