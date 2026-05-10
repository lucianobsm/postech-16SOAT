# Tech Challenge Backend

API REST desenvolvida com Spring Boot 3 e PostgreSQL.

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Security + OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL 16
- Docker + Docker Compose
- Maven

## Pré-requisitos

- [Java 21](https://adoptium.net/)
- [Docker](https://www.docker.com/)

## Configuração

Copie o arquivo de exemplo e ajuste as credenciais se necessário:

```bash
cp .env.example .env
```

O `.env` padrão já funciona para desenvolvimento:

```env
DB_NAME=tech_challenge
DB_USER=postgres
DB_PASS=postgres
```

## Como rodar

### Apenas o banco (app rodando pela IDE/Maven)

```bash
docker compose -f docker-compose.db.yml up -d
./mvnw spring-boot:run
```

### Aplicação completa via Docker

```bash
docker compose up --build
```

### Após atualizar o código

```bash
docker compose up --build -d
```

## Endpoints

| Método | Rota    | Descrição                              | Auth |
|--------|---------|----------------------------------------|------|
| GET    | `/ping` | Verifica se a app e o banco estão ok   | Não  |

### Exemplo de resposta — `GET /ping`

```json
{
  "status": "ok",
  "db": "PostgreSQL 16.x on x86_64-pc-linux-musl..."
}
```

## Estrutura do projeto

```
src/
  main/
    java/com/fiap/tech_challenge_backend/
      TechChallengeBackendApplication.java  # entrypoint
      SecurityConfig.java                   # configuração de segurança
      PingController.java                   # endpoint de healthcheck
    resources/
      application.yml                       # configurações da aplicação
docker-compose.yml        # banco + aplicação
docker-compose.db.yml     # apenas o banco
Dockerfile                # build da imagem da aplicação
.env.example              # template de variáveis de ambiente
```
