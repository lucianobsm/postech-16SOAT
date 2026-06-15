package com.fiap.tech_challenge_backend.estoque.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PecaInsumo - Lógica de Estoque")
class PecaInsumoTest {

    private PecaInsumo peca;

    @BeforeEach
    void setUp() {
        peca = PecaInsumo.builder()
                .nome("Filtro de óleo")
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    @Nested
    @DisplayName("entrada")
    class Entrada {

        @Test
        @DisplayName("deve adicionar unidades ao estoque")
        void deveAdicionarUnidades() {
            peca.entrada(5);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);
        }

        @Test
        @DisplayName("deve aceitar entrada de uma unidade")
        void deveAceitarEntradaDeUma() {
            peca.entrada(1);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(11);
        }
    }

    @Nested
    @DisplayName("saida")
    class Saida {

        @Test
        @DisplayName("deve subtrair unidades do estoque")
        void deveSubtrairUnidades() {
            peca.saida(4);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(6);
        }

        @Test
        @DisplayName("deve permitir saída que zera o estoque")
        void devePermitirSaidaTotal() {
            peca.saida(10);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(0);
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque é insuficiente")
        void deveLancarExcecaoQuandoInsuficiente() {
            assertThatThrownBy(() -> peca.saida(11))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantidade insuficiente");
        }
    }

    @Nested
    @DisplayName("estoqueAbaixoDoMinimo")
    class EstoqueAbaixoDoMinimo {

        @Test
        @DisplayName("deve retornar true quando estoque < mínimo")
        void deveRetornarTrueQuandoAbaixo() {
            peca.setQuantidadeEstoque(2);

            assertThat(peca.estoqueAbaixoDoMinimo()).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando estoque == mínimo")
        void deveRetornarFalseQuandoIgual() {
            peca.setQuantidadeEstoque(3);

            assertThat(peca.estoqueAbaixoDoMinimo()).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando estoque > mínimo")
        void deveRetornarFalseQuandoAcima() {
            assertThat(peca.estoqueAbaixoDoMinimo()).isFalse();
        }
    }
}
