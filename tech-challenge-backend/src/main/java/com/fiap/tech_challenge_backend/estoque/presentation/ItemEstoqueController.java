package com.fiap.tech_challenge_backend.estoque.presentation;

import com.fiap.tech_challenge_backend.estoque.application.EstoqueService;
import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estoque/itens")
@RequiredArgsConstructor
@Tag(name = "Estoque - Itens", description = "Gerenciamento de peças e insumos do estoque")
public class ItemEstoqueController {

    private final EstoqueService service;

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

    @PostMapping("/entrada")
    @Operation(summary = "Dar entrada no estoque: cadastra a peça/insumo se não existir ou repõe o estoque se já existir")
    public PecaInsumoResponseDTO darEntrada(@Valid @RequestBody EntradaEstoqueRequestDTO request) {
        return service.darEntrada(request);
    }

    @PatchMapping("/{id}/venda")
    @Operation(summary = "Registrar venda de peça/insumo")
    public void registrarVenda(
            @PathVariable UUID id,
            @RequestParam @Positive Integer quantidade,
            @RequestParam(required = false) String observacao) {
        service.registrarVenda(id, quantidade, observacao);
    }

    @PatchMapping("/{id}/reserva")
    @Operation(summary = "Registrar reserva de peça/insumo para ordem de serviço")
    public void reservar(
            @PathVariable UUID id,
            @RequestParam @Positive Integer quantidade,
            @RequestParam(required = false) String observacao) {
        service.reservar(id, quantidade, observacao);
    }
}
