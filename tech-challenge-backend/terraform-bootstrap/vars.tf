variable "aws_region" {
  description = "Regiao AWS onde o bucket de state sera criado"
  type        = string
  default     = "us-east-1"
}

variable "state_bucket_name" {
  description = "Nome do bucket S3 usado como backend do Terraform principal (precisa bater com tech-challenge-backend/terraform/backend.tf)"
  type        = string
  default     = "tech-challenge-soat16-grupo15"
}

variable "tags" {
  description = "Tags aplicadas ao bucket de state"
  type        = map(string)
  default = {
    "Name"    = "tech-challenge-terraform-state"
    "Purpose" = "terraform-remote-state"
  }
}
