# Pipeline de Aplicação — Build, Testes, ECR e Deploy no Kubernetes

Este documento explica o workflow **`pipeline.yml`**: o que cada job faz, quando
ele roda, quais secrets ele precisa e como a imagem Docker chega até os pods
rodando no EKS.

Os outros dois workflows desta pasta não fazem parte deste guia:
- `terraform.yml` — provisiona a infraestrutura (VPC/EKS/ECR/K8s). Veja
  [`TERRAFORM-PIPELINE.md`](./TERRAFORM-PIPELINE.md).
- `sonar.yml` — análise estática no SonarCloud.

> **Pré-requisito:** o `terraform.yml` (`action: apply`) precisa ter rodado
> pelo menos uma vez antes deste pipeline conseguir fazer deploy — é ele quem
> cria o cluster EKS, o repositório ECR e aplica os manifests do Kubernetes
> pela primeira vez (ver `tech-challenge-backend/terraform/k8s-deployment.tf`).
> O `pipeline.yml` só **atualiza a imagem** de um Deployment que já existe.

---

## Visão geral: os 5 jobs

```
build ──┬──> test ──────────────┐
         └──> integration-test ──┴──> build-and-push-image ──> deploy
```

| Job | O que faz | Quando roda |
|---|---|---|
| `build` | Compila a aplicação Java com Maven | Sempre |
| `test` | Roda os testes unitários + relatório JaCoCo | Sempre |
| `integration-test` | Roda os testes de integração (Failsafe) | Sempre |
| `build-and-push-image` | Builda a imagem Docker e publica no ECR | Só em push na `main` ou disparo manual (não roda em Pull Request) |
| `deploy` | Atualiza a imagem do Deployment no EKS | Só depois que `build-and-push-image` roda e publica com sucesso |

Os três primeiros jobs (`build`, `test`, `integration-test`) já existiam e não
mudaram de comportamento. Este documento foca nos dois novos: **`build-and-push-image`**
e **`deploy`**.

---

## Quando o pipeline dispara

```yaml
on:
  push:
    branches: [ "feature/**", "main" ]
  pull_request:
    branches: [ "main", "develop" ]
  workflow_dispatch: {}
```

- **Push em `feature/**`** — roda `build`, `test`, `integration-test`. Como
  não é a `main`, os jobs de ECR/deploy **não** rodam (o `if:` deles exige
  `github.ref == 'refs/heads/main'` ou disparo manual). Serve só para validar
  a branch antes de abrir PR.
- **Pull Request para `main`/`develop`** — roda `build`, `test`,
  `integration-test`. `build-and-push-image` e `deploy` **nunca** rodam em PR
  (mesmo que o PR seja contra `main`), porque publicar imagem/fazer deploy a
  partir de um PR ainda não revisado seria arriscado.
- **Push direto na `main`** (ex.: depois que um PR é mergeado) — roda os 5
  jobs em sequência. Se `test` ou `integration-test` falharem, `build-and-push-image`
  e `deploy` não rodam (dependência via `needs:`).
- **Disparo manual (`workflow_dispatch`)** — "Actions" → "Java CI/CD Pipeline"
  → "Run workflow". Útil para forçar um novo build+deploy sem precisar de um
  commit novo (ex.: credenciais do ECR/EKS mudaram, ou você quer re-implantar
  a mesma imagem). Funciona em qualquer branch selecionada no disparo, mas o
  `if:` do `deploy` deve ser usado com cuidado: rodar manualmente numa branch
  que não é `main` builda e publica a imagem normalmente (o `if:` do
  `build-and-push-image` libera para `workflow_dispatch` em qualquer branch),
  então prefira disparar manualmente a partir da `main`.

---

## Secrets que este pipeline precisa

São os **mesmos secrets AWS já usados pelo `terraform.yml`** — nenhum secret
novo é necessário:

| Secret | Para quê |
|---|---|
| `AWS_ACCESS_KEY_ID` | Autenticação AWS (login no ECR + `aws eks update-kubeconfig`) |
| `AWS_SECRET_ACCESS_KEY` | Autenticação AWS |
| `AWS_SESSION_TOKEN` | Autenticação AWS (obrigatório no AWS Academy, credenciais são temporárias) |

**⚠️ Mesmo aviso do `terraform.yml`:** as credenciais do AWS Academy expiram
em poucas horas. Se o job `build-and-push-image` ou `deploy` falhar na etapa
"Configurar Credenciais AWS" com erro `ExpiredToken`/`InvalidClientTokenId`,
pegue credenciais novas no painel do Academy e atualize os três secrets.

---

## Job `build-and-push-image` — passo a passo

1. **Checkout** — baixa o código do repositório.
2. **Configurar Credenciais AWS** — autentica com os secrets `AWS_*` (via
   `aws-actions/configure-aws-credentials`), região `us-east-1`.
3. **Login no Amazon ECR** — `aws-actions/amazon-ecr-login` faz `docker login`
   no registry da conta e devolve a URL do registry (`steps.ecr-login.outputs.registry`).
4. **Build, Tag e Push da Imagem Docker** — dentro de `tech-challenge-backend/`
   (onde está o `Dockerfile`):
   - `docker build` gera a imagem com **duas tags**:
     - `<registry>/tech-challenge-backend:<sha-do-commit>` — tag imutável,
       identifica exatamente qual commit gerou aquela imagem.
     - `<registry>/tech-challenge-backend:latest` — conveniência, sempre
       aponta para o build mais recente da `main`.
   - `docker push` publica as duas tags no ECR.
   - O nome completo da imagem (`registry/repo:sha`) é exposto como
     `outputs.image` do job, para o job `deploy` usar em seguida — assim o
     `deploy` não precisa adivinhar ou reconstruir essa string.

Por que taguear com o SHA do commit em vez de só `latest`? Porque o
Deployment do Kubernetes usa `imagePullPolicy: IfNotPresent`. Se o `deploy`
sempre apontasse para a tag `latest`, o Kubernetes veria "a tag não mudou" e
**não puxaria a imagem nova nem reiniciaria os pods** — o deploy pareceria ter
funcionado, mas o código antigo continuaria rodando. Usar uma tag única por
commit (o SHA) garante que cada deploy force um rollout de verdade.

O repositório ECR (`tech-challenge-backend`) já precisa existir — ele é criado
pelo Terraform (`tech-challenge-backend/terraform/ecr.tf`), não por este
pipeline.

---

## Job `deploy` — passo a passo

Roda com `environment: production` (mesmo gate usado no `apply`/`destroy` do
`terraform.yml` — se você configurar "Required reviewers" nesse Environment,
o deploy fica pausado esperando aprovação manual antes de tocar no cluster).

1. **Configurar Credenciais AWS** — igual ao job anterior.
2. **Atualizar kubeconfig do Cluster EKS** —
   ```
   aws eks update-kubeconfig --region us-east-1 --name eks-tech-challenge
   ```
   Isso configura o `kubectl` do runner para falar com o cluster certo. O
   `kubectl` já vem instalado nos runners `ubuntu-latest` do GitHub Actions.
3. **Atualizar Imagem do Deployment** —
   ```
   kubectl -n tech-challenge set image deployment/tech-challenge-app app=<registry>/tech-challenge-backend:<sha>
   ```
   Isso troca **só a imagem** do container `app` dentro do Deployment
   `tech-challenge-app` (namespace `tech-challenge` — criado pelo Terraform).
   O Kubernetes então dispara um **rolling update**: sobe pods novos com a
   imagem nova, espera ficarem prontos (`readinessProbe` em `/actuator/health`),
   e só depois derruba os pods antigos — a estratégia `RollingUpdate` já está
   definida em `tech-challenge-backend/k8s/app-deployment.yaml`
   (`maxSurge: 1`, `maxUnavailable: 0`, ou seja: zero downtime, nunca fica com
   menos réplicas saudáveis do que o normal durante a troca).
4. **Aguardar Rollout** —
   ```
   kubectl -n tech-challenge rollout status deployment/tech-challenge-app --timeout=300s
   ```
   Fica esperando até o Kubernetes confirmar que todos os pods novos estão
   `Ready` (usando as mesmas `readinessProbe`/`livenessProbe` do deployment).
   Se algum pod novo não subir dentro de 5 minutos (crash loop, imagem
   inválida, health check falhando, etc.), este step falha e o job é marcado
   como falho — assim você fica sabendo do problema no próprio Actions, sem
   precisar entrar no cluster manualmente para descobrir que o deploy travou.

### Como a imagem "nova" chega até aqui

```
push/PR merge na main
        │
        ▼
build-and-push-image
  builda a partir de tech-challenge-backend/Dockerfile
  push para <account>.dkr.ecr.us-east-1.amazonaws.com/tech-challenge-backend:<sha>
        │  (outputs.image passa essa string pro próximo job)
        ▼
deploy
  kubectl set image ... app=<mesma imagem>:<sha>
        │
        ▼
Kubernetes (EKS) faz o rolling update
  pods antigos (imagem anterior) são substituídos por pods novos (imagem :<sha>)
```

Não existe "build local + `kind load docker-image`" nesse fluxo (isso é só
para desenvolvimento local, mencionado no comentário do
`k8s-deployment.tf`) — em produção a única forma da imagem chegar aos nós do
EKS é via `docker push` para o ECR seguido de um `kubectl set image`/`apply`
que force os nós a puxar (`pull`) essa imagem do registry.

---

## Troubleshooting rápido

- **`build-and-push-image` falha em "Login no Amazon ECR"** — normalmente
  credenciais AWS expiradas (ver seção de Secrets acima) ou o repositório ECR
  ainda não existe (rode o `terraform.yml` com `action: apply` primeiro).
- **`deploy` falha em "Atualizar kubeconfig"** — o cluster `eks-tech-challenge`
  ainda não existe, ou a role usada pelas credenciais AWS não tem
  `eks:DescribeCluster`/acesso ao cluster (ver
  `tech-challenge-backend/terraform/access_entry.tf` — hoje só a role
  `voclabs` e a role usada pelo Terraform têm acesso; se você trocar as
  credenciais do GitHub Actions para uma role diferente, precisa criar um
  `aws_eks_access_entry` para ela).
- **`deploy` falha em "Aguardar Rollout" (timeout)** — a imagem subiu para o
  ECR, mas os pods novos não ficaram `Ready` a tempo. Rode
  `kubectl -n tech-challenge get pods` e `kubectl -n tech-challenge describe pod <pod>`
  (localmente, com o kubeconfig atualizado) para ver o motivo — geralmente é
  `CrashLoopBackOff` (erro na aplicação) ou `ImagePullBackOff` (problema de
  permissão do nó para puxar do ECR).
- **Deploy "funcionou" mas o código antigo continua rodando** — confira se a
  imagem publicada usou a tag do SHA (não `latest`); esse é exatamente o
  problema que a tag por commit evita (ver explicação acima).
- **Quero aprovar manualmente cada deploy antes dele rodar** — configure
  "Required reviewers" no Environment `production` (Settings → Environments),
  do mesmo jeito descrito no `TERRAFORM-PIPELINE.md` para o `apply`/`destroy`.
