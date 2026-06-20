package com.fiap.tech_challenge_backend.atendimento.application;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.infrastructure.UsuarioRepository;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.infrastructure.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.infrastructure.OsHistoricoStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OsHistoricoStatusRepository osHistoricoStatusRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               OsHistoricoStatusRepository osHistoricoStatusRepository,
                               UsuarioRepository usuarioRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public OrdemServico alterarStatus(UUID ordemServicoId,
                                      StatusOrdemServico novoStatus,
                                      UUID usuarioId) {
        OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ordem de serviço não encontrada: " + ordemServicoId));

        StatusOrdemServico statusAtual = ordemServico.getStatus();
        if (statusAtual == novoStatus) {
            return ordemServico;
        }

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Usuário responsável não encontrado: " + usuarioId));
        }

        ordemServico.setStatus(novoStatus);
        OrdemServico ordemAtualizada = ordemServicoRepository.save(ordemServico);

        OsHistoricoStatus historicoStatus = OsHistoricoStatus.builder()
            .ordemServico(ordemAtualizada)
            .statusOrigem(statusAtual)
            .statusDestino(novoStatus)
            .usuario(usuario)
            .build();

        osHistoricoStatusRepository.save(historicoStatus);
        ordemAtualizada.getHistoricoStatus().add(historicoStatus);

        return ordemAtualizada;
    }
}

