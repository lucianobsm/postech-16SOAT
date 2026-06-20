package com.fiap.tech_challenge_backend.atendimento.adapters.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.AlterarStatusRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.ports.in.AlterarStatusOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.ports.in.AtualizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.ports.in.BuscarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.ports.in.CriarOrdemServicoUseCase;
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

/**
 * Adapter de entrada: expõe a API REST de Ordens de Serviço.
 * Acesso restrito a perfis ADMIN e FUNCIONARIO via JWT.
 * Camada: Adapters/In (Hexagonal Architecture)
 */
@RestController
@RequestMapping("/atendimento/ordens")
@Tag(name = "Atendimento - Ordens de Serviço", description = "CRUD e gestão do ciclo de vida das Ordens de Serviço")
@SecurityRequirement(name = "bearerAuth")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarUseCase;
    private final BuscarOrdemServicoUseCase buscarUseCase;
    private final AtualizarOrdemServicoUseCase atualizarUseCase;
    private final AlterarStatusOrdemServicoUseCase alterarStatusUseCase;

    public OrdemServicoController(CriarOrdemServicoUseCase criarUseCase,
                                  BuscarOrdemServicoUseCase buscarUseCase,
                                  AtualizarOrdemServicoUseCase atualizarUseCase,
                                  AlterarStatusOrdemServicoUseCase alterarStatusUseCase) {
        this.criarUseCase = criarUseCase;
        this.buscarUseCase = buscarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.alterarStatusUseCase = alterarStatusUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Abrir nova Ordem de Serviço")
    public OrdemServicoResponseDTO criar(@Valid @RequestBody OrdemServicoRequestDTO request) {
        return criarUseCase.criar(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Listar todas as Ordens de Serviço")
    public List<OrdemServicoResponseDTO> listarTodos() {
        return buscarUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Buscar Ordem de Serviço por ID")
    public OrdemServicoResponseDTO buscarPorId(@PathVariable UUID id) {
        return buscarUseCase.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Atualizar mecânico responsável e flag de urgência")
    public OrdemServicoResponseDTO atualizar(@PathVariable UUID id,
                                             @Valid @RequestBody OrdemServicoAtualizarRequestDTO request) {
        return atualizarUseCase.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover Ordem de Serviço (somente ADMIN)")
    public void remover(@PathVariable UUID id) {
        atualizarUseCase.remover(id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Alterar status da Ordem de Serviço (registra histórico automaticamente)")
    public OrdemServicoResponseDTO alterarStatus(@PathVariable UUID id,
                                                 @Valid @RequestBody AlterarStatusRequestDTO request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        return alterarStatusUseCase.alterarStatus(id, request.novoStatus(), jwt.getSubject());
    }
}

