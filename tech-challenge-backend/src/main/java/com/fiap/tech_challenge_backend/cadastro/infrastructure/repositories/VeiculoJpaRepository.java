package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VeiculoJpaRepository extends JpaRepository<Veiculo, UUID>  {
    boolean existsByPlacaValor(String valor);
    Optional<Veiculo> findByPlacaValor(String valor);
}
