package com.fiap.tech_challenge_backend.estoque.infrastructure;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PecaInsumoRepository extends JpaRepository<PecaInsumo, UUID> {

    @Query("SELECT p FROM PecaInsumo p WHERE p.quantidadeEstoque < p.quantidadeMinima")
    List<PecaInsumo> findAbaixoDoMinimo();

    List<PecaInsumo> findByTipo(TipoPecaInsumo tipo);

    @Query("SELECT COUNT(op) FROM OsPeca op WHERE op.peca.id = ?1")
    long countByPecaInUso(UUID pecaId);
}
