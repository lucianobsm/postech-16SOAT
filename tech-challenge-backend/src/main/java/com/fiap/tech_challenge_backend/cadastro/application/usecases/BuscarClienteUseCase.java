package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.springframework.stereotype.Service;

@Service
public class BuscarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public BuscarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public BuscarClienteResponse execute(String cpfCnpj) {
        return clienteRepository.buscarPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .map(c -> new BuscarClienteResponse(
                        c.getId(),
                        c.getNome(),
                        c.getCpfCnpj().valor(),
                        c.getTelefone().valor(),
                        c.getCep().valor(),
                        c.getNumero(),
                        c.getRua(),
                        c.getComplemento(),
                        c.getCidade(),
                        c.getEstado()))
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));
    }
}
