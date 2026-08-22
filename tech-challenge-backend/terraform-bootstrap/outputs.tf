output "state_bucket_name" {
  value = aws_s3_bucket.terraform_state.id
}

output "state_bucket_arn" {
  value = aws_s3_bucket.terraform_state.arn
}

output "backend_config_snippet" {
  description = "Cole isso em tech-challenge-backend/terraform/backend.tf caso o nome/regiao do bucket mude"
  value       = <<-EOT
    terraform {
      backend "s3" {
        bucket = "${aws_s3_bucket.terraform_state.id}"
        key    = "terraform/terraform.tfstate"
        region = "${var.aws_region}"
      }
    }
  EOT
}
