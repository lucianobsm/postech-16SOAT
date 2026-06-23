package com.fiap.tech_challenge_backend.ordemservico.application.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AtribuirMecanicoRequestDTO")
class AtribuirMecanicoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("deve ser valido quando mecanicoId e informado")
    void deveSerValidoComMecanicoId() {
        var dto = new AtribuirMecanicoRequestDTO(UUID.randomUUID());

        var violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("deve ser invalido quando mecanicoId e nulo")
    void deveSerInvalidoQuandoMecanicoIdNulo() {
        var dto = new AtribuirMecanicoRequestDTO(null);

        var violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("O mecanico e obrigatorio");
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("mecanicoId");
    }
}
