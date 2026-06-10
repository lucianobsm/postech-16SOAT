package com.fiap.tech_challenge_backend.estoque.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidade que representa uma Peça ou Insumo do estoque.
 * Responsável pelo gerenciamento de inventário de peças e insumos utilizados nos serviços.
 * 
 * As validações garantem que:
 * - Preços são sempre positivos
 * - Preço de compra nunca é maior que preço de venda
 * - Quantidade em estoque nunca é negativa
 * 
 * Contexto Delimitado: estoque
 */
@Entity
@Table(name = "peca_insumo", indexes = {
    @Index(name = "idx_peca_nome", columnList = "nome"),
    @Index(name = "idx_peca_quantidade_estoque", columnList = "quantidade_estoque")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PecaInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "O nome da peça/insumo é obrigatório")
    @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "O preço de venda é obrigatório")
    @Positive(message = "O preço de venda deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço de venda deve ter no máximo 8 dígitos inteiros e 2 decimais")
    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @NotNull(message = "O preço de compra é obrigatório")
    @Positive(message = "O preço de compra deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço de compra deve ter no máximo 8 dígitos inteiros e 2 decimais")
    @Column(name = "preco_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCompra;

    @Size(max = 50, message = "A quantidade por unidade deve ter no máximo 50 caracteres")
    @Column(name = "quantidade_por_unidade", length = 50)
    private String quantidadePorUnidade;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    /**
     * Valida se o preço de compra é menor ou igual ao preço de venda
     * e se ambos são positivos (validação de negócio)
     */
    @PostLoad
    @PostPersist
    @PostUpdate
    void validarPrecos() {
        if (this.precoCompra != null && this.precoVenda != null) {
            if (this.precoCompra.compareTo(this.precoVenda) > 0) {
                throw new IllegalArgumentException(
                    "O preço de compra não pode ser maior que o preço de venda"
                );
            }
        }
    }
}


