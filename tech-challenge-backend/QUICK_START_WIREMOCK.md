# WireMock - Quick Start Guide

## 🎯 Iniciar em 3 Passos

### 1️⃣ Iniciar Docker Compose

```bash
docker-compose up -d
```

Verificar se está rodando:
```bash
docker-compose ps
```

### 2️⃣ Acessar Admin Interface

Abra no navegador:
```
http://localhost:8081/__admin/
```

Aqui você pode:
- Ver todos os mocks registrados
- Adicionar novos mocks manualmente
- Ver histórico de requisições

### 3️⃣ Testar Endpoints

```bash
# Login bem-sucedido
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"senha123"}'

# Listar clientes
curl http://localhost:8081/clientes \
  -H "Authorization: Bearer token123"

# Listar ordens de serviço
curl http://localhost:8081/api/atendimento/ordens \
  -H "Authorization: Bearer token123"

# Listar estoque
curl http://localhost:8081/estoque/itens \
  -H "Authorization: Bearer token123"

# Erro 404
curl http://localhost:8081/api/atendimento/ordens/buscar?id=999999
```

## 📂 Estrutura de Mocks

Todos os mocks estão em:
```
src/test/resources/wiremock/mappings/
```

Adicione novo mock JSON nessa pasta e reinicie:
```bash
docker-compose -f docker-compose.wiremock.yml restart wiremock
```

## 🛑 Parar Todos os Serviços

```bash
docker-compose down
```

## 📋 Mocks Disponíveis

| Endpoint | Método | Arquivo | Status |
|----------|--------|---------|--------|
| `/auth/login` | POST | `auth-login-success.json` | 200 ✅ |
| `/auth/login` | POST | `auth-login-unauthorized.json` | 401 ❌ |
| `/clientes` | GET | `cliente-list-all.json` | 200 ✅ |
| `/clientes/{id}` | GET | `cliente-get-by-cpf.json` | 200 ✅ |
| `/clientes` | POST | `cliente-create.json` | 201 ✅ |
| `/clientes/extended` | GET | `cliente-list-from-file.json` | 200 ✅ |
| `/api/atendimento/ordens` | GET | `ordem-servico-list.json` | 200 ✅ |
| `/api/atendimento/ordens/buscar` | GET | `ordem-servico-get-by-id.json` | 200 ✅ |
| `/estoque/itens` | GET | `estoque-list.json` | 200 ✅ |
| `/api/atendimento/ordens/buscar?id=999999` | GET | `error-not-found.json` | 404 ❌ |

## 🔧 Exemplo: Adicionar Novo Mock

1. Crie arquivo em `src/test/resources/wiremock/mappings/seu-mock.json`:

```json
{
  "request": {
    "method": "GET",
    "url": "/seu-endpoint"
  },
  "response": {
    "status": 200,
    "jsonBody": {
      "resultado": "sucesso"
    }
  }
}
```

2. Reinicie o WireMock:

```bash
docker-compose restart wiremock
```

3. Teste:

```bash
curl http://localhost:8081/seu-endpoint
```

## 🚨 Troubleshooting

### WireMock não inicia

```bash
# Ver logs
docker logs tech-challenge-wiremock

# Checar porta 8081
lsof -i :8081
```

### Mocks não funcionam

1. Verifique sintaxe JSON online
2. Reinicie o serviço
3. Acesse `/admin/` para confirmar que o mock foi registrado

### Erro 401 no admin

Considere usar a versão standalone sem autenticação.

## 📊 Estrutura de Resposta

### Simples JSON

```json
{
  "response": {
    "jsonBody": {"id": 1, "nome": "João"}
  }
}
```

### Com Arquivo Estático

```json
{
  "response": {
    "bodyFileName": "arquivo-resposta.json"
  }
}
```

### Com Template

```json
{
  "response": {
    "jsonBody": {
      "nome": "{{jsonPath request.body '$.nome'}}",
      "timestamp": "{{now format='yyyy-MM-dd'T'HH:mm:ss'}}"
    }
  }
}
```

## 🔐 Headers Comuns

### Verificar Authorization

```json
{
  "request": {
    "headers": {
      "Authorization": {
        "matches": "Bearer .*"
      }
    }
  }
}
```

### Adicionar Response Header

```json
{
  "response": {
    "headers": {
      "Content-Type": "application/json",
      "Authorization": "Bearer new-token"
    }
  }
}
```

## 💡 Dicas

- Use a interface `/admin/` para inspecionar requisições
- Teste padrões regex com `/admin/recorder`
- Use `bodyFileName` para respostas complexas
- Experimente response templating para dados dinâmicos
- Use `fixedDelayMilliseconds` para simular latência

## 📚 Documentação Completa

Veja `GUIA_WIREMOCK.md` para mais exemplos e recursos avançados.
