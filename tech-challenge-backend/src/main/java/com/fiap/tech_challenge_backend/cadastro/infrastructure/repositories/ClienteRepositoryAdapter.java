package com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories;

import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public boolean existePorCpfCnpj(CpfCnpj cpfCnpj) {

        return clienteJpaRepository.existsByCpfCnpjValor(cpfCnpj.valor());
    }

    @Override
    public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) {
        return clienteJpaRepository.findByCpfCnpjValor(cpfCnpj.valor());
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return clienteJpaRepository.findById(id);
    }

    @Override
    public List<Cliente> listar() {
        return this.clienteJpaRepository.findAll();
    }

    @Override
    public void deletar(UUID id) {
        this.clienteJpaRepository.deleteById(id);
    }
}
