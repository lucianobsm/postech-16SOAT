package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa um Veículo cadastrado no sistema.
 * Responsável pelo gerenciamento de dados de veículos dos clientes.
 * Contexto Delimitado: cadastro
 */
@Entity
@Table(name = "veiculos", uniqueConstraints = {
    @UniqueConstraint(name = "uk_veiculo_placa", columnNames = "placa")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(
            name = "valor",
            column = @Column(name = "placa", nullable = false, unique = true, length = 8)
    )
    private Placa placa;

    @NotBlank(message = "A marca é obrigatória")
    @Size(min = 2, max = 100, message = "A marca deve ter entre 2 e 100 caracteres")
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @NotBlank(message = "O modelo é obrigatório")
    @Size(min = 2, max = 100, message = "O modelo deve ter entre 2 e 100 caracteres")
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @NotBlank(message = "A cor é obrigatória")
    @Size(min = 2, max = 50, message = "A cor deve ter entre 2 e 50 caracteres")
    @Column(name = "cor", nullable = false, length = 50)
    private String cor;
}

