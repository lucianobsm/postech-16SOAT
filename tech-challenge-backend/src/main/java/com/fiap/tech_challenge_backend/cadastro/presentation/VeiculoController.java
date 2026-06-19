package com.fiap.tech_challenge_backend.cadastro.presentation;

import com.fiap.tech_challenge_backend.cadastro.application.usecases.AtualizarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.usecases.BuscarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.usecases.CadastroVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.usecases.DeletarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.usecases.ListarVeiculosUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável por cadastrar e atualizar dados do veículo.
 * Contexto Delimitado: cadastro
 * Camada: Presentation
 */
@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final CadastroVeiculoUseCase cadastroVeiculoUseCase;
    private final ListarVeiculosUseCase listarVeiculosUseCase;
    private final BuscarVeiculoUseCase buscarVeiculoUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final DeletarVeiculoUseCase deletarVeiculoUseCase;

    public VeiculoController(
            CadastroVeiculoUseCase cadastroVeiculoUseCase,
            ListarVeiculosUseCase listarVeiculosUseCase,
            BuscarVeiculoUseCase buscarVeiculoUseCase,
            AtualizarVeiculoUseCase atualizarVeiculoUseCase,
            DeletarVeiculoUseCase deletarVeiculoUseCase
    ) {
        this.cadastroVeiculoUseCase = cadastroVeiculoUseCase;
        this.listarVeiculosUseCase = listarVeiculosUseCase;
        this.buscarVeiculoUseCase = buscarVeiculoUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroVeiculoResponse cadastrar(@Valid @RequestBody CadastroVeiculoRequest request) {
        return cadastroVeiculoUseCase.execute(request);
    }

    @GetMapping
    public List<BuscarVeiculoResponse> listar() {
        return listarVeiculosUseCase.execute();
    }

    @GetMapping("/{placa}")
    public BuscarVeiculoResponse buscar(@PathVariable String placa) {
        return buscarVeiculoUseCase.execute(placa);
    }

    @PutMapping("/{placa}")
    public BuscarVeiculoResponse atualizar(@PathVariable String placa, @Valid @RequestBody AtualizarVeiculoRequest request) {
        return atualizarVeiculoUseCase.execute(placa, request);
    }

    @DeleteMapping("/{placa}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String placa) {
        deletarVeiculoUseCase.execute(placa);
    }
}
