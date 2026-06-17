package com.fiap.tech_challenge_backend.estoque.infrastructure;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEstoque, UUID> {

    List<MovimentacaoEstoque> findByPecaInsumoOrderByCriadoEmDesc(PecaInsumo pecaInsumo);
}
