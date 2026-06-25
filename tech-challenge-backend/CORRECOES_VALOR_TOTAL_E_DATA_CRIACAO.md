# Correção de Bugs: valorTotal e dataCriacao

## 🐛 Bugs Corrigidos

### **Bug 1: valorTotal Incorreto**

**Problema:**
- O campo `valorTotal` da OS estava sendo armazenado como um valor mutável
- Não estava sendo calculado a partir dos orçamentos vinculados
- Quando havia múltiplos orçamentos, o valor não era somado

**Solução:**
- Remover o armazenamento do `valorTotal` na atualização
- Calcular dinamicamente a partir da soma de todos os orçamentos
- O valor é sempre recalculado ao retornar a OS

**Impacto:**
```
Antes:
OS com 2 orçamentos (R$ 500 + R$ 300)
valorTotal retornado: qualquer valor aleatório

Depois:
OS com 2 orçamentos (R$ 500 + R$ 300)
valorTotal retornado: R$ 800 (soma automática)
```

---

### **Bug 2: Alteração de valorTotal e dataCriacao**

**Problema:**
- Endpoint `PATCH /api/v1/ordens-servico/editar` aceitava `valorTotal` no request body
- Permitia alterar `valorTotal` de forma manual (errado, deveria ser calculado)
- Não há proteção contra alteração de `dataCriacao` (embora não estivesse no DTO)

**Solução:**
- Remover `valorTotal` do `OrdemServicoAtualizarRequestDTO`
- Remover a chamada `os.setValorTotal(request.valorTotal())` no service
- Garantir que `dataCriacao` é imutável (já estava OK)

---

## 📝 Mudanças Implementadas

### 1. **OrdemServicoAtualizarRequestDTO.java**

**Antes:**
```java
public record OrdemServicoAtualizarRequestDTO(
    UUID clienteId,
    UUID veiculoId,
    UUID mecanicoId,
    StatusOrdemServico status,
    
    @NotNull(message = "O valor total é obrigatório")
    @PositiveOrZero(message = "O valor total não pode ser negativo")
    BigDecimal valorTotal,  // ❌ REMOVIDO
    
    LocalDateTime dataInicioExecucao,
    LocalDateTime dataFinalizacao,
    Boolean urgente
) { }
```

**Depois:**
```java
public record OrdemServicoAtualizarRequestDTO(
    UUID clienteId,
    UUID veiculoId,
    UUID mecanicoId,
    StatusOrdemServico status,
    
    LocalDateTime dataInicioExecucao,
    LocalDateTime dataFinalizacao,
    Boolean urgente
) { }
```

**Removidas importações desnecessárias:**
- `BigDecimal`
- `jakarta.validation.constraints.PositiveOrZero`

---

### 2. **OrdemServicoResponseDTO.java**

**Antes:**
```java
public static OrdemServicoResponseDTO from(OrdemServico os) {
    return new OrdemServicoResponseDTO(
        // ...
        os.getValorTotal(),  // ❌ Valor armazenado diretamente
        // ...
    );
}
```

**Depois:**
```java
public static OrdemServicoResponseDTO from(OrdemServico os) {
    // ✅ Calcula valorTotal a partir da soma de todos os orçamentos
    BigDecimal valorTotalCalculado = os.getOrcamentos() != null && !os.getOrcamentos().isEmpty()
            ? os.getOrcamentos().stream()
                .map(orc -> orc.getValorTotal() != null ? orc.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
            : BigDecimal.ZERO;

    return new OrdemServicoResponseDTO(
        // ...
        valorTotalCalculado,  // ✅ Calculado dinamicamente
        // ...
    );
}
```

**Lógica:**
- Se há orçamentos: soma todos os `valorTotal` deles
- Se não há orçamentos: retorna `BigDecimal.ZERO`
- Valores null são tratados como ZERO

---

### 3. **OrdemServicoService.java**

**Antes:**
```java
public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoAtualizarRequestDTO request) {
    // ...
    os.setCliente(cliente);
    os.setVeiculo(veiculo);
    os.setMecanico(mecanico);
    os.setStatus(request.status());
    os.setValorTotal(request.valorTotal());  // ❌ REMOVIDO
    os.setDataInicioExecucao(request.dataInicioExecucao());
    os.setDataFinalizacao(request.dataFinalizacao());
    os.definirUrgente(request.urgente());
    
    return OrdemServicoResponseDTO.from(ordemServicoRepository.salvar(os));
}
```

**Depois:**
```java
public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoAtualizarRequestDTO request) {
    // ...
    os.setCliente(cliente);
    os.setVeiculo(veiculo);
    os.setMecanico(mecanico);
    os.setStatus(request.status());
    // ✅ Removida a chamada setValorTotal()
    os.setDataInicioExecucao(request.dataInicioExecucao());
    os.setDataFinalizacao(request.dataFinalizacao());
    os.definirUrgente(request.urgente());
    
    return OrdemServicoResponseDTO.from(ordemServicoRepository.salvar(os));
}
```

---

## ✅ Fluxo Correto Agora

### **Criação de OS**
```
1. POST /api/v1/ordens-servico/criar
2. OS criada com valorTotal = 0 (sem orçamentos)
3. Retorna: valorTotal = 0
```

### **Criação de Orçamento INICIAL**
```
1. POST /api/v1/ordens-servico/criar-orcamento?id=20260001
   Body: { "tipo": "INICIAL", "servicos": [...], "pecas": [...] }
2. Orçamento criado com valorTotal = R$ 500
3. GET /api/v1/ordens-servico/buscar?id=20260001
4. Retorna: valorTotal = R$ 500 (soma do 1 orçamento)
```

### **Criação de Orçamento ADICIONAL**
```
1. POST /api/v1/ordens-servico/criar-orcamento?id=20260001
   Body: { "tipo": "ADICIONAL", "servicos": [...], "pecas": [...] }
2. Orçamento criado com valorTotal = R$ 300
3. GET /api/v1/ordens-servico/buscar?id=20260001
4. Retorna: valorTotal = R$ 800 (soma: R$ 500 + R$ 300)
```

### **Atualização de OS**
```
1. PATCH /api/v1/ordens-servico/editar?id=20260001
   Body: { "clienteId": "...", "status": "EM_EXECUCAO", ... }
   ❌ Não aceita mais: "valorTotal": "123.45"
2. OS atualizada
3. GET retorna: valorTotal = R$ 800 (mantém soma dos orçamentos)
```

---

## 🔒 Imutabilidade Garantida

| Campo | Criação | Atualização | Retorno |
|-------|---------|------------|---------|
| `id` | Gerado | ❌ Não permitido | ✅ Retorna |
| `dataCriacao` | Definida | ❌ Não permitido | ✅ Retorna |
| `valorTotal` | Calculado | ❌ Removido do DTO | ✅ Calculado |
| `status` | Inicial | ✅ Permitido | ✅ Retorna |
| `cliente` | Obrigatório | ✅ Permitido | ✅ Retorna |
| `veiculo` | Obrigatório | ✅ Permitido | ✅ Retorna |
| `mecanico` | Opcional | ✅ Permitido | ✅ Retorna |

---

## 📊 Arquivos Modificados

| Arquivo | Mudanças |
|---------|----------|
| `OrdemServicoAtualizarRequestDTO.java` | ✏️ Removido campo `valorTotal` + imports |
| `OrdemServicoResponseDTO.java` | ✏️ Refatorado método `from()` para calcular soma |
| `OrdemServicoService.java` | ✏️ Removida linha `setValorTotal()` |

---

## ✨ Benefícios

✅ **Consistência:** valorTotal sempre reflete a realidade
✅ **Integridade:** Não permite inconsistências
✅ **Segurança:** dataCriacao imutável
✅ **Automatização:** Cálculos feitos automaticamente
✅ **Previsibilidade:** Comportamento determinístico

---

## 🧪 Testes Recomendados

```bash
# Teste 1: Criar OS sem orçamento
POST /api/v1/ordens-servico/criar-orcamento?id=20260001
Body: { "tipo": "INICIAL", ... }
Expect: valorTotal = valor_do_orcamento

# Teste 2: Adicionar orçamento adicional
POST /api/v1/ordens-servico/criar-orcamento?id=20260001
Body: { "tipo": "ADICIONAL", ... }
Expect: valorTotal = valor_orcamento_1 + valor_orcamento_2

# Teste 3: Tentar enviar valorTotal na atualização
PATCH /api/v1/ordens-servico/editar?id=20260001
Body: { "valorTotal": 999.99, ... }
Expect: Campo ignorado, valor mantém soma dos orçamentos
```

