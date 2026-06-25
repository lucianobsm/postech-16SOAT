# Análise Completa do Projeto Tech Challenge Backend

**Data da Análise:** 24 de Junho de 2026  
**Versão do Projeto:** 0.0.1-SNAPSHOT  
**Arquitetura:** Hexagonal com Bounded Contexts (DDD)

---

## 1. MAPA DE DOMÍNIOS

### 1.1 Contextos Delimitados Identificados

#### **ACESSO** (Authentication & Authorization)
- **Responsabilidade:** Gerenciar usuários, autenticação e autorização
- **Padrão:** JWT com OAuth2 Resource Server
- **Controllers:** 
  - `AuthController` - Login, autenticação
  - `MeController` - Informações do usuário autenticado

#### **CADASTRO** (Registration)
- **Responsabilidade:** Gerenciar clientes e veículos
- **Controllers:**
  - `ClienteController` - CRUD de Clientes
  - `VeiculoController` - CRUD de Veículos (comentado)
- **Nota:** VeiculoController está temporariamente desabilitado

#### **ATENDIMENTO** (Service Management)
- **Responsabilidade:** Gerenciar ordens de serviço, orçamentos e histórico
- **Controllers:**
  - `OrdemServicoController` - CRUD e ciclo de vida das OS
  - `ClienteOrdemServicoController` - Criação de OS do lado do cliente
  - `RelatorioAtendimentoController` - Relatórios de atendimento
- **Domínio de Serviços:** Regras de negócio para transições de status

#### **ESTOQUE** (Inventory)
- **Responsabilidade:** Gerenciar peças, insumos e movimentações
- **Controllers:**
  - `ItemEstoqueController` - CRUD de Peças/Insumos
  - `MovimentacaoController` - Registrar movimentações

#### **SHARED** (Shared Kernel)
- **Responsabilidade:** Objetos e exceções compartilhadas
- **Controllers:**
  - `PingController` - Health check

---

### 1.2 Entities Identificadas

#### **ACESSO**
```
Usuario
├── id (Long)
├── nome (String)
├── email (String)
├── senha (String - encriptada)
├── perfil (PerfilUsuario enum)
├── ativo (Boolean)
└── dataCriacao (LocalDateTime)
```

#### **CADASTRO**
```
Cliente
├── id (Long)
├── nome (String)
├── cpfCnpj (String - unique)
├── email (String)
├── telefone (String)
├── endereço (String)
├── dataCriacao (LocalDateTime)
└── veiculos (List<ClienteVeiculo>)

Veiculo
├── id (Long)
├── placa (String - unique)
├── marca (String)
├── modelo (String)
├── ano (Integer)
├── dataCriacao (LocalDateTime)
└── clientes (List<ClienteVeiculo>)

ClienteVeiculo (Many-to-Many)
├── clienteId (Long)
└── veiculoId (Long)
```

#### **ATENDIMENTO**
```
OrdemServico
├── id (Long)
├── cliente (Cliente) ⬅️ ForeignKey
├── veiculo (Veiculo) ⬅️ ForeignKey
├── mecanico (Usuario) ⬅️ ForeignKey
├── status (StatusOrdemServico enum)
├── descricao (String)
├── valorTotalAcumulado (BigDecimal)
├── valorTotal (BigDecimal)
├── dataCriacao (LocalDateTime)
├── dataAtualizacao (LocalDateTime)
├── orcamentos (List<OsOrcamento>)
├── servicos (List<OsServico>)
├── pecas (List<OsPeca>)
└── historicoStatus (List<OsHistoricoStatus>)

OsOrcamento
├── id (Long)
├── ordemServico (OrdemServico)
├── tipo (TipoOrcamento enum)
├── status (StatusOrcamento enum)
├── valor (BigDecimal)
├── descricao (String)
├── dataCriacao (LocalDateTime)
└── dataAprovacao (LocalDateTime)

OsServico
├── id (Long)
├── ordemServico (OrdemServico)
├── servicoCatalogo (ServicoCatalogo)
├── valorUnitario (BigDecimal)
├── quantidade (Integer)
├── dataRealizacao (LocalDateTime)
└── descricao (String)

OsPeca
├── id (Long)
├── ordemServico (OrdemServico)
├── pecaInsumo (PecaInsumo)
├── quantidadeUsada (Integer)
├── valorUnitario (BigDecimal)
└── dataUso (LocalDateTime)

OsHistoricoStatus
├── id (Long)
├── ordemServico (OrdemServico)
├── statusAnterior (StatusOrdemServico)
├── statusNovo (StatusOrdemServico)
├── usuario (Usuario)
├── motivo (String)
├── dataMudanca (LocalDateTime)
└── descricao (String)

ServicoCatalogo
├── id (Long)
├── nome (String)
├── descricao (String)
├── valorBase (BigDecimal)
├── tempoEstimado (Integer)
├── ativo (Boolean)
└── dataCriacao (LocalDateTime)
```

#### **ESTOQUE**
```
PecaInsumo
├── id (Long)
├── nome (String)
├── descricao (String)
├── quantidadeEmEstoque (Integer)
├── quantidadeMinima (Integer)
├── valorUnitario (BigDecimal)
├── tipo (String)
├── ativo (Boolean)
├── dataCriacao (LocalDateTime)
└── dataAtualizacao (LocalDateTime)

MovimentacaoEstoque
├── id (Long)
├── pecaInsumo (PecaInsumo)
├── tipo (TipoMovimentacao enum)
├── quantidade (Integer)
├── motivo (String)
├── usuarioCriacao (Usuario)
├── dataCriacao (LocalDateTime)
└── observacoes (String)
```

---

### 1.3 Enums Identificados

#### **StatusOrdemServico**
```
RECEBIDA          → Estado inicial
EM_DIAGNOSTICO    → Analisando o veículo
AGUARDANDO_APROVACAO → Aguardando aprovação do orçamento
EM_EXECUCAO       → Serviço em progresso
FINALIZADA        → Serviço finalizado
ENTREGUE          → Veículo entregue ao cliente
```

#### **StatusOrcamento**
```
PENDENTE          → Aguardando resposta
APROVADO          → Cliente aprovou
REJEITADO         → Cliente rejeitou
```

#### **TipoOrcamento**
```
INICIAL           → Primeiro orçamento
ADICIONAL         → Orçamento adicional (serviços adicionais)
```

#### **PerfilUsuario**
```
ADMIN             → Administrador do sistema
FUNCIONARIO       → Mecânico/Atendente
CLIENTE           → Cliente externo
```

#### **TipoMovimentacao**
```
ENTRADA           → Entrada de peça
SAIDA             → Saída de peça (uso em OS)
AJUSTE            → Ajuste de estoque
DEVOLUCAO         → Devolução de peça
```

---

### 1.4 Exceções do Domínio

#### **Acesso**
- `UsuarioJaCadastradoException` - Email já existe
- `UsuarioNaoEncontradoException` - Usuário não encontrado

#### **Cadastro**
- `ClienteJaCadastradoException` - CPF/CNPJ já existe
- `ClienteNaoEncontradoException` - Cliente não encontrado
- `VeiculoJaCadastradoException` - Placa já existe

#### **Atendimento**
- `OrdemServicoStatusException` - Transição de status inválida

#### **Shared (Base)**
- `ApplicationException` - Exceção genérica da aplicação
- `BusinessRuleException` - Violação de regra de negócio
- `ConflictException` - Conflito (duplicado, etc)
- `NotFoundException` - Recurso não encontrado
- `ValorInvalidoException` - Valor inválido

---

## 2. ENDPOINTS POR CONTEXTO

### 2.1 ACESSO (Authentication)

| Método | Endpoint | Descrição | Autenticação | Status |
|--------|----------|-----------|--------------|--------|
| POST | `/auth/login` | Autenticar usuário | ❌ Público | 200/401 |
| GET | `/me` | Dados do usuário autenticado | ✅ JWT | 200/401 |

---

### 2.2 CADASTRO (Registration)

#### **Clientes**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| POST | `/clientes` | Criar cliente | ❌ Público | - | 201 |
| GET | `/clientes` | Listar clientes | ✅ JWT | - | 200 |
| GET | `/clientes/{cpfCnpj}` | Buscar cliente | ✅ JWT | - | 200 |
| PUT | `/clientes/{cpfCnpj}` | Atualizar cliente | ✅ JWT | - | 200 |
| DELETE | `/clientes/{cpfCnpj}` | Deletar cliente | ✅ JWT | ADMIN | 200 |

#### **Veículos** (Comentado)

```
POST   /veiculos
GET    /veiculos
GET    /veiculos/{id}
PUT    /veiculos/{id}
DELETE /veiculos/{id}
```

---

### 2.3 ATENDIMENTO (Service Management)

#### **Ordens de Serviço**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| POST | `/api/atendimento/ordens` | Criar OS (cliente) | ✅ JWT | ADMIN/FUNC/CLIENTE | 201 |
| GET | `/api/atendimento/ordens` | Listar todas OS | ✅ JWT | ADMIN/FUNC | 200 |
| GET | `/api/atendimento/ordens/buscar?id={id}` | Buscar OS por ID | ✅ JWT | ADMIN/FUNC | 200 |
| PUT | `/api/atendimento/ordens/atualizar?id={id}` | Atualizar OS | ✅ JWT | ADMIN/FUNC | 200 |
| DELETE | `/api/atendimento/ordens/remover?id={id}` | Remover OS | ✅ JWT | ADMIN | 200 |
| PATCH | `/api/atendimento/ordens/status?id={id}` | Alterar status | ✅ JWT | ADMIN/FUNC | 200 |

#### **Orçamentos**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| POST | `/api/atendimento/ordens/orcamento?id={idOS}` | Criar orçamento | ✅ JWT | ADMIN/FUNC | 201 |
| GET | `/api/atendimento/ordens/orcamento?idOS={id}&idOrcamento={id}` | Buscar orçamento | ✅ JWT | ADMIN/FUNC | 200 |

#### **Relatórios**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| GET | `/api/atendimento/relatorio/...` | Relatórios diversos | ✅ JWT | - | 200 |

#### **Cliente Ordem de Serviço**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| POST | `/api/atendimento/cliente/ordens` | Criar OS (cliente) | ✅ JWT | CLIENTE | 201 |
| GET | `/api/atendimento/cliente/ordens` | Minhas ordens | ✅ JWT | CLIENTE | 200 |

---

### 2.4 ESTOQUE (Inventory)

#### **Peças/Insumos**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| POST | `/estoque/itens` | Criar peça/insumo | ✅ JWT | ADMIN/FUNC | 201 |
| GET | `/estoque/itens` | Listar peças | ✅ JWT | - | 200 |
| GET | `/estoque/itens/{id}` | Buscar peça por ID | ✅ JWT | - | 200 |
| PUT | `/estoque/itens/{id}` | Atualizar peça | ✅ JWT | ADMIN/FUNC | 200 |
| DELETE | `/estoque/itens/{id}` | Deletar peça | ✅ JWT | ADMIN | 200 |
| GET | `/estoque/itens/ativo/{ativo}` | Filtrar por status | ✅ JWT | - | 200 |
| GET | `/estoque/itens/quantidade/{id}` | Verificar quantidade | ✅ JWT | - | 200 |

#### **Movimentações**

| Método | Endpoint | Descrição | Autenticação | Permissão | Status |
|--------|----------|-----------|--------------|-----------|--------|
| POST | `/estoque/movimentacoes` | Registrar movimentação | ✅ JWT | FUNC | 201 |

---

### 2.5 SHARED (Health)

| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| GET | `/ping` | Health check | 200 |

---

## 3. FLUXOS DE NEGÓCIO PRINCIPAIS

### 3.1 Happy Path: Criar e Acompanhar Ordem de Serviço

```
1. [CLIENTE] Acessa sistema com autenticação JWT
   └─> GET /me ✓

2. [CLIENTE] Cria Ordem de Serviço
   └─> POST /api/atendimento/ordens (CPF + Placa)
   └─> Response: OrdemServico com status = RECEBIDA ✓

3. [FUNCIONARIO] Lista ordens de serviço
   └─> GET /api/atendimento/ordens ✓

4. [FUNCIONARIO] Busca detalhes da OS
   └─> GET /api/atendimento/ordens/buscar?id=123 ✓

5. [FUNCIONARIO] Muda status para EM_DIAGNOSTICO
   └─> PATCH /api/atendimento/ordens/status?id=123
   └─> Cria entrada em OsHistoricoStatus ✓

6. [FUNCIONARIO] Cria primeiro orçamento
   └─> POST /api/atendimento/ordens/orcamento?id=123
   └─> OsOrcamento: tipo=INICIAL, status=PENDENTE ✓

7. [FUNCIONARIO] Altera status para AGUARDANDO_APROVACAO
   └─> PATCH /api/atendimento/ordens/status?id=123 ✓

8. [CLIENTE] Aprova orçamento (via API de aprovação)
   └─> StatusOrcamento muda para APROVADO ✓

9. [FUNCIONARIO] Altera status para EM_EXECUCAO
   └─> PATCH /api/atendimento/ordens/status?id=123 ✓

10. [FUNCIONARIO] Registra peças/serviços utilizados
    └─> Atualiza OsPeca e OsServico ✓

11. [FUNCIONARIO] Finaliza OS
    └─> PATCH /api/atendimento/ordens/status?id=123
    └─> Status = FINALIZADA ✓

12. [FUNCIONARIO] Entrega veículo
    └─> PATCH /api/atendimento/ordens/status?id=123
    └─> Status = ENTREGUE ✓
```

### 3.2 Fluxo: Controle de Estoque

```
1. [ADMIN] Cria peça/insumo no catálogo
   └─> POST /estoque/itens
   └─> PecaInsumo com quantidadeEmEstoque, quantidadeMinima ✓

2. [FUNCIONARIO] Registra entrada de peça
   └─> POST /estoque/movimentacoes
   └─> TipoMovimentacao = ENTRADA ✓
   └─> Atualiza quantidadeEmEstoque ✓

3. [FUNCIONARIO] Usa peça em OS
   └─> POST /api/atendimento/ordens/{id}/pecas
   └─> Cria OsPeca ✓
   └─> Reduz quantidadeEmEstoque ✓

4. [ADMIN] Verifica estoque baixo
   └─> GET /estoque/itens
   └─> Identifica itens com quantidade < quantidadeMinima ✓
```

### 3.3 Fluxo: Gerenciar Clientes

```
1. [CLIENTE] Cadastra-se
   └─> POST /clientes (CPF/CNPJ único)
   └─> Response: CadastroClienteResponse ✓

2. [FUNCIONARIO] Lista clientes
   └─> GET /clientes ✓

3. [FUNCIONARIO] Busca cliente por CPF
   └─> GET /clientes/{cpfCnpj}
   └─> Response: BuscarClienteResponse com veículos ✓

4. [CLIENTE] Atualiza dados
   └─> PUT /clientes/{cpfCnpj}
   └─> Response: BuscarClienteResponse atualizado ✓

5. [ADMIN] Deleta cliente
   └─> DELETE /clientes/{cpfCnpj}
   └─> Precisa estar sem OS pendentes ✓
```

---

### 3.4 Validações Críticas do Domínio

#### **Ordem de Serviço**
- ✅ Cliente deve existir
- ✅ Veículo deve existir
- ✅ Transições de status são validadas (ex: não pode ir de RECEBIDA direto para ENTREGUE)
- ✅ OrdemServico não pode ser deletada com status != RECEBIDA
- ✅ Só ADMIN pode deletar

#### **Orçamento**
- ✅ StatusOrcamento: PENDENTE → APROVADO ou REJEITADO
- ✅ TipoOrcamento: INICIAL obrigatório, ADICIONAL após aprovação do INICIAL
- ✅ Valor deve ser positivo

#### **Peça/Insumo**
- ✅ Nome único
- ✅ Quantidade não pode ser negativa
- ✅ Quantidade mínima deve ser menor que quantidade em estoque
- ✅ Valor unitário positivo

#### **Cliente**
- ✅ CPF/CNPJ único
- ✅ Email válido
- ✅ Telefone obrigatório

#### **Transições de Status (Ordem de Serviço)**
```
RECEBIDA
    ↓
EM_DIAGNOSTICO
    ↓
AGUARDANDO_APROVACAO
    ↓
EM_EXECUCAO
    ↓
FINALIZADA
    ↓
ENTREGUE

Observações:
- Não permite pular fases
- Cada transição registra em OsHistoricoStatus
- Usuário que realizou a transição é registrado
```

---

## 4. ESTRUTURA DE TESTES ATUAL

### 4.1 Localização dos Testes

```
src/test
├── java/com/fiap/tech_challenge_backend
│   ├── acesso/
│   │   ├── application/services/
│   │   │   └── AuthServiceTest.java (108 linhas)
│   │   └── presentation/
│   │       └── AuthControllerTest.java (97 linhas)
│   │
│   ├── atendimento/
│   │   ├── application/services/
│   │   │   └── IdGeneratorServiceTest.java (159 linhas)
│   │   └── adapters/in/web/
│   │       └── (nenhum teste de integração)
│   │
│   ├── cadastro/
│   │   └── (nenhum teste)
│   │
│   ├── estoque/
│   │   ├── application/
│   │   │   └── EstoqueServiceTest.java (205 linhas)
│   │   ├── domain/entities/
│   │   │   └── PecaInsumoTest.java (104 linhas)
│   │   └── presentation/
│   │       └── ItemEstoqueControllerIT.java (175 linhas)
│   │
│   └── cucumber/
│       ├── runners/
│       │   └── CucumberRunnerTest.java (17 linhas)
│       ├── stepdefinitions/
│       │   ├── ClienteSteps.java
│       │   └── CommonSteps.java
│       └── config/
│           └── CucumberSpringConfiguration.java (13 linhas)
│
└── resources
    ├── features/
    │   ├── cliente/
    │   │   └── clientes.feature
    │   ├── estoque/
    │   │   └── controle-estoque.feature
    │   ├── ordem-servico/
    │   │   ├── acompanhamento-ordem-servico.feature
    │   │   ├── criar-ordem-servico.feature
    │   │   └── orcamento.feature
    │   ├── servico/
    │   │   └── servicos.feature
    │   ├── validacoes/
    │   │   └── validacoes-obrigatorias.feature
    │   └── veiculo/
    │       └── veiculos.feature
    │
    └── wiremock/
        ├── mappings/ (10 mocks)
        └── files/
```

### 4.2 Padrões de Teste Utilizados

#### **Unit Tests**
- **Framework:** JUnit 5 (via spring-boot-starter-test)
- **Mock Framework:** Mockito
- **Assertions:** Hamcrest (via spring-boot-starter-test)
- **Exemplo:**
  ```java
  @Test
  @DisplayName("Deve criar cliente com sucesso")
  void testCriarClienteComSucesso() {
    // Arrange
    when(repository.save(...)).thenReturn(...);
    
    // Act
    CadastroClienteResponse result = useCase.execute(request);
    
    // Assert
    assertNotNull(result);
    assertEquals("João", result.nome());
  }
  ```

#### **Integration Tests**
- **Container:** Testcontainers (PostgreSQL)
- **Framework:** Spring Boot Test
- **HTTP Testing:** MockMvc
- **Database:** PostgreSQL 16 Alpine (via Testcontainers)
- **Exemplo:**
  ```java
  @SpringBootTest
  @AutoConfigureMockMvc
  @Testcontainers
  class ItemEstoqueControllerIT {
    @Container
    static PostgreSQLContainer<?> postgres = ...;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCriarPeca() throws Exception {
      mockMvc.perform(post("/estoque/itens")
        .contentType(APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated());
    }
  }
  ```

#### **BDD/Cucumber Tests**
- **Status:** ✅ Infrastructure pronta, features parcialmente implementadas
- **Step Definitions:** Apenas ClienteSteps e CommonSteps criadas
- **Features:** 8 features documentadas em português
- **Runner:** CucumberRunnerTest configurado corretamente
- **Padrão:** Given/When/Then em português

### 4.3 Cobertura Atual

**Estimativa Inicial:** ~30-40% (baseado em testes existentes)

| Contexto | Cobertura | Notas |
|----------|-----------|-------|
| **ACESSO** | ~50% | AuthService e AuthController testados |
| **CADASTRO** | ~5% | Apenas testes Cucumber parciais |
| **ATENDIMENTO** | ~20% | Apenas IdGeneratorService testado |
| **ESTOQUE** | ~60% | EstoqueService, PecaInsumo, ItemEstoqueControllerIT |
| **SHARED** | ~10% | Mínimo testado |

**Total Estimado:** ~30-40% (Meta: ≥80%)

### 4.4 Dependências de Teste Existentes

```xml
<!-- JUnit 5 (via spring-boot-starter-test) -->
org.springframework.boot:spring-boot-starter-test

<!-- Spring Security Testing -->
org.springframework.security:spring-security-test

<!-- Testcontainers -->
org.springframework.boot:spring-boot-testcontainers
org.testcontainers:junit-jupiter
org.testcontainers:postgresql

<!-- WireMock -->
org.wiremock:wiremock-standalone (3.9.1)
com.github.tomakehurst:wiremock-jre8 (2.35.0)
org.springframework.cloud:spring-cloud-contract-wiremock (4.1.3)

<!-- Cucumber: ❌ FALTANDO! -->
<!-- Precisa adicionar:
- io.cucumber:cucumber-java:7.14.0
- io.cucumber:cucumber-junit-platform-engine:7.14.0
- io.cucumber:cucumber-spring:7.14.0
-->
```

---

## 5. DEPENDÊNCIAS E CONFIGURAÇÃO

### 5.1 Dependências Existentes

```
Spring Boot:  3.5.15-SNAPSHOT
Java:         21
PostgreSQL:   16 (driver)
Flyway:       11.7.2 (migrations)

Testing:
├── JUnit 5 ✓
├── Mockito ✓
├── Hamcrest ✓
├── Testcontainers ✓ (PostgreSQL)
├── MockMvc ✓
├── WireMock ✓ (3 dependências)
├── Spring Security Test ✓
└── Cucumber ❌ FALTANDO

Coverage:
└── JaCoCo 0.8.12 ✓ (configurado)
```

### 5.2 Dependências que Precisam Ser Adicionadas

Para implementar suite completa de Cucumber:

```xml
<!-- Cucumber BDD -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>

<!-- JSON para manipulação em steps -->
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20240303</version>
    <scope>test</scope>
</dependency>
```

### 5.3 Configuração Atual

- ✅ Spring Boot 3.5.15 com Spring 6
- ✅ Maven como build tool
- ✅ Testcontainers configurado para PostgreSQL
- ✅ MockMvc para testes de integração
- ✅ JaCoCo para cobertura de código
- ✅ Docker Compose consolidado (postgres, wiremock, app)
- ✅ Flyway para migrations
- ✅ Cucumber Runner configurado (faltam dependências)

---

## 6. ESTRUTURA ESPERADA PARA IMPLEMENTAÇÃO

### 6.1 Arquivos Feature (.feature)

**Já Existentes:**
- ✅ `src/test/resources/features/cliente/clientes.feature`
- ✅ `src/test/resources/features/estoque/controle-estoque.feature`
- ✅ `src/test/resources/features/ordem-servico/acompanhamento-ordem-servico.feature`
- ✅ `src/test/resources/features/ordem-servico/criar-ordem-servico.feature`
- ✅ `src/test/resources/features/ordem-servico/orcamento.feature`
- ✅ `src/test/resources/features/servico/servicos.feature`
- ✅ `src/test/resources/features/validacoes/validacoes-obrigatorias.feature`
- ✅ `src/test/resources/features/veiculo/veiculos.feature`

### 6.2 Step Definitions

**Já Existentes:**
- ⚠️ `ClienteSteps.java` - Parcialmente implementado
- ⚠️ `CommonSteps.java` - Parcialmente implementado

**Faltam:**
- OrdemServicoSteps
- OrcamentoSteps
- EstoqueSteps
- ServicoSteps
- VeiculoSteps
- ValidacoesSteps

### 6.3 Configuração Cucumber

- ✅ `CucumberRunnerTest.java` - Configurado corretamente
- ✅ `CucumberSpringConfiguration.java` - Configurado para Spring
- ⚠️ Dependências Maven faltando

---

## 7. CRÍTICOS PARA SUCESSO

### 7.1 Atenção Especial

1. **Transições de Status:** Validar rigorosamente cada transição de StatusOrdemServico
2. **Foreign Keys:** OrdemServico referencia Cliente, Veiculo, Usuario - tomar cuidado com cascata de deletions
3. **Valores Monetários:** Sempre usar BigDecimal, não Float/Double
4. **Autenticação JWT:** Todos os endpoints (exceto /auth/login e POST /clientes) exigem token
5. **Permissões:** Diferentes operações exigem roles diferentes (ADMIN, FUNCIONARIO, CLIENTE)
6. **Histórico:** OsHistoricoStatus deve ser preenchido automaticamente em cada transição
7. **Estoque:** Movimentações devem ser registradas e quantidades devem ser verificadas

### 7.2 Padrões Identificados

- **DTO Pattern:** Request/Response DTOs para todos os endpoints
- **UseCase Pattern:** Cada operação tem seu UseCase na application layer
- **Repository Pattern:** Interfaces no domain, implementações no infrastructure
- **Enum-based Status:** Estados usando enums (type-safe)
- **Timestamp Automático:** `@PrePersist` e `@PreUpdate` para dataCriacao/dataAtualizacao

---

## 8. RESUMO EXECUTIVO

| Aspecto | Status | Observação |
|---------|--------|-----------|
| **Domínios Identificados** | ✅ 5 | Acesso, Cadastro, Atendimento, Estoque, Shared |
| **Entities Mapeadas** | ✅ 13 | Completas com relacionamentos |
| **Enums Identificados** | ✅ 5 | Todos os estados mapeados |
| **Endpoints Documentados** | ✅ 30+ | Por contexto |
| **Fluxos de Negócio** | ✅ 4 | Detalhados com validações |
| **Testes Unitários** | ✅ 5 | 878 linhas de código |
| **Testes Integração** | ✅ 1 | ItemEstoqueControllerIT |
| **Cucumber Setup** | ✅ Parcial | Runner e config prontos, dependências faltam |
| **Features .feature** | ✅ 8 | Em português, Given/When/Then |
| **Step Definitions** | ⚠️ 2 | ClienteSteps e CommonSteps parciais |
| **Cobertura Atual** | 30-40% | Meta: ≥80% |
| **Pronto para Implementação** | ✅ SIM | Infraestrutura pronta |

---

## 9. PRÓXIMOS PASSOS RECOMENDADOS

1. **Adicionar Dependências Cucumber ao pom.xml**
2. **Implementar Step Definitions para cada domínio**
3. **Expandir/Corrigir features existentes**
4. **Criar testes de integração para controllers ainda não testados**
5. **Implementar testes de negócio (validações críticas)**
6. **Executar teste de cobertura com JaCoCo**
7. **Validar cobertura ≥80% dos domínios críticos**

---

**Documento gerado:** 2026-06-24  
**Autor:** Análise Automática de Arquitetura  
**Versão:** 1.0
