package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository clienteJpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        return clienteJpaRepository.save(cliente);
    }

    @Override
    public boolean existePorCpfCnpj(String cpfCnpj) {
        return clienteJpaRepository.existsByCpfCnpj(cpfCnpj);
    }
}
