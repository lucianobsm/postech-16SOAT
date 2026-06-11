package com.fiap.tech_challenge_backend.cadastro.application.ports;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;


public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    boolean existePorCpfCnpj(String cpfCnpj);
}
