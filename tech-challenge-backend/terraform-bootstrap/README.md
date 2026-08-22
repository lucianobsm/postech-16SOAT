# Bootstrap do Bucket de State

Cria o bucket S3 (`tech-challenge-soat16-grupo15`, por padrão) usado pelo
backend remoto do Terraform principal em
[`../terraform/backend.tf`](../terraform/backend.tf).

## Por que isso é um módulo separado

O `terraform/` principal usa esse bucket S3 *como backend*, então ele não
pode criar o próprio bucket que precisa existir antes do `terraform init`
funcionar — seria uma dependência circular. Este diretório resolve isso:
é um módulo Terraform independente, com state **local** (arquivo
`terraform.tfstate` nesta pasta, já coberto pelo `.gitignore` — nunca é
commitado), que roda **uma única vez**, antes de tocar no `terraform/`
principal.

## Como rodar (uma única vez, manual — fora do CI)

```bash
cd tech-challenge-backend/terraform-bootstrap

# Credenciais AWS (Academy: pegue no painel "AWS Details" → "AWS CLI")
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...

terraform init
terraform apply
```

Depois disso o bucket existe na AWS e o `terraform/` principal já consegue
rodar `terraform init` normalmente (ver
[`../../.github/workflows/TERRAFORM-PIPELINE.md`](../../.github/workflows/TERRAFORM-PIPELINE.md)).

## O que é criado

- `aws_s3_bucket.terraform_state` — o bucket em si (`prevent_destroy = true`,
  já que ele guarda o state de toda a infra — remova essa proteção antes de
  um `terraform destroy` intencional).
- `aws_s3_bucket_versioning` — versionamento ligado, para recuperar uma
  versão anterior do `.tfstate` se algo corromper o arquivo atual.
- `aws_s3_bucket_server_side_encryption_configuration` — criptografia em
  repouso (AES256).
- `aws_s3_bucket_public_access_block` — bloqueia qualquer acesso público ao
  bucket.

## O que **não** é criado

- **Tabela DynamoDB de lock.** O backend em `terraform/backend.tf` não
  configura lock de state (isso já é documentado em
  `TERRAFORM-PIPELINE.md`); se quiser adicionar depois, crie a tabela aqui e
  adicione `dynamodb_table = "..."` no bloco `backend "s3"`.

## Se o bucket já existir

Se alguém já criou o bucket manualmente (ex.: pelo Console AWS) antes deste
módulo existir, rode `terraform import aws_s3_bucket.terraform_state
tech-challenge-soat16-grupo15` em vez de `apply`, para o Terraform passar a
gerenciar o recurso existente sem tentar recriá-lo.
