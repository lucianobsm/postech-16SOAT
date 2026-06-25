# Implementação: Orçamento Adicional com Textos Diferenciados

## 📋 Resumo Executivo

Foram implementadas melhorias na comunicação de orçamentos adicionais (ADICIONAL vs INICIAL) em **Email** e **PDF**, com textos empáticos e profissionais que explicam claramente os novos problemas encontrados.

---

## 🎯 O Que Mudou

### **1. Detecção Automática de Tipo de Orçamento**

**Em:** `OrdemServicoService.java - enviarEmailOrcamento()`

```java
// Detecta se é ADICIONAL ou INICIAL
String corpoHtml = orcamento.getTipo() == TipoOrcamento.ADICIONAL
    ? montarTemplateEmailOrcamentoAdicional(os, orcamento, linkAprovacao)
    : montarTemplateEmailOrcamento(os, orcamento, linkAprovacao);

// Define assunto diferenciado
String assunto = orcamento.getTipo() == TipoOrcamento.ADICIONAL
    ? String.format("Orçamento Adicional Necessário - OS #%s | Novos Problemas Identificados", os.getId())
    : String.format("Seu orçamento está pronto para análise - OS #%s", os.getId());
```

### **2. Email Customizado para ADICIONAL**

**Novo método:** `montarTemplateEmailOrcamentoAdicional()`

Características:
- ✅ Cabeçalho com ícone de alerta (⚠️)
- ✅ Seção "Por Que um Orçamento Adicional?"
- ✅ Explicação empática sobre novos problemas
- ✅ Caixa de contato direto com mecânico
- ✅ Botões de aprovação/rejeição destacados
- ✅ Cores diferenciadas (amarelo/vermelho para alerta)

### **3. PDF Customizado para ADICIONAL**

**Novo método:** `adicionarAlertaOrcamentoAdicional()`

Características:
- ✅ Seção de alerta vermelha após cabeçalho
- ✅ Mensagem destacada: "ORÇAMENTO ADICIONAL - PROBLEMAS IDENTIFICADOS"
- ✅ Explicação clara do motivo
- ✅ Recomendação de contato com mecânico
- ✅ Fundo vermelho (#DC3545) com bordas profissionais

---

## 📧 **Email - Diferenças**

### INICIAL
```
Assunto: Seu orçamento está pronto para análise - OS #20260001

[Cabeçalho azul padrão]
[Seções normais: dados, serviços, peças]
[Valor total destacado em verde]
```

### ADICIONAL
```
Assunto: Orçamento Adicional Necessário - OS #20260001 | Novos Problemas Identificados

[Cabeçalho azul padrão]
[ALERTA AMARELO: "ℹ️ Informação Importante"]
[CAIXA VERMELHA: "Problemas Identificados"]
[CAIXA VERDE: "✓ O Que Fazer Agora?"]
[CAIXA AZUL: "📞 Dúvidas? Converse com Nosso Mecânico"]
[Seções de orçamento]
[Botões: Aprovar | Rejeitar]
```

---

## 📄 **PDF - Diferenças**

### INICIAL
```
[Cabeçalho profissional azul]
[Título: "ORÇAMENTO DE MANUTENÇÃO E REPARO VEICULAR"]
[Dados do orçamento]
[Tabelas de serviços e peças]
[Valor total destacado]
```

### ADICIONAL
```
[Cabeçalho profissional azul]
[ALERTA VERMELHO:]
╔════════════════════════════════════════╗
║ ⚠️  ORÇAMENTO ADICIONAL                ║
║ PROBLEMAS IDENTIFICADOS               ║
╚════════════════════════════════════════╝
"Durante a execução dos serviços diagnósticos, 
nossos especialistas identificaram problemas 
adicionais que requerem atenção técnica..."

[Título: "ORÇAMENTO DE MANUTENÇÃO E REPARO VEICULAR"]
[Dados do orçamento]
[Tabelas de serviços e peças]
[Valor total destacado em VERDE]
```

---

## 💬 **Textos Principais**

### Email - Alerta
```
⚠️ Informação Importante:
Durante o diagnóstico técnico de seu veículo, nossos 
especialistas identificaram PROBLEMAS ADICIONAIS que 
precisam de atenção. Para garantir a segurança e o 
bom funcionamento do seu veículo, preparamos um 
orçamento complementar.
```

### Email - Por Que?
```
🔧 Por Que um Orçamento Adicional?

Ao decorrer da execução inicial dos serviços, nossos 
mecânicos identificaram problemas adicionais que não 
eram visíveis na inspeção preliminar. Estes problemas, 
se não resolvidos, podem:

• Afetar a segurança do veículo
• Causar maiores danos a longo prazo
• Impedir o funcionamento adequado de sistemas importantes

É uma situação comum em diagnósticos completos - 
quanto mais detalhado, mais problemas podem aparecer.
```

### Email - Contato
```
📞 Dúvidas? Converse com Nosso Mecânico

Recomendamos ENTRAR EM CONTATO com o mecânico 
responsável para:

• Esclarecer detalhes sobre os problemas encontrados
• Entender a importância de cada serviço
• Discutir opções e alternativas
• Responder qualquer dúvida técnica

Equipe responsável está disponível:
📞 (11) 3000-0000 | 📧 mecanico@oficina.com.br
```

---

## 🔄 **Fluxo de Detecção**

```
1. Endpoint: POST /api/v1/ordens-servico/criar-orcamento?id=20260001

2. Request Body:
{
  "tipo": "ADICIONAL",  ← Detectado aqui
  "prazoEstipulado": "2026-07-01",
  "servicos": [...],
  "pecas": [...]
}

3. Lógica:
   if (orcamento.getTipo() == TipoOrcamento.ADICIONAL) {
     // Email customizado
     // PDF com alerta
     // Assunto diferenciado
   }

4. Resultado:
   ✅ Email com contexto de problema adicional
   ✅ PDF com seção de alerta em vermelho
   ✅ Botão de contato com mecânico destacado
```

---

## 📊 **Arquivos Modificados**

| Arquivo | Mudanças |
|---------|----------|
| `OrdemServicoService.java` | ✏️ Método `enviarEmailOrcamento()` refatorado |
| `OrdemServicoService.java` | ✨ Novo método `montarTemplateEmailOrcamentoAdicional()` |
| `PdfGeneratorAdapter.java` | ✏️ Adicionado import `TipoOrcamento` |
| `PdfGeneratorAdapter.java` | ✨ Novo método `adicionarAlertaOrcamentoAdicional()` |
| `TEMPLATE_ORCAMENTO_ADICIONAL.md` | 📄 Documentação de templates |

---

## 🎨 **Cores Utilizadas**

### Email
- **Alerta:** `#fff3cd` (fundo) com `#ffc107` (borda) - Amarelo
- **Problema:** `#fff` (fundo) com `#e74c3c` (borda) - Vermelho
- **Ação:** `#e8f5e9` (fundo) com `#27ae60` (borda) - Verde
- **Contato:** `#e3f2fd` (fundo) com `#2196f3` (borda) - Azul

### PDF
- **Alerta:** `#DC3545` (fundo vermelho) com texto branco
- **Texto complementar:** `#FFCC00` (amarelo) para destaques
- **Bordas:** `1.5pt` sólida

---

## ✅ **Checklist de Implementação**

- [x] Método `montarTemplateEmailOrcamentoAdicional()` criado
- [x] Método `adicionarAlertaOrcamentoAdicional()` criado
- [x] Detecção automática de `TipoOrcamento` implementada
- [x] Assunto diferenciado para ADICIONAL
- [x] Email com cores e seções específicas
- [x] PDF com alerta vermelho na parte superior
- [x] Textos profissionais e empáticos
- [x] Contato do mecânico destacado
- [x] Compilação sem erros
- [x] Documentação completa

---

## 🚀 **Como Usar**

### Criar Orçamento INICIAL
```bash
curl -X POST "http://localhost:8080/api/v1/ordens-servico/criar-orcamento?id=20260001" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INICIAL",
    "prazoEstipulado": "2026-07-01",
    "servicos": [{"servicoId": 1}],
    "pecas": [{"pecaId": 5, "quantidade": 2}]
  }'
```

**Resultado:** Email padrão, PDF padrão

### Criar Orçamento ADICIONAL
```bash
curl -X POST "http://localhost:8080/api/v1/ordens-servico/criar-orcamento?id=20260001" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "ADICIONAL",
    "prazoEstipulado": "2026-07-15",
    "servicos": [{"servicoId": 2}],
    "pecas": [{"pecaId": 6, "quantidade": 1}]
  }'
```

**Resultado:** 
- Email com assunto alterado
- Email com seções de alerta
- PDF com seção vermelha de aviso
- Contato do mecânico destacado

---

## 💡 **Benefícios**

✅ **Clareza:** Cliente entende por que há um orçamento adicional
✅ **Empatia:** Texto reconhece que é uma situação inesperada
✅ **Transparência:** Explica a importância dos novos serviços
✅ **Contato:** Facilita comunicação com mecânico
✅ **Profissionalismo:** Design consistente entre email e PDF
✅ **Confiança:** Motiva aprovação pelo entendimento da situação

