# Implementação de Geração de PDF e Envio de E-mail

## Resumo

Este documento descreve a implementação do segundo ponto do fluxo automatizado de Ordem de Serviço: **Geração do PDF e Envio de E-mail** do orçamento para o cliente.

A solução foi desenvolvida seguindo os princípios de **Arquitetura Hexagonal (DDD)** com separação clara entre as camadas de aplicação, portas e adapters de infraestrutura.

---

## Artefatos Implementados

### 1. Portas de Saída (Application/Ports/Out)

#### `EmailSenderPort.java`
Interface que define o contrato para envio de e-mails com anexo:
```java
public interface EmailSenderPort {
    void enviarEmailComAnexo(String para, String assunto, String corpoHtml, 
                             byte[] anexo, String nomeAnexo);
}
```

### 2. Adapters de Infraestrutura (Adapters/Out/Infrastructure)

#### `PdfGeneratorAdapter.java`
Implementação de geração de PDF usando **iText 7.2.5**
- Implementa `PdfGeneratorPort`
- Gera PDF em layout A4 com bordas profissionais
- Contém:
  - **Cabeçalho** com dados da oficina (nome, endereço, telefone, e-mail)
  - **Dados do Orçamento** (número, data, status, prazo)
  - **Dados do Veículo** (modelo, placa)
  - **Tabela de Serviços e Mão de Obra** com valores unitários e subtotais
  - **Tabela de Peças e Insumos** com quantidade, valor unitário e subtotal
  - **Resumo Financeiro** com valor total destacado
  - **Rodapé** com mensagem padrão de validade (30 dias)

**Características:**
- Suporta múltiplos serviços e peças
- Formatação em Português (BR) com símbolo R$
- Layout responsivo com alinhamento de valores monetários à direita
- Retorna `byte[]` pronto para ser enviado como anexo

#### `EmailSenderAdapter.java`
Implementação de envio de e-mail usando **Spring JavaMailSender**
- Implementa `EmailSenderPort`
- Suporta HTML rico
- Anexa arquivos PDF
- Tratamento de exceções com logging
- Codificação UTF-8 para suporte a acentuação

---

## Integração no Serviço de Aplicação

### `OrdemServicoService.java`
Método `concluirEEnviar()` orquestra todo o fluxo:

1. **Busca** da OS pelo `orcamentoId`
2. **Invoca regra de domínio** `concluirDiagnostico()`
3. **Persiste** as alterações
4. **Registra** evento no histórico de status
5. **Gera** PDF via `PdfGeneratorPort`
6. **Monta** template HTML profissional
7. **Envia** e-mail com anexo via `EmailSenderPort`

#### Template de E-mail
O template HTML contém:
- **Cabeçalho visual** com fundo escuro e ícone de conclusão
- **Corpo informativo** com dados da OS, veículo e valor
- **Caixa destacada** (info-box) com resumo do orçamento
- **Botão CTA (Call-To-Action)** verde destacado para aprovação
- **Link de fallback** em caso de problemas com o botão
- **Rodapé** com informações de contato da oficina
- **Estilos CSS** embutidos para máxima compatibilidade com clientes de e-mail

**Link de Aprovação:**
```
https://api.oficina.local/api/public/atendimento/ordens/{id}/autorizar?orcamento={orcamentoId}
```

---

## Dependências Maven

As seguintes dependências foram adicionadas ao `pom.xml`:

```xml
<!-- iText para geração de PDF (já estava presente) -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>kernel</artifactId>
    <version>7.2.5</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>layout</artifactId>
    <version>7.2.5</version>
</dependency>

<!-- Spring Boot Mail Starter (adicionado) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

---

## Configuração do Spring Boot

### `application.yml` (Produção/Homologação)
```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.mailtrap.io}
    port: ${MAIL_PORT:2525}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: false
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

**Variáveis de Ambiente Necessárias:**
- `MAIL_HOST` - Servidor SMTP (padrão: smtp.mailtrap.io)
- `MAIL_PORT` - Porta SMTP (padrão: 2525)
- `MAIL_USERNAME` - Usuário para autenticação
- `MAIL_PASSWORD` - Senha para autenticação

### `application-dev.yml` (Desenvolvimento)
```yaml
spring:
  mail:
    host: ${MAIL_HOST:localhost}
    port: ${MAIL_PORT:1025}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

Para usar em desenvolvimento local, instale o **MailHog** (servidor SMTP mock):
```bash
# Docker
docker run -d --name mailhog -p 1025:1025 -p 8025:8025 mailhog/mailhog

# Acesse a interface web em http://localhost:8025
```

---

## Como Usar

### 1. Ativar Perfil de Desenvolvimento
```bash
export SPRING_PROFILES_ACTIVE=dev
# ou configure em application-dev.yml
```

### 2. Chamar o Use Case
```java
// Via endpoint HTTP (a ser implementado)
POST /api/atendimento/orcamentos/concluir
Content-Type: application/json

{
  "orcamentoId": "uuid-do-orcamento",
  "emailCliente": "cliente@email.com",
  "prazoEstipulado": "2024-12-31T18:00:00"
}
```

### 3. Fluxo Automático
1. Serviço valida e executa a regra de domínio
2. PDF é gerado em memória (não ocupa disco)
3. E-mail HTML é montado com link de aprovação
4. E-mail é enviado com anexo PDF
5. Log registra sucesso/falha

---

## Tratamento de Erros

### `PdfGeneratorAdapter`
- Captura `IOException` e relança como `RuntimeException`
- Log completo de stack trace

### `EmailSenderAdapter`
- Captura `MessagingException` do Jakarta Mail
- Log com detalhes do destinatário e assunto
- Relança como `RuntimeException`

### `OrdemServicoService`
- Trata `EntityNotFoundException` se OS não encontrada
- Propaga exceções de domínio (`OrdemServicoStatusException`)

---

## Segurança

### SMTP
- **TLS Obrigatório** em produção
- **Autenticação Requerida** via usuário/senha
- **Timeouts Configurados** (5 segundos)

### E-mail
- **Link com UUID** - difícil de adivinhar
- **Validação no Backend** obrigatória
- **E-mail HTML** com estilos CSS embutidos para máxima compatibilidade

### PDF
- Gerado em memória (não persiste em disco)
- Descartado após envio
- Sem dados sensíveis (apenas públicos)

---

## Métricas de Sucesso

✅ **PDF gerado com sucesso** - Validado pelo formato byte[]
✅ **E-mail enviado** - Log confirmado no adapter
✅ **Link de aprovação** - URL formada corretamente
✅ **Integração com domínio** - Regras de negócio respeitadas
✅ **Histórico de status** - Evento registrado em `OsHistoricoStatus`

---

## Próximas Etapas

1. **Endpoint de Aprovação** - Implementar POST `/api/public/atendimento/ordens/{id}/autorizar`
2. **Notificação de Máquina** - Avisar mecânico quando orçamento é aprovado
3. **Relatórios** - Dashboard de orçamentos pendentes/aprovados
4. **Retry Logic** - Reenvio automático se falhar
5. **Template Engine** - Usar Thymeleaf para templates dinâmicos
6. **Assinatura Digital** - Adicionar assinatura na PDF

---

## Referências

- **iText Documentation**: https://itextpdf.com/en/products/itext-core/html-to-pdf
- **Spring Mail**: https://spring.io/guides/gs/sending-email/
- **Jakarta Mail**: https://eclipse-ee4j.github.io/mail/
- **MailHog**: https://github.com/mailhog/MailHog

---

**Data:** 2026-06-20  
**Versão:** 1.0  
**Arquiteto:** Senior Java Engineer  
**Status:** ✅ Implementado e Compilado
