package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusRepository;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@DisplayName("OsHistoricoStatusRepositoryAdapter")
class OsHistoricoStatusRepositoryAdapterTest {

    private OsHistoricoStatusRepositoryAdapter adapter;

    @Mock
    private OsHistoricoStatusRepository repository;

    private OsHistoricoStatus historico;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new OsHistoricoStatusRepositoryAdapter(repository);

        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .status(StatusOrdemServico.RECEBIDA)
                .build();

        historico = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .ordemServico(ordemServico)
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve salvar um histórico de status")
    void testSalvar() {
        when(repository.save(any(OsHistoricoStatus.class))).thenReturn(historico);

        OsHistoricoStatus resultado = adapter.salvar(historico);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatusDestino()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        verify(repository, times(1)).save(historico);
    }

    @Test
    @DisplayName("Deve buscar históricos por lista de ids de ordens com ordenação")
    void testBuscarPorOrdensServicoOrdenado() {
        List<Long> ordemIds = List.of(1L, 2L, 3L);
        List<OsHistoricoStatus> historicos = List.of(historico);
        when(repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds)).thenReturn(historicos);

        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(ordemIds);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getStatusDestino()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        verify(repository, times(1)).findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de ids é null")
    void testBuscarPorOrdensServicoOrdenadoComListaNula() {
        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(null);

        assertThat(resultado).isEmpty();
        verify(repository, never()).findByOrdemServicoIdsOrderedWithUsuario(anyList());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de ids está vazia")
    void testBuscarPorOrdensServicoOrdenadoComListaVazia() {
        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(List.of());

        assertThat(resultado).isEmpty();
        verify(repository, never()).findByOrdemServicoIdsOrderedWithUsuario(anyList());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não encontrar históricos")
    void testBuscarPorOrdensServicoOrdenadoVazio() {
        List<Long> ordemIds = List.of(1L, 2L);
        when(repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds)).thenReturn(List.of());

        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(ordemIds);

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }

    @Test
    @DisplayName("Deve retornar múltiplos históricos ordenados")
    void testBuscarPorOrdensServicoOrdenadoMultiplos() {
        OsHistoricoStatus historico2 = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusDestino(StatusOrdemServico.AGUARDANDO_APROVACAO)
                .dataMudanca(LocalDateTime.now())
                .build();

        List<Long> ordemIds = List.of(1L, 2L);
        List<OsHistoricoStatus> historicos = List.of(historico, historico2);
        when(repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds)).thenReturn(historicos);

        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(ordemIds);

        assertThat(resultado).hasSize(2);
        verify(repository, times(1)).findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }
}
