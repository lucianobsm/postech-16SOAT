package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants.AtendimentoApiPaths;
import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.RelatorioOrdensServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AtendimentoApiPaths.RELATORIOS_BASE)
@Tag(name = "Atendimento - Relatórios", description = "Relatórios administrativos de atendimento")
@SecurityRequirement(name = "bearerAuth")
public class RelatorioAtendimentoController {

    private final RelatorioOrdensServicoUseCase relatorioUseCase;

    public RelatorioAtendimentoController(RelatorioOrdensServicoUseCase relatorioUseCase) {
        this.relatorioUseCase = relatorioUseCase;
    }

    @GetMapping(AtendimentoApiPaths.RELATORIO_ORDENS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Listar relatório detalhado de ordens de serviço")
    public List<RelatorioOrdemServicoResponseDTO> listarOrdensServico(
            @RequestParam(required = false) String expand) {
        return relatorioUseCase.listarRelatorio(expand);
    }

    @GetMapping(AtendimentoApiPaths.RELATORIO_ORDENS_POR_STATUS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Listar relatório de ordens de serviço por status")
    public List<RelatorioOrdemServicoResponseDTO> listarOrdensServicoPorStatus(
            @RequestParam StatusOrdemServico status,
            @RequestParam(required = false) String expand) {
        return relatorioUseCase.listarRelatorioPorStatus(status, expand);
    }
}

