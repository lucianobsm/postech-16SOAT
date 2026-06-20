package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
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
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

import com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ordens_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "O cliente da ordem de servico e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ordem_servico_cliente"))
    private Cliente cliente;

    @NotNull(message = "O veiculo da ordem de servico e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ordem_servico_veiculo"))
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id",
        foreignKey = @ForeignKey(name = "fk_ordem_servico_mecanico"))
    private Usuario mecanico;

    @NotNull(message = "O status da ordem de servico e obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private StatusOrdemServico status = StatusOrdemServico.RECEBIDA;

    @NotNull(message = "O valor total acumulado e obrigatorio")
    @PositiveOrZero(message = "O valor total acumulado nao pode ser negativo")
    @Column(name = "valor_total_acumulado", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotalAcumulado = BigDecimal.ZERO;

    @PositiveOrZero(message = "O valor total nao pode ser negativo")
    @Column(name = "valor_total", nullable = true, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @NotNull(message = "A data de criacao e obrigatoria")
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @NotNull(message = "A flag de urgência é obrigatória")
    @Column(name = "urgente", nullable = false)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private Boolean urgente = false;

    @Valid
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsOrcamento> orcamentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataMudanca ASC")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsHistoricoStatus> historicoStatus = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        atualizarDatasPorStatus();
    }

    @PreUpdate
    void preUpdate() {
        atualizarDatasPorStatus();
    }

    private void atualizarDatasPorStatus() {
        if (status == StatusOrdemServico.EM_EXECUCAO && dataInicioExecucao == null) {
            dataInicioExecucao = LocalDateTime.now();
        }
        if (status == StatusOrdemServico.FINALIZADA && dataFinalizacao == null) {
            dataFinalizacao = LocalDateTime.now();
        }
    }

    public void concluirDiagnostico(UUID orcamentoId, LocalDateTime prazoEstipulado) {
        if (this.status != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusException(
                    "A OS deve estar no status EM_DIAGNOSTICO para concluir o diagnóstico. Status atual: " + this.status);
        }

        OsOrcamento orcamento = this.orcamentos.stream()
                .filter(o -> o.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orçamento não encontrado na OS: " + orcamentoId));

        orcamento.setPrazoEstipulado(prazoEstipulado);
        orcamento.calcularTotal();

        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
    }

    public void autorizarPeloCliente() {
        if (this.status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new OrdemServicoStatusException(
                    "Apenas orçamentos no status AGUARDANDO_APROVACAO podem ser autorizados. Status atual: " + this.status);
        }

        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.dataInicioExecucao = LocalDateTime.now();
    }

    public void definirUrgente(Boolean urgente) {
        if (urgente == null) {
            return;
        }

        if (Boolean.TRUE.equals(urgente)
                && (status == StatusOrdemServico.FINALIZADA || status == StatusOrdemServico.ENTREGUE)) {
            throw new IllegalArgumentException("Não é permitido marcar como urgente uma OS finalizada ou entregue");
        }

        this.urgente = urgente;
    }
}

