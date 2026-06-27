package com.fiap.tech_challenge_backend.cadastro.presentation;

/*
 * NOTA: Este controller está temporariamente comentado porque as use cases
 * do contexto de cadastro ainda não foram implementadas no merge.
 *
 * Use cases que precisam ser criadas:
 * - CadastroVeiculoUseCase
 * - ListarVeiculosUseCase
 * - BuscarVeiculoUseCase
 * - AtualizarVeiculoUseCase
 * - DeletarVeiculoUseCase
 *
 * DTOs necessários:
 * - AtualizarVeiculoRequest
 * - CadastroVeiculoRequest
 * - CadastroVeiculoResponse
 * - BuscarVeiculoResponse
 *
 * Contexto Delimitado: cadastro
 * Camada: Presentation
 */

/*
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
            DeletarVeiculoUseCase deletarVeiculoUseCase) {
        this.cadastroVeiculoUseCase = cadastroVeiculoUseCase;
        this.listarVeiculosUseCase = listarVeiculosUseCase;
        this.buscarVeiculoUseCase = buscarVeiculoUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroVeiculoResponse create(@Valid @RequestBody CadastroVeiculoRequest request) {
        return cadastroVeiculoUseCase.create(request);
    }

    @GetMapping
    public List<BuscarVeiculoResponse> list() {
        return listarVeiculosUseCase.list();
    }

    @GetMapping("/{id}")
    public BuscarVeiculoResponse getById(@PathVariable String id) {
        return buscarVeiculoUseCase.get(id);
    }

    @PutMapping("/{id}")
    public CadastroVeiculoResponse update(@PathVariable String id, @Valid @RequestBody AtualizarVeiculoRequest request) {
        return atualizarVeiculoUseCase.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        deletarVeiculoUseCase.delete(id);
    }
}
*/
