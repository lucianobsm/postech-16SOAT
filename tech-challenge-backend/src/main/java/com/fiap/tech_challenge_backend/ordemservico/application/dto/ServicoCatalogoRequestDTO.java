package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServicoCatalogoRequestDTO(
        @NotBlank(message = "O nome e obrigatorio") String nome,
        String descricao,
        @NotNull(message = "O preco de mao de obra e obrigatorio")
        @Positive(message = "O preco de mao de obra deve ser maior que zero") BigDecimal precoMaoDeObra
) {
}
