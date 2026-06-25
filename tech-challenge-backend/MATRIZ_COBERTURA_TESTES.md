# 📊 Matriz de Cobertura de Testes por Módulo

**Atualizado:** 24 de Junho de 2026

---

## 🎯 Cobertura Geral

```
████████████████████████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
13% (1.177 de 8.448 instruções) | 5% de branches

META: 80% (6.758+ de 8.448 instruções)
FALTAM: ~5.581 instruções (~67%)
```

---

## 📁 CADASTRO (Cliente, Veículo)

### Controllers
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| **ClienteController** | **76%** | 7 | ✅ BOA |
| **VeiculoController** | 0% | 0 | 🔴 FALTAM |

### Use Cases
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| CadastroClienteUseCase | 9% | 0 | 🔴 BAIXA |
| ListarClientesUseCase | 9% | 0 | 🔴 BAIXA |
| BuscarClienteUseCase | 9% | 0 | 🔴 BAIXA |
| AtualizarClienteUseCase | 9% | 0 | 🔴 BAIXA |
| DeletarClienteUseCase | 9% | 0 | 🔴 BAIXA |
| CadastroVeiculoUseCase | 0% | 0 | 🔴 NENHUM |

### Domain
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| Cliente (Entity) | 0% | 0 | 🔴 FALTAM |
| Veiculo (Entity) | 0% | 0 | 🔴 FALTAM |

### Total Cadastro
```
████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
14% (7 testes / 15+ recomendados)
FALTAM: 8+ testes para atingir 70%
```

---

## 🚗 ATENDIMENTO (Ordem de Serviço, Orçamento)

### Controllers
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| OrdemServicoController | 9% | 0 | 🔴 BAIXA |
| ClienteOrdemServicoController | 0% | 0 | 🔴 NENHUM |
| RelatorioAtendimentoController | 0% | 0 | 🔴 NENHUM |

### Services
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| OrdemServicoService | 11% | 0 | 🔴 MUITO BAIXA |
| RelatorioEnriquecimentoService | 0% | 0 | 🔴 NENHUM |
| IdGeneratorService | 0% | 0 | 🔴 NENHUM |

### Use Cases
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| CriarOrdemServicoUseCase | 0% | 0 | 🔴 NENHUM |
| AlterarStatusOrdemServicoUseCase | 0% | 0 | 🔴 NENHUM |
| CriarOrcamentoUseCase | 0% | 0 | 🔴 NENHUM |
| BuscarOrcamentoUseCase | 0% | 0 | 🔴 NENHUM |

### Domain
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| OrdemServico (Entity) | 0% | 0 | 🔴 FALTAM |
| OsOrcamento (Entity) | 0% | 0 | 🔴 FALTAM |
| OsHistoricoStatus | 0% | 0 | 🔴 FALTAM |

### Total Atendimento
```
██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
3% (0 testes / 20+ recomendados)
FALTAM: 20+ testes para atingir 50%
```

---

## 📦 ESTOQUE (Peças, Insumos, Movimentação)

### Controllers
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| ItemEstoqueController | 1% | 0 | 🔴 CRÍTICO |
| MovimentacaoController | 0% | 0 | 🔴 NENHUM |

### Services
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| EstoqueService | 4% | 0 | 🔴 CRÍTICO |
| MovimentacaoService | 0% | 0 | 🔴 NENHUM |

### Domain
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| PecaInsumo (Entity) | 66% | 1 | 🟡 ACEITÁVEL |
| MovimentacaoEstoque (Entity) | 0% | 0 | 🔴 FALTAM |

### Total Estoque
```
█░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
2% (1 teste / 12+ recomendados)
FALTAM: 11+ testes para atingir 60%
```

---

## 🔐 ACESSO (Autenticação, Autorização)

### Controllers
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| AuthController | 78% | 5 | ✅ BOM |
| MeController | 0% | 0 | 🔴 FALTAM |

### Services
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| AuthService | 0% | 0 | 🔴 FALTAM |
| JwtTokenProvider | 57% | 0 | 🟡 ACEITÁVEL |

### Domain
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| Usuario (Entity) | 0% | 0 | 🔴 FALTAM |
| Role (Enum) | 100% | 0 | ✅ 100% |

### Total Acesso
```
███████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
42% (5 testes / 8+ recomendados)
FALTAM: 3+ testes para atingir 80%
```

---

## 📝 COMPARTILHADO (Shared)

### Value Objects
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| CpfCnpj | 10% | 0 | 🔴 CRÍTICO |
| Email | 0% | 0 | 🔴 CRÍTICO |
| Telefone | 0% | 0 | 🔴 CRÍTICO |
| Cep | 0% | 0 | 🔴 CRÍTICO |

### Infrastructure
| Classe | Cobertura | Testes | Status |
|--------|-----------|--------|--------|
| SecurityConfig | 9% | 0 | 🔴 BAIXA |
| RestExceptionHandler | 0% | 0 | 🔴 FALTAM |

### Total Compartilhado
```
█░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
2% (0 testes / 15+ recomendados)
FALTAM: 15+ testes para atingir 70%
```

---

## 📊 Resumo por Prioridade

### 🔴 CRÍTICO (0-10% cobertura) - 35+ testes faltando

| Módulo | Classes | Gap |
|--------|---------|-----|
| **Estoque** | ItemEstoqueController, EstoqueService | 11+ testes |
| **Value Objects** | CpfCnpj, Email, Telefone, Cep | 15+ testes |
| **Atendimento** | OrdemServicoController, Services | 20+ testes |
| **TOTAL CRÍTICO** | **12 classes** | **~46+ testes** |

### 🟡 ALTO (11-40% cobertura) - 15+ testes faltando

| Módulo | Classes | Gap |
|--------|---------|-----|
| **Cadastro** | Use Cases | 8+ testes |
| **Acesso** | Services | 3+ testes |
| **TOTAL ALTO** | **6 classes** | **~11+ testes** |

### 🟢 BOM (60%+ cobertura) - Apenas manutenção

| Módulo | Classes | Cobertura |
|--------|---------|-----------|
| ClienteController | ✅ | 76% |
| AuthController | ✅ | 78% |
| SecurityConfig | ✅ | 86% |
| **TOTAL BOM** | **3 classes** | **70%+** |

---

## 🚀 Plano de Implementação

### Fase 1: Value Objects (CRÍTICO)
```
└── src/test/java/.../unit/shared/domain/valueobjects/
    ├── CpfCnpjUnitTest.java (5 testes)
    ├── EmailUnitTest.java (4 testes)
    ├── TelefoneUnitTest.java (3 testes)
    └── CepUnitTest.java (3 testes)

IMPACTO: +15 testes, +15-20% cobertura
```

### Fase 2: Domain Entities (CRÍTICO)
```
└── src/test/java/.../unit/*/domain/entities/
    ├── ClienteUnitTest.java (5 testes)
    ├── OrdemServicoUnitTest.java (5 testes)
    ├── PecaInsumoUnitTest.java (3 testes)
    └── UsuarioUnitTest.java (4 testes)

IMPACTO: +17 testes, +20-25% cobertura
```

### Fase 3: Services (CRÍTICO)
```
└── src/test/java/.../unit/*/application/services/
    ├── EstoqueServiceUnitTest.java (6 testes)
    ├── OrdemServicoServiceUnitTest.java (8 testes)
    └── AuthServiceUnitTest.java (4 testes)

IMPACTO: +18 testes, +15-20% cobertura
```

### Fase 4: Integração (MÉDIO)
```
└── src/test/java/.../integration/*/
    ├── ItemEstoqueControllerIntegrationTest.java (5 testes)
    ├── OrdemServicoControllerIntegrationTest.java (5 testes)
    └── RepositoryIntegrationTest.java (3 testes)

IMPACTO: +13 testes, +10-15% cobertura
```

---

## 📈 Projeção de Implementação

```
FASE 1: Value Objects (15 testes)
████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Cobertura estimada: 28%

FASE 2: Entities (17 testes)
████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Cobertura estimada: 48%

FASE 3: Services (18 testes)
████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Cobertura estimada: 64%

FASE 4: Integração (13 testes)
████████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Cobertura estimada: 80%+
```

---

## ✅ Próximos Passos

1. **Imediato:** Implementar testes de Value Objects (Fase 1)
2. **Curto Prazo:** Implementar testes de Domain Entities (Fase 2)
3. **Médio Prazo:** Implementar testes de Services (Fase 3)
4. **Longo Prazo:** Completar com testes de integração (Fase 4)

**Meta Final:** 80% de cobertura em todas as 8 áreas (Cadastro, Atendimento, Estoque, Acesso, Compartilhado, BDD)

---

**Preparado:** 24 de Junho de 2026  
**Status:** Pronto para Implementação  
**Estimativa Total:** 63 testes novos necessários
