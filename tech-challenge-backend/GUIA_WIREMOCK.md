# Guia de Uso - WireMock

## O que é WireMock?

WireMock é um simulador HTTP flexível para testes de API. Ele permite criar mocks de serviços HTTP para testes sem depender de serviços reais.

## Instalação e Configuração

### 1. Dependências Maven

As dependências do WireMock já foram adicionadas ao `pom.xml`:

```xml
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock-standalone</artifactId>
    <version>3.9.1</version>
    <scope>test</scope>
</dependency>
```

### 2. Docker Compose

Para iniciar todos os serviços (PostgreSQL, WireMock e App):

```bash
docker-compose up -d
```

Isso iniciará:
- **PostgreSQL** na porta 5433
- **WireMock** na porta 8081
- **Aplicação** na porta 8080

Para parar:

```bash
docker-compose down
```

## Estrutura de Pastas

```
src/test/resources/wiremock/
├── mappings/              # Definições de mocks (JSON)
│   ├── auth-login-success.json
│   ├── auth-login-unauthorized.json
│   ├── cliente-list-all.json
│   ├── cliente-get-by-cpf.json
│   ├── cliente-create.json
│   ├── ordem-servico-list.json
│   ├── ordem-servico-get-by-id.json
│   └── error-not-found.json
└── files/                 # Arquivos de resposta (XML, HTML, etc)
```

## Criando Novos Mocks

### Estrutura Básica de um Mock

```json
{
  "request": {
    "method": "GET",
    "url": "/api/endpoint",
    "headers": {
      "Authorization": {
        "matches": "Bearer .*"
      }
    }
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "jsonBody": {
      "id": 1,
      "nome": "Exemplo"
    }
  }
}
```

### Exemplos de Padrões

#### 1. Correspondência Exata de URL

```json
{
  "request": {
    "method": "GET",
    "url": "/clientes"
  },
  "response": {
    "status": 200,
    "jsonBody": []
  }
}
```

#### 2. URL com Regex

```json
{
  "request": {
    "method": "GET",
    "urlPattern": "/clientes/\\d+"
  },
  "response": {
    "status": 200,
    "jsonBody": {"id": 1, "nome": "João"}
  }
}
```

#### 3. Correspondência de Request Body

```json
{
  "request": {
    "method": "POST",
    "url": "/auth/login",
    "bodyPatterns": [
      {
        "matchesJsonSchema": {
          "type": "object",
          "properties": {
            "email": {"type": "string"},
            "password": {"type": "string"}
          },
          "required": ["email", "password"]
        }
      }
    ]
  },
  "response": {
    "status": 200,
    "jsonBody": {"accessToken": "token123"}
  }
}
```

#### 4. Usando Templating (Response Dinâmica)

```json
{
  "request": {
    "method": "POST",
    "url": "/clientes"
  },
  "response": {
    "status": 201,
    "jsonBody": {
      "id": "{{randomValue length=19}}",
      "nome": "{{jsonPath request.body '$.nome'}}",
      "dataCriacao": "{{now format='yyyy-MM-dd'T'HH:mm:ss'}}"
    }
  }
}
```

## Usando WireMock nos Testes

### Teste Básico

```java
@Test
void testComWireMock() {
    stubFor(get(urlEqualTo("/clientes"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\": 1}]")));

    // Seu código aqui que faz request para WireMock
}
```

### Verificar que a Requisição foi Feita

```java
verify(getRequestedFor(urlEqualTo("/clientes"))
    .withHeader("Authorization", matching("Bearer .*")));
```

### Usar WireMock com Spring Boot

```java
@SpringBootTest
@AutoConfigureWebClient
class MyIntegrationTest {
    
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8081))
        .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.api.url", wireMock::baseUrl);
    }
}
```

## Cenários de Teste Pré-Configurados

### 1. Autenticação (auth-login-success.json)
- **URL**: `/auth/login`
- **Método**: POST
- **Resposta**: Token JWT válido
- **Status**: 200

### 2. Falha de Autenticação (auth-login-unauthorized.json)
- **URL**: `/auth/login`
- **Método**: POST
- **Resposta**: Erro de autenticação
- **Status**: 401

### 3. Listar Clientes (cliente-list-all.json)
- **URL**: `/clientes`
- **Método**: GET
- **Resposta**: Array com 2 clientes
- **Status**: 200

### 4. Buscar Cliente por CPF (cliente-get-by-cpf.json)
- **URL**: `/clientes/{id}`
- **Método**: GET
- **Resposta**: Dados do cliente com veículos
- **Status**: 200

### 5. Criar Cliente (cliente-create.json)
- **URL**: `/clientes`
- **Método**: POST
- **Resposta**: Cliente criado com response templating
- **Status**: 201

### 6. Listar Ordens de Serviço (ordem-servico-list.json)
- **URL**: `/api/atendimento/ordens`
- **Método**: GET
- **Resposta**: Array com 2 ordens de serviço
- **Status**: 200

### 7. Buscar Ordem de Serviço (ordem-servico-get-by-id.json)
- **URL**: `/api/atendimento/ordens/buscar?id={id}`
- **Método**: GET
- **Resposta**: Dados completos da OS com histórico
- **Status**: 200

### 8. Erro 404 (error-not-found.json)
- **URL**: `/api/atendimento/ordens/buscar?id=999999`
- **Método**: GET
- **Resposta**: Erro de recurso não encontrado
- **Status**: 404

## Executar Testes com WireMock

```bash
# Rodar todos os testes
mvn test

# Rodar apenas testes de integração com WireMock
mvn test -Dtest=WireMock*
```

## Admin Interface

Quando WireMock está rodando, você pode acessar a interface administrativa:

- **URL**: http://localhost:8081/__admin/

Funcionalidades:
- Ver todos os mocks registrados
- Adicionar novos mocks
- Ver histórico de requisições
- Resetar mocks

## Exemplos de Uso Avançado

### Mock com Delay

```json
{
  "request": {
    "method": "GET",
    "url": "/slow-endpoint"
  },
  "response": {
    "status": 200,
    "fixedDelayMilliseconds": 3000,
    "jsonBody": {"data": "delayed"}
  }
}
```

### Mock com Múltiplas Respostas

```json
{
  "request": {
    "method": "GET",
    "url": "/toggle-endpoint"
  },
  "scenarioName": "State Machine",
  "requiredScenarioState": "Started",
  "newScenarioState": "Next",
  "response": {
    "status": 200,
    "jsonBody": {"state": "first"}
  }
}
```

## Troubleshooting

### WireMock não inicia no Docker

```bash
# Ver logs
docker logs tech-challenge-wiremock

# Verificar porta disponível
lsof -i :8081
```

### Mocks não sendo encontrados

1. Verifique se os arquivos estão em `src/test/resources/wiremock/mappings/`
2. Verifique a sintaxe JSON com um validador online
3. Reinicie o WireMock: `docker-compose -f docker-compose.wiremock.yml restart wiremock`

### Erro de Correspondência

Use a interface admin para ver requisições que não foram capturadas e ajuste o padrão do mock.

## Referências

- [WireMock Documentation](https://wiremock.org/)
- [Response Templating](https://wiremock.org/docs/response-templating/)
- [Matching Requests](https://wiremock.org/docs/request-matching/)
