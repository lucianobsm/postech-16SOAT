package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "os_pecas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsPeca {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "A ordem de servico e obrigatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_peca_ordem_servico"))
    @ToString.Exclude
    private OrdemServico ordemServico;

    @NotNull(message = "A peca/insumo e obrigatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_peca_peca_insumo"))
    private PecaInsumo peca;

    @NotNull(message = "A quantidade da peca usada na OS e obrigatoria")
    @Positive(message = "A quantidade da peca deve ser maior que zero")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @NotNull(message = "O preco de venda aplicado e obrigatorio")
    @PositiveOrZero(message = "O preco de venda aplicado nao pode ser negativo")
    @Column(name = "preco_venda_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVendaAplicado;
}

