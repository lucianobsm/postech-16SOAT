package com.fiap.tech_challenge_backend.cadastro.presentation;

import com.fiap.tech_challenge_backend.cadastro.application.ports.in.AtualizarVeiculoInputPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.BuscarVeiculoInputPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.CadastrarVeiculoInputPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.DeletarVeiculoInputPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.ListarVeiculosInputPort;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
@SecurityRequirement(name = "bearerAuth")
public class VeiculoController {

    private final CadastrarVeiculoInputPort cadastroVeiculoUseCase;
    private final ListarVeiculosInputPort listarVeiculosUseCase;
    private final BuscarVeiculoInputPort buscarVeiculoUseCase;
    private final AtualizarVeiculoInputPort atualizarVeiculoUseCase;
    private final DeletarVeiculoInputPort deletarVeiculoUseCase;

    public VeiculoController(
            CadastrarVeiculoInputPort cadastroVeiculoUseCase,
            ListarVeiculosInputPort listarVeiculosUseCase,
            BuscarVeiculoInputPort buscarVeiculoUseCase,
            AtualizarVeiculoInputPort atualizarVeiculoUseCase,
            DeletarVeiculoInputPort deletarVeiculoUseCase) {
        this.cadastroVeiculoUseCase = cadastroVeiculoUseCase;
        this.listarVeiculosUseCase = listarVeiculosUseCase;
        this.buscarVeiculoUseCase = buscarVeiculoUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroVeiculoResponse create(@Valid @RequestBody CadastroVeiculoRequest request) {
        return cadastroVeiculoUseCase.execute(request);
    }

    @GetMapping
    public List<BuscarVeiculoResponse> list() {
        return listarVeiculosUseCase.execute();
    }

    @GetMapping("/{placa}")
    public BuscarVeiculoResponse getByPlaca(@PathVariable String placa) {
        return buscarVeiculoUseCase.execute(placa);
    }

    @PutMapping("/{placa}")
    public BuscarVeiculoResponse update(@PathVariable String placa, @Valid @RequestBody AtualizarVeiculoRequest request) {
        return atualizarVeiculoUseCase.execute(placa, request);
    }

    @DeleteMapping("/{placa}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RelatorioResponseDTO<Void>> delete(@PathVariable String placa) {
        deletarVeiculoUseCase.execute(placa);
        return ResponseEntity.ok(RelatorioResponseDTO.deleteVeiculoSucesso());
    }
}
