# 📋 Reorganização da Estrutura de Testes

## 🎯 Objetivo
Separar os testes em três categorias principais para melhor organização e manutenibilidade.

---

## 📁 Estrutura Proposta

```
src/test/java/com/fiap/tech_challenge_backend/

├── cucumber/                          # Testes BDD (Behavior-Driven Development)
│   ├── stepdefinitions/
│   │   ├── ClienteSteps.java
│   │   ├── OrdemServicoSteps.java
│   │   ├── EstoqueSteps.java
│   │   ├── VeiculoSteps.java
│   │   ├── ServicoSteps.java
│   │   ├── ValidationSteps.java
│   │   ├── CommonSteps.java
│   │   └── OrcamentoSteps.java
│   ├── runners/
│   │   └── CucumberRunnerTest.java
│   └── config/
│       ├── CucumberSpringConfiguration.java
│       ├── TestContext.java
│       └── Hooks.java
│
├── integration/                       # Testes de Integração
│   ├── controller/
│   │   ├── cadastro/
│   │   │   └── ClienteControllerIntegrationTest.java
│   │   ├── atendimento/
│   │   │   └── OrdemServicoControllerIntegrationTest.java
│   │   └── estoque/
│   │       └── ItemEstoqueControllerIntegrationTest.java
│   ├── service/
│   │   ├── cadastro/
│   │   │   └── CadastroServiceIntegrationTest.java
│   │   ├── estoque/
│   │   │   └── EstoqueServiceIntegrationTest.java
│   │   └── atendimento/
│   │       └── OrdemServicoServiceIntegrationTest.java
│   └── repository/
│       └── ClienteRepositoryIntegrationTest.java
│
└── unit/                              # Testes Unitários (Isolados)
    ├── cadastro/
    │   ├── application/
    │   │   └── usecases/
    │   │       ├── CadastroClienteUseCaseTest.java ✅
    │   │       ├── ListarClientesUseCaseTest.java ✅
    │   │       ├── BuscarClienteUseCaseTest.java ✅
    │   │       ├── AtualizarClienteUseCaseTest.java ✅
    │   │       └── DeletarClienteUseCaseTest.java ✅
    │   ├── domain/
    │   │   └── entities/
    │   │       └── ClienteTest.java
    │   └── presentation/
    │       └── ClienteControllerUnitTest.java
    │
    ├── estoque/
    │   ├── application/
    │   │   ├── EstoqueServiceUnitTest.java
    │   │   └── MovimentacaoServiceUnitTest.java
    │   ├── domain/
    │   │   └── entities/
    │   │       └── PecaInsumoUnitTest.java
    │   └── presentation/
    │       └── ItemEstoqueControllerUnitTest.java
    │
    ├── atendimento/
    │   ├── application/
    │   │   ├── services/
    │   │   │   ├── OrdemServicoServiceUnitTest.java
    │   │   │   └── OrcamentoServiceUnitTest.java
    │   │   └── usecases/
    │   │       └── CriarOrdemServicoUseCaseUnitTest.java
    │   ├── domain/
    │   │   └── entities/
    │   │       └── OrdemServicoUnitTest.java
    │   └── presentation/
    │       └── OrdemServicoControllerUnitTest.java
    │
    ├── acesso/
    │   ├── application/
    │   │   └── services/
    │   │       └── AuthServiceUnitTest.java
    │   ├── domain/
    │   │   └── entities/
    │   │       └── UsuarioUnitTest.java
    │   └── presentation/
    │       └── AuthControllerUnitTest.java
    │
    └── shared/
        ├── domain/
        │   └── valueobjects/
        │       ├── CpfCnpjUnitTest.java
        │       ├── EmailUnitTest.java
        │       └── TelefoneUnitTest.java
        └── infrastructure/
            └── security/
                └── SecurityConfigUnitTest.java
```

---

## 📊 Testes Criados até Agora ✅

### Unit Tests
- ✅ `CadastroClienteUseCaseTest` - 3 testes
- ✅ `ListarClientesUseCaseTest` - 3 testes
- ✅ `BuscarClienteUseCaseTest` - 3 testes
- ✅ `AtualizarClienteUseCaseTest` - 2 testes
- ✅ `DeletarClienteUseCaseTest` - 2 testes

**Total Unit Tests:** 13 testes

### Integration Tests
- ✅ Movimento dos testes de controller existentes
- ✅ ClienteControllerTest (7 testes)
- ✅ AuthControllerTest (5 testes)

**Total Integration Tests:** 12 testes

### Cucumber Tests
- ✅ CucumberRunnerTest com 6 cenários BDD

**Total Cucumber Tests:** 6 testes

---

## 🎯 Próximas Etapas para Aumentar Cobertura

### 1. Testes Unitários Faltantes (Prioridade Alta)
- [ ] ClienteTest (domain entity)
- [ ] PecaInsumoTest (já existe, mover para unit)
- [ ] OrdemServicoTest (domain entity)
- [ ] UsuarioTest (domain entity)
- [ ] ValueObjects (CpfCnpj, Email, Telefone, Cep)
- [ ] EstoqueServiceUnitTest
- [ ] MovimentacaoServiceUnitTest
- [ ] OrcamentoServiceUnitTest

### 2. Testes de Integração (Prioridade Média)
- [ ] ItemEstoqueControllerIntegrationTest
- [ ] OrdemServicoControllerIntegrationTest
- [ ] EstoqueServiceIntegrationTest
- [ ] OrdemServicoServiceIntegrationTest
- [ ] ClienteRepositoryIntegrationTest

### 3. Melhorias nos Testes Existentes
- [ ] Adicionar mais cenários negativos
- [ ] Adicionar testes de exceção
- [ ] Adicionar testes de validação

---

## 📈 Meta de Cobertura

| Nível | Cobertura Atual | Meta | Status |
|-------|-----------------|------|--------|
| Unitário | 13% | 70% | 🔴 Baixa |
| Integração | 10% | 50% | 🔴 Baixa |
| E2E (Cucumber) | 6% | 30% | 🔴 Baixa |
| **TOTAL** | **13%** | **80%** | 🟡 Progresso |

---

## ✅ Comandos para Organizar

### Mover testes para nova estrutura
```bash
# Testes Cucumber (já estruturados em cucumber/)
# Testes Integration (mover controllers)
# Testes Unit (novos testes em unit/)

# Executar testes da nova estrutura
mvn clean test -Dtest="**/unit/**,**/integration/**,**/cucumber/**"

# Gerar relatório de cobertura
mvn clean test jacoco:report
```

---

## 📝 Checklist de Implementação

- ✅ Criar estrutura de pastas (unit, integration, cucumber)
- ✅ Criar testes unitários para cadastro use cases
- ⏳ Criar testes unitários para estoque
- ⏳ Criar testes unitários para atendimento
- ⏳ Criar testes de integração para controllers
- ⏳ Mover testes Cucumber para pasta cucumber
- ⏳ Executar todos os testes
- ⏳ Validar cobertura

---

## 🚀 Execução

Para executar os testes da nova estrutura:

```bash
# Todos os testes
mvn clean test

# Apenas unitários
mvn clean test -Dtest="**/unit/**"

# Apenas integração
mvn clean test -Dtest="**/integration/**"

# Apenas BDD
mvn clean test -Dtest="**/cucumber/**"

# Com cobertura
mvn clean test jacoco:report

# Abrir relatório
start target/site/jacoco/index.html
```

---

**Data:** 24 de Junho de 2026  
**Status:** 🟡 Em Progresso  
**Progresso:** 31 testes criados, meta 80+ testes
