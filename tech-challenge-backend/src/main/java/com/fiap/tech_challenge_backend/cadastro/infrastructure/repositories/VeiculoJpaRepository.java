package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoJpaRepository extends JpaRepository<Veiculo, UUID>  {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Veiculo v WHERE v.placa.valor = :valor AND v.deletedAt IS NULL")
    boolean existsByPlacaValor(@Param("valor") String valor);

    @Query("SELECT v FROM Veiculo v WHERE v.placa.valor = :valor AND v.deletedAt IS NULL")
    Optional<Veiculo> findByPlacaValor(@Param("valor") String valor);

    @Query("SELECT v FROM Veiculo v WHERE v.deletedAt IS NULL ORDER BY v.placa.valor ASC")
    List<Veiculo> findAllActive();
}
