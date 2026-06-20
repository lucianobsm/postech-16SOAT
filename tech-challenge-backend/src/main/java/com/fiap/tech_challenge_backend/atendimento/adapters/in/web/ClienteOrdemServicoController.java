package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AutorizarOrdemServicoUseCase;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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
public class ClienteOrdemServicoController {

    private static final Logger log = LoggerFactory.getLogger(ClienteOrdemServicoController.class);

    private final AutorizarOrdemServicoUseCase autorizarOrdemServicoUseCase;

    public ClienteOrdemServicoController(AutorizarOrdemServicoUseCase autorizarOrdemServicoUseCase) {
        this.autorizarOrdemServicoUseCase = autorizarOrdemServicoUseCase;
    }

    /**
     * Endpoint público para o cliente autorizar seu orçamento via link do e-mail.
     * Acesso direto via navegador (GET) - sem necessidade de token JWT.
     *
     * @param id UUID da Ordem de Serviço a ser autorizada
     * @return ResponseEntity com status de sucesso ou erro
     */
    @GetMapping("/{id}/autorizar")
    public ResponseEntity<Map<String, Object>> autorizarOrcamento(@PathVariable UUID id) {
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
}
