package com.fiap.tech_challenge_backend.acesso.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 150, message = "O email deve ter no máximo 150 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 60, max = 255, message = "A senha deve conter um hash válido")
    @JsonIgnore
    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Size(min = 10, max = 20, message = "O telefone deve ter entre 10 e 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;

    @NotNull(message = "O perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 20)
    private PerfilUsuario perfil;

    @NotBlank(message = "O CPF/CNPJ é obrigatório")
    @Size(min = 11, max = 18, message = "O CPF/CNPJ deve ter entre 11 e 18 caracteres")
    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;
}

