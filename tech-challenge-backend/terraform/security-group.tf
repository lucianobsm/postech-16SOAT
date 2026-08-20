# AVISO: este SG e passado em vpc_config.security_group_ids do
# aws_eks_cluster, mas o EKS so anexa esse SG "adicional" as ENIs de
# controle - os nos (e portanto os pods) usam o "cluster security group"
# que o proprio EKS cria automaticamente (exposto como
# aws_eks_cluster.cluster.vpc_config[0].cluster_security_group_id). Regras
# de trafego de/para os nos (ex.: NodePort do ingress-nginx) precisam ir em
# eks-node-security-group.tf, nao aqui. A regra HTTP abaixo nunca teve
# efeito real sobre trafego para os nos.
resource "aws_security_group" "security_group" {
  name        = "security-group-${var.project_name}"
  description = "Security group for use for exposing EKS cluster"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "All traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}