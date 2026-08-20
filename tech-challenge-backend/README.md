# Tech Challenge Backend

API REST para gerenciamento de ordens de serviço em oficina mecânica, desenvolvida com Spring Boot 3, PostgreSQL e arquitetura em camadas com Clean Architecture.

## 📋 Visão Geral

Sistema completo para:
- Autenticação e controle de acesso (usuários)
- Gerenciamento de clientes e veículos
- Criação e acompanhamento de ordens de serviço (OS)
- Gerenciamento de orçamentos e aprovações
- Controle de peças e estoque
- Rastreamento de histórico de status e operações

### Principais entregas
- Abertura de OS com cliente, veículo, serviços e peças
- Consulta e atualização do status da OS
- Aprovação/rejeição de orçamento por endpoint público
- Listagem priorizada de OS ativas
- Notificações por e-mail a cada mudança de status
- Containerização com Docker e manifestos Kubernetes

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|-----------|---------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.15-SNAPSHOT | Framework web |
| PostgreSQL | 16 | Banco de dados |
| Spring Security | 6.x | Autenticação e autorização |
| OAuth2 Resource Server | 6.x | Segurança de API |
| Spring Data JPA | 3.x | Persistência |
| Flyway | 11.7.2 | Migrações de banco |
| SpringDoc OpenAPI | 2.8.16 | Documentação Swagger |
| WireMock | 3.9.1 | Mocking de chamadas externas |
| Maven | 3.x | Gerenciador de dependências |
| Docker + Docker Compose | Latest | Containerização |

## 📦 Pré-requisitos

### Obrigatório
- **Java 21** - [Download](https://adoptium.net/)
- **Docker** (para executar com containers) - [Download](https://www.docker.com/products/docker-desktop)
- **Docker Compose** - Incluído no Docker Desktop

### Opcional (para rodar sem Docker)
- **PostgreSQL 16** - [Download](https://www.postgresql.org/download/)
- **Maven 3.8+** - [Download](https://maven.apache.org/)

## 🚀 Como Executar Localmente

### Opção 1: Com Docker Compose (Recomendado)

Mais simples e garante que o ambiente esteja correto.

#### 1. Preparar variáveis de ambiente
```bash
cp .env.example .env
```

O arquivo `.env` padrão já contém os valores necessários:
```env
DB_NAME=tech_challenge
DB_USER=postgres
DB_PASS=postgres
```

#### 2. Iniciar a aplicação
```bash
docker compose up --build
```

Aguarde até ver mensagens indicando que a aplicação está pronta:
- PostgreSQL: `database system is ready to accept connections`
- WireMock: `WireMock started`
- Aplicação: `Started TechChallengeBackendApplication`

#### 3. Acessar a aplicação
- **API**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Health Check**: [http://localhost:8080/ping](http://localhost:8080/ping)

#### 4. Parar a aplicação
```bash
docker compose down
```

Para remover os volumes (dados do banco também):
```bash
docker compose down -v
```

---

### Opção 2: Banco em Docker + Aplicação Local

Útil para desenvolvimento e debug.

#### 1. Iniciar apenas o banco de dados
```bash
docker compose up postgres wiremock -d
```

Aguarde 10-15 segundos para o banco inicializar.

#### 2. Configurar variáveis de ambiente locais

**Em Windows (PowerShell):**
```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5433/tech_challenge"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "postgres"
```

**Em Linux/Mac (Bash):**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/tech_challenge
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

Ou copie o `.env` e use em sua IDE.

#### 3. Executar a aplicação

**Via Maven:**
```bash
./mvnw spring-boot:run
```

Ou no Windows:
```bash
mvnw.cmd spring-boot:run
```

**Via IDE:**
- Abra o projeto em sua IDE (IntelliJ, Eclipse, VS Code)
- Configure o Java 21 como JDK
- Execute `TechChallengeBackendApplication.main()`

#### 4. Acessar a aplicação
- **API**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

#### 5. Parar a aplicação
Pressione `Ctrl+C` no terminal
```bash
docker compose down
```

---

### Opção 3: Tudo Local (sem Docker)

#### 1. Configurar PostgreSQL

**No Windows (usando pgAdmin ou psql):**
```sql
CREATE DATABASE tech_challenge;
```

**No Linux/Mac:**
```bash
createdb -h localhost -U postgres -p 5432 tech_challenge
```

#### 2. Configurar variáveis de ambiente

**Windows (PowerShell):**
```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/tech_challenge"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "postgres"
```

**Linux/Mac (Bash):**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tech_challenge
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

#### 3. Executar a aplicação
```bash
./mvnw spring-boot:run
```

#### 4. Migrações de banco
As migrações são executadas automaticamente via Flyway na inicialização.

---

## 🔌 Verificar se está funcionando

### Endpoint de Health Check
```bash
curl http://localhost:8080/ping
```

Resposta esperada:
```json
{
  "status": "ok",
  "db": "PostgreSQL 16.x on x86_64-pc-linux-musl, compiled by gcc (GCC) 12.2.0, 64-bit"
}
```

### Kubernetes (local, via Kind)

```bash
kubectl apply -k k8s/
```

Os manifestos incluem `Namespace`, `ConfigMap`, `Secret`, `PostgreSQL`, `Deployment`, `Service` e `HPA`. Passo a passo completo (criar cluster Kind, carregar a imagem, testar) em [`k8s/README.md`](k8s/README.md).

### Acessar Swagger UI
Abra no navegador: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## ☁️ Deploy na AWS (EKS via Terraform)

Sobe a mesma aplicação num cluster EKS real, provisionado via Terraform (pasta `terraform/`), usando os mesmos manifestos de `k8s/`.

> ⚠️ **Custo**: o cluster EKS e os nodes (`t3.medium`) geram cobrança contínua enquanto existirem — não é free tier. Se estiver numa AWS Academy com budget limitado, rode `terraform destroy` assim que terminar os testes.

### Pré-requisitos
- **Terraform** ≥ 1.5 — [Download](https://developer.hashicorp.com/terraform/downloads)
- **AWS CLI** configurado com as credenciais da sua sessão (AWS Academy: `aws configure` com as credenciais temporárias do Learner Lab)
- **kubectl**
- **Docker** (para publicar a imagem da aplicação)

### 1. Configurar os segredos

O `Secret` do Kubernetes (`tech-challenge-secrets`) **não** é lido a partir de `k8s/secret.yaml` quando provisionado via Terraform — aquele arquivo tem só placeholders e existe pro fluxo local com Kind, onde pode ficar versionado sem problema. No caminho AWS, os valores reais vêm de variáveis do Terraform marcadas como `sensitive`, sem default, definidas em [`terraform/vars.tf`](terraform/vars.tf) (`db_user`, `db_pass`, `jwt_secret`, `mail_username`, `mail_password`) e montadas em [`terraform/k8s-secret.tf`](terraform/k8s-secret.tf) via `yamlencode()` — assim o segredo real nunca precisa existir como texto num arquivo `.yaml` commitado.

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
```

Edite o `terraform.tfvars` recém-criado com os valores reais. Ele já está no `.gitignore` (`terraform/*.tfvars`) — só o `.example` (sem valores reais) fica versionado. Alternativa sem arquivo, via variáveis de ambiente:

```bash
export TF_VAR_db_user=postgres
export TF_VAR_db_pass=...
export TF_VAR_jwt_secret=...
export TF_VAR_mail_username=...
export TF_VAR_mail_password=...
```

Pro `jwt_secret`, a aplicação usa os bytes crus da string (`getBytes()`) como chave HMAC-SHA256, que exige no mínimo 256 bits (32 bytes) — gere um valor válido com:

```bash
python -c "import secrets; print(secrets.token_urlsafe(32))"
```

> ⚠️ `sensitive = true` só esconde o valor na saída do `plan`/`apply` no terminal — o valor real continua em texto puro no **state file**, armazenado no bucket S3 do backend. Quem tiver acesso de leitura a esse bucket vê os segredos. Para um projeto acadêmico isso costuma ser aceitável, mas vale no mínimo habilitar *versioning* + *encryption at rest* (SSE-S3, sem custo extra) no bucket.

### 2. Provisionar a infraestrutura

```bash
terraform init
terraform apply
```

Isso cria a VPC, o cluster EKS, o node group, o repositório ECR, os addons necessários (EBS CSI driver, metrics-server) e aplica todos os manifestos de `k8s/` (namespace, ConfigMap, Secret, Postgres, aplicação, Service, HPA, PDB). O `Ingress` também é criado, mas fica inerte — não há controller instalado (ver `TODO.md`), então a forma de acessar a API é via port-forward (passo 5).

### 3. Publicar a imagem da aplicação no ECR

O Terraform cria o repositório, mas não builda nem publica a imagem — isso é manual:

```bash
terraform output docker_build_and_push
```

Copie e rode os três comandos exibidos (`docker login`, `docker build`, `docker push`), **a partir da raiz do projeto** (onde está o `Dockerfile`), não de dentro de `terraform/`.

### 4. Apontar o kubectl para o cluster

```bash
terraform output configure_kubectl
```

Copie e rode o comando exibido (`aws eks update-kubeconfig ...`).

### 5. Verificar se os pods subiram

```bash
kubectl -n tech-challenge get pods -w
```

Espere os pods do Postgres e da aplicação ficarem `1/1 Running`. Se algum ficar em `ImagePullBackOff`, a imagem não foi publicada (volte ao passo 3). Se ficar em `CrashLoopBackOff`, veja os logs (`kubectl -n tech-challenge logs <pod> --previous`) e os eventos (`kubectl -n tech-challenge describe pod <pod>`).

### 6. Testar via port-forward

```bash
kubectl -n tech-challenge port-forward svc/tech-challenge-app 8080:80
```

Em outro terminal:
```bash
curl http://localhost:8080/actuator/health
```

Ou no navegador:
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

As coleções do Postman/Insomnia (seção "📚 Coleções Postman/Insomnia" mais abaixo) também funcionam aqui, apontando para `localhost:8080`.

### 7. Testar o autoscaling (HPA)

Gere carga contra o endpoint (ex.: com `hey`, `ab`, ou um pod `busybox` em loop de `wget`) e acompanhe:

```bash
kubectl -n tech-challenge get hpa -w
```

O número de réplicas deve subir conforme o uso de CPU/memória passa dos limites configurados (70%/75%).

### 8. Derrubar tudo

```bash
cd terraform
terraform destroy
```

---

## 📚 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/fiap/tech_challenge_backend/
│   │   ├── acesso/                          # Módulo de autenticação
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── presentation/
│   │   │
│   │   ├── cadastro/                        # Módulo de clientes e veículos
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── presentation/
│   │   │
│   │   ├── atendimento/                     # Módulo de ordens e orçamentos
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── presentation/
│   │   │
│   │   ├── estoque/                         # Módulo de peças e insumos
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── presentation/
│   │   │
│   │   ├── acompanhamento/                  # Módulo de acompanhamento
│   │   ├── relatorio/                       # Módulo de relatórios
│   │   │
│   │   ├── shared/                          # Código compartilhado
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── exceptions/
│   │   │
│   │   └── TechChallengeBackendApplication.java
│   │
│   └── resources/
│       ├── application.yml                  # Configurações principais
│       ├── application-dev.yml
│       ├── application-test.yml
│       └── db/
│           └── migration/                   # Scripts SQL do Flyway
│
├── test/
│   ├── java/
│   │   ├── integration/                     # Testes de integração
│   │   ├── unit/                            # Testes unitários
│   │   └── cucumber/                        # Testes BDD
│   │
│   └── resources/
│       ├── features/                        # Cenários Gherkin
│       └── wiremock/                        # Configurações WireMock
│
├── pom.xml                                  # Configuração Maven
├── Dockerfile                               # Build da imagem Docker
├── docker-compose.yml                       # Orquestração de containers
├── .env.example                             # Template de variáveis de ambiente
├── .gitignore
└── README.md
```

---

## 🔐 Autenticação

A API usa OAuth2 Resource Server com JWT.

### Para testar sem autenticação:
Use o endpoint `/ping` que não requer token.

### Para testar com autenticação:
1. Acesse [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
2. Use a seção de autenticação no Swagger para fazer login
3. Um token JWT será gerado
4. Use o token em chamadas subsequentes

---

## 🧪 Testes

### Executar todos os testes
```bash
./mvnw test
```

### Executar apenas testes unitários
```bash
./mvnw test -Dgroups=unit
```

### Executar apenas testes de integração
```bash
./mvnw test -Dgroups=integration
```

### Executar testes Cucumber/BDD
```bash
./mvnw verify
```

### Gerar relatório de cobertura
```bash
./mvnw clean test jacoco:report
```

Acesse o relatório em: `target/site/jacoco/index.html`

---

## 🐛 Debug

### Debug com Docker
Se estiver usando Docker Compose, o debugger está disponível na porta `5005`:

**Em sua IDE (IntelliJ/Eclipse):**
1. Run → Edit Configurations → Add new → Remote JVM Debug
2. Host: `localhost`, Port: `5005`
3. Clique em Debug

### Debug Local
1. Abra a aplicação via IDE
2. Set breakpoints
3. Run → Debug

---

## 📡 Endpoints Principais

### Autenticação
- `POST /auth/login` - Fazer login
- `GET /auth/me` - Obter dados do usuário autenticado

### Clientes
- `POST /clientes` - Criar cliente
- `GET /clientes` - Listar clientes
- `GET /clientes/{cpfCnpj}` - Buscar cliente
- `PUT /clientes/{cpfCnpj}` - Atualizar cliente
- `DELETE /clientes/{cpfCnpj}` - Deletar cliente

### Veículos
- `POST /veiculos` - Criar veículo
- `GET /veiculos` - Listar veículos
- `GET /veiculos/{placa}` - Buscar veículo
- `PUT /veiculos/{placa}` - Atualizar veículo
- `DELETE /veiculos/{placa}` - Deletar veículo

### Ordens de Serviço
- `POST /api/v1/ordens-servico/criar` - Criar OS
- `GET /api/v1/ordens-servico/listar-os` - Listar OS
- `GET /api/v1/ordens-servico/listar-os-priorizadas` - Listar OS ativas priorizadas
- `GET /api/v1/ordens-servico/buscar?id={id}` - Buscar OS
- `PUT /api/v1/ordens-servico/editar?id={id}` - Atualizar OS
- `DELETE /api/v1/ordens-servico/deletar?id={id}` - Remover OS
- `PATCH /api/v1/ordens-servico/alterar-status?id={id}` - Alterar status da OS
- `POST /api/v1/ordens-servico/criar-orcamento?id={id}` - Criar orçamento
- `GET /api/v1/ordens-servico/buscar-orcamento?idOS={id}&idOrcamento={orcamentoId}` - Buscar orçamento

### Público
- `GET /api/public/atendimento/ordens/{id}/autorizar` - Autorizar orçamento por link
- `PATCH /api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status` - Responder orçamento

### Estoque
- `POST /estoque/pecas` - Criar peça/insumo
- `GET /estoque/pecas` - Listar itens
- `GET /estoque/pecas/{codigo}` - Buscar item
- `PUT /estoque/pecas/{codigo}` - Atualizar item
- `DELETE /estoque/pecas/{codigo}` - Deletar item

Veja a documentação completa no Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📝 Variáveis de Ambiente

As variáveis estão definidas em `.env.example`. Principais:

```env
# Banco de Dados
DB_NAME=tech_challenge
DB_USER=postgres
DB_PASS=postgres

# Conexão (ajustes automáticos pelo Docker Compose)
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/tech_challenge
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Spring Profile
SPRING_PROFILES_ACTIVE=dev
```

No Kubernetes, os valores sensíveis vão para `Secret` e os demais parâmetros para `ConfigMap`.

---

## 🐳 Comandos Docker Úteis

```bash
# Iniciar tudo
docker compose up --build

# Iniciar em background
docker compose up -d

# Ver logs
docker compose logs -f

# Ver logs de um serviço específico
docker compose logs -f app

# Parar tudo
docker compose down

# Remover volumes (dados do banco também)
docker compose down -v

# Reconstruir imagens
docker compose up --build

# Executar comando no container
docker compose exec app bash

# Ver status dos containers
docker compose ps
```

---

## 📚 Coleções Postman/Insomnia

Disponível em: `Insomnia_Collection_TechChallenge.json`

Importe no Insomnia ou Postman para testar todos os endpoints.

---

## 🤝 Contribuindo

1. Crie uma branch para sua feature: `git checkout -b feature/minha-feature`
2. Commit suas mudanças: `git commit -m "feat: descrição"`
3. Push para a branch: `git push origin feature/minha-feature`
4. Abra um Pull Request

---

## 📞 Suporte e Dúvidas

Para dúvidas sobre a estrutura ou como rodar o projeto:

1. Verifique se todas as portas estão livres (8080, 5433)
2. Verifique o Java 21: `java -version`
3. Verifique o Docker: `docker --version` e `docker compose --version`
4. Consulte os logs: `docker compose logs -f`

---

## 📄 Licença

Este projeto foi desenvolvido como Tech Challenge para o programa de Pós-Graduação em Arquitetura de Software da FIAP (Especialização 16SOAT).

---

**Desenvolvido com ❤️ pelo Grupo 15**
