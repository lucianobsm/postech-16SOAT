package com.fiap.tech_challenge_backend.cadastro.presentation;

import com.fiap.tech_challenge_backend.cadastro.application.CadastroClienteUseCase;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por cadastrar e atualizar dados do cliemte.
 * Contexto Delimitado: cadastro
 * Camada: Presentation
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final CadastroClienteUseCase cadastroClienteUseCase;

    public ClienteController(CadastroClienteUseCase cadastroClienteUseCase) {
        this.cadastroClienteUseCase = cadastroClienteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroClienteResponse cadastrar(@Valid @RequestBody CadastroClienteRequest request) {
        return cadastroClienteUseCase.execute(request);
    }

}
