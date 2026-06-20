package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepository {

    private final VeiculoJpaRepository veiculoJpaRepository;

    public VeiculoRepositoryAdapter(VeiculoJpaRepository veiculoJpaRepository) {
        this.veiculoJpaRepository = veiculoJpaRepository;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        return this.veiculoJpaRepository.save(veiculo);
    }

    @Override
    public boolean existePorPlaca(Placa placa) {
        return this.veiculoJpaRepository.existsByPlacaValor(placa.valor());
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(Placa placa) {
        return this.veiculoJpaRepository.findByPlacaValor(placa.valor());
    }

    @Override
    public Optional<Veiculo> buscarPorId(UUID id) {
        return this.veiculoJpaRepository.findById(id);
    }

    @Override
    public List<Veiculo> listar() {
        return this.veiculoJpaRepository.findAll();
    }

    @Override
    public void deletar(UUID id) {
        this.veiculoJpaRepository.deleteById(id);
    }
}
