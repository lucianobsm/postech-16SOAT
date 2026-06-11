package com.fiap.tech_challenge_backend.acesso.application.adapters;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.UsuarioJaCadastradoException;
import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.cadastro.application.ports.CriarUsuarioClienteCommand;
import com.fiap.tech_challenge_backend.cadastro.application.ports.CriarUsuarioClientePort;
import org.springframework.stereotype.Component;

@Component
public class CriarUsuarioClienteAdapter implements CriarUsuarioClientePort {

    private final UsuarioRepository usuarioRepository;

    public CriarUsuarioClienteAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario criarUsuarioCliente(CriarUsuarioClienteCommand command) {
        if (usuarioRepository.existePorEmail(command.email())) {
            throw new UsuarioJaCadastradoException("email", command.email());
        }
        if (usuarioRepository.existePorCpfCnpj(command.cpfCnpj())) {
            throw new UsuarioJaCadastradoException("CPF/CNPJ", command.cpfCnpj());
        }

        Usuario usuario = Usuario.builder()
                .nome(command.nome())
                .email(command.email())
                .telefone(command.telefone())
                .cpfCnpj(command.cpfCnpj())
                .perfil(PerfilUsuario.CLIENTE)
                .build();

        return usuarioRepository.salvar(usuario);
    }

}
