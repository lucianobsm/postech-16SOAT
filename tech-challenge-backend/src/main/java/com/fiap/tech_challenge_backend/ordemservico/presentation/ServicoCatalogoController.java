package com.fiap.tech_challenge_backend.ordemservico.presentation;

import com.fiap.tech_challenge_backend.ordemservico.application.AtendimentoService;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.ServicoCatalogoRequestDTO;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.ServicoCatalogoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atendimento/servicos")
@RequiredArgsConstructor
@Tag(name = "Catalogo de Servicos", description = "Gerenciamento do catalogo de servicos de mao de obra")
public class ServicoCatalogoController {

    private final AtendimentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo servico no catalogo")
    public ServicoCatalogoResponseDTO criar(@Valid @RequestBody ServicoCatalogoRequestDTO request) {
        return service.criarServico(request);
    }

    @GetMapping
    @Operation(summary = "Listar servicos do catalogo")
    public List<ServicoCatalogoResponseDTO> listar() {
        return service.listarServicos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar servico por ID")
    public ServicoCatalogoResponseDTO buscarPorId(@PathVariable UUID id) {
        return service.buscarServicoPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar servico do catalogo")
    public ServicoCatalogoResponseDTO atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ServicoCatalogoRequestDTO request) {
        return service.atualizarServico(id, request);
    }
}
