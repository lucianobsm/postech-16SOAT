package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.application.ports.UsuarioRepository;
import com.fiap.tech_challenge_backend.atendimento.application.dto.AprovarRejeitarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.ConcluirOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.DeletarOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.PecaOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.ServicoOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrdemServicoClienteRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AlterarStatusOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AtualizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AutorizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ConcluirOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoClienteUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.RelatorioOrdensServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ResponderOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.EmailSenderPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PdfGeneratorPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.ServicoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.services.TempoAtendimentoDomainService;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrdemServicoService
        implements CriarOrdemServicoUseCase,
                   CriarOrdemServicoClienteUseCase,
                   BuscarOrdemServicoUseCase,
                   AtualizarOrdemServicoUseCase,
                   AlterarStatusOrdemServicoUseCase,
                   RelatorioOrdensServicoUseCase,
                   ConcluirOrcamentoUseCase,
                   AutorizarOrdemServicoUseCase,
                   CriarOrcamentoUseCase,
                   BuscarOrcamentoUseCase,
                   ResponderOrcamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PdfGeneratorPort pdfGeneratorPort;
    private final EmailSenderPort emailSenderPort;
    private final ServicoCatalogoRepositoryPort servicoCatalogoRepository;
    private final PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;
    private final IdGeneratorService idGeneratorService;

    public OrdemServicoService(OrdemServicoRepositoryPort ordemServicoRepository,
                               OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                               ClienteRepository clienteRepository,
                               VeiculoRepository veiculoRepository,
                               UsuarioRepository usuarioRepository,
                               PdfGeneratorPort pdfGeneratorPort,
                               EmailSenderPort emailSenderPort,
                               ServicoCatalogoRepositoryPort servicoCatalogoRepository,
                               PecaInsumoCatalogoRepositoryPort pecaInsumoRepository,
                               IdGeneratorService idGeneratorService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pdfGeneratorPort = pdfGeneratorPort;
        this.emailSenderPort = emailSenderPort;
        this.servicoCatalogoRepository = servicoCatalogoRepository;
        this.pecaInsumoRepository = pecaInsumoRepository;
        this.idGeneratorService = idGeneratorService;
    }

    // ─────────────────────────────────────────────
    // CriarOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrdemServicoResponseDTO criar(OrdemServicoRequestDTO request) {
        log.info("Criando nova Ordem de Serviço | Cliente: {} | Veiculo: {} | Mecanico: {}",
                request.clienteId(), request.veiculoId(), request.mecanicoId());

        Cliente cliente = clienteRepository.buscarPorId(request.clienteId())
                .orElseThrow(() -> {
                    log.error("Cliente não encontrado: {}", request.clienteId());
                    return new EntityNotFoundException("Cliente não encontrado: " + request.clienteId());
                });

        Veiculo veiculo = veiculoRepository.buscarPorId(request.veiculoId())
                .orElseThrow(() -> {
                    log.error("Veículo não encontrado: {}", request.veiculoId());
                    return new EntityNotFoundException("Veículo não encontrado: " + request.veiculoId());
                });

        Usuario mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = usuarioRepository.buscarPorId(request.mecanicoId())
                    .orElseThrow(() -> {
                        log.error("Mecânico não encontrado: {}", request.mecanicoId());
                        return new EntityNotFoundException("Mecânico não encontrado: " + request.mecanicoId());
                    });
            log.debug("Mecanico localizado: {}", mecanico.getNome());
        }

        Long novoIdOS = idGeneratorService.gerarIdOrdemServico();

        OrdemServico os = OrdemServico.builder()
                .id(novoIdOS)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotal(BigDecimal.ZERO)
                .dataCriacao(LocalDateTime.now())
                .urgente(Boolean.FALSE)
                .build();

        os.definirUrgente(request.urgente());

        OrdemServico saved = ordemServicoRepository.salvar(os);

        log.info("Ordem de Serviço criada com sucesso | ID: {} | Status: RECEBIDA", saved.getId());

        // Registra evento inicial no histórico (status_origem = null)
        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(saved)
                .statusOrigem(null)
                .statusDestino(StatusOrdemServico.RECEBIDA)
                .build());

        return OrdemServicoResponseDTO.from(saved);
    }

    // ─────────────────────────────────────────────
    // CriarOrdemServicoClienteUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrdemServicoResponseDTO criar(CriarOrdemServicoClienteRequestDTO request) {
        log.info("Criando nova Ordem de Serviço por Cliente | CPF/CNPJ: {} | Placa: {}",
                request.cpfCnpjCliente(), request.placaVeiculo());

        CpfCnpj cpfCnpj = new CpfCnpj(request.cpfCnpjCliente());
        Placa placa = new Placa(request.placaVeiculo());

        Cliente cliente = clienteRepository.buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() -> {
                    log.error("Cliente não encontrado com CPF/CNPJ: {}", request.cpfCnpjCliente());
                    return new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + request.cpfCnpjCliente());
                });

        Veiculo veiculo = veiculoRepository.buscarPorPlaca(placa)
                .orElseThrow(() -> {
                    log.error("Veículo não encontrado com placa: {}", request.placaVeiculo());
                    return new IllegalArgumentException("Veículo não encontrado com placa: " + request.placaVeiculo());
                });

        log.debug("Cliente localizado: {} | Veiculo localizado: {}", cliente.getNome(), veiculo.getModelo());

        Long novoIdOS = idGeneratorService.gerarIdOrdemServico();

        OrdemServico os = OrdemServico.builder()
                .id(novoIdOS)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(null)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotal(BigDecimal.ZERO)
                .queixaCliente(request.queixaCliente())
                .observacoes(request.observacoes())
                .dataCriacao(LocalDateTime.now())
                .urgente(Boolean.FALSE)
                .build();

        os.definirUrgente(request.urgente());

        OrdemServico saved = ordemServicoRepository.salvar(os);

        log.info("Ordem de Serviço criada por Cliente com sucesso | ID: {} | Status: RECEBIDA", saved.getId());

        // Registra evento inicial no histórico (status_origem = null)
        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(saved)
                .statusOrigem(null)
                .statusDestino(StatusOrdemServico.RECEBIDA)
                .build());

        return OrdemServicoResponseDTO.from(saved);
    }

    // ─────────────────────────────────────────────
    // BuscarOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(Long id) {
        log.info("Buscando Ordem de Serviço por ID: {}", id);

        return ordemServicoRepository.buscarPorId(id)
                .map(os -> {
                    log.debug("OS encontrada: {} | Status: {} | Orcamentos carregados: {}",
                            os.getId(), os.getStatus(), os.getOrcamentos().size());

                    os.getOrcamentos().forEach(orc ->
                        log.debug("  Orcamento: {} | Tipo: {} | Servicos: {} | Pecas: {}",
                                orc.getId(), orc.getTipo(), orc.getServicos().size(), orc.getPecas().size())
                    );

                    return OrdemServicoResponseDTO.from(os);
                })
                .orElseThrow(() -> {
                    log.warn("Ordem de Serviço não encontrada para o ID: {}", id);
                    return new EntityNotFoundException("Ordem de serviço não encontrada: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarTodos() {
        return ordemServicoRepository.listarPriorizadas().stream()
                .map(OrdemServicoResponseDTO::from)
                .toList();
    }

    // ─────────────────────────────────────────────
    // AtualizarOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoAtualizarRequestDTO request) {
        OrdemServico os = buscarEntidade(id);

        Cliente cliente = clienteRepository.buscarPorId(request.clienteId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente não encontrado: " + request.clienteId()));

        Veiculo veiculo = veiculoRepository.buscarPorId(request.veiculoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Veículo não encontrado: " + request.veiculoId()));

        Usuario mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = usuarioRepository.buscarPorId(request.mecanicoId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Mecânico não encontrado: " + request.mecanicoId()));
        }

        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setMecanico(mecanico);
        os.setStatus(request.status());
        os.setValorTotal(request.valorTotal());
        os.setDataInicioExecucao(request.dataInicioExecucao());
        os.setDataFinalizacao(request.dataFinalizacao());
        os.definirUrgente(request.urgente());

        return OrdemServicoResponseDTO.from(ordemServicoRepository.salvar(os));
    }

    @Override
    public DeletarOrdemServicoResponseDTO remover(Long id) {
        log.info("Iniciando remoção de Ordem de Serviço | ID: {}", id);

        if (!ordemServicoRepository.existePorId(id)) {
            log.error("Tentativa de remover Ordem de Serviço inexistente | ID: {}", id);
            throw new EntityNotFoundException("Ordem de serviço não encontrada: " + id);
        }

        ordemServicoRepository.remover(id);
        log.info("Ordem de Serviço removida com sucesso | ID: {}", id);

        return DeletarOrdemServicoResponseDTO.sucesso(id);
    }

    // ─────────────────────────────────────────────
    // AlterarStatusOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrdemServicoResponseDTO alterarStatus(Long ordemServicoId,
                                                  StatusOrdemServico novoStatus,
                                                  UUID mecanicoId,
                                                  String usuarioEmail) {
        log.info("Alterando status da OS: {} | Novo Status: {} | Mecanico ID: {} | Usuario: {}",
                ordemServicoId, novoStatus, mecanicoId, usuarioEmail);

        OrdemServico os = buscarEntidade(ordemServicoId);

        StatusOrdemServico statusAtual = os.getStatus();
        if (statusAtual == novoStatus && mecanicoId == null) {
            log.debug("Status e mecânico não foram alterados. Retornando OS atual.");
            return OrdemServicoResponseDTO.from(os);
        }

        Usuario usuario = usuarioEmail != null
                ? usuarioRepository.procuraPorEmail(new Email(usuarioEmail)).orElse(null)
                : null;

        Usuario novoMecanico = null;
        if (mecanicoId != null) {
            novoMecanico = usuarioRepository.buscarPorId(mecanicoId)
                    .orElseThrow(() -> {
                        log.error("Mecânico não encontrado: {}", mecanicoId);
                        return new EntityNotFoundException("Mecânico não encontrado: " + mecanicoId);
                    });
            log.debug("Mecânico localizado: {} | Nome: {}", novoMecanico.getId(), novoMecanico.getNome());
        }

        os.alterarStatus(novoStatus, novoMecanico);
        OrdemServico salva = ordemServicoRepository.salvar(os);

        log.info("Status alterado com sucesso | OS: {} | De: {} | Para: {} | Mecanico: {}",
                ordemServicoId, statusAtual, novoStatus,
                novoMecanico != null ? novoMecanico.getId() : "nenhum");

        OsHistoricoStatus historico = OsHistoricoStatus.builder()
                .ordemServico(salva)
                .statusOrigem(statusAtual)
                .statusDestino(novoStatus)
                .usuario(usuario)
                .build();

        osHistoricoStatusRepository.salvar(historico);
        salva.getHistoricoStatus().add(historico);

        return OrdemServicoResponseDTO.from(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatorioOrdemServicoResponseDTO> listarRelatorio(String expand) {
        return montarRelatorio(ordemServicoRepository.listarPriorizadas(), expand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatorioOrdemServicoResponseDTO> listarRelatorioPorStatus(StatusOrdemServico status, String expand) {
        return montarRelatorio(ordemServicoRepository.listarPorStatus(status), expand);
    }

    private List<RelatorioOrdemServicoResponseDTO> montarRelatorio(List<OrdemServico> ordens, String expand) {
        if (ordens.isEmpty()) {
            return List.of();
        }

        LocalDateTime referencia = LocalDateTime.now();

        List<Long> ordemIds = ordens.stream().map(OrdemServico::getId).toList();
        Map<Long, List<OsHistoricoStatus>> historicoPorOrdem = osHistoricoStatusRepository.buscarPorOrdensServicoOrdenado(ordemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        h -> h.getOrdemServico().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return ordens.stream()
                .map(os -> {
                    List<OsHistoricoStatus> historico = historicoPorOrdem.getOrDefault(os.getId(), List.of());
                    String tempoTotalAtendimento = TempoAtendimentoDomainService.calcularTempoTotalFormatado(os, referencia);
                    Map<String, String> tempoPorStatus = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(os, historico, referencia);

                    return new RelatorioOrdemServicoResponseDTO(
                            os.getId(),
                            os.getCliente().getNome(),
                            os.getStatus().name(),
                            os.getUrgente(),
                            tempoTotalAtendimento,
                            tempoPorStatus
                    );
                })
                .toList();
    }

    // ─────────────────────────────────────────────
    // ConcluirOrcamentoUseCase
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public void concluirEEnviar(ConcluirOrcamentoRequestDTO request) {
        OrdemServico os = ordemServicoRepository.buscarPorOrcamentoId(request.orcamentoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordem de serviço não encontrada para o orçamento: " + request.orcamentoId()));

        StatusOrdemServico statusAnterior = os.getStatus();

        os.concluirDiagnostico(request.orcamentoId(), request.prazoEstipulado());

        OrdemServico salva = ordemServicoRepository.salvar(os);

        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(salva)
                .statusOrigem(statusAnterior)
                .statusDestino(StatusOrdemServico.AGUARDANDO_APROVACAO)
                .build());

        OsOrcamento orcamentoConcluido = salva.getOrcamentos().stream()
                .filter(o -> o.getId().equals(request.orcamentoId()))
                .findFirst()
                .orElseThrow();

        byte[] documento = pdfGeneratorPort.gerarDocumentoTexto(orcamentoConcluido);

        String corpoHtml = montarTemplateEmailAprovacao(salva, orcamentoConcluido);

        emailSenderPort.enviarEmailComAnexo(
                request.emailCliente(),
                String.format("Seu orçamento está pronto para aprovação - OS #%s", salva.getId()),
                corpoHtml,
                documento,
                String.format("orcamento_os_%s.pdf", salva.getId())
        );

        log.info("Orçamento finalizado e e-mail enviado para: {} | OS: {} | Orçamento: {}",
                request.emailCliente(), salva.getId(), orcamentoConcluido.getId());
    }

    // ─────────────────────────────────────────────
    // AutorizarOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public void autorizar(Long id) {
        OrdemServico os = buscarEntidade(id);

        StatusOrdemServico statusAnterior = os.getStatus();

        os.autorizarPeloCliente();

        OrdemServico salva = ordemServicoRepository.salvar(os);

        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(salva)
                .statusOrigem(statusAnterior)
                .statusDestino(StatusOrdemServico.EM_EXECUCAO)
                .usuario(null)
                .build());

        log.info("Ordem de serviço autorizada pelo cliente. OS: {} | Status anterior: {} | Status novo: {}",
                id, statusAnterior, StatusOrdemServico.EM_EXECUCAO);
    }

    // ─────────────────────────────────────────────
    // Métodos Privados - Template de E-mail
    // ─────────────────────────────────────────────

    private String montarTemplateEmailAprovacao(OrdemServico os, OsOrcamento orcamento) {
        String linkAprovacao = String.format(
                "https://api.oficina.local/api/public/atendimento/ordens/%s/autorizar?orcamento=%s",
                os.getId(), orcamento.getId()
        );

        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "    .header h1 { margin: 0; font-size: 24px; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }" +
                "    .content p { margin: 10px 0; }" +
                "    .info-box { background-color: #fff; padding: 15px; border-left: 4px solid #3498db; margin: 15px 0; }" +
                "    .info-box strong { color: #2c3e50; }" +
                "    .cta-button { display: inline-block; margin: 20px 0; padding: 15px 40px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; }" +
                "    .cta-button:hover { background-color: #229954; }" +
                "    .footer { margin-top: 20px; padding-top: 20px; border-top: 1px solid #bdc3c7; font-size: 12px; color: #7f8c8d; text-align: center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>Seu Orçamento Está Pronto!</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá,</p>" +
                "      <p>Temos o prazer de informar que <strong>o diagnóstico do seu veículo foi concluído</strong> e seu orçamento está pronto para análise e aprovação.</p>" +
                "      <div class=\"info-box\">" +
                "        <p><strong>Número da Ordem de Serviço:</strong> " + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + os.getVeiculo().getModelo() + "</p>" +
                "        <p><strong>Placa:</strong> " + os.getVeiculo().getPlaca() + "</p>" +
                "        <p><strong>Valor Total do Orçamento:</strong> <strong style=\"color: #27ae60; font-size: 18px;\">R$ " + String.format("%.2f", orcamento.getValorTotal()) + "</strong></p>" +
                "      </div>" +
                "      <p>O arquivo em anexo contém todos os detalhes do orçamento, incluindo:</p>" +
                "      <ul>" +
                "        <li>Lista de serviços e mão de obra</li>" +
                "        <li>Peças e insumos necessários</li>" +
                "        <li>Prazo estimado para conclusão</li>" +
                "      </ul>" +
                "      <p><strong>Para autorizar o início dos trabalhos, clique no botão abaixo:</strong></p>" +
                "      <center>" +
                "        <a href=\"" + linkAprovacao + "\" class=\"cta-button\">Aprovar Orçamento</a>" +
                "      </center>" +
                "      <p>Ou copie e cole este link em seu navegador:</p>" +
                "      <p><small>" + linkAprovacao + "</small></p>" +
                "      <p>Se tiver dúvidas sobre o orçamento, não hesite em entrar em contato conosco através do telefone ou e-mail.</p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>Oficina Mecânica Premium<br>" +
                "      Telefone: (11) 3000-0000<br>" +
                "      E-mail: contato@oficina.com.br</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Por favor, não responda.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    // ─────────────────────────────────────────────
    // CriarOrcamentoUseCase
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public OrcamentoResponseDTO criar(Long ordemServicoId, CriarOrcamentoRequestDTO request) {
        log.info("Criando orçamento | OS: {} | Tipo: {} | Prazo: {} | Serviços: {} | Peças: {}",
                ordemServicoId, request.tipo(), request.prazoEstipulado(),
                request.servicos().size(), request.pecas().size());

        OsOrcamento orcamentoCriado = criarOrcamentoInterno(ordemServicoId, request);

        log.info("Orçamento criado com sucesso | ID: {} | Tipo: {} | Valor: {} | Data Criação: {} | OS Status: {}",
                orcamentoCriado.getId(), orcamentoCriado.getTipo(), orcamentoCriado.getValorTotal(),
                orcamentoCriado.getDataCriacao());

        // Enviar email fora da transação - em um contexto separado
        try {
            enviarEmailOrcamentoAssincrono(ordemServicoId, orcamentoCriado.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar email de orçamento (não crítico) | OS: {} | Erro: {}",
                    ordemServicoId, e.getMessage());
        }

        return OrcamentoResponseDTO.from(orcamentoCriado);
    }

    @Transactional
    private OsOrcamento criarOrcamentoInterno(Long ordemServicoId, CriarOrcamentoRequestDTO request) {
        OrdemServico os = buscarEntidade(ordemServicoId);

        Long novoIdOrcamento = idGeneratorService.gerarIdOrcamento();

        OsOrcamento novoOrcamento = OsOrcamento.builder()
                .id(novoIdOrcamento)
                .ordemServico(os)
                .tipo(request.tipo())
                .status(StatusOrcamento.PENDENTE)
                .prazoEstipulado(request.prazoEstipulado() != null
                        ? java.time.LocalDateTime.now().plusDays(5)
                        : null)
                .build();

        // Buscar e adicionar serviços com preços atualizados do catálogo
        for (ServicoOrcamentoRequestDTO servicoReq : request.servicos()) {
            ServicoCatalogo servico = servicoCatalogoRepository.buscarPorId(servicoReq.servicoId())
                    .orElseThrow(() -> {
                        log.error("Serviço não encontrado: {}", servicoReq.servicoId());
                        return new EntityNotFoundException("Serviço não encontrado: " + servicoReq.servicoId());
                    });

            log.debug("Adicionando serviço ao orçamento | Serviço: {} | Preço Atual: {}",
                    servico.getNome(), servico.getPrecoMaoDeObra());

            OsServico osServico = OsServico.builder()
                    .orcamento(novoOrcamento)
                    .ordemServicoId(os.getId())
                    .servico(servico)
                    .precoMaoDeObraAplicado(servico.getPrecoMaoDeObra())
                    .build();

            novoOrcamento.adicionarServico(osServico);
        }

        // Buscar e adicionar peças com preços atualizados do estoque
        for (PecaOrcamentoRequestDTO pecaReq : request.pecas()) {
            PecaInsumo peca = pecaInsumoRepository.buscarPorId(pecaReq.pecaId())
                    .orElseThrow(() -> {
                        log.error("Peça não encontrada: {}", pecaReq.pecaId());
                        return new EntityNotFoundException("Peça não encontrada: " + pecaReq.pecaId());
                    });

            log.debug("Adicionando peça ao orçamento | Peça: {} | Qtd: {} | Preço Unit: {}",
                    peca.getNome(), pecaReq.quantidade(), peca.getPrecoVenda());

            OsPeca osPeca = OsPeca.builder()
                    .orcamento(novoOrcamento)
                    .ordemServicoId(os.getId())
                    .peca(peca)
                    .quantidade(pecaReq.quantidade())
                    .precoVendaAplicado(peca.getPrecoVenda())
                    .build();

            novoOrcamento.adicionarPeca(osPeca);
        }

        os.adicionarOrcamento(novoOrcamento);
        OrdemServico salva = ordemServicoRepository.salvar(os);

        return salva.getOrcamentos().get(salva.getOrcamentos().size() - 1);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    private void enviarEmailOrcamentoAssincrono(Long ordemServicoId, Long orcamentoId) {
        OrdemServico os = buscarEntidade(ordemServicoId);

        OsOrcamento orcamento = os.getOrcamentos().stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Orçamento não encontrado: " + orcamentoId));

        enviarEmailOrcamento(os, orcamento);
    }

    private void enviarEmailOrcamento(OrdemServico os, OsOrcamento orcamento) {
        validarEmailClienteOuLancarExcecao(os);

        String emailCliente = os.getCliente().getUsuario().getEmail().toString();

        log.info("Enviando email de orçamento | OS: {} | Orcamento: {} | Cliente: {}",
                os.getId(), orcamento.getId(), emailCliente);

        try {
            String linkAprovacao = String.format(
                    "http://localhost:8080/api/public/atendimento/ordens/%s/orcamentos/%s/status",
                    os.getId(), orcamento.getId()
            );

            byte[] documento = pdfGeneratorPort.gerarDocumentoTexto(orcamento);

            String corpoHtml = montarTemplateEmailOrcamento(os, orcamento, linkAprovacao);

            emailSenderPort.enviarEmailComAnexo(
                    emailCliente,
                    String.format("Seu orçamento está pronto para análise - OS #%s", os.getId()),
                    corpoHtml,
                    documento,
                    String.format("orcamento_os_%s.pdf", os.getId())
            );

            log.info("Email de orçamento enviado com sucesso | OS: {} | Destinatário: {}",
                    os.getId(), emailCliente);

        } catch (Exception e) {
            log.error("Erro ao enviar email de orçamento | OS: {} | Email: {} | Erro: {}",
                    os.getId(), emailCliente, e.getMessage(), e);
            throw new RuntimeException(
                    "Falha ao enviar email de orçamento para: " + emailCliente + ". Motivo: " + e.getMessage(), e);
        }
    }

    private void validarEmailClienteOuLancarExcecao(OrdemServico os) {
        if (os.getCliente() == null) {
            throw new IllegalArgumentException("Ordem de serviço não possui cliente associado");
        }

        if (os.getCliente().getUsuario() == null) {
            throw new IllegalArgumentException(
                    "Cliente '" + os.getCliente().getNome() + "' não possui usuário cadastrado. " +
                    "Impossível enviar orçamento por email.");
        }

        if (os.getCliente().getUsuario().getEmail() == null) {
            throw new IllegalArgumentException(
                    "Cliente '" + os.getCliente().getNome() + "' não possui email cadastrado. " +
                    "Impossível enviar orçamento por email.");
        }
    }

    private String montarTemplateEmailOrcamento(OrdemServico os, OsOrcamento orcamento, String linkAprovacao) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; }" +
                "    .info-box { background-color: #fff; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; }" +
                "    .button { display: inline-block; padding: 12px 30px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                "    .button-reject { background-color: #e74c3c; }" +
                "    .footer { font-size: 12px; color: #7f8c8d; text-align: center; margin-top: 20px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>Seu Orçamento Está Pronto!</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá <strong>" + os.getCliente().getNome() + "</strong>,</p>" +
                "      <p>Temos o prazer de informar que <strong>o diagnóstico do seu veículo foi concluído</strong> e seu orçamento está pronto para análise e aprovação.</p>" +
                "      <div class=\"info-box\">" +
                "        <p><strong>Número da OS:</strong> #" + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + os.getVeiculo().getModelo() + " - Placa: " + os.getVeiculo().getPlaca() + "</p>" +
                "        <p><strong>Valor Total:</strong> <strong style=\"color: #27ae60; font-size: 18px;\">R$ " + String.format("%.2f", orcamento.getValorTotal()) + "</strong></p>" +
                "        <p><strong>Prazo Estimado:</strong> " + (orcamento.getPrazoEstipulado() != null ? orcamento.getPrazoEstipulado() : "A ser confirmado") + "</p>" +
                "      </div>" +
                "      <p>O arquivo em anexo <strong>orcamento_os_" + os.getId() + ".pdf</strong> contém todos os detalhes:</p>" +
                "      <ul>" +
                "        <li>Serviços inclusos na manutenção</li>" +
                "        <li>Peças e insumos necessários</li>" +
                "        <li>Custos detalhados</li>" +
                "        <li>Prazo de execução</li>" +
                "      </ul>" +
                "      <p style=\"text-align: center; margin: 30px 0;\">" +
                "        <a href=\"" + linkAprovacao + "?status=APROVADO\" class=\"button\">✓ APROVAR ORÇAMENTO</a>" +
                "      </p>" +
                "      <p style=\"text-align: center;\">" +
                "        <a href=\"" + linkAprovacao + "?status=REJEITADO\" class=\"button button-reject\">✗ REJEITAR ORÇAMENTO</a>" +
                "      </p>" +
                "      <p style=\"font-size: 12px; color: #95a5a6; text-align: center;\"><small>Ou copie e cole este link no seu navegador: " + linkAprovacao + "</small></p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p><strong>Oficina Mecânica Premium</strong></p>" +
                "      <p>Telefone: (11) 3000-0000 | Email: contato@oficina.com.br</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Por favor, não responda direto neste endereço.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    // ─────────────────────────────────────────────
    // BuscarOrcamentoUseCase
    // ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public OrcamentoResponseDTO buscarPorId(Long ordemServicoId, Long orcamentoId) {
        log.info("Buscando orçamento | OS: {} | Orcamento: {}", ordemServicoId, orcamentoId);

        OrdemServico os = buscarEntidade(ordemServicoId);

        OsOrcamento orcamento = os.getOrcamentos().stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Orçamento não encontrado: {} na OS: {}", orcamentoId, ordemServicoId);
                    return new EntityNotFoundException(
                            "Orçamento não encontrado: " + orcamentoId + " na Ordem de Serviço: " + ordemServicoId);
                });

        log.debug("Orçamento encontrado | ID: {} | Tipo: {} | Status: {} | Valor: {}",
                orcamento.getId(), orcamento.getTipo(), orcamento.getStatus(), orcamento.getValorTotal());

        return OrcamentoResponseDTO.from(orcamento);
    }

    // ─────────────────────────────────────────────
    // ResponderOrcamentoUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrcamentoResponseDTO responder(Long ordemServicoId, Long orcamentoId, AprovarRejeitarOrcamentoRequestDTO request) {
        log.info("Respondendo orçamento | OS: {} | Orcamento: {} | Status: {}",
                ordemServicoId, orcamentoId, request.status());

        OrdemServico os = buscarEntidade(ordemServicoId);

        if (request.status() == StatusOrcamento.APROVADO) {
            os.aprovarOrcamento(orcamentoId);
            log.info("Orçamento aprovado | Orcamento: {} | OS Status: {} | Valor Acumulado: {}",
                    orcamentoId, os.getStatus(), os.getValorTotalAcumulado());
        } else if (request.status() == StatusOrcamento.REJEITADO) {
            os.rejeitarOrcamento(orcamentoId);
            log.info("Orçamento rejeitado | Orcamento: {}", orcamentoId);
        }

        OrdemServico salva = ordemServicoRepository.salvar(os);

        OsOrcamento orcamentoAtualizado = salva.getOrcamentos().stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow();

        return OrcamentoResponseDTO.from(orcamentoAtualizado);
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordem de serviço não encontrada: " + id));
    }
}


