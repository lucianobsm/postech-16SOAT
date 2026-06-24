package com.fiap.tech_challenge_backend.ordemservico.application;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.infrastructure.repositories.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.ordemservico.infrastructure.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.ordemservico.infrastructure.ServicoCatalogoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.VeiculoJpaRepository;
import com.fiap.tech_challenge_backend.estoque.application.EstoqueService;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtendimentoService")
class AtendimentoServiceTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private ServicoCatalogoRepository servicoCatalogoRepository;
    @Mock private PecaInsumoRepository pecaInsumoRepository;
    @Mock private ClienteJpaRepository clienteRepository;
    @Mock private VeiculoJpaRepository veiculoRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;
    @Mock private EstoqueService estoqueService;

    @InjectMocks private AtendimentoService service;

    private UUID osId;
    private UUID clienteId;
    private UUID veiculoId;
    private UUID mecanicoId;
    private UUID pecaId;
    private UUID servicoId;

    private Cliente cliente;
    private Veiculo veiculo;
    private Usuario mecanico;
    private PecaInsumo peca;
    private ServicoCatalogo servico;
    private OrdemServico os;

    @BeforeEach
    void setUp() {
        osId       = UUID.randomUUID();
        clienteId  = UUID.randomUUID();
        veiculoId  = UUID.randomUUID();
        mecanicoId = UUID.randomUUID();
        pecaId     = UUID.randomUUID();
        servicoId  = UUID.randomUUID();

        cliente  = Cliente.builder().id(clienteId).nome("Ana").build();
        veiculo  = Veiculo.builder().id(veiculoId).placa(new Placa("BRA2E19")).modelo("Civic").build();
        mecanico = Usuario.builder().id(mecanicoId).nome("Marcos").build();

        peca = PecaInsumo.builder()
                .id(pecaId).nome("Oleo 5W30")
                .precoVenda(new BigDecimal("59.90"))
                .precoCompra(new BigDecimal("36.50"))
                .quantidadeEstoque(40).quantidadeMinima(5)
                .tipo(TipoPecaInsumo.INSUMO)
                .build();

        servico = ServicoCatalogo.builder()
                .id(servicoId).nome("Troca de Oleo")
                .precoMaoDeObra(new BigDecimal("120.00"))
                .build();

        os = OrdemServico.builder()
                .id(osId).cliente(cliente).veiculo(veiculo)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotal(BigDecimal.ZERO)
                .pecas(new HashSet<>())
                .servicos(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("criarOrdemServico")
    class CriarOrdemServico {

        @Test
        @DisplayName("deve criar OS com status RECEBIDA e valor 0")
        void deveCriarOsComStatusRecebida() {
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
            when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
            when(ordemServicoRepository.save(any())).thenReturn(os);

            var result = service.criarOrdemServico(new CriarOrdemServicoRequestDTO(clienteId, veiculoId, null));

            assertThat(result.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
            assertThat(result.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
            verify(ordemServicoRepository).save(any());
        }

        @Test
        @DisplayName("deve atribuir mecanico quando mecanicoId fornecido")
        void deveAtribuirMecanicoQuandoInformado() {
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
            when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
            when(usuarioRepository.findById(mecanicoId)).thenReturn(Optional.of(mecanico));
            var osComMecanico = OrdemServico.builder().id(osId).cliente(cliente).veiculo(veiculo)
                    .mecanico(mecanico).status(StatusOrdemServico.RECEBIDA)
                    .valorTotal(BigDecimal.ZERO).pecas(new HashSet<>()).servicos(new HashSet<>()).build();
            when(ordemServicoRepository.save(any())).thenReturn(osComMecanico);

            var result = service.criarOrdemServico(new CriarOrdemServicoRequestDTO(clienteId, veiculoId, mecanicoId));

            assertThat(result.mecanicoNome()).isEqualTo("Marcos");
        }

        @Test
        @DisplayName("deve lancar EntityNotFoundException quando cliente nao existe")
        void deveLancarExcecaoClienteNaoEncontrado() {
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criarOrdemServico(
                    new CriarOrdemServicoRequestDTO(clienteId, veiculoId, null)))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Cliente");
        }

        @Test
        @DisplayName("deve lancar EntityNotFoundException quando veiculo nao existe")
        void deveLancarExcecaoVeiculoNaoEncontrado() {
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
            when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criarOrdemServico(
                    new CriarOrdemServicoRequestDTO(clienteId, veiculoId, null)))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Veiculo");
        }
    }

    @Nested
    @DisplayName("avancarStatus")
    class AvancarStatus {

        @Test
        @DisplayName("deve avancar de RECEBIDA para EM_DIAGNOSTICO")
        void deveAvancarDeRecebidaParaEmDiagnostico() {
            os.setStatus(StatusOrdemServico.RECEBIDA);
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
            when(ordemServicoRepository.save(any())).thenReturn(os);

            var result = service.avancarStatus(osId);

            assertThat(result.status()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        }

        @Test
        @DisplayName("deve avancar por todos os status ate ENTREGUE")
        void deveAvancarPorTodosOsStatus() {
            StatusOrdemServico[] fluxo = {
                StatusOrdemServico.RECEBIDA,
                StatusOrdemServico.EM_DIAGNOSTICO,
                StatusOrdemServico.AGUARDANDO_APROVACAO,
                StatusOrdemServico.EM_EXECUCAO,
                StatusOrdemServico.FINALIZADA,
                StatusOrdemServico.ENTREGUE
            };
            for (int i = 0; i < fluxo.length - 1; i++) {
                os.setStatus(fluxo[i]);
                when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
                when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                var result = service.avancarStatus(osId);
                assertThat(result.status()).isEqualTo(fluxo[i + 1]);
            }
        }

        @Test
        @DisplayName("deve lancar excecao ao tentar avancar OS ja ENTREGUE")
        void deveLancarExcecaoAoAvancarEntregue() {
            os.setStatus(StatusOrdemServico.ENTREGUE);
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.avancarStatus(osId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("entregue");
        }
    }

    @Nested
    @DisplayName("adicionarPeca")
    class AdicionarPeca {

        @Test
        @DisplayName("deve adicionar peca, reservar estoque e recalcular total")
        void deveAdicionarPecaEReservar() {
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.of(peca));
            when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = service.adicionarPeca(osId, new AdicionarPecaRequestDTO(pecaId, 2));

            verify(estoqueService).reservar(eq(pecaId), eq(2), anyString());
            assertThat(result.pecas()).hasSize(1);
            assertThat(result.valorTotal()).isEqualByComparingTo("119.80");
        }

        @Test
        @DisplayName("deve lancar excecao ao adicionar peca em OS FINALIZADA")
        void deveLancarExcecaoOsFinalizada() {
            os.setStatus(StatusOrdemServico.FINALIZADA);
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.adicionarPeca(osId, new AdicionarPecaRequestDTO(pecaId, 1)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("FINALIZADA");
        }

        @Test
        @DisplayName("deve lancar EntityNotFoundException quando peca nao existe")
        void deveLancarExcecaoPecaNaoEncontrada() {
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
            when(pecaInsumoRepository.findById(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.adicionarPeca(osId, new AdicionarPecaRequestDTO(pecaId, 1)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("removerPeca")
    class RemoverPeca {

        @Test
        @DisplayName("deve remover peca, cancelar reserva e recalcular total")
        void deveRemoverPecaECancelarReserva() {
            var osPecaId = UUID.randomUUID();
            var osPeca   = OsPeca.builder()
                    .id(osPecaId).ordemServico(os).peca(peca)
                    .quantidade(2).precoVendaAplicado(new BigDecimal("59.90"))
                    .build();
            os.getPecas().add(osPeca);
            os.setValorTotal(new BigDecimal("119.80"));

            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
            when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = service.removerPeca(osId, osPecaId);

            verify(estoqueService).cancelarReserva(eq(pecaId), eq(2), anyString());
            assertThat(result.pecas()).isEmpty();
            assertThat(result.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve lancar EntityNotFoundException ao remover osPeca inexistente")
        void deveLancarExcecaoOsPecaNaoEncontrada() {
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.removerPeca(osId, UUID.randomUUID()))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("peca");
        }
    }

    @Nested
    @DisplayName("adicionarServico")
    class AdicionarServico {

        @Test
        @DisplayName("deve adicionar servico e recalcular total")
        void deveAdicionarServico() {
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
            when(servicoCatalogoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
            when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = service.adicionarServico(osId, new AdicionarServicoRequestDTO(servicoId));

            assertThat(result.servicos()).hasSize(1);
            assertThat(result.valorTotal()).isEqualByComparingTo("120.00");
        }

        @Test
        @DisplayName("deve lancar excecao ao adicionar servico em OS ENTREGUE")
        void deveLancarExcecaoOsEntregue() {
            os.setStatus(StatusOrdemServico.ENTREGUE);
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.adicionarServico(osId, new AdicionarServicoRequestDTO(servicoId)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ENTREGUE");
        }
    }

    @Nested
    @DisplayName("removerServico")
    class RemoverServico {

        @Test
        @DisplayName("deve remover servico e recalcular total")
        void deveRemoverServico() {
            var osServicoId = UUID.randomUUID();
            var osServico   = OsServico.builder()
                    .id(osServicoId).ordemServico(os).servico(servico)
                    .precoMaoDeObraAplicado(new BigDecimal("120.00"))
                    .build();
            os.getServicos().add(osServico);
            os.setValorTotal(new BigDecimal("120.00"));

            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));
            when(ordemServicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = service.removerServico(osId, osServicoId);

            assertThat(result.servicos()).isEmpty();
            assertThat(result.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve lancar EntityNotFoundException ao remover osServico inexistente")
        void deveLancarExcecaoOsServicoNaoEncontrado() {
            when(ordemServicoRepository.findWithDetailsById(osId)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.removerServico(osId, UUID.randomUUID()))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("servico");
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("deve listar todas as OS quando status e nulo")
        void deveListarTodasAsOs() {
            when(ordemServicoRepository.findAll()).thenReturn(List.of(os));

            assertThat(service.listar(null)).hasSize(1);
            verify(ordemServicoRepository).findAll();
            verify(ordemServicoRepository, never()).findByStatus(any());
        }

        @Test
        @DisplayName("deve filtrar por status quando informado")
        void deveFiltrarPorStatus() {
            when(ordemServicoRepository.findByStatus(StatusOrdemServico.RECEBIDA)).thenReturn(List.of(os));

            var result = service.listar(StatusOrdemServico.RECEBIDA);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo(StatusOrdemServico.RECEBIDA);
            verify(ordemServicoRepository, never()).findAll();
        }
    }

    @Nested
    @DisplayName("ServicoCatalogo")
    class ServicoCatalogoTests {

        @Test
        @DisplayName("deve criar servico no catalogo")
        void deveCriarServico() {
            when(servicoCatalogoRepository.save(any())).thenReturn(servico);

            var result = service.criarServico(new ServicoCatalogoRequestDTO(
                    "Troca de Oleo", null, new BigDecimal("120.00")));

            assertThat(result.nome()).isEqualTo("Troca de Oleo");
        }

        @Test
        @DisplayName("deve atualizar servico existente")
        void deveAtualizarServico() {
            when(servicoCatalogoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
            when(servicoCatalogoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var result = service.atualizarServico(servicoId, new ServicoCatalogoRequestDTO(
                    "Troca de Oleo Atualizado", "desc", new BigDecimal("150.00")));

            assertThat(result.nome()).isEqualTo("Troca de Oleo Atualizado");
            assertThat(result.precoMaoDeObra()).isEqualByComparingTo("150.00");
        }

        @Test
        @DisplayName("deve lancar EntityNotFoundException ao atualizar servico inexistente")
        void deveLancarExcecaoAoAtualizar() {
            when(servicoCatalogoRepository.findById(servicoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizarServico(servicoId,
                    new ServicoCatalogoRequestDTO("x", null, BigDecimal.ONE)))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("deve listar servicos do catalogo")
        void deveListarServicos() {
            when(servicoCatalogoRepository.findAll()).thenReturn(List.of(servico));

            assertThat(service.listarServicos()).hasSize(1);
        }
    }
}
