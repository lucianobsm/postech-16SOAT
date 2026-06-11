package com.fiap.tech_challenge_backend.cadastro.application;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteJaCadastradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.CriarUsuarioClienteCommand;
import com.fiap.tech_challenge_backend.cadastro.application.ports.CriarUsuarioClientePort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Use Case responsável pelo cadastro de clientes.
 * Contexto Delimitado: cadastro
 * Camada: Application
 */
@Service
public class CadastroClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final CriarUsuarioClientePort criarUsuarioClientePort;

    public CadastroClienteUseCase(
            ClienteRepository clienteRepository,
            CriarUsuarioClientePort criarUsuarioClientePort
    ) {
        this.clienteRepository = clienteRepository;
        this.criarUsuarioClientePort = criarUsuarioClientePort;
    }

    @Transactional
    public CadastroClienteResponse execute(CadastroClienteRequest request) {
        validarClienteExistente(request.cpfCnpj());

        Usuario usuario = criarUsuarioClientePort.criarUsuarioCliente(
                new CriarUsuarioClienteCommand(
                        request.nome(),
                        request.email(),
                        request.telefone(),
                        request.cpfCnpj()
                )
        );

        Cliente cliente = Cliente.builder()
                .usuario(usuario)
                .nome(request.nome())
                .cpfCnpj(request.cpfCnpj())
                .telefone(request.telefone())
                .cep(request.cep())
                .rua(request.rua())
                .numero(request.numero())
                .complemento(request.complemento())
                .cidade(request.cidade())
                .estado(request.estado())
                .build();

        Cliente clienteCadastrado = clienteRepository.salvar(cliente);

        return new CadastroClienteResponse(
                clienteCadastrado.getId(),
                clienteCadastrado.getNome(),
                clienteCadastrado.getCpfCnpj(),
                clienteCadastrado.getTelefone(),
                clienteCadastrado.getCep(),
                clienteCadastrado.getRua(),
                clienteCadastrado.getNumero(),
                clienteCadastrado.getComplemento(),
                clienteCadastrado.getCidade(),
                clienteCadastrado.getEstado()
        );
    }

    private void validarClienteExistente(String cpfCnpj) {
        if (clienteRepository.existePorCpfCnpj(cpfCnpj)) {
            throw new ClienteJaCadastradoException(cpfCnpj);
        }
    }

}
