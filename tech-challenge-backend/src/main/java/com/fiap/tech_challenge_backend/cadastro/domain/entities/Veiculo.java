package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 8, message = "A placa deve ter entre 7 e 8 caracteres")
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
             message = "A placa deve estar no formato Mercosul (ABC1D23) ou antigo (ABC1234)")
    @Column(name = "placa", nullable = false, unique = true, length = 8)
    private String placa;

    @NotBlank(message = "O modelo é obrigatório")
    @Size(min = 2, max = 100, message = "O modelo deve ter entre 2 e 100 caracteres")
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;
}

