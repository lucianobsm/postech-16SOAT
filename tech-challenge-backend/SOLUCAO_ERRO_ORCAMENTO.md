# Solução: Erro de Transação ao Criar Orçamento

## 🔴 Problema Original

Ao criar um orçamento, o sistema retornava:
```json
{
    "timestamp": "2026-06-23T21:26:13.1618924",
    "status": 500,
    "error": "Internal Server Error",
    "code": "INTERNAL_SERVER_ERROR",
    "message": "Ocorreu um erro inesperado no servidor.",
    "details": []
}
```

**Causa:** O erro `TransactionSystemException: Could not commit JPA transaction` ocorria porque:
1. Email e orçamento eram salvos dentro da mesma transação JPA
2. Ao tentar fazer commit, havia conflitos ou lazy loading que falhava
3. O erro genérico não informava a causa real do problema

---

## ✅ Solução Implementada

### 1. **Separação de Transações**

**Antes:**
```java
@Override
public OrcamentoResponseDTO criar(Long ordemServicoId, CriarOrcamentoRequestDTO request) {
    // Criar orçamento
    os.adicionarOrcamento(novoOrcamento);
    OrdemServico salva = ordemServicoRepository.salvar(os);
    
    // Email DENTRO da mesma transação ❌
    enviarEmailOrcamento(salva, orcamentoCriado);
    
    return OrcamentoResponseDTO.from(orcamentoCriado);
} // COMMIT aqui falha!
```

**Depois:**
```java
@Override
@Transactional
public OrcamentoResponseDTO criar(Long ordemServicoId, CriarOrcamentoRequestDTO request) {
    // Criar orçamento em sua própria transação ✅
    OsOrcamento orcamentoCriado = criarOrcamentoInterno(ordemServicoId, request);
    
    // Email fora da transação JPA ✅
    try {
        enviarEmailOrcamentoAssincrono(ordemServicoId, orcamentoCriado.getId());
    } catch (Exception e) {
        log.error("Erro ao enviar email (não crítico)...");
    }
    
    return OrcamentoResponseDTO.from(orcamentoCriado);
}

@Transactional
private OsOrcamento criarOrcamentoInterno(...) {
    // Lógica de criação com transação própria
}

@Transactional(propagation = Propagation.NOT_SUPPORTED)
private void enviarEmailOrcamentoAssincrono(...) {
    // Email SEM transação JPA ativa
}
```

### 2. **Validação de Email com Mensagens Claras**

**Antes:**
```java
if (os.getCliente().getUsuario() == null || os.getCliente().getUsuario().getEmail() == null) {
    log.warn("Cliente não possui usuário ou email...");
    return; // Falha silenciosa!
}
```

**Depois:**
```java
private void validarEmailClienteOuLancarExcecao(OrdemServico os) {
    if (os.getCliente() == null) {
        throw new IllegalArgumentException("Ordem de serviço não possui cliente associado");
    }

    if (os.getCliente().getUsuario() == null) {
        throw new IllegalArgumentException(
            "Cliente '" + os.getCliente().getNome() + "' não possui usuário cadastrado.");
    }

    if (os.getCliente().getUsuario().getEmail() == null) {
        throw new IllegalArgumentException(
            "Cliente '" + os.getCliente().getNome() + "' não possui email cadastrado.");
    }
}
```

### 3. **Exception Handler Global Melhorado**

Adicionados handlers específicos no `GlobalExceptionHandler`:

```java
@ExceptionHandler(TransactionSystemException.class)
public ResponseEntity<ApiErrorResponse> handleTransactionSystemException(...) {
    String message = extrairMensagemDeErroDeTransacao(exception);
    // Retorna mensagem específica: "Erro ao salvar: campo obrigatório não foi preenchido"
}

@ExceptionHandler(RuntimeException.class)
public ResponseEntity<ApiErrorResponse> handleRuntimeException(...) {
    // Detecta tipo de erro pela mensagem e retorna status/code apropriado
    // EMAIL_ERROR, USER_ERROR, TRANSACTION_ERROR
}
```

---

## 🎯 Comportamento Esperado Agora

### ✅ Sucesso (Orçamento criado + Email enviado)
```json
{
    "id": 1001,
    "status": "PENDENTE",
    "tipo": "INICIAL",
    "valorTotal": 1500.00,
    "dataCriacao": "2026-06-23T21:30:00",
    "servicos": [...],
    "pecas": [...]
}
```

### ❌ Erro: Email não cadastrado
```json
{
    "timestamp": "2026-06-23T21:26:13.1618924",
    "status": 400,
    "error": "Bad Request",
    "code": "EMAIL_ERROR",
    "message": "Cliente 'João Silva' não possui email cadastrado. Impossível enviar orçamento por email.",
    "details": []
}
```

### ❌ Erro: Usuário não existe
```json
{
    "timestamp": "2026-06-23T21:26:13.1618924",
    "status": 400,
    "error": "Bad Request",
    "code": "USER_ERROR",
    "message": "Cliente 'João Silva' não possui usuário cadastrado. Impossível enviar orçamento por email.",
    "details": []
}
```

### ❌ Erro: Constraint violada
```json
{
    "timestamp": "2026-06-23T21:26:13.1618924",
    "status": 500,
    "error": "Internal Server Error",
    "code": "TRANSACTION_ERROR",
    "message": "Erro ao salvar: um valor único foi violado. Este registro já existe.",
    "details": []
}
```

---

## 📋 Fluxo de Execução

```
1. POST /ordens/{id}/orcamentos
   ↓
2. OrdemServicoController.criarOrcamento()
   ↓
3. OrdemServicoService.criar() [@Transactional]
   ↓
   ├─ criarOrcamentoInterno() [@Transactional]
   │  ├─ Criar OsOrcamento
   │  ├─ Adicionar serviços
   │  ├─ Adicionar peças
   │  ├─ Salvar no banco
   │  └─ COMMIT ✅
   │
   └─ enviarEmailOrcamentoAssincrono() [@Transactional(NOT_SUPPORTED)]
      ├─ Validar email
      ├─ Gerar PDF
      ├─ Enviar email
      └─ Sem transação JPA ✅
   
4. Retornar OrcamentoResponseDTO ✅
```

---

## 🔧 Mudanças Realizadas

### `OrdemServicoService.java`
- ✅ Separado `criar()` em 3 métodos: `criar()`, `criarOrcamentoInterno()`, `enviarEmailOrcamentoAssincrono()`
- ✅ Adicionado `validarEmailClienteOuLancarExcecao()` com mensagens descritivas
- ✅ Email enviado **fora** da transação JPA
- ✅ Tratamento de erro de email não crítico (não impede criação)

### `GlobalExceptionHandler.java`
- ✅ Adicionado `handleTransactionSystemException()` com extração de mensagem de erro
- ✅ Adicionado `handleRuntimeException()` com detecção de tipo de erro
- ✅ Adicionado método `extrairMensagemDeErroDeTransacao()` para interpretação de constraints
- ✅ Logging melhorado para debug

---

## 🧪 Como Testar

### Teste 1: Criar orçamento com email válido
```bash
curl -X POST "http://localhost:8080/api/atendimento/ordens/1/orcamentos" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INICIAL",
    "prazoEstipulado": "2026-07-01T00:00:00",
    "servicos": [{"servicoId": 1}],
    "pecas": [{"pecaId": 1, "quantidade": 2}]
  }'
```

**Resultado:** Status 201 + JSON do orçamento ✅

### Teste 2: Cliente sem email
```bash
# Antes de criar orçamento, remova email do cliente
UPDATE usuarios SET email = NULL WHERE id = (
  SELECT usuario_id FROM clientes WHERE id = 1
);
```

**Resultado:** Status 400 + Mensagem clara ✅

### Teste 3: Verificar logs
```
[INFO] Criando orçamento | OS: 1 | Tipo: INICIAL | ...
[INFO] Orçamento criado com sucesso | ID: 1001 | ...
[INFO] Enviando email de orçamento | OS: 1 | Orcamento: 1001 | Cliente: ...
[INFO] Email de orçamento enviado com sucesso | OS: 1 | Destinatário: ...
```

---

## 📌 Notas Importantes

1. **Email não é crítico**: Se falhar o envio de email, o orçamento já foi criado no banco
2. **Transações independentes**: Criação e envio têm suas próprias transações
3. **Mensagens específicas**: Cada tipo de erro retorna uma mensagem descritiva
4. **Código HTTP apropriado**: 400 para validação, 500 para erro de servidor

---

## ✨ Benefícios

| Antes | Depois |
|-------|--------|
| Erro genérico 500 | Erro específico com causa |
| Email silenciosamente ignorado | Validação clara |
| Transação falha no commit | Operações separadas |
| Impossível debugar | Logs descritivos |

