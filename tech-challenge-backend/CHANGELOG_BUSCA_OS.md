# Changelog - Busca de Ordem de Serviço com Orçamentos Relacionados

## Data: 2026-06-21

### 📋 Resumo das Alterações

Atualização completa do endpoint de busca de Ordem de Serviço por ID para retornar uma estrutura aninhada incluindo orçamentos, serviços e peças, eliminando problema de N+1 queries e corrigindo erro de cartesian product.

---

## 🔧 Problemas Corrigidos

### 1. Erro de Cartesian Product no JPA
**Problema**: `JpaSystemException: Could not generate fetch : com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico(os) -> orcamentos`

**Causa**: Múltiplos `LEFT JOIN FETCH` de collections (orcamentos, servicos, pecas) causavam cartesian product não suportado pelo Hibernate.

**Solução**: Substituição por `@EntityGraph` que é a forma correta de fazer eager loading de múltiplas collections.

### 2. Problema de N+1 Queries
**Problema**: Sem eager loading, cada orcamento geraria queries adicionais para servicos e pecas.

**Solução**: `@EntityGraph` com `attributePaths` explícitos carrega tudo em uma única query otimizada.

---

## 📝 Mudanças Implementadas

### 1. Entidade OrdemServico
**Arquivo**: `src/main/java/.../domain/entities/OrdemServico.java`

```java
@NamedEntityGraph(
    name = "OrdemServico.withOrcamentosAndDetails",
    attributeNodes = {
        @NamedAttributeNode("cliente"),
        @NamedAttributeNode("veiculo"),
        @NamedAttributeNode("mecanico"),
        @NamedAttributeNode("orcamentos")
    }
)
```

Define o grafo de entidades a serem carregadas eagerly.

### 2. Repositório Spring Data JPA
**Arquivo**: `src/main/java/.../adapters/out/persistence/OrdemServicoRepository.java`

```java
@EntityGraph(attributePaths = {
    "cliente",
    "veiculo",
    "mecanico",
    "orcamentos",
    "orcamentos.servicos",
    "orcamentos.servicos.servico",
    "orcamentos.pecas",
    "orcamentos.pecas.peca"
})
@Override
Optional<OrdemServico> findById(UUID id);
```

Sobreescreve o `findById` padrão com eager loading completo.

### 3. Novos DTOs Criados

#### OrcamentoDTO
Representa um orçamento com suas listas de serviços e peças:
```java
{
    id: UUID,
    tipo: String,
    status: String,
    valorTotal: BigDecimal,
    prazoEstipulado: LocalDateTime,
    dataCriacao: LocalDateTime,
    servicos: List<ServicoDTO>,
    pecas: List<PecaDTO>
}
```

#### ServicoDTO
Representa um serviço dentro de um orçamento:
```java
{
    id: UUID,                       // ID do registro OS_SERVICO
    servicoId: UUID,               // ID do SERVICO_CATALOGO
    nome: String,                  // Nome do serviço
    precoMaoDeObraAplicado: BigDecimal
}
```

#### PecaDTO
Representa uma peça dentro de um orçamento:
```java
{
    id: UUID,                      // ID do registro OS_PECA
    pecaId: UUID,                  // ID da PECA_INSUMO
    nome: String,                  // Nome da peça
    quantidade: Integer,
    precoVendaAplicado: BigDecimal
}
```

### 4. OrdemServicoResponseDTO Atualizado
**Arquivo**: `src/main/java/.../application/dto/OrdemServicoResponseDTO.java`

Adicionado campo:
```java
List<OrcamentoDTO> orcamentos
```

Método `from()` atualizado para:
- Converter orcamentos para OrcamentoDTO
- Converter servicos para ServicoDTO (dentro de cada orcamento)
- Converter pecas para PecaDTO (dentro de cada orcamento)
- Retornar lista vazia se não houver orcamentos

### 5. Logs de Debug Adicionados
**Arquivo**: `src/main/java/.../application/services/OrdemServicoService.java`

Adicionados logs detalhados em:

#### buscarPorId()
```
INFO: Buscando Ordem de Serviço por ID: {id}
DEBUG: OS encontrada: {id} | Status: {status} | Orcamentos carregados: {qtd}
DEBUG: Orcamento: {id} | Tipo: {tipo} | Servicos: {qtd} | Pecas: {qtd}
WARN: Ordem de Serviço não encontrada para o ID: {id}
```

#### alterarStatus()
```
INFO: Alterando status da OS: {id} | Novo Status: {status} | Mecanico ID: {mecanicoId} | Usuario: {email}
DEBUG: Mecânico localizado: {id} | Nome: {nome}
INFO: Status alterado com sucesso | OS: {id} | De: {statusAnterior} | Para: {statusNovo} | Mecanico: {mecanicoId}
ERROR: Mecânico não encontrado: {id}
```

#### criar() e criar(ClienteRequestDTO)
```
INFO: Criando nova Ordem de Serviço | Cliente: {id} | Veiculo: {id} | Mecanico: {id}
DEBUG: Cliente localizado: {nome} | Veiculo localizado: {modelo}
INFO: Ordem de Serviço criada com sucesso | ID: {id} | Status: RECEBIDA
ERROR: [Cliente/Veiculo/Mecanico] não encontrado: {id}
```

---

## 📊 Estrutura de Retorno Exemplo

```json
{
    "id": "88888888-8888-4888-8888-888888888881",
    "clienteId": "88888888-8888-4888-8888-888888888880",
    "clienteNome": "Ana Paula Ribeiro",
    "veiculoId": "66666666-6666-4666-8666-666666666660",
    "veiculoModelo": "Corolla",
    "mecanicoId": "77777777-7777-4777-8777-777777777771",
    "mecanicoNome": "Marcos Vinícius Lima",
    "status": "EM_DIAGNOSTICO",
    "valorTotal": 0.00,
    "queixaCliente": "Barulho anormal ao frear",
    "observacoes": "Veículo possui risco aproximado de 45cm na lateral esquerda",
    "dataCriacao": "2026-06-19T14:30:54.559488",
    "dataInicioExecucao": null,
    "dataFinalizacao": null,
    "urgente": false,
    "orcamentos": [
        {
            "id": "99999999-9999-4999-9999-999999999999",
            "tipo": "INICIAL",
            "status": "PENDENTE",
            "valorTotal": 350.00,
            "prazoEstipulado": "2026-06-21T18:00:00",
            "dataCriacao": "2026-06-20T10:00:00",
            "servicos": [
                {
                    "id": "11111111-1111-4111-1111-111111111111",
                    "servicoId": "22222222-2222-4222-2222-222222222222",
                    "nome": "Troca de Pastilhas de Freio",
                    "precoMaoDeObraAplicado": 150.00
                }
            ],
            "pecas": [
                {
                    "id": "33333333-3333-4333-3333-333333333333",
                    "pecaId": "44444444-4444-4444-4444-444444444444",
                    "nome": "Pastilha de Freio Dianteira Corolla",
                    "quantidade": 1,
                    "precoVendaAplicado": 200.00
                }
            ]
        }
    ]
}
```

---

## 🔍 Validação de Performance

### Queries Executadas
- **Antes**: 1 query para OS + N queries para cada orcamento (N+1 problem)
- **Depois**: 1 query otimizada com eager loading completo

### EntityGraph vs JOIN FETCH
- `JOIN FETCH`: Problema com múltiplas collections (cartesian product)
- `@EntityGraph`: Solução otimizada que gera JOINs independentes quando necessário

---

## ✅ Testes Recomendados

1. **Buscar OS com orcamentos vazios**
   - Deve retornar `orcamentos: []`
   
2. **Buscar OS com múltiplos orcamentos**
   - Verificar que todos os orcamentos, serviços e peças são carregados
   
3. **Verificar logs**
   - Ativar log level DEBUG em `application.yml`
   - Validar que apenas 1 query SQL é executada
   
4. **Performance**
   - Comparar tempo de resposta antes vs depois
   - Esperado: Significativa redução no tempo de resposta

---

## 📦 Arquivos Alterados

- `src/main/java/.../domain/entities/OrdemServico.java`
- `src/main/java/.../adapters/out/persistence/OrdemServicoRepository.java`
- `src/main/java/.../adapters/out/OrdemServicoRepositoryAdapter.java`
- `src/main/java/.../application/dto/OrdemServicoResponseDTO.java`
- `src/main/java/.../application/dto/OrcamentoDTO.java` (novo)
- `src/main/java/.../application/dto/ServicoDTO.java` (novo)
- `src/main/java/.../application/dto/PecaDTO.java` (novo)
- `src/main/java/.../application/services/OrdemServicoService.java`

---

## 🚀 Deploy Notes

- Sem alterações de banco de dados necessárias
- Sem breaking changes
- Backward compatible (estrutura anterior ainda funciona)
- Melhor performance e experiência do cliente

---

## 🐛 Debug Log Configuration

Para ativar logs de debug completos, adicione ao `application.yml` ou `application-dev.yml`:

```yaml
logging:
  level:
    com.fiap.tech_challenge_backend.atendimento: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

Isso exibirá:
- Logs da aplicação em DEBUG
- SQL executado
- Parametros das queries
