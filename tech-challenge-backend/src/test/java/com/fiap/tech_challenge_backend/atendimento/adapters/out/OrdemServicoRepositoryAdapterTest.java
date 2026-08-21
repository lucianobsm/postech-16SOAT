package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("OrdemServicoRepositoryAdapter")
class OrdemServicoRepositoryAdapterTest {

    private OrdemServicoRepositoryAdapter adapter;

    @Mock
    private OrdemServicoRepository repository;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new OrdemServicoRepositoryAdapter(repository);

        ordemServico = OrdemServico.builder()
                .id(1L)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    @DisplayName("Deve salvar uma ordem de serviço")
    void testSalvar() {
        when(repository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        OrdemServico resultado = adapter.salvar(ordemServico);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(ordemServico);
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço por id")
    void testBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(ordemServico));

        Optional<OrdemServico> resultado = adapter.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não encontrar ordem de serviço por id")
    void testBuscarPorIdNaoEncontrado() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<OrdemServico> resultado = adapter.buscarPorId(999L);

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve listar todas as ordens de serviço")
    void testListarTodos() {
        List<OrdemServico> ordens = List.of(ordemServico);
        when(repository.findAll()).thenReturn(ordens);

        List<OrdemServico> resultado = adapter.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar ordens de serviço priorizadas")
    void testListarPriorizadas() {
        List<OrdemServico> ordens = List.of(ordemServico);
        when(repository.findAllPrioritized()).thenReturn(ordens);

        List<OrdemServico> resultado = adapter.listarPriorizadas();

        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAllPrioritized();
    }

    @Test
    @DisplayName("Deve listar ordens de serviço ativas priorizadas")
    void testListarAtivasPriorizadas() {
        List<OrdemServico> ordens = List.of(ordemServico);
        when(repository.findAllAtivasPrioritized()).thenReturn(ordens);

        List<OrdemServico> resultado = adapter.listarAtivasPriorizadas();

        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAllAtivasPrioritized();
    }

    @Test
    @DisplayName("Deve listar ordens de serviço por status")
    void testListarPorStatus() {
        List<OrdemServico> ordens = List.of(ordemServico);
        when(repository.findAllByStatusPrioritized(StatusOrdemServico.RECEBIDA)).thenReturn(ordens);

        List<OrdemServico> resultado = adapter.listarPorStatus(StatusOrdemServico.RECEBIDA);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        verify(repository, times(1)).findAllByStatusPrioritized(StatusOrdemServico.RECEBIDA);
    }

    @Test
    @DisplayName("Deve remover uma ordem de serviço por id")
    void testRemover() {
        doNothing().when(repository).deleteById(1L);

        adapter.remover(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço por id do orçamento")
    void testBuscarPorOrcamentoId() {
        when(repository.findByOrcamentoId(1L)).thenReturn(Optional.of(ordemServico));

        Optional<OrdemServico> resultado = adapter.buscarPorOrcamentoId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(repository, times(1)).findByOrcamentoId(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não encontrar ordem por id do orçamento")
    void testBuscarPorOrcamentoIdNaoEncontrado() {
        when(repository.findByOrcamentoId(999L)).thenReturn(Optional.empty());

        Optional<OrdemServico> resultado = adapter.buscarPorOrcamentoId(999L);

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findByOrcamentoId(999L);
    }

    @Test
    @DisplayName("Deve verificar se existe ordem de serviço por id")
    void testExistePorId() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = adapter.existePorId(1L);

        assertThat(resultado).isTrue();
        verify(repository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Deve retornar false quando não existir ordem de serviço")
    void testExistePorIdNaoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = adapter.existePorId(999L);

        assertThat(resultado).isFalse();
        verify(repository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Deve listar ordens de serviço para relatório")
    void testListarParaRelatorio() {
        List<OrdemServico> ordens = List.of(ordemServico);
        when(repository.findAllForRelatorio()).thenReturn(ordens);

        List<OrdemServico> resultado = adapter.listarParaRelatorio();

        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAllForRelatorio();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver ordens para relatório")
    void testListarParaRelatorioVazio() {
        when(repository.findAllForRelatorio()).thenReturn(List.of());

        List<OrdemServico> resultado = adapter.listarParaRelatorio();

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAllForRelatorio();
    }
}
