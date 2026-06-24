package com.fiap.tech_challenge_backend.ordemservico.application;

import com.fiap.tech_challenge_backend.acesso.infrastructure.repositories.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.ordemservico.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.*;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.ordemservico.infrastructure.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.ordemservico.infrastructure.ServicoCatalogoRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.repositories.VeiculoJpaRepository;
import com.fiap.tech_challenge_backend.estoque.application.EstoqueService;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AtendimentoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoCatalogoRepository servicoCatalogoRepository;
    private final PecaInsumoRepository pecaInsumoRepository;
    private final ClienteJpaRepository clienteRepository;
    private final VeiculoJpaRepository veiculoRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final EstoqueService estoqueService;

    public OrdemServicoResponseDTO criarOrdemServico(CriarOrdemServicoRequestDTO request) {
        var cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado: " + request.clienteId()));
        var veiculo = veiculoRepository.findById(request.veiculoId())
                .orElseThrow(() -> new EntityNotFoundException("Veiculo nao encontrado: " + request.veiculoId()));

        var builder = OrdemServico.builder()
                .cliente(cliente)
                .veiculo(veiculo);

        if (request.mecanicoId() != null) {
            var mecanico = usuarioRepository.findById(request.mecanicoId())
                    .orElseThrow(() -> new EntityNotFoundException("Mecanico nao encontrado: " + request.mecanicoId()));
            builder.mecanico(mecanico);
        }

        var os = ordemServicoRepository.save(builder.build());
        return OrdemServicoResponseDTO.from(os);
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(UUID id) {
        var os = ordemServicoRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de servico nao encontrada: " + id));
        return OrdemServicoResponseDTO.from(os);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listar(StatusOrdemServico status) {
        List<OrdemServico> ordens = status != null
                ? ordemServicoRepository.findByStatus(status)
                : ordemServicoRepository.findAll();
        return ordens.stream().map(OrdemServicoResponseDTO::from).toList();
    }

    public OrdemServicoResponseDTO atribuirMecanico(UUID id, AtribuirMecanicoRequestDTO request) {
        var os = buscarEntidade(id);
        var mecanico = usuarioRepository.findById(request.mecanicoId())
                .orElseThrow(() -> new EntityNotFoundException("Mecanico nao encontrado: " + request.mecanicoId()));
        os.setMecanico(mecanico);
        return OrdemServicoResponseDTO.from(ordemServicoRepository.save(os));
    }

    public OrdemServicoResponseDTO avancarStatus(UUID id) {
        var os = buscarEntidade(id);
        StatusOrdemServico proximo = switch (os.getStatus()) {
            case RECEBIDA             -> StatusOrdemServico.EM_DIAGNOSTICO;
            case EM_DIAGNOSTICO       -> StatusOrdemServico.AGUARDANDO_APROVACAO;
            case AGUARDANDO_APROVACAO -> StatusOrdemServico.EM_EXECUCAO;
            case EM_EXECUCAO          -> StatusOrdemServico.FINALIZADA;
            case FINALIZADA           -> StatusOrdemServico.ENTREGUE;
            case ENTREGUE             -> throw new IllegalArgumentException(
                    "Ordem de servico ja foi entregue e nao pode avancar de status");
        };
        os.setStatus(proximo);
        return OrdemServicoResponseDTO.from(ordemServicoRepository.save(os));
    }

    public OrdemServicoResponseDTO adicionarPeca(UUID osId, AdicionarPecaRequestDTO request) {
        var os = buscarEntidade(osId);
        validarOsEditavel(os);

        var peca = pecaInsumoRepository.findById(request.pecaId())
                .orElseThrow(() -> new EntityNotFoundException("Peca/insumo nao encontrado: " + request.pecaId()));

        estoqueService.reservar(peca.getId(), request.quantidade(), "Reserva OS-" + os.getId());

        os.getPecas().add(OsPeca.builder()
                .ordemServico(os)
                .peca(peca)
                .quantidade(request.quantidade())
                .precoVendaAplicado(peca.getPrecoVenda())
                .build());

        recalcularValorTotal(os);
        return OrdemServicoResponseDTO.from(ordemServicoRepository.save(os));
    }

    public OrdemServicoResponseDTO removerPeca(UUID osId, UUID osPecaId) {
        var os = buscarEntidade(osId);
        validarOsEditavel(os);

        var osPeca = os.getPecas().stream()
                .filter(p -> p.getId().equals(osPecaId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item de peca nao encontrado na OS: " + osPecaId));

        estoqueService.cancelarReserva(osPeca.getPeca().getId(), osPeca.getQuantidade(),
                "Cancelamento reserva OS-" + os.getId());

        os.getPecas().remove(osPeca);
        recalcularValorTotal(os);
        return OrdemServicoResponseDTO.from(ordemServicoRepository.save(os));
    }

    public OrdemServicoResponseDTO adicionarServico(UUID osId, AdicionarServicoRequestDTO request) {
        var os = buscarEntidade(osId);
        validarOsEditavel(os);

        var servico = servicoCatalogoRepository.findById(request.servicoId())
                .orElseThrow(() -> new EntityNotFoundException("Servico nao encontrado: " + request.servicoId()));

        os.getServicos().add(OsServico.builder()
                .ordemServico(os)
                .servico(servico)
                .precoMaoDeObraAplicado(servico.getPrecoMaoDeObra())
                .build());

        recalcularValorTotal(os);
        return OrdemServicoResponseDTO.from(ordemServicoRepository.save(os));
    }

    public OrdemServicoResponseDTO removerServico(UUID osId, UUID osServicoId) {
        var os = buscarEntidade(osId);
        validarOsEditavel(os);

        var osServico = os.getServicos().stream()
                .filter(s -> s.getId().equals(osServicoId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item de servico nao encontrado na OS: " + osServicoId));

        os.getServicos().remove(osServico);
        recalcularValorTotal(os);
        return OrdemServicoResponseDTO.from(ordemServicoRepository.save(os));
    }

    public ServicoCatalogoResponseDTO criarServico(ServicoCatalogoRequestDTO request) {
        var servico = ServicoCatalogo.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .precoMaoDeObra(request.precoMaoDeObra())
                .build();
        return ServicoCatalogoResponseDTO.from(servicoCatalogoRepository.save(servico));
    }

    public ServicoCatalogoResponseDTO atualizarServico(UUID id, ServicoCatalogoRequestDTO request) {
        var servico = servicoCatalogoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servico nao encontrado: " + id));
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setPrecoMaoDeObra(request.precoMaoDeObra());
        return ServicoCatalogoResponseDTO.from(servicoCatalogoRepository.save(servico));
    }

    @Transactional(readOnly = true)
    public ServicoCatalogoResponseDTO buscarServicoPorId(UUID id) {
        return ServicoCatalogoResponseDTO.from(servicoCatalogoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servico nao encontrado: " + id)));
    }

    @Transactional(readOnly = true)
    public List<ServicoCatalogoResponseDTO> listarServicos() {
        return servicoCatalogoRepository.findAll().stream()
                .map(ServicoCatalogoResponseDTO::from).toList();
    }

    private OrdemServico buscarEntidade(UUID id) {
        return ordemServicoRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de servico nao encontrada: " + id));
    }

    private void validarOsEditavel(OrdemServico os) {
        if (os.getStatus() == StatusOrdemServico.FINALIZADA
                || os.getStatus() == StatusOrdemServico.ENTREGUE) {
            throw new IllegalArgumentException(
                    "Nao e possivel modificar uma OS com status " + os.getStatus());
        }
    }

    private void recalcularValorTotal(OrdemServico os) {
        BigDecimal totalPecas = os.getPecas().stream()
                .map(p -> p.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(p.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalServicos = os.getServicos().stream()
                .map(OsServico::getPrecoMaoDeObraAplicado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        os.setValorTotal(totalPecas.add(totalServicos));
    }
}
