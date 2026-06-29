package com.fiap.tech_challenge_backend.acompanhamento.application;

import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoOsResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AcompanhamentoService {

    private final OrdemServicoRepository ordemServicoRepository;

    public AcompanhamentoService(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<AcompanhamentoOsResponseDTO> listarPorCliente(UUID clienteId) {
        return ordemServicoRepository.findByClienteIdWithDetails(clienteId).stream()
                .map(AcompanhamentoOsResponseDTO::from)
                .toList();
    }

    public AcompanhamentoOsResponseDTO buscarDetalhe(UUID clienteId, Long osId) {
        OrdemServico ordem = ordemServicoRepository.findById(osId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de servico nao encontrada"));

        if (!ordem.getCliente().getId().equals(clienteId)) {
            throw new EntityNotFoundException("Ordem de servico nao encontrada para o cliente informado");
        }

        return AcompanhamentoOsResponseDTO.from(ordem);
    }
}
