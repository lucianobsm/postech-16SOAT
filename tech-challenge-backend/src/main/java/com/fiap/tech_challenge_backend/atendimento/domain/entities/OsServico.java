package com.fiap.tech_challenge_backend.atendimento.domain.entities;

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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "os_servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsServico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "O orcamento da ordem de servico e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_servico_orcamento"))
    @ToString.Exclude
    private OsOrcamento orcamento;

    @Column(name = "ordem_servico_id")
    private Long ordemServicoId;

    @NotNull(message = "O servico do catalogo e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_servico_servico_catalogo"))
    private ServicoCatalogo servico;

    @NotNull(message = "O preco de mao de obra aplicado e obrigatorio")
    @PositiveOrZero(message = "O preco de mao de obra aplicado nao pode ser negativo")
    @Column(name = "preco_mao_de_obra_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMaoDeObraAplicado;
}

