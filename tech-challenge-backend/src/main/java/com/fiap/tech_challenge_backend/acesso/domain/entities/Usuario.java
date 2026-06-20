package com.fiap.tech_challenge_backend.acesso.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa um Usuário do sistema.
 * Responsável pelo gerenciamento de credenciais, perfis e segurança.
 * Contexto Delimitado: acesso
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_usuario_cpf_cnpj", columnNames = "cpf_cnpj")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "email", nullable = false, unique = true, length = 150)
    )
    private Email email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 60, max = 255, message = "A senha deve conter um hash válido")
    @JsonIgnore
    @Column(name = "senha", nullable = false)
    private String senha;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "telefone", length = 11)
    )
    private Telefone telefone;

    @NotNull(message = "O perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 20)
    private PerfilUsuario perfil;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 14)
    )
    private CpfCnpj cpfCnpj;
}

