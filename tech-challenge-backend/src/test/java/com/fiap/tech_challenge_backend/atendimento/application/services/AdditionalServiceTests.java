package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("Additional Service Tests - Edge Cases")
class AdditionalServiceTests {

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
    @DisplayName("Deve criar ordem com urgente true")
    void testCriarOrdemComUrgente() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                true
        );

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);

        OrdemServico osParaSalvar = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .urgente(true)
                .build();

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoId)).thenReturn(Optional.of(veiculo));
        when(idGeneratorService.gerarIdOrdemServico()).thenReturn(1L);
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);

        OrdemServicoResponseDTO resultado = service.criar(request);

        assertThat(resultado.urgente()).isTrue();
    }

    @Test
    @DisplayName("Deve criar ordem com urgente false")
    void testCriarOrdemComUrgenteFalse() {
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

        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);

        OrdemServico osParaSalvar = OrdemServico.builder()
                .id(2L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .urgente(false)
                .build();

        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoId)).thenReturn(Optional.of(veiculo));
        when(idGeneratorService.gerarIdOrdemServico()).thenReturn(2L);
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);

        OrdemServicoResponseDTO resultado = service.criar(request);

        assertThat(resultado.urgente()).isFalse();
    }

    @Test
    @DisplayName("Deve listar com múltiplas ordens")
    void testListarTodosComMultiplasOrdens() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Test");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Modelo Test");

        List<OrdemServico> ordens = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            OrdemServico os = OrdemServico.builder()
                    .id(i)
                    .cliente(cliente)
                    .veiculo(veiculo)
                    .status(StatusOrdemServico.RECEBIDA)
                    .valorTotalAcumulado(BigDecimal.valueOf(i * 100))
                    .orcamentos(List.of())
                    .build();
            ordens.add(os);
        }

        when(ordemServicoRepository.listarPriorizadas()).thenReturn(ordens);

        List<OrdemServicoResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(5);
        for (int i = 0; i < 5; i++) {
            assertThat(resultado.get(i).id()).isEqualTo(i + 1);
        }
    }

    @Test
    @DisplayName("Deve atualizar status da ordem")
    void testAtualizarStatusOrdem() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                clienteId,
                veiculoId,
                null,
                StatusOrdemServico.EM_EXECUCAO,
                LocalDateTime.now(),
                null,
                true
        );

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);

        OrdemServico osAtual = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(List.of())
                .build();

        OrdemServico osAtualizada = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.EM_EXECUCAO)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(List.of())
                .build();

        when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(osAtual));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoId)).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osAtualizada);

        OrdemServicoResponseDTO resultado = service.atualizar(1L, request);

        assertThat(resultado.status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("Deve validar cliente não encontrado na atualização")
    void testAtualizarComClienteNaoEncontrado() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                clienteId,
                veiculoId,
                null,
                StatusOrdemServico.EM_EXECUCAO,
                null,
                null,
                true
        );

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        OrdemServico osAtual = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .status(StatusOrdemServico.RECEBIDA)
                .build();

        when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(osAtual));
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(1L, request))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve criar ordem do cliente com sucesso")
    void testCriarOrdemServicoClienteComObservacoes() {
        CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                "98765432100",
                "XYZ9876",
                "Carro faz barulho",
                "Som estranho no motor",
                false
        );

        Cliente cliente = new Cliente();
        cliente.setNome("Maria Santos");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Honda Civic");

        OrdemServico osParaSalvar = OrdemServico.builder()
                .id(2L)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Carro faz barulho")
                .observacoes("Som estranho no motor")
                .valorTotalAcumulado(BigDecimal.ZERO)
                .urgente(false)
                .orcamentos(List.of())
                .build();

        when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.of(veiculo));
        when(idGeneratorService.gerarIdOrdemServico()).thenReturn(2L);
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);

        OrdemServicoResponseDTO resultado = service.criar(request);

        assertThat(resultado.id()).isEqualTo(2L);
        verify(osHistoricoStatusRepository, times(1)).salvar(any());
    }

    @Test
    @DisplayName("Deve verificar que relatório enriquecimento service é injetado")
    void testRelatorioEnriquecimentoServiceInjected() {
        assertThat(service).isNotNull();
        verify(relatorioEnriquecimentoService, never()).enriquecer(any(), any(), any());
    }

    @Test
    @DisplayName("RelatorioEnriquecimentoService - deve enriquecer com valor zero")
    void testEnriquecerComValorZero() {
        RelatorioEnriquecimentoService relatorioService = new RelatorioEnriquecimentoService();

        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "RECEBIDA",
                false,
                "0h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = relatorioService.enriquecer(baseDTO, os, null);

        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("RelatorioEnriquecimentoService - deve enriquecer com múltiplos expands")
    void testEnriquecerComTodosExpands() {
        RelatorioEnriquecimentoService relatorioService = new RelatorioEnriquecimentoService();

        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João",
                "FINALIZADA",
                true,
                "10h",
                tempoPorStatus
        );

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Ford");

        Usuario mecanico = new Usuario();
        mecanico.setNome("Carlos");

        OsOrcamento orc = OsOrcamento.builder()
                .id(1L)
                .valorTotal(BigDecimal.valueOf(1000))
                .build();

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .orcamentos(List.of(orc))
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = relatorioService.enriquecer(
                baseDTO, os, new String[]{"Cliente", "Veiculo", "Mecanico", "Orcamentos"}
        );

        assertThat(resultado.cliente()).isNotNull();
        assertThat(resultado.veiculo()).isNotNull();
        assertThat(resultado.mecanico()).isNotNull();
        assertThat(resultado.orcamentos()).isNotNull();
    }

    @Test
    @DisplayName("RelatorioEnriquecimentoService - deve ignorar expands inválidos")
    void testEnriquecerComExpandsInvalidos() {
        RelatorioEnriquecimentoService relatorioService = new RelatorioEnriquecimentoService();

        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "RECEBIDA",
                false,
                "0h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = relatorioService.enriquecer(
                baseDTO, os, new String[]{"InvalidExpand", "AnotherInvalid"}
        );

        assertThat(resultado.cliente()).isNull();
        assertThat(resultado.veiculo()).isNull();
    }
}
