terraform {
  required_version = ">= 0.13"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0"
    }
  }

  # Sem bloco "backend" aqui de proposito: este modulo cria o bucket S3 que o
  # terraform/ principal usa como backend remoto. Ele nao pode depender de si
  # mesmo, entao o state deste bootstrap fica local (terraform.tfstate nesta
  # pasta, ja coberto pelo .gitignore). Roda uma unica vez, por fora do
  # pipeline de CI.
}

provider "aws" {
  region = var.aws_region
}
