package com.fiap.tech_challenge_backend.acompanhamento.presentation;

import com.fiap.tech_challenge_backend.acompanhamento.application.AcompanhamentoService;
import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoOsResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes/{clienteId}/ordens")
@Tag(name = "Acompanhamento", description = "Consulta do status das ordens de servico pelo cliente")
public class AcompanhamentoController {

    private final AcompanhamentoService service;

    public AcompanhamentoController(AcompanhamentoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todas as ordens de servico do cliente")
    public ResponseEntity<List<AcompanhamentoOsResponseDTO>> listar(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @GetMapping("/{osId}")
    @Operation(summary = "Consulta o detalhe de uma ordem de servico do cliente")
    public ResponseEntity<AcompanhamentoOsResponseDTO> detalhe(
            @PathVariable UUID clienteId,
            @PathVariable UUID osId) {
        return ResponseEntity.ok(service.buscarDetalhe(clienteId, osId));
    }
}
