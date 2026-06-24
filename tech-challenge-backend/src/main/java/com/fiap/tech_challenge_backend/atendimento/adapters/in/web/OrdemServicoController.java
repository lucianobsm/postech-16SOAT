package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants.AtendimentoApiPaths;
import com.fiap.tech_challenge_backend.atendimento.application.dto.AlterarStatusRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrdemServicoClienteRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AlterarStatusOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AtualizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoClienteUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(AtendimentoApiPaths.OS_BASE)
@Tag(name = "Atendimento - Ordens de Serviço", description = "CRUD e gestão do ciclo de vida das Ordens de Serviço")
@SecurityRequirement(name = "bearerAuth")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarUseCase;
    private final CriarOrdemServicoClienteUseCase criarClienteUseCase;
    private final BuscarOrdemServicoUseCase buscarUseCase;
    private final AtualizarOrdemServicoUseCase atualizarUseCase;
    private final AlterarStatusOrdemServicoUseCase alterarStatusUseCase;

    public OrdemServicoController(CriarOrdemServicoUseCase criarUseCase,
                                  CriarOrdemServicoClienteUseCase criarClienteUseCase,
                                  BuscarOrdemServicoUseCase buscarUseCase,
                                  AtualizarOrdemServicoUseCase atualizarUseCase,
                                  AlterarStatusOrdemServicoUseCase alterarStatusUseCase) {
        this.criarUseCase = criarUseCase;
        this.criarClienteUseCase = criarClienteUseCase;
        this.buscarUseCase = buscarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.alterarStatusUseCase = alterarStatusUseCase;
    }

    @PostMapping(AtendimentoApiPaths.NOVA_OS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Abrir nova Ordem de Serviço")
    public OrdemServicoResponseDTO criar(@Valid @RequestBody OrdemServicoRequestDTO request) {
        return criarUseCase.criar(request);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO', 'CLIENTE')")
    @Operation(summary = "Abrir nova Ordem de Serviço (Cliente) - Identifica cliente por CPF/CNPJ e veículo por placa")
    public OrdemServicoResponseDTO criarPorCliente(@Valid @RequestBody CriarOrdemServicoClienteRequestDTO request) {
        return criarClienteUseCase.criar(request);
    }

    @GetMapping(AtendimentoApiPaths.LISTAR_OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Listar todas as Ordens de Serviço")
    public List<OrdemServicoResponseDTO> listarTodos() {
        return buscarUseCase.listarTodos();
    }

    @GetMapping(AtendimentoApiPaths.BUSCAR_OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Buscar Ordem de Serviço por ID")
    public OrdemServicoResponseDTO buscarPorId(@PathVariable UUID id) {
        return buscarUseCase.buscarPorId(id);
    }

    @PutMapping(AtendimentoApiPaths.ATUALIZAR_OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Atualizar dados completos da Ordem de Serviço (exceto data de abertura)")
    public OrdemServicoResponseDTO atualizar(@PathVariable UUID id,
                                             @Valid @RequestBody OrdemServicoAtualizarRequestDTO request) {
        return atualizarUseCase.atualizar(id, request);
    }

    @DeleteMapping(AtendimentoApiPaths.REMOVER_OS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover Ordem de Serviço (somente ADMIN)")
    public void remover(@PathVariable UUID id) {
        atualizarUseCase.remover(id);
    }

    @PatchMapping(AtendimentoApiPaths.STATUS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Alterar status da Ordem de Serviço (registra histórico automaticamente)")
    public OrdemServicoResponseDTO alterarStatus(@PathVariable UUID id,
                                                 @Valid @RequestBody AlterarStatusRequestDTO request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        return alterarStatusUseCase.alterarStatus(id, request.novoStatus(), request.mecanicoId(), jwt.getSubject());
    }
}


