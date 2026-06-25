# Docker - Consolidação em Arquivo Único

## ✅ O que foi feito

### Antes (❌ Múltiplos arquivos)
- `docker-compose.yml` - PostgreSQL + App
- `docker-compose.wiremock.yml` - PostgreSQL + WireMock

### Agora (✅ Um único arquivo)
- `docker-compose.yml` - PostgreSQL + WireMock + App
- Arquivo `docker-compose.wiremock.yml` **REMOVIDO**

## 📋 Estrutura do Docker Compose Consolidado

```yaml
version: '3.8'

services:
  postgres:           # Porta 5433
    - Database
    - health check
    - volume: postgres_data
  
  wiremock:           # Porta 8081
    - Mock API Server
    - health check
    - mappings + files
    - depends_on: postgres
  
  app:                # Porta 8080
    - Spring Boot Application
    - depends_on: postgres, wiremock

networks:
  tech-network:      # Rede interna para comunicação

volumes:
  postgres_data:     # Persistência do banco
```

## 🚀 Como Usar

### Iniciar Todos os Serviços
```bash
docker-compose up -d
```

### Parar Todos os Serviços
```bash
docker-compose down
```

### Ver Status
```bash
docker-compose ps
```

### Ver Logs
```bash
# Todos os serviços
docker-compose logs -f

# Serviço específico
docker-compose logs -f postgres
docker-compose logs -f wiremock
docker-compose logs -f app
```

### Reiniciar Um Serviço
```bash
docker-compose restart wiremock
```

## 🌐 Acessos

Após iniciar com `docker-compose up -d`:

| Serviço | URL | Porta |
|---------|-----|-------|
| **PostgreSQL** | localhost:5433 | 5433 |
| **WireMock Admin** | http://localhost:8081/__admin/ | 8081 |
| **Aplicação** | http://localhost:8080 | 8080 |

## 🔧 Configurações

### Variáveis de Ambiente (`.env`)

```env
DB_NAME=tech_challenge
DB_USER=postgres
DB_PASS=postgres
```

### Valores Padrão (se .env não existir)

```
DB_NAME=tech_challenge
DB_USER=postgres
DB_PASS=postgres
```

## 📁 Arquivos WireMock

Estrutura que o Docker Compose monta automaticamente:

```
src/test/resources/wiremock/
├── mappings/          # Auto-monitorado
│   └── *.json
└── files/             # Auto-monitorado
    └── *.json
```

Qualquer arquivo adicionado nestas pastas é **automaticamente carregado** pelo WireMock.

## ✨ Benefícios da Consolidação

1. **Um único ponto de configuração** - sem duplicação
2. **Comunicação interna entre serviços** via rede `tech-network`
3. **Dependências ordenadas corretamente** - WireMock aguarda PostgreSQL
4. **Fácil manutenção** - todas as definições em um arquivo
5. **Escalabilidade** - pronto para adicionar mais serviços se necessário

## 🔄 Migrando de Múltiplos para Único

### Passo 1: Remover comando antigo
```bash
# NÃO use mais isso:
docker-compose -f docker-compose.wiremock.yml up -d

# USE isto:
docker-compose up -d
```

### Passo 2: Atualizar Documentação
Todos os guias foram atualizados para usar apenas:
```bash
docker-compose [comando]
```

## 📚 Documentos Atualizados

- ✅ QUICK_START_WIREMOCK.md
- ✅ GUIA_WIREMOCK.md
- ✅ RESUMO_WIREMOCK.md
- ✅ pom.xml (dependências)

## 🎯 Próximos Passos (Opcional)

Para adicionar novos serviços no futuro:

1. Adicione uma nova seção `services:` no `docker-compose.yml`
2. Configure a rede `tech-network`
3. Configure `depends_on` se necessário
4. Documente as novas portas e URLs

Exemplo:
```yaml
services:
  redis:
    image: redis:7-alpine
    container_name: tech-challenge-redis
    ports:
      - "6379:6379"
    networks:
      - tech-network
    depends_on:
      - postgres
```

## 🔍 Validação

Confirme que tudo está funcionando:

```bash
# Iniciar
docker-compose up -d

# Verificar
docker-compose ps

# Testar PostgreSQL
psql -h localhost -U postgres -d tech_challenge

# Testar WireMock
curl http://localhost:8081/__admin/

# Testar Aplicação
curl http://localhost:8080
```

## ✅ Status Final

- ✅ Arquivo `docker-compose.yml` consolidado
- ✅ Arquivo `docker-compose.wiremock.yml` removido
- ✅ Documentação atualizada
- ✅ Comandos simplificados
- ✅ Pronto para uso em produção
