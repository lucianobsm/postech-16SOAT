package com.fiap.tech_challenge_backend.estoque.domain.entities;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que registra cada movimentação de estoque (entrada, saída ou ajuste).
 * Contexto Delimitado: estoque
 */
@Entity
@Table(name = "movimentacoes_estoque")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "A peça/insumo é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_insumo_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_movimentacao_peca_insumo"))
    @ToString.Exclude
    private PecaInsumo pecaInsumo;

    @NotNull(message = "O tipo de movimentação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 20)
    private TipoMovimentacao tipoMovimentacao;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Size(max = 500)
    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
    }
}
