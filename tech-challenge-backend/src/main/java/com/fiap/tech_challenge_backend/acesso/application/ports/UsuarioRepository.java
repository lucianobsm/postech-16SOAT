package com.fiap.tech_challenge_backend.acesso.application.ports;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    boolean existePorEmail(String email);

    boolean existePorCpfCnpj(String cpfCnpj);
}
