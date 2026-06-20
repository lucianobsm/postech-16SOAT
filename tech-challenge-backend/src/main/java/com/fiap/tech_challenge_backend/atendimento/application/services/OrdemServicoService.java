package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.infrastructure.UsuarioRepository;
import com.fiap.tech_challenge_backend.atendimento.application.dto.ConcluirOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AlterarStatusOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AtualizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AutorizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ConcluirOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.RelatorioOrdensServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.EmailSenderPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PdfGeneratorPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.services.TempoAtendimentoDomainService;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.infrastructure.VeiculoRepository;
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
                   BuscarOrdemServicoUseCase,
                   AtualizarOrdemServicoUseCase,
                   AlterarStatusOrdemServicoUseCase,
                   RelatorioOrdensServicoUseCase,
                   ConcluirOrcamentoUseCase,
                   AutorizarOrdemServicoUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PdfGeneratorPort pdfGeneratorPort;
    private final EmailSenderPort emailSenderPort;

    public OrdemServicoService(OrdemServicoRepositoryPort ordemServicoRepository,
                               OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                               ClienteRepository clienteRepository,
                               VeiculoRepository veiculoRepository,
                               UsuarioRepository usuarioRepository,
                               PdfGeneratorPort pdfGeneratorPort,
                               EmailSenderPort emailSenderPort) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pdfGeneratorPort = pdfGeneratorPort;
        this.emailSenderPort = emailSenderPort;
    }

    // ─────────────────────────────────────────────
    // CriarOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrdemServicoResponseDTO criar(OrdemServicoRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente não encontrado: " + request.clienteId()));

        Veiculo veiculo = veiculoRepository.findById(request.veiculoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Veículo não encontrado: " + request.veiculoId()));

        Usuario mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = usuarioRepository.findById(request.mecanicoId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Mecânico não encontrado: " + request.mecanicoId()));
        }

        OrdemServico os = OrdemServico.builder()
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
    public OrdemServicoResponseDTO buscarPorId(UUID id) {
        return ordemServicoRepository.buscarPorId(id)
                .map(OrdemServicoResponseDTO::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordem de serviço não encontrada: " + id));
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
    public OrdemServicoResponseDTO atualizar(UUID id, OrdemServicoAtualizarRequestDTO request) {
        OrdemServico os = buscarEntidade(id);

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente não encontrado: " + request.clienteId()));

        Veiculo veiculo = veiculoRepository.findById(request.veiculoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Veículo não encontrado: " + request.veiculoId()));

        Usuario mecanico = null;
        if (request.mecanicoId() != null) {
            mecanico = usuarioRepository.findById(request.mecanicoId())
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
    public void remover(UUID id) {
        if (!ordemServicoRepository.existePorId(id)) {
            throw new EntityNotFoundException("Ordem de serviço não encontrada: " + id);
        }
        ordemServicoRepository.remover(id);
    }

    // ─────────────────────────────────────────────
    // AlterarStatusOrdemServicoUseCase
    // ─────────────────────────────────────────────

    @Override
    public OrdemServicoResponseDTO alterarStatus(UUID ordemServicoId,
                                                  StatusOrdemServico novoStatus,
                                                  String usuarioEmail) {
        OrdemServico os = buscarEntidade(ordemServicoId);

        StatusOrdemServico statusAtual = os.getStatus();
        if (statusAtual == novoStatus) {
            return OrdemServicoResponseDTO.from(os);
        }

        Usuario usuario = usuarioEmail != null
                ? usuarioRepository.findByEmail(usuarioEmail).orElse(null)
                : null;

        os.setStatus(novoStatus);
        OrdemServico salva = ordemServicoRepository.salvar(os);

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

        boolean expandHistorico = "historico".equalsIgnoreCase(expand);
        LocalDateTime referencia = LocalDateTime.now();

        Map<UUID, List<OsHistoricoStatus>> historicoPorOrdem = Collections.emptyMap();
        if (expandHistorico) {
            List<UUID> ordemIds = ordens.stream().map(OrdemServico::getId).toList();
            historicoPorOrdem = osHistoricoStatusRepository.buscarPorOrdensServicoOrdenado(ordemIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                            h -> h.getOrdemServico().getId(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));
        }

        Map<UUID, List<OsHistoricoStatus>> finalHistoricoPorOrdem = historicoPorOrdem;

        return ordens.stream()
                .map(os -> {
                    String tempoTotalAtendimento = null;
                    Map<String, String> tempoPorStatus = null;

                    if (expandHistorico) {
                        List<OsHistoricoStatus> historico = finalHistoricoPorOrdem.getOrDefault(os.getId(), List.of());
                        tempoTotalAtendimento = TempoAtendimentoDomainService.calcularTempoTotalFormatado(os, referencia);
                        tempoPorStatus = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(os, historico, referencia);
                    }

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
    public void autorizar(UUID id) {
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

    private OrdemServico buscarEntidade(UUID id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordem de serviço não encontrada: " + id));
    }
}


