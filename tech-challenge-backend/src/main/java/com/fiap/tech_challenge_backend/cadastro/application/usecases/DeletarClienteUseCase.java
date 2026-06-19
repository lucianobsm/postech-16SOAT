package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.DeletarUsuarioClientePort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeletarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final DeletarUsuarioClientePort deletarUsuarioClientePort;

    public DeletarClienteUseCase(
            ClienteRepository clienteRepository,
            DeletarUsuarioClientePort deletarUsuarioClientePort
    ) {
        this.clienteRepository = clienteRepository;
        this.deletarUsuarioClientePort = deletarUsuarioClientePort;
    }

    @Transactional
    public void execute(String cpfCnpj) {
        Cliente cliente = clienteRepository.buscarPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));

        clienteRepository.deletar(cliente.getId());
        deletarUsuarioClientePort.deletar(cpfCnpj);
    }

}
