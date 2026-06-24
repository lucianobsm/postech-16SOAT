package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "os_orcamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsOrcamento {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "A ordem de servico do orcamento e obrigatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_orcamento_ordem_servico"))
    @ToString.Exclude
    private OrdemServico ordemServico;

    @NotNull(message = "O tipo do orcamento e obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoOrcamento tipo;

    @NotNull(message = "O status do orcamento e obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StatusOrcamento status = StatusOrcamento.PENDENTE;

    @NotNull(message = "O valor total do orcamento e obrigatorio")
    @PositiveOrZero(message = "O valor total do orcamento nao pode ser negativo")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "prazo_estipulado")
    private LocalDateTime prazoEstipulado;

    @NotNull(message = "A data de criacao do orcamento e obrigatoria")
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Valid
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsServico> servicos = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsPeca> pecas = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    public void calcularTotal() {
        BigDecimal totalServicos = servicos.stream()
                .map(OsServico::getPrecoMaoDeObraAplicado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPecas = pecas.stream()
                .map(p -> p.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(p.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.valorTotal = totalServicos.add(totalPecas);
    }

    public void aprovar() {
        this.status = StatusOrcamento.APROVADO;
    }

    public void rejeitar() {
        this.status = StatusOrcamento.REJEITADO;
    }

    public void adicionarServico(OsServico servico) {
        servico.setOrcamento(this);
        this.servicos.add(servico);
    }

    public void adicionarPeca(OsPeca peca) {
        peca.setOrcamento(this);
        this.pecas.add(peca);
    }
}

