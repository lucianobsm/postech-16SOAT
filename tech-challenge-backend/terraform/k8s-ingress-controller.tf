# NodePort em vez do LoadBalancer default do chart: no AWS Academy nao
# queremos que o Helm crie um ELB/NLB (recurso cobrado, ~US$16-20/mes).
# Com NodePort o trafego chega direto nos nos EC2 (ja pagos/incluidos no
# lab) nas portas fixas abaixo, liberadas no security-group.tf.
resource "helm_release" "ingress_nginx" {
  name             = "ingress-nginx"
  repository       = "https://kubernetes.github.io/ingress-nginx"
  chart            = "ingress-nginx"
  version          = "4.15.1"
  namespace        = "ingress-nginx"
  create_namespace = true

  depends_on = [aws_eks_node_group.node_group]

  set = [
    {
      name  = "controller.service.type"
      value = "NodePort"
    },
    {
      name  = "controller.service.nodePorts.http"
      value = "30080"
    },
    {
      name  = "controller.service.nodePorts.https"
      value = "30443"
    }
  ]
}
