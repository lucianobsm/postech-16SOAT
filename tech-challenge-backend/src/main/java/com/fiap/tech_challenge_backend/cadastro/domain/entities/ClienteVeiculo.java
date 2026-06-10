package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Entidade que representa a associação entre Cliente e Veículo.
 * Permite relacionamento N:M (muitos para muitos) entre clientes e veículos.
 * Mantém histórico de veículos que já pertenceram ao cliente através do campo 'ativo'.
 * 
 * Contexto Delimitado: cadastro
 */
@Entity
@Table(name = "cliente_veiculo", indexes = {
    @Index(name = "idx_cliente_veiculo_cliente_id", columnList = "cliente_id"),
    @Index(name = "idx_cliente_veiculo_veiculo_id", columnList = "veiculo_id"),
    @Index(name = "idx_cliente_veiculo_ativo", columnList = "ativo"),
    @Index(name = "idx_cliente_veiculo_cliente_ativo", columnList = "cliente_id, ativo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteVeiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador composto (chave primária) - parte 1: ID do Cliente
     * Relacionado à entidade Cliente através de chave estrangeira
     */
    @Id
    @Column(name = "cliente_id", nullable = false)
    @NotNull(message = "O ID do cliente é obrigatório")
    private UUID clienteId;

    /**
     * Identificador composto (chave primária) - parte 2: ID do Veículo
     * Relacionado à entidade Veiculo através de chave estrangeira
     */
    @Id
    @Column(name = "veiculo_id", nullable = false)
    @NotNull(message = "O ID do veículo é obrigatório")
    private UUID veiculoId;

    /**
     * Indica se o veículo é atualmente ativo para o cliente.
     * true = veículo atualmente pertence ao cliente
     * false = veículo que pertenceu ao cliente no passado (histórico)
     * Padrão: true (ativo)
     */
    @NotNull(message = "O status de ativo é obrigatório")
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Referência bidirecional para a entidade Cliente
     * fetch = FetchType.LAZY para otimização de performance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", 
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_cliente_veiculo_cliente"))
    private Cliente cliente;

    /**
     * Referência bidirecional para a entidade Veiculo
     * fetch = FetchType.LAZY para otimização de performance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_cliente_veiculo_veiculo"))
    private Veiculo veiculo;
}


