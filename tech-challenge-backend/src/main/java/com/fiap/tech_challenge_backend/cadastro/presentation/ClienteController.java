package com.fiap.tech_challenge_backend.cadastro.presentation;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.usecases.*;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroClienteResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável por cadastrar e atualizar dados do cliente.
 * Contexto Delimitado: cadastro
 * Camada: Presentation
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

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
        log.debug("Requisição recebida: Listar clientes");
        try {
            List<BuscarClienteResponse> clientes = listarClientesUseCase.execute();
            log.info("Clientes listados com sucesso | Total: {}", clientes.size());
            return clientes;
        } catch (Exception e) {
            log.error("Erro ao listar clientes", e);
            throw e;
        }
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
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deletar(@PathVariable String cpfCnpj) {
        log.debug("Requisição recebida: Deletar cliente | CPF/CNPJ: {}", cpfCnpj);

        try {
            deletarClienteUseCase.execute(cpfCnpj);

            log.info("Cliente deletado com sucesso | CPF/CNPJ: {}", cpfCnpj);

            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Cliente deletado com sucesso");
            resposta.put("cpfCnpj", cpfCnpj);
            resposta.put("timestamp", LocalDateTime.now());

            return resposta;

        } catch (Exception e) {
            log.error("Erro ao deletar cliente | CPF/CNPJ: {} | Erro: {}", cpfCnpj, e.getMessage(), e);
            throw e;
        }
    }
}
