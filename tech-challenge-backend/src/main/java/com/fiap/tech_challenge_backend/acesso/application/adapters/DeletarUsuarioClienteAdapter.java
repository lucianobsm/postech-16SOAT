package com.fiap.tech_challenge_backend.acesso.application.adapters;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.UsuarioNaoEncontradoException;
import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.cadastro.application.ports.DeletarUsuarioClientePort;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.springframework.stereotype.Component;

@Component
public class DeletarUsuarioClienteAdapter implements DeletarUsuarioClientePort {

    private final UsuarioRepository usuarioRepository;

    public DeletarUsuarioClienteAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public void deletar(String cpfCnpj) {
        Usuario usuarioEncontrado = usuarioRepository.procuraPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new UsuarioNaoEncontradoException(cpfCnpj));

        usuarioRepository.deletar(usuarioEncontrado.getId());
    }
}
