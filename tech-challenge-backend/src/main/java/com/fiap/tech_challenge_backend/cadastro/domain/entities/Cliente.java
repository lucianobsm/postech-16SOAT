package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa um Cliente da oficina mecânica.
 * Responsável pelo gerenciamento de dados cadastrais de clientes.
 * Contexto Delimitado: cadastro
 */
@Entity
@Table(name = "clientes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_cliente_cpf_cnpj", columnNames = "cpf_cnpj")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Relacionamento unidirecional com Usuario.
     * Um cliente pode estar associado a um usuário do sistema (nullable).
     */
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id",
                foreignKey = @ForeignKey(name = "fk_cliente_usuario"))
    private Usuario usuario;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 14)
    )
    private CpfCnpj cpfCnpj;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "telefone", length = 11)
    )
    private Telefone telefone;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "cep", length = 8)
    )
    private Cep cep;

    @Size(max = 150, message = "A rua deve ter no máximo 150 caracteres")
    @Column(name = "rua", length = 150)
    private String rua;

    @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
    @Column(name = "numero", length = 20)
    private String numero;

    @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    @Column(name = "complemento", length = 100)
    private String complemento;

    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    @Column(name = "cidade", length = 100)
    private String cidade;

    @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres")
    @Column(name = "estado", length = 2)
    private String estado;
}

