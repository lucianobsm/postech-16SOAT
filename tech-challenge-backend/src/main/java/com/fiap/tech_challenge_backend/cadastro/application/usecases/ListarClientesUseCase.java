package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarClientesUseCase {

    private final ClienteRepository clienteRepository;

    public ListarClientesUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<BuscarClienteResponse> execute() {
        return clienteRepository.listar().stream()
                .map(c -> new BuscarClienteResponse(
                        c.getId(),
                        c.getNome(),
                        c.getCpfCnpj().valor(),
                        c.getTelefone().valor(),
                        c.getCep().valor(),
                        c.getRua(),
                        c.getNumero(),
                        c.getComplemento(),
                        c.getCidade(),
                        c.getEstado()
                ))
                .toList();
    }
}
