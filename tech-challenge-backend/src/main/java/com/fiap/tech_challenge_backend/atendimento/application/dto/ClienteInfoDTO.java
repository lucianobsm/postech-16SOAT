package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;

import java.util.UUID;

public record ClienteInfoDTO(
        UUID id,
        String nome,
        String telefone,
        String email
) {
    public static ClienteInfoDTO from(Cliente cliente) {
        return new ClienteInfoDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone() != null ? cliente.getTelefone().valor() : null,
                cliente.getUsuario() != null && cliente.getUsuario().getEmail() != null
                        ? cliente.getUsuario().getEmail().valor()
                        : null
        );
    }
}
