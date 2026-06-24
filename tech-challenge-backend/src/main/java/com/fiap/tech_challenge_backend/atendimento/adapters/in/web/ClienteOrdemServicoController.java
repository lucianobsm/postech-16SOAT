package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.application.dto.AprovarRejeitarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AutorizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ResponderOrcamentoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador público dedicado às operações do cliente.
 * Processa aprovações de orçamentos através de links seguros via e-mail.
 * Camada: Adapters/In (Hexagonal Architecture)
 *
 * SEGURANÇA: Este controlador é acessível sem autenticação JWT.
 * O UUID da ordem de serviço funciona como token de segurança (guessable = 0).
 */
@RestController
@RequestMapping("/api/public/atendimento/ordens")
@Tag(name = "Atendimento - Ordens do Cliente", description = "Endpoints públicos para cliente responder orçamentos via e-mail")
public class ClienteOrdemServicoController {

    private static final Logger log = LoggerFactory.getLogger(ClienteOrdemServicoController.class);

    private final AutorizarOrdemServicoUseCase autorizarOrdemServicoUseCase;
    private final ResponderOrcamentoUseCase responderOrcamentoUseCase;

    public ClienteOrdemServicoController(AutorizarOrdemServicoUseCase autorizarOrdemServicoUseCase,
                                         ResponderOrcamentoUseCase responderOrcamentoUseCase) {
        this.autorizarOrdemServicoUseCase = autorizarOrdemServicoUseCase;
        this.responderOrcamentoUseCase = responderOrcamentoUseCase;
    }

    /**
     * Endpoint público para o cliente autorizar seu orçamento via link do e-mail.
     * Acesso direto via navegador (GET) - sem necessidade de token JWT.
     *
     * @param id UUID da Ordem de Serviço a ser autorizada
     * @return ResponseEntity com status de sucesso ou erro
     */
    @GetMapping("/{id}/autorizar")
    public ResponseEntity<Map<String, Object>> autorizarOrcamento(@PathVariable Long id) {
        try {
            log.info("Autorização de orçamento solicitada pelo cliente. OS: {}", id);

            autorizarOrdemServicoUseCase.autorizar(id);

            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Seu orçamento foi autorizado com sucesso!");
            resposta.put("ordemServicoId", id);
            resposta.put("proximoPasso", "Em breve nossos mecânicos iniciarão os trabalhos no seu veículo.");
            resposta.put("statusAtual", "EM_EXECUCAO");

            log.info("Orçamento autorizado com sucesso. OS: {}", id);

            return ResponseEntity.ok(resposta);

        } catch (EntityNotFoundException e) {
            log.warn("Tentativa de autorizar OS inexistente. ID: {}", id);

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Ordem de Serviço não encontrada");
            erro.put("mensagem", "O código da ordem de serviço fornecido não existe em nosso sistema.");
            erro.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);

        } catch (RuntimeException e) {
            log.warn("Erro ao autorizar orçamento. OS: {} | Mensagem: {}", id, e.getMessage());

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Erro ao processar autorização");
            erro.put("mensagem", e.getMessage() != null
                ? e.getMessage()
                : "Não foi possível autorizar este orçamento. Verifique se o link está correto ou entre em contato conosco.");
            erro.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
        }
    }

    @PatchMapping("/{id}/orcamentos/{orcamentoId}/status")
    @Operation(summary = "Cliente aprova ou rejeita um orçamento")
    public ResponseEntity<Map<String, Object>> responderOrcamento(@PathVariable Long id,
                                                                  @PathVariable Long orcamentoId,
                                                                  @Valid @RequestBody AprovarRejeitarOrcamentoRequestDTO request) {
        try {
            log.info("Cliente respondendo orçamento | OS: {} | Orcamento: {} | Status: {}", id, orcamentoId, request.status());

            OrcamentoResponseDTO orcamento = responderOrcamentoUseCase.responder(id, orcamentoId, request);

            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Sua resposta foi registrada com sucesso!");
            resposta.put("orcamento", orcamento);

            if ("APROVADO".equals(request.status().toString())) {
                resposta.put("proximoPasso", "Seu orçamento foi aprovado! Em breve iniciaremos os trabalhos.");
            } else {
                resposta.put("proximoPasso", "Seu orçamento foi rejeitado. Entre em contato para discutir outras opções.");
            }

            log.info("Orçamento respondido com sucesso | OS: {} | Status: {}", id, request.status());

            return ResponseEntity.ok(resposta);

        } catch (EntityNotFoundException e) {
            log.warn("Tentativa de responder orçamento inexistente. OS: {} | Orcamento: {}", id, orcamentoId);

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Orçamento não encontrado");
            erro.put("mensagem", "O orçamento fornecido não existe ou não pertence a esta ordem de serviço.");
            erro.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);

        } catch (IllegalArgumentException e) {
            log.warn("Erro ao responder orçamento | OS: {} | Mensagem: {}", id, e.getMessage());

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Operação inválida");
            erro.put("mensagem", e.getMessage());
            erro.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);

        } catch (RuntimeException e) {
            log.warn("Erro ao processar resposta de orçamento | OS: {} | Erro: {}", id, e.getMessage());

            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Erro ao processar resposta");
            erro.put("mensagem", "Não foi possível registrar sua resposta. Tente novamente ou entre em contato conosco.");
            erro.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
        }
    }
}
