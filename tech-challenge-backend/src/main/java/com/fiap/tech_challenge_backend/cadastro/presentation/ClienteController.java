package com.fiap.tech_challenge_backend.cadastro.presentation;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.usecases.*;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroClienteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável por cadastrar e atualizar dados do cliente.
 * Contexto Delimitado: cadastro
 * Camada: Presentation
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final CadastroClienteUseCase cadastroClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;

    public ClienteController(
            CadastroClienteUseCase cadastroClienteUseCase,
            BuscarClienteUseCase buscarClienteUseCase,
            ListarClientesUseCase listarClientesUseCase,
            AtualizarClienteUseCase atualizarClienteUseCase,
            DeletarClienteUseCase deletarClienteUseCase
    ) {
        this.cadastroClienteUseCase = cadastroClienteUseCase;
        this.buscarClienteUseCase = buscarClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.deletarClienteUseCase = deletarClienteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroClienteResponse cadastrar(@Valid @RequestBody CadastroClienteRequest request) {
        return cadastroClienteUseCase.execute(request);
    }

    @GetMapping
    public List<BuscarClienteResponse> listar() {
        return listarClientesUseCase.execute();
    }

    @GetMapping("/{cpfCnpj}")
    public BuscarClienteResponse buscar(@PathVariable String cpfCnpj) {
        return buscarClienteUseCase.execute(cpfCnpj);
    }

    @PutMapping("/{cpfCnpj}")
    public BuscarClienteResponse atualizar(@PathVariable String cpfCnpj, @Valid @RequestBody AtualizarClienteRequest request) {
        return atualizarClienteUseCase.execute(cpfCnpj, request);
    }

    @DeleteMapping("/{cpfCnpj}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String cpfCnpj) {
        deletarClienteUseCase.execute(cpfCnpj);
    }
}
