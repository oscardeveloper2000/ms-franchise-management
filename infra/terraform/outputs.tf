output "ecr_repository_url" {
  description = "URL del repositorio ECR donde se publicará la imagen Docker."
  value       = aws_ecr_repository.app.repository_url
}

# La IP pública de la tarea se obtiene con un script aparte, no con un output de Terraform.
