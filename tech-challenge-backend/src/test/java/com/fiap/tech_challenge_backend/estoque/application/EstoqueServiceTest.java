package com.fiap.tech_challenge_backend.estoque.application;

import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
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

    @Mock private PecaInsumoRepository pecaInsumoRepository;
    @Mock private MovimentacaoRepository movimentacaoRepository;
    @InjectMocks private EstoqueService service;

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
                .tipo(TipoPecaInsumo.PECA)
                .build();
    }

    private PecaInsumoRequestDTO requestPadrao() {
        return new PecaInsumoRequestDTO(
                "Filtro de óleo", null,
                new BigDecimal("35.00"), new BigDecimal("25.00"),
                null, 10, 3, TipoPecaInsumo.PECA
        );
    }

    // ─────────────────────────────────────────────
    // darEntrada (cadastro de peça nova ou reposição)
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("darEntrada")
    class DarEntrada {

        private EntradaEstoqueRequestDTO requestNova(Integer quantidade) {
            return new EntradaEstoqueRequestDTO(
                    null, "Vela de ignição", "NGK",
                    new BigDecimal("18.00"), new BigDecimal("9.00"),
                    "Jogo c/4", quantidade, 5, "Compra inicial", TipoPecaInsumo.PECA
            );
        }

        @Test
        @DisplayName("deve cadastrar nova peça quando id é nulo e gerar ENTRADA")
        void deveCadastrarNovaPecaQuandoIdNulo() {
            when(pecaInsumoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            var captorPeca = ArgumentCaptor.forClass(PecaInsumo.class);
            var response = service.darEntrada(requestNova(40));

            verify(pecaInsumoRepository, never()).findById(any());
            verify(pecaInsumoRepository).save(captorPeca.capture());
            assertThat(captorPeca.getValue().getNome()).isEqualTo("Vela de ignição");
            assertThat(captorPeca.getValue().getQuantidadeEstoque()).isEqualTo(40);
            assertThat(captorPeca.getValue().getTipo()).isEqualTo(TipoPecaInsumo.PECA);
            assertThat(response.nome()).isEqualTo("Vela de ignição");

            var captorMov = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captorMov.capture());
            assertThat(captorMov.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(captorMov.getValue().getQuantidade()).isEqualTo(40);
        }

        @Test
        @DisplayName("deve cadastrar nova peça quando id informado não existe")
        void deveCadastrarNovaPecaQuandoIdNaoExiste() {
            UUID inexistente = UUID.randomUUID();
            when(pecaInsumoRepository.findById(inexistente)).thenReturn(Optional.empty());
            when(pecaInsumoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            var request = new EntradaEstoqueRequestDTO(
                    inexistente, "Vela de ignição", null,
                    new BigDecimal("18.00"), new BigDecimal("9.00"),
                    null, 10, null, null, TipoPecaInsumo.INSUMO
            );

            service.darEntrada(request);

            var captorMov = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captorMov.capture());
            assertThat(captorMov.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
        }

        @Test
        @DisplayName("deve usar quantidadeMinima = 0 quando nula no cadastro")
        void deveUsarQuantidadeMinimaZeroQuandoNula() {
            when(pecaInsumoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            var request = new EntradaEstoqueRequestDTO(
                    null, "Vela", null,
                    new BigDecimal("18.00"), new BigDecimal("9.00"),
                    null, 10, null, null, TipoPecaInsumo.PECA
            );

            var response = service.darEntrada(request);

            assertThat(response.quantidadeMinima()).isZero();
        }

        @Test
        @DisplayName("deve repor estoque quando id aponta para peça existente e gerar ENTRADA")
        void deveReporEstoqueQuandoPecaExiste() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            var request = new EntradaEstoqueRequestDTO(
                    pecaId, null, null, null, null, null, 5, null, "Reposição NF-002", null
            );

            service.darEntrada(request);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);
            var captorMov = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captorMov.capture());
            assertThat(captorMov.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(captorMov.getValue().getQuantidade()).isEqualTo(5);
            assertThat(captorMov.getValue().getObservacao()).isEqualTo("Reposição NF-002");
        }

        @Test
        @DisplayName("deve lançar exceção quando nome ausente no cadastro de peça nova")
        void deveLancarExcecaoQuandoNomeAusente() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "  ", null,
                    new BigDecimal("18.00"), new BigDecimal("9.00"),
                    null, 10, null, null, TipoPecaInsumo.PECA
            );

            assertThatThrownBy(() -> service.darEntrada(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nome é obrigatório");
            verify(movimentacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando preços ausentes no cadastro de peça nova")
        void deveLancarExcecaoQuandoPrecosAusentes() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "Vela", null, null, null, null, 10, null, null, TipoPecaInsumo.PECA
            );

            assertThatThrownBy(() -> service.darEntrada(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Preço");
            verify(movimentacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando tipo ausente no cadastro de peça nova")
        void deveLancarExcecaoQuandoTipoAusente() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "Vela", null,
                    new BigDecimal("18.00"), new BigDecimal("9.00"),
                    null, 10, null, null, null
            );

            assertThatThrownBy(() -> service.darEntrada(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tipo");
            verify(movimentacaoRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────
    // atualizar
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("deve gerar movimentação de AJUSTE quando estoque muda")
        void deveGerarAjusteQuandoEstoqueMuda() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            var request = new PecaInsumoRequestDTO("Filtro de óleo", null,
                    new BigDecimal("35.00"), new BigDecimal("25.00"), null, 15, 3, TipoPecaInsumo.PECA);

            service.atualizar(pecaId, request);

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.AJUSTE);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(5);
        }

        @Test
        @DisplayName("não deve gerar movimentação quando estoque não muda")
        void naoDeveGerarAjusteQuandoEstoqueIgual() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);

            service.atualizar(pecaId, requestPadrao());

            verify(movimentacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve atualizar o tipo da peça")
        void deveAtualizarTipo() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var request = new PecaInsumoRequestDTO("Filtro de óleo", null,
                    new BigDecimal("35.00"), new BigDecimal("25.00"), null, 10, 3, TipoPecaInsumo.INSUMO);

            var captor = ArgumentCaptor.forClass(PecaInsumo.class);
            service.atualizar(pecaId, request);
            verify(pecaInsumoRepository).save(captor.capture());

            assertThat(captor.getValue().getTipo()).isEqualTo(TipoPecaInsumo.INSUMO);
        }

        @Test
        @DisplayName("deve lançar EntityNotFoundException quando peça não encontrada")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            UUID inexistente = UUID.randomUUID();
            when(pecaInsumoRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizar(inexistente, requestPadrao()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────
    // buscarPorId
    // ─────────────────────────────────────────────
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

    // ─────────────────────────────────────────────
    // listar
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("deve retornar todos os itens quando tipo é nulo")
        void deveListarTodos() {
            when(pecaInsumoRepository.findAll()).thenReturn(List.of(peca));

            assertThat(service.listarTodos(null)).hasSize(1);
            verify(pecaInsumoRepository).findAll();
            verify(pecaInsumoRepository, never()).findByTipo(any());
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há itens")
        void deveRetornarListaVazia() {
            when(pecaInsumoRepository.findAll()).thenReturn(List.of());

            assertThat(service.listarTodos(null)).isEmpty();
        }

        @Test
        @DisplayName("deve filtrar por tipo PECA")
        void deveFiltrarPorTipoPeca() {
            when(pecaInsumoRepository.findByTipo(TipoPecaInsumo.PECA)).thenReturn(List.of(peca));

            var lista = service.listarTodos(TipoPecaInsumo.PECA);

            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).tipo()).isEqualTo(TipoPecaInsumo.PECA);
            verify(pecaInsumoRepository, never()).findAll();
        }

        @Test
        @DisplayName("deve filtrar por tipo INSUMO")
        void deveFiltrarPorTipoInsumo() {
            var insumo = PecaInsumo.builder()
                    .id(UUID.randomUUID()).nome("Óleo 5W30")
                    .precoVenda(new BigDecimal("60.00")).precoCompra(new BigDecimal("40.00"))
                    .quantidadeEstoque(20).quantidadeMinima(5).tipo(TipoPecaInsumo.INSUMO).build();
            when(pecaInsumoRepository.findByTipo(TipoPecaInsumo.INSUMO)).thenReturn(List.of(insumo));

            var lista = service.listarTodos(TipoPecaInsumo.INSUMO);

            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).tipo()).isEqualTo(TipoPecaInsumo.INSUMO);
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

    // ─────────────────────────────────────────────
    // remover
    // ─────────────────────────────────────────────
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

    // ─────────────────────────────────────────────
    // registrarSaida
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("registrarSaida")
    class RegistrarSaida {

        @Test
        @DisplayName("deve diminuir estoque e registrar movimentação de SAIDA")
        void deveRegistrarSaida() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            service.registrarSaida(pecaId, 3, "Ajuste");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(7);
            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.SAIDA);
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque insuficiente")
        void deveLancarExcecaoQuandoInsuficiente() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));

            assertThatThrownBy(() -> service.registrarSaida(pecaId, 999, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantidade insuficiente");
        }
    }

    // ─────────────────────────────────────────────
    // registrarVenda
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("registrarVenda")
    class RegistrarVenda {

        @Test
        @DisplayName("deve diminuir estoque e registrar movimentação de VENDA")
        void deveRegistrarVenda() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            service.registrarVenda(pecaId, 4, "Venda OS-001");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(6);
            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.VENDA);
            assertThat(captor.getValue().getObservacao()).isEqualTo("Venda OS-001");
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque insuficiente")
        void deveLancarExcecaoQuandoInsuficiente() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));

            assertThatThrownBy(() -> service.registrarVenda(pecaId, 999, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ─────────────────────────────────────────────
    // reservar
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("reservar")
    class Reservar {

        @Test
        @DisplayName("deve diminuir estoque e registrar movimentação de RESERVA")
        void deveReservar() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.save(any())).thenReturn(peca);
            when(movimentacaoRepository.save(any())).thenReturn(new MovimentacaoEstoque());

            service.reservar(pecaId, 2, "Reserva OS-010");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(8);
            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.RESERVA);
            assertThat(captor.getValue().getObservacao()).isEqualTo("Reserva OS-010");
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque insuficiente")
        void deveLancarExcecaoQuandoInsuficiente() {
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));

            assertThatThrownBy(() -> service.reservar(pecaId, 999, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ─────────────────────────────────────────────
    // listarMovimentacoes
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("listarMovimentacoes")
    class ListarMovimentacoes {

        @Test
        @DisplayName("deve retornar movimentações da peça")
        void deveRetornarMovimentacoes() {
            var mov = MovimentacaoEstoque.builder()
                    .pecaInsumo(peca)
                    .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                    .quantidade(5)
                    .build();
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(movimentacaoRepository.findByPecaInsumoOrderByCriadoEmDesc(peca))
                    .thenReturn(List.of(mov));

            var resultado = service.listarMovimentacoes(pecaId);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).tipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
        }

        @Test
        @DisplayName("deve lançar exceção quando peça não encontrada")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            UUID inexistente = UUID.randomUUID();
            when(pecaInsumoRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.listarMovimentacoes(inexistente))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
