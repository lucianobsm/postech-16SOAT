# Controller que atende a classe "nginx" fica em k8s-ingress-controller.tf,
# instalado com Service NodePort (sem ELB/NLB, sem custo na AWS Academy).
resource "kubectl_manifest" "ingress" {
  depends_on         = [kubectl_manifest.service, helm_release.ingress_nginx]
  yaml_body          = file("${local.k8s_manifests}/app-ingress.yaml")
  override_namespace = local.k8s_namespace
}
