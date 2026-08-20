# SG real anexado as instancias do node group - criado automaticamente
# pelo EKS (nao pelo security-group.tf, que so afeta as ENIs de controle).
# Ver comentario em security-group.tf.
resource "aws_security_group_rule" "node_nodeport_http" {
  type              = "ingress"
  description       = "Ingress NodePort HTTP"
  from_port         = 30080
  to_port           = 30080
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_eks_cluster.cluster.vpc_config[0].cluster_security_group_id
}

resource "aws_security_group_rule" "node_nodeport_https" {
  type              = "ingress"
  description       = "Ingress NodePort HTTPS"
  from_port         = 30443
  to_port           = 30443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_eks_cluster.cluster.vpc_config[0].cluster_security_group_id
}
