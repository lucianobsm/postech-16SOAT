package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("OrdemServicoService")
class OrdemServicoServiceTest {

    private OrdemServicoService service;

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PdfGeneratorPort pdfGeneratorPort;

    @Mock
    private EmailSenderPort emailSenderPort;

    @Mock
    private ServicoCatalogoRepositoryPort servicoCatalogoRepository;

    @Mock
    private PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private RelatorioEnriquecimentoService relatorioEnriquecimentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new OrdemServicoService(
                ordemServicoRepository,
                osHistoricoStatusRepository,
                clienteRepository,
                veiculoRepository,
                usuarioRepository,
                pdfGeneratorPort,
                emailSenderPort,
                servicoCatalogoRepository,
                pecaInsumoRepository,
                idGeneratorService,
                relatorioEnriquecimentoService
        );
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso")
    void testCriarOrdemServico() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();

        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                mecanicoId,
                true
        );

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);
        veiculo.setModelo("Ford Ka");

        Usuario mecanico = new Usuario();
        mecanico.setId(mecanicoId);
        mecanico.setNome("Carlos Mecânico");

        OrdemServico osParaSalvar = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .urgente(true)
                .build();

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoId)).thenReturn(Optional.of(veiculo));
        when(usuarioRepository.buscarPorId(mecanicoId)).thenReturn(Optional.of(mecanico));
        when(idGeneratorService.gerarIdOrdemServico()).thenReturn(1L);
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);

        OrdemServicoResponseDTO resultado = service.criar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
        verify(ordemServicoRepository, times(1)).salvar(any(OrdemServico.class));
        verify(osHistoricoStatusRepository, times(1)).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void testCriarComClienteNaoEncontrado() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                false
        );

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado")
    void testCriarComVeiculoNaoEncontrado() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                false
        );

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Veículo não encontrado");

        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve buscar ordem de serviço por id")
    void testBuscarPorId() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);
        veiculo.setModelo("Ford Ka");

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(List.of())
                .build();

        when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

        OrdemServicoResponseDTO resultado = service.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
        verify(ordemServicoRepository, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordem não encontrada")
    void testBuscarPorIdNaoEncontrado() {
        when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ordem de serviço não encontrada");
    }

    @Test
    @DisplayName("Deve listar todos as ordens de serviço")
    void testListarTodos() {
        Cliente cliente = new Cliente();
        cliente.setNome("João");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Ford");

        OrdemServico os1 = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(List.of())
                .build();

        OrdemServico os2 = OrdemServico.builder()
                .id(2L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.FINALIZADA)
                .valorTotalAcumulado(BigDecimal.valueOf(500))
                .orcamentos(List.of())
                .build();

        when(ordemServicoRepository.listarPriorizadas()).thenReturn(List.of(os1, os2));

        List<OrdemServicoResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).id()).isEqualTo(1L);
        assertThat(resultado.get(1).id()).isEqualTo(2L);
        verify(ordemServicoRepository, times(1)).listarPriorizadas();
    }

    @Test
    @DisplayName("Deve remover ordem de serviço")
    void testRemoverOrdemServico() {
        when(ordemServicoRepository.existePorId(1L)).thenReturn(true);
        doNothing().when(ordemServicoRepository).remover(1L);

        DeletarOrdemServicoResponseDTO resultado = service.remover(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo("DELETADO");
        verify(ordemServicoRepository, times(1)).remover(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover ordem inexistente")
    void testRemoverOrdemNaoExistente() {
        when(ordemServicoRepository.existePorId(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.remover(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ordem de serviço não encontrada");

        verify(ordemServicoRepository, never()).remover(anyLong());
    }

    @Test
    @DisplayName("Deve atualizar ordem de serviço")
    void testAtualizarOrdemServico() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                clienteId,
                veiculoId,
                mecanicoId,
                StatusOrdemServico.EM_DIAGNOSTICO,
                null,
                null,
                false
        );

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);

        Usuario mecanico = new Usuario();
        mecanico.setId(mecanicoId);

        OrdemServico osAtual = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(List.of())
                .build();

        OrdemServico osAtualizada = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .status(StatusOrdemServico.EM_DIAGNOSTICO)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(List.of())
                .build();

        when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(osAtual));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoId)).thenReturn(Optional.of(veiculo));
        when(usuarioRepository.buscarPorId(mecanicoId)).thenReturn(Optional.of(mecanico));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osAtualizada);

        OrdemServicoResponseDTO resultado = service.atualizar(1L, request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        verify(ordemServicoRepository, times(1)).salvar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve criar ordem de serviço pelo cliente")
    void testCriarOrdemServicoCliente() {
        CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Carro não inicia",
                "Verificar bateria",
                true
        );

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Ford Ka");

        OrdemServico osParaSalvar = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Carro não inicia")
                .observacoes("Verificar bateria")
                .valorTotalAcumulado(BigDecimal.ZERO)
                .urgente(true)
                .orcamentos(List.of())
                .build();

        when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.of(veiculo));
        when(idGeneratorService.gerarIdOrdemServico()).thenReturn(1L);
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);

        OrdemServicoResponseDTO resultado = service.criar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
        verify(ordemServicoRepository, times(1)).salvar(any(OrdemServico.class));
    }
}
