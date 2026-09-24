variable "aws_region" {
  description = "Región AWS para desplegar la infraestructura."
  type        = string
  default     = "us-east-1"
}

variable "app_name" {
  description = "Nombre base de la aplicación para identificar recursos."
  type        = string
  default     = "franquicias-api"
}

variable "dynamodb_table_name" {
  description = "Nombre de la tabla DynamoDB. Debe coincidir con la propiedad aws.dynamodb.table-name en application.yml."
  type        = string
  default     = "franquicias"
}
