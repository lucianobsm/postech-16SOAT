package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

public interface EmailSenderPort {

    void enviarEmailComAnexo(String para, String assunto, String corpoHtml, byte[] anexo, String nomeAnexo);
}
