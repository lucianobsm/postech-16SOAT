package com.fiap.tech_challenge_backend.ordemservico.presentation;

import com.fiap.tech_challenge_backend.ordemservico.application.AtendimentoService;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atendimento/ordens")
@RequiredArgsConstructor
@Tag(name = "Ordens de Servico", description = "Gerenciamento do ciclo de vida das ordens de servico")
public class OrdemServicoController {

    private final AtendimentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar nova ordem de servico")
    public OrdemServicoResponseDTO criar(@Valid @RequestBody CriarOrdemServicoRequestDTO request) {
        return service.criarOrdemServico(request);
    }

    @GetMapping
    @Operation(summary = "Listar ordens de servico, filtra por status quando informado")
    public List<OrdemServicoResponseDTO> listar(@RequestParam(required = false) StatusOrdemServico status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem de servico por ID")
    public OrdemServicoResponseDTO buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @PatchMapping("/{id}/mecanico")
    @Operation(summary = "Atribuir mecanico a ordem de servico")
    public OrdemServicoResponseDTO atribuirMecanico(
            @PathVariable UUID id,
            @Valid @RequestBody AtribuirMecanicoRequestDTO request) {
        return service.atribuirMecanico(id, request);
    }

    @PatchMapping("/{id}/status/avancar")
    @Operation(summary = "Avancar status da OS: RECEBIDA > EM_DIAGNOSTICO > AGUARDANDO_APROVACAO > EM_EXECUCAO > FINALIZADA > ENTREGUE")
    public OrdemServicoResponseDTO avancarStatus(@PathVariable UUID id) {
        return service.avancarStatus(id);
    }

    @PostMapping("/{id}/pecas")
    @Operation(summary = "Adicionar peca/insumo a OS, realiza reserva no estoque")
    public OrdemServicoResponseDTO adicionarPeca(
            @PathVariable UUID id,
            @Valid @RequestBody AdicionarPecaRequestDTO request) {
        return service.adicionarPeca(id, request);
    }

    @DeleteMapping("/{id}/pecas/{osPecaId}")
    @Operation(summary = "Remover peca/insumo da OS, cancela reserva no estoque")
    public OrdemServicoResponseDTO removerPeca(
            @PathVariable UUID id,
            @PathVariable UUID osPecaId) {
        return service.removerPeca(id, osPecaId);
    }

    @PostMapping("/{id}/servicos")
    @Operation(summary = "Adicionar servico de mao de obra a OS")
    public OrdemServicoResponseDTO adicionarServico(
            @PathVariable UUID id,
            @Valid @RequestBody AdicionarServicoRequestDTO request) {
        return service.adicionarServico(id, request);
    }

    @DeleteMapping("/{id}/servicos/{osServicoId}")
    @Operation(summary = "Remover servico de mao de obra da OS")
    public OrdemServicoResponseDTO removerServico(
            @PathVariable UUID id,
            @PathVariable UUID osServicoId) {
        return service.removerServico(id, osServicoId);
    }
}
