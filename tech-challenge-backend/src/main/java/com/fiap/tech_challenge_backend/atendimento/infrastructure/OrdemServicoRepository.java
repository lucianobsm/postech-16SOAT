package com.fiap.tech_challenge_backend.atendimento.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, UUID> {
}

