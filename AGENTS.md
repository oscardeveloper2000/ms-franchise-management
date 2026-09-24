# AGENTS.md — Microservicio de Franquicias

## Comandos

- Compilar: `mvn clean compile`
- Ejecutar tests: `mvn test`
- Empaquetar: `mvn clean package`
- Levantar localmente (con DynamoDB Local vía Docker Compose): `docker compose up -d`
- Correr la app localmente: `mvn spring-boot:run`
- Formatear/lint (si se configura Spotless o Checkstyle): `mvn spotless:apply`

## Checklist de validación antes de dar por terminada una tarea

1. `mvn clean test` pasa sin errores.
2. No existe ningún `.block()` en el código de `application` ni `infrastructure`.
3. `domain` no tiene imports de `org.springframework.*`, `software.amazon.awssdk.*` ni `reactor.*`.
4. Cada nuevo caso de uso tiene: interfaz en `application/port/in`, implementación en `application/usecase`, y (si aplica) test unitario.
5. Cada nuevo endpoint tiene un DTO propio de request/response — nunca expone directamente una clase de `domain` o de `infrastructure/adapter/out/persistence`.
6. Si se tocó la capa de persistencia, el cambio correspondiente existe también en el Terraform de la tabla DynamoDB (`infra/terraform/`).
7. El `Dockerfile` sigue construyendo la imagen sin cambios manuales adicionales (`docker build -t franquicias-api .`).

## Estructura de infraestructura como código

```
infra/terraform/
├── main.tf          # Provider AWS (línea 6.x, requerida por ECS Express Mode), backend de state
├── dynamodb.tf       # Definición de la tabla y sus índices
├── ecr.tf            # Repositorio ECR para la imagen Docker
├── ecs-express.tf    # Roles IAM (execution, infrastructure, task) + servicio ECS Express Mode
├── variables.tf
└── outputs.tf
```

## Notas para el agente

- Antes de generar código de un caso de uso nuevo, revisa primero si ya existe un puerto similar en `application/port` para no duplicar contratos.
- El servicio de cómputo para el despliegue es Amazon ECS Express Mode (ya decidido; App Runner dejó de aceptar clientes nuevos desde el 30 de abril de 2026). El Terraform de despliegue debe incluir el recurso `aws_ecs_express_gateway_service` apuntando a la imagen Docker publicada en ECR, con los tres roles IAM correspondientes (execution, infrastructure, task).
- El endpoint `GET /health` es obligatorio y no debe eliminarse ni modificarse para depender de servicios externos — el health check del Application Load Balancer de ECS Express Mode depende de que responda 200 de forma consistente.
- Mantén el `README.md` actualizado con los pasos para levantar el proyecto localmente, tal como pide el enunciado de la prueba técnica.