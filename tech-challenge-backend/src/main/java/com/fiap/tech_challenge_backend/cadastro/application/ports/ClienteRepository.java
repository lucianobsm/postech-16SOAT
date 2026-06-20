package com.fiap.tech_challenge_backend.cadastro.application.ports;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    boolean existePorCpfCnpj(CpfCnpj cpfCnpj);

    Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj);

    Optional<Cliente> buscarPorId(UUID id);

    List<Cliente> listar();

    void deletar(UUID id);
}
