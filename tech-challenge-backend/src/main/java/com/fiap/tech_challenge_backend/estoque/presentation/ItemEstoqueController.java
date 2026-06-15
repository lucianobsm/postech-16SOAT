package com.fiap.tech_challenge_backend.estoque.presentation;

import com.fiap.tech_challenge_backend.estoque.application.EstoqueService;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelo gerenciamento de peças e insumos do estoque.
 * Contexto Delimitado: estoque
 * Camada: Presentation
 */
@RestController
@RequestMapping("/estoque/itens")
@RequiredArgsConstructor
@Tag(name = "Estoque - Itens", description = "Gerenciamento de peças e insumos do estoque")
public class ItemEstoqueController {

    private final EstoqueService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar peça ou insumo")
    public PecaInsumoResponseDTO cadastrar(@Valid @RequestBody PecaInsumoRequestDTO request) {
        return service.cadastrar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar peça ou insumo")
    public PecaInsumoResponseDTO atualizar(@PathVariable UUID id, @Valid @RequestBody PecaInsumoRequestDTO request) {
        return service.atualizar(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID")
    public PecaInsumoResponseDTO buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar todos os itens do estoque")
    public List<PecaInsumoResponseDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/abaixo-do-minimo")
    @Operation(summary = "Listar itens com estoque abaixo do mínimo")
    public List<PecaInsumoResponseDTO> listarAbaixoDoMinimo() {
        return service.listarAbaixoDoMinimo();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover item")
    public void remover(@PathVariable UUID id) {
        service.remover(id);
    }

    @PatchMapping("/{id}/entrada")
    @Operation(summary = "Registrar entrada de estoque")
    public void registrarEntrada(
            @PathVariable UUID id,
            @RequestParam @Positive Integer quantidade,
            @RequestParam(required = false) String observacao) {
        service.registrarEntrada(id, quantidade, observacao);
    }

    @PatchMapping("/{id}/saida")
    @Operation(summary = "Registrar saída de estoque")
    public void registrarSaida(
            @PathVariable UUID id,
            @RequestParam @Positive Integer quantidade,
            @RequestParam(required = false) String observacao) {
        service.registrarSaida(id, quantidade, observacao);
    }
}
