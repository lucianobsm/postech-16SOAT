package com.fiap.tech_challenge_backend.cadastro.application.ports;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;

public interface CriarUsuarioClientePort {

    Usuario criarUsuarioCliente(CriarUsuarioClienteCommand command);

}
