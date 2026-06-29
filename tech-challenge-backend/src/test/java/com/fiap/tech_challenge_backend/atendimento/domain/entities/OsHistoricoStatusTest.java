package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OsHistoricoStatus - Testes Unitários")
class OsHistoricoStatusTest {

    private OsHistoricoStatus historico;
    private OrdemServico ordemServico;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .build();

        Veiculo veiculo = Veiculo.builder()
                .id(UUID.randomUUID())
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();

        ordemServico = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Problema no motor")
                .dataCriacao(LocalDateTime.now())
                .build();

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .email(new Email("mecanico@oficina.com"))
                .nome("Carlos Mecânico")
                .build();

        historico = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .ordemServico(ordemServico)
                .usuario(usuario)
                .dataMudanca(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar histórico de status corretamente")
    void testCriarHistoricoDeStatus() {
        assertNotNull(historico);
        assertEquals(StatusOrdemServico.RECEBIDA, historico.getStatusOrigem());
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, historico.getStatusDestino());
        assertEquals(ordemServico, historico.getOrdemServico());
        assertEquals(usuario, historico.getUsuario());
    }

    @Test
    @DisplayName("Deve ter ID gerado automaticamente")
    void testIdGerado() {
        assertNotNull(historico.getId());
    }

    @Test
    @DisplayName("Deve ter data de mudança preenchida")
    void testDataMudancaPreenchida() {
        assertNotNull(historico.getDataMudanca());
    }

    @Test
    @DisplayName("Deve inicializar dataMudanca ao persistir se null")
    void testPrePersistComDataMudancaNula() {
        OsHistoricoStatus novo = OsHistoricoStatus.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .build();
        novo.prePersist();

        assertNotNull(novo.getDataMudanca());
    }

    @Test
    @DisplayName("Deve manter dataMudanca existente ao persistir")
    void testPrePersistComDataMudancaExistente() {
        LocalDateTime data = LocalDateTime.now().minusDays(1);
        OsHistoricoStatus novo = OsHistoricoStatus.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(data)
                .build();
        novo.prePersist();

        assertEquals(data, novo.getDataMudanca());
    }

    @Test
    @DisplayName("Deve permitir statusOrigem null")
    void testStatusOrigemNull() {
        OsHistoricoStatus novo = OsHistoricoStatus.builder()
                .statusOrigem(null)
                .statusDestino(StatusOrdemServico.RECEBIDA)
                .build();

        assertNull(novo.getStatusOrigem());
    }

    @Test
    @DisplayName("Deve permitir usuário null")
    void testUsuarioNull() {
        OsHistoricoStatus novo = OsHistoricoStatus.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .usuario(null)
                .build();

        assertNull(novo.getUsuario());
    }

    @Test
    @DisplayName("Deve representar transição de status corretamente")
    void testTransicaoDeStatus() {
        assertEquals(StatusOrdemServico.RECEBIDA, historico.getStatusOrigem());
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, historico.getStatusDestino());
    }

    @Test
    @DisplayName("Deve preservar referência da ordem de serviço")
    void testReferenciaDaOrdemServico() {
        assertEquals(ordemServico.getId(), historico.getOrdemServico().getId());
    }

    @Test
    @DisplayName("Deve preservar referência do usuário")
    void testReferenciaDoUsuario() {
        assertEquals(usuario.getId(), historico.getUsuario().getId());
    }
}
