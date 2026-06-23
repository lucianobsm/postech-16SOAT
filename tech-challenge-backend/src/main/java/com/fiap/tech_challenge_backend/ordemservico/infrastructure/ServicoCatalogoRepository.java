package com.fiap.tech_challenge_backend.ordemservico.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicoCatalogoRepository extends JpaRepository<ServicoCatalogo, UUID> {
}
