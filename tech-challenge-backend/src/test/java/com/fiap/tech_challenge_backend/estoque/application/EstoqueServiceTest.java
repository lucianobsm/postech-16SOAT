package com.fiap.tech_challenge_backend.estoque.application;

import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.infrastructure.MovimentacaoRepository;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstoqueService")
class EstoqueServiceTest {

    @Mock
    private PecaInsumoRepository pecaInsumoRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @InjectMocks
    private EstoqueService service;

    private PecaInsumo peca;
    private UUID pecaId;

    @BeforeEach
    void setUp() {
        pecaId = UUID.randomUUID();
        peca = PecaInsumo.builder()
                .id(pecaId)
                .nome("Filtro de óleo")
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    @Nested
    @DisplayName("cadastrar")
    class Cadastrar {

        @Test
        @DisplayName("deve cadastrar peça/insumo com sucesso")
        void deveCadastrarComSucesso() {
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            var request = new PecaInsumoRequestDTO(
                    "Filtro de óleo", null,
                    new BigDecimal("35.00"), new BigDecimal("25.00"),
                    null, 10, 3
            );

            var response = service.cadastrar(request);

            assertThat(response.id()).isEqualTo(pecaId);
            assertThat(response.nome()).isEqualTo("Filtro de óleo");
            verify(pecaInsumoRepository).save(any(PecaInsumo.class));
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar peça quando encontrada")
        void deveRetornarPeca() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));

            assertThat(service.buscarPorId(pecaId).nome()).isEqualTo("Filtro de óleo");
        }

        @Test
        @DisplayName("deve lançar EntityNotFoundException quando não encontrada")
        void deveLancarExcecao() {
            UUID inexistente = UUID.randomUUID();
            when(pecaInsumoRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(inexistente))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("deve retornar todos os itens")
        void deveListarTodos() {
            when(pecaInsumoRepository.findAll()).thenReturn(List.of(peca));

            assertThat(service.listarTodos()).hasSize(1);
        }

        @Test
        @DisplayName("deve retornar itens abaixo do mínimo")
        void deveListarAbaixoDoMinimo() {
            peca.setQuantidadeEstoque(1);
            when(pecaInsumoRepository.findAbaixoDoMinimo()).thenReturn(List.of(peca));

            var lista = service.listarAbaixoDoMinimo();

            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).abaixoDoMinimo()).isTrue();
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("deve remover peça existente")
        void deveRemover() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));

            assertThatCode(() -> service.remover(pecaId)).doesNotThrowAnyException();
            verify(pecaInsumoRepository).deleteById(pecaId);
        }

        @Test
        @DisplayName("deve lançar exceção ao remover peça inexistente")
        void deveLancarExcecao() {
            UUID inexistente = UUID.randomUUID();
            when(pecaInsumoRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.remover(inexistente))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(pecaInsumoRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("registrarEntrada")
    class RegistrarEntrada {

        @Test
        @DisplayName("deve aumentar estoque e registrar movimentação de ENTRADA")
        void deveRegistrarEntrada() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            service.registrarEntrada(pecaId, 5, "Compra mensal");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(captor.getValue().getObservacao()).isEqualTo("Compra mensal");
        }
    }

    @Nested
    @DisplayName("registrarSaida")
    class RegistrarSaida {

        @Test
        @DisplayName("deve diminuir estoque e registrar movimentação de SAIDA")
        void deveRegistrarSaida() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            service.registrarSaida(pecaId, 3, "Uso em OS-001");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(7);

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.SAIDA);
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque é insuficiente")
        void deveLancarExcecaoQuandoInsuficiente() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));

            assertThatThrownBy(() -> service.registrarSaida(pecaId, 999, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantidade insuficiente");
        }
    }
}
