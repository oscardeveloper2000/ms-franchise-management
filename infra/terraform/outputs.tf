output "ecr_repository_url" {
  description = "URL del repositorio ECR donde se publicará la imagen Docker."
  value       = aws_ecr_repository.app.repository_url
}

output "ecs_express_service_url" {
  description = "Rutas de ingreso del servicio ECS Express Gateway."
  value       = aws_ecs_express_gateway_service.app.ingress_paths
}
