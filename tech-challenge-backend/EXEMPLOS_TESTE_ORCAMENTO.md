# Exemplos de Teste - Criação de Orçamento

## 📝 Casos de Teste

### Caso 1: ✅ Criação com Sucesso

**Requisição:**
```bash
curl -X POST "http://localhost:8080/api/atendimento/ordens/1/orcamentos" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INICIAL",
    "prazoEstipulado": "2026-07-01",
    "servicos": [
      {
        "servicoId": 1
      }
    ],
    "pecas": [
      {
        "pecaId": 5,
        "quantidade": 2
      }
    ]
  }'
```

**Resposta (201 Created):**
```json
{
  "id": 10001,
  "ordemServicoId": 1,
  "tipo": "INICIAL",
  "status": "PENDENTE",
  "dataCriacao": "2026-06-23T21:35:00",
  "prazoEstipulado": "2026-07-01T00:00:00",
  "valorTotal": 1250.50,
  "servicos": [
    {
      "servicoId": 1,
      "nomServico": "Troca de óleo",
      "precoMaoDeObraAplicado": 150.00
    }
  ],
  "pecas": [
    {
      "pecaId": 5,
      "nomePeca": "Filtro de óleo",
      "quantidade": 2,
      "precoVendaAplicado": 50.25
    }
  ]
}
```

**Logs:**
```
[2026-06-23T21:35:00] INFO  OrdemServicoService: Criando orçamento | OS: 1 | Tipo: INICIAL | Prazo: 2026-07-01 | Serviços: 1 | Peças: 1
[2026-06-23T21:35:00] DEBUG OrdemServicoService: Adicionando serviço ao orçamento | Serviço: Troca de óleo | Preço Atual: 150.00
[2026-06-23T21:35:00] DEBUG OrdemServicoService: Adicionando peça ao orçamento | Peça: Filtro de óleo | Qtd: 2 | Preço Unit: 50.25
[2026-06-23T21:35:01] INFO  OrdemServicoService: Orçamento criado com sucesso | ID: 10001 | Tipo: INICIAL | Valor: 1250.50 | Data Criação: 2026-06-23T21:35:00
[2026-06-23T21:35:01] INFO  OrdemServicoService: Enviando email de orçamento | OS: 1 | Orcamento: 10001 | Cliente: joao@email.com
[2026-06-23T21:35:02] INFO  OrdemServicoService: Email de orçamento enviado com sucesso | OS: 1 | Destinatário: joao@email.com
```

✅ **Resultado:** Orçamento criado + Email enviado com sucesso

---

### Caso 2: ❌ Cliente sem Email Cadastrado

**Requisição:**
```bash
curl -X POST "http://localhost:8080/api/atendimento/ordens/2/orcamentos" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INICIAL",
    "prazoEstipulado": "2026-07-01",
    "servicos": [{"servicoId": 1}],
    "pecas": [{"pecaId": 5, "quantidade": 1}]
  }'
```

**Resposta (400 Bad Request):**
```json
{
  "timestamp": "2026-06-23T21:35:03.1234567",
  "status": 400,
  "error": "Bad Request",
  "code": "EMAIL_ERROR",
  "message": "Cliente 'Maria Silva' não possui email cadastrado. Impossível enviar orçamento por email.",
  "details": []
}
```

**Logs:**
```
[2026-06-23T21:35:03] INFO  OrdemServicoService: Criando orçamento | OS: 2 | Tipo: INICIAL | ...
[2026-06-23T21:35:03] INFO  OrdemServicoService: Orçamento criado com sucesso | ID: 10002 | Tipo: INICIAL | Valor: 200.00 | ...
[2026-06-23T21:35:03] INFO  OrdemServicoService: Enviando email de orçamento | OS: 2 | Orcamento: 10002 | Cliente: null
[2026-06-23T21:35:03] ERROR OrdemServicoService: Cliente 'Maria Silva' não possui email cadastrado. Impossível enviar orçamento por email.
[2026-06-23T21:35:03] ERROR GlobalExceptionHandler: Erro em tempo de execução
```

❌ **Resultado:** Falha clara na validação de email

---

### Caso 3: ❌ Cliente sem Usuário Associado

**Requisição:** (mesmo que Caso 2, mas cliente sem usuário)

**Resposta (400 Bad Request):**
```json
{
  "timestamp": "2026-06-23T21:35:04.2345678",
  "status": 400,
  "error": "Bad Request",
  "code": "USER_ERROR",
  "message": "Cliente 'Pedro Santos' não possui usuário cadastrado. Impossível enviar orçamento por email.",
  "details": []
}
```

❌ **Resultado:** Mensagem clara sobre usuário não cadastrado

---

### Caso 4: ❌ Serviço Não Encontrado

**Requisição:**
```bash
curl -X POST "http://localhost:8080/api/atendimento/ordens/1/orcamentos" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INICIAL",
    "prazoEstipulado": "2026-07-01",
    "servicos": [{"servicoId": 9999}],
    "pecas": []
  }'
```

**Resposta (404 Not Found):**
```json
{
  "timestamp": "2026-06-23T21:35:05.3456789",
  "status": 404,
  "error": "Not Found",
  "code": "ENTITY_NOT_FOUND",
  "message": "Serviço não encontrado: 9999",
  "details": []
}
```

❌ **Resultado:** Erro específico de entidade não encontrada

---

### Caso 5: ❌ Erro de Transação (Constraint)

**Cenário:** Orçamento com valor negativo (violação de constraint)

**Resposta (500 Internal Server Error):**
```json
{
  "timestamp": "2026-06-23T21:35:06.4567890",
  "status": 500,
  "error": "Internal Server Error",
  "code": "TRANSACTION_ERROR",
  "message": "Erro ao salvar: um valor não atende aos critérios válidos.",
  "details": []
}
```

**Logs:**
```
[2026-06-23T21:35:06] ERROR GlobalExceptionHandler: Erro na transação JPA
org.springframework.transaction.TransactionSystemException: Could not commit JPA transaction
    Caused by: org.hibernate.constraint.ConstraintViolationException: CHECK constraint failed...
```

❌ **Resultado:** Mensagem de constraint traduzida

---

## 🔄 Comparação: Antes vs Depois

### Antes (Erro Genérico)
```
POST /api/atendimento/ordens/1/orcamentos

✅ Orçamento criado no banco
✅ Email enviado para cliente
❌ Erro no commit da transação

Response: 500 Internal Server Error
{
    "message": "Ocorreu um erro inesperado no servidor.",
    "code": "INTERNAL_SERVER_ERROR",
    "details": []
}

❌ Cliente não sabe o que fazer
❌ Desenvolvedor não consegue debugar
❌ Orçamento foi criado mas erro retornado = confusão
```

### Depois (Erro Claro)
```
POST /api/atendimento/ordens/1/orcamentos

Cenário 1: Email válido
✅ Orçamento criado no banco
✅ Email enviado para cliente
✅ Status 201 Created
   Retorna dados do orçamento

Cenário 2: Email inválido/inexistente
✅ Orçamento criado no banco
❌ Email não enviado
✅ Status 400 Bad Request
   message: "Cliente 'João Silva' não possui email cadastrado..."
   
✅ Cliente sabe exatamente o problema
✅ Desenvolvedor consegue debugar facilmente
✅ Transação bem-sucedida mesmo se email falhar
```

---

## 📊 Fluxo de Teste Completo

```sql
-- 1. Criar cliente com usuário e email
INSERT INTO clientes (id, nome, cpf_cnpj, usuario_id) 
VALUES (1, 'João Silva', '12345678901234', 1);

INSERT INTO usuarios (id, nome, email, tipo) 
VALUES (1, 'João Silva', 'joao@email.com', 'CLIENTE');

-- 2. Criar veículo
INSERT INTO veiculos (id, cliente_id, placa, modelo) 
VALUES (1, 1, 'ABC1234', 'Chevrolet Onix 2022');

-- 3. Criar ordem de serviço
INSERT INTO ordens_servico (id, cliente_id, veiculo_id, status, data_criacao) 
VALUES (1, 1, 1, 'RECEBIDA', NOW());

-- 4. Criar serviço do catálogo
INSERT INTO servicos_catalogo (id, nome, preco_mao_de_obra) 
VALUES (1, 'Troca de óleo', 150.00);

-- 5. Criar peça do estoque
INSERT INTO pecas_insumo (id, nome, preco_venda) 
VALUES (5, 'Filtro de óleo', 50.25);

-- 6. Criar orçamento (via API)
POST /api/atendimento/ordens/1/orcamentos
{...}

-- 7. Verificar resultado
SELECT * FROM os_orcamentos WHERE id = 10001;
SELECT * FROM os_servicos WHERE orcamento_id = 10001;
SELECT * FROM os_pecas WHERE orcamento_id = 10001;

-- 8. Testes adicionais
-- Remover email para testar erro
UPDATE usuarios SET email = NULL WHERE id = 1;
POST /api/atendimento/ordens/1/orcamentos --> 400 Bad Request

-- Adicionar email de volta
UPDATE usuarios SET email = 'joao@email.com' WHERE id = 1;
POST /api/atendimento/ordens/1/orcamentos --> 201 Created
```

---

## ✅ Checklist de Validação

- [ ] Orçamento criado com sucesso (Status 201)
- [ ] Email enviado para cliente válido
- [ ] Erro claro quando email não existe (Status 400)
- [ ] Erro claro quando usuário não existe (Status 400)
- [ ] Erro específico de constraint violada (Status 500)
- [ ] Logs aparecem com informações descritivas
- [ ] Orçamento salvo no banco mesmo se email falhar
- [ ] Response JSON bem formatado com error code específico

