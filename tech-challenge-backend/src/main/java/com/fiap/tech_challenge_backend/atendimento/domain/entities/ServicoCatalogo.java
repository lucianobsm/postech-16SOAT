package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidade que representa um Serviço do Catálogo de Mão de Obra.
 * Define os serviços/mão de obra que a oficina oferece aos clientes.
 * Exemplos: Troca de Óleo, Alinhamento, Balanceamento, Freio, Suspensão, etc.
 * 
 * As validações garantem que:
 * - O preço de mão de obra é sempre positivo
 * - O nome e descrição são válidos
 * 
 * Contexto Delimitado: atendimento
 */
@Entity
@Table(name = "servico_catalogo", indexes = {
    @Index(name = "idx_servico_nome", columnList = "nome"),
    @Index(name = "idx_servico_preco", columnList = "preco_mao_de_obra")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "O nome do serviço é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "O preço de mão de obra é obrigatório")
    @Positive(message = "O preço de mão de obra deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
    @Column(name = "preco_mao_de_obra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMaoDeObra;

    @NotBlank(message = "A categoria do serviço é obrigatória")
    @Pattern(
        regexp = "DIAGNOSTICO|CORRETIVA|PREVENTIVA|GARANTIA",
        message = "A categoria deve ser DIAGNOSTICO, CORRETIVA, PREVENTIVA ou GARANTIA"
    )
    @Column(name = "categoria", nullable = false, length = 30)
    @Builder.Default
    private String categoria = "PREVENTIVA";
}


