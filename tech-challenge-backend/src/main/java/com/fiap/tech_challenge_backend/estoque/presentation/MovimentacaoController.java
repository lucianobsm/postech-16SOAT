package com.fiap.tech_challenge_backend.estoque.presentation;

import com.fiap.tech_challenge_backend.estoque.application.EstoqueService;
import com.fiap.tech_challenge_backend.estoque.application.dto.MovimentacaoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelo histórico de movimentações do estoque.
 * Contexto Delimitado: estoque
 * Camada: Presentation
 */
@RestController
@RequestMapping("/estoque/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Estoque - Movimentações", description = "Histórico de movimentações de estoque")
public class MovimentacaoController {

    private final EstoqueService service;

    @GetMapping("/item/{pecaInsumoId}")
    @Operation(summary = "Listar movimentações de uma peça ou insumo")
    public List<MovimentacaoResponseDTO> listarPorItem(@PathVariable UUID pecaInsumoId) {
        return service.listarMovimentacoes(pecaInsumoId);
    }
}
