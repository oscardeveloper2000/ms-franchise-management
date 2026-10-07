# Microservicio de Franquicias

API reactiva para administrar franquicias, sus sucursales y los productos ofertados en cada una, construida con Spring WebFlux, arquitectura hexagonal, y persistencia en Amazon DynamoDB.

## Tabla de contenido

- [Arquitectura](#arquitectura)
- [Stack técnico](#stack-técnico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Endpoints](#endpoints)
- [Probar con Postman](#probar-con-postman)
- [Cómo ejecutar el proyecto en local](#cómo-ejecutar-el-proyecto-en-local)
- [Cómo desplegar en AWS](#cómo-desplegar-en-aws)
- [Decisiones de diseño](#decisiones-de-diseño)

## Arquitectura

El proyecto sigue **arquitectura hexagonal** (puertos y adaptadores):

```
domain/            → Modelos de negocio puros (Franquicia, Sucursal, Producto), sin dependencias externas
application/       → Casos de uso y puertos (interfaces) que definen los contratos hacia adentro y hacia afuera
infrastructure/    → Adaptadores concretos: controllers WebFlux (entrada) y persistencia DynamoDB (salida)
```

Una **franquicia es el aggregate root**: cada franquicia (con todas sus sucursales y productos anidados) se persiste como un único ítem en DynamoDB, garantizando consistencia en cada operación de escritura.

## Stack técnico

| Componente                  | Tecnología                                                                                |
| --------------------------- | ----------------------------------------------------------------------------------------- |
| Lenguaje                    | Java 21                                                                                   |
| Framework                   | Spring Boot 3.5.9 + Spring WebFlux (programación reactiva/funcional con Router Functions) |
| Build                       | Maven                                                                                     |
| Persistencia                | Amazon DynamoDB (AWS SDK v2, `DynamoDbEnhancedAsyncClient`)                               |
| Contenedor                  | Docker (build multi-stage)                                                                |
| Infraestructura como código | Terraform                                                                                 |
| Cómputo en la nube          | Amazon ECS Fargate                                                                        |

## Estructura del proyecto

```
src/main/java/com/epam/franquicias/
├── domain/
│   ├── model/              # Franquicia, Sucursal, Producto
│   └── exception/          # Excepciones de invariantes del dominio
├── application/
│   ├── port/in/            # Interfaces de casos de uso
│   ├── port/out/           # Interfaces de repositorio
│   ├── usecase/            # Implementación de los casos de uso
│   └── exception/          # Excepciones de orquestación (no encontrado)
└── infrastructure/
    ├── adapter/in/web/     # Router Functions, Handlers, DTOs
    ├── adapter/out/persistence/  # Entidades DynamoDB, mapper, adaptador
    └── config/             # Configuración de Spring (beans de AWS y casos de uso)

infra/terraform/             # Infraestructura como código (DynamoDB, ECR, ECS Fargate)
```

## Endpoints

Base URL AWS: `http://18.232.53.87:8080`

| Método | Ruta                                                                               | Descripción                                               |
| ------ | ---------------------------------------------------------------------------------- | --------------------------------------------------------- |
| GET    | `/health`                                                                          | Health check                                              |
| POST   | `/franquicias`                                                                     | Crear una franquicia                                      |
| PATCH  | `/franquicias/{franquiciaId}`                                                      | Actualizar nombre de una franquicia                       |
| POST   | `/franquicias/{franquiciaId}/sucursales`                                           | Agregar sucursal a una franquicia                         |
| PATCH  | `/franquicias/{franquiciaId}/sucursales/{sucursalId}`                              | Actualizar nombre de una sucursal                         |
| POST   | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos`                    | Agregar producto a una sucursal                           |
| PATCH  | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`       | Actualizar nombre de un producto                          |
| PATCH  | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock` | Modificar el stock de un producto                         |
| DELETE | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`       | Eliminar un producto de una sucursal                      |
| GET    | `/franquicias/{franquiciaId}/productos/top-stock`                                  | Producto con más stock por cada sucursal de la franquicia |

### Ejemplos de uso (curl)

**Advertencia:** la IP pública cambia cada vez que la tarea de Fargate se reinicia. Para obtener la IP actual, ejecuta `./infra/terraform/get-task-ip.sh`; no asumas que `18.232.53.87` seguirá siendo válida indefinidamente.

Los siguientes ejemplos apuntan al servicio desplegado en Amazon ECS Fargate. Sustituye los valores `{franquiciaId}`, `{sucursalId}` y `{productoId}` por los UUID reales devueltos en cada respuesta anterior.

**1. Crear una franquicia**

```bash
curl -X POST http://18.232.53.87:8080/franquicias \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Mi Franquicia"}'
```

**2. Actualizar el nombre de una franquicia**

```bash
curl -X PATCH http://18.232.53.87:8080/franquicias/{franquiciaId} \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Mi Franquicia Renombrada"}'
```

**3. Agregar una sucursal a una franquicia**

```bash
curl -X POST http://18.232.53.87:8080/franquicias/{franquiciaId}/sucursales \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Sucursal Norte"}'
```

**4. Actualizar el nombre de una sucursal**

```bash
curl -X PATCH http://18.232.53.87:8080/franquicias/{franquiciaId}/sucursales/{sucursalId} \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Sucursal Norte Renombrada"}'
```

**5. Agregar un producto a una sucursal**

```bash
curl -X POST http://18.232.53.87:8080/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Coca-Cola 500ml", "cantidadStock": 100}'
```

**6. Actualizar el nombre de un producto**

```bash
curl -X PATCH http://18.232.53.87:8080/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId} \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Coca-Cola 600ml"}'
```

**7. Modificar el stock de un producto**

```bash
curl -X PATCH http://18.232.53.87:8080/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock \
  -H "Content-Type: application/json" \
  -d '{"cantidadStock": 75}'
```

**8. Eliminar un producto de una sucursal**

```bash
curl -X DELETE http://18.232.53.87:8080/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}
```

**9. Obtener el producto con más stock por sucursal**

```bash
curl http://18.232.53.87:8080/franquicias/{franquiciaId}/productos/top-stock
```

**Health check**

```bash
curl http://18.232.53.87:8080/health
```

## Probar con Postman

Importa la colección [postman/franquicias-api.postman_collection.json](postman/franquicias-api.postman_collection.json)
en Postman. Configura la variable `baseUrl` con `http://18.232.53.87:8080` para acceder al servicio desplegado en ECS Fargate.
Para probar localmente, cambia su valor a `http://localhost:8080`.

La colección guarda automáticamente `franquiciaId`, `sucursalId` y `productoId` a partir de las respuestas,
por lo que las solicitudes pueden ejecutarse en el orden mostrado.

## Respuestas de error

Las respuestas de error tienen este formato:

```json
{
  "mensaje": "El nombre de la franquicia no puede ser nulo ni vacío",
  "status": 400
}
```

| Estado | Caso                                             |
| ------ | ------------------------------------------------ |
| `400`  | Datos inválidos, nombres vacíos o stock negativo |
| `404`  | Franquicia, sucursal o producto inexistente      |
| `500`  | Error inesperado del servidor                    |

## Cómo ejecutar el proyecto en local

### Prerrequisitos

- Java 21
- Maven 3.9+
- Docker (opcional, solo si quieres correrlo en contenedor)
- AWS CLI configurado, si se utiliza DynamoDB real
- Credenciales de AWS configuradas (`aws configure`) con permisos sobre DynamoDB, ya que la aplicación se conecta a una tabla real de DynamoDB incluso en ejecución local

Variables de entorno relevantes:

```text
AWS_REGION=us-east-1
AWS_DYNAMODB_TABLE_NAME=franquicias
```

En local, el SDK de AWS utiliza la cadena de proveedores de credenciales por defecto.
No se deben guardar credenciales en el repositorio.

### Pasos

1. Clona el repositorio:

   ```bash
   git clone <url-del-repositorio>
   cd ms-franchise-management
   ```

2. Asegúrate de tener credenciales de AWS válidas configuradas localmente:

   ```bash
   aws configure
   aws sts get-caller-identity
   ```

3. Compila el proyecto:

   ```bash
   mvn clean package
   ```

4. Ejecuta la aplicación:

   ```bash
   mvn spring-boot:run
   ```

   La aplicación arrancará en `http://localhost:8080`. Verifica con:

   ```bash
   curl http://localhost:8080/health
   ```

### Ejecutar con Docker

```bash
docker build -t franquicias-api .
docker run -p 8080:8080 \
  -e AWS_ACCESS_KEY_ID=<tu-access-key> \
  -e AWS_SECRET_ACCESS_KEY=<tu-secret-key> \
  -e AWS_REGION=us-east-1 \
  -e AWS_DYNAMODB_TABLE_NAME=franquicias \
  franquicias-api
```

### Ejecutar los tests

```bash
mvn test
```

La suite actual cubre las invariantes de `Franquicia`, `Sucursal` y `Producto`, además del caso de uso reactivo
de creación de franquicias. Los tests no levantan el contexto de Spring ni acceden a AWS.

## Cómo desplegar en AWS

La infraestructura completa se gestiona con Terraform, ubicada en `infra/terraform/`. Aprovisiona: una tabla DynamoDB, un repositorio ECR, los roles IAM necesarios, y un servicio de Amazon ECS Fargate sin balanceador de carga.

### Prerrequisitos

- Terraform >= 1.6.0
- AWS CLI configurado con credenciales válidas
- Docker

### Pasos

1. Aprovisiona la tabla DynamoDB y el repositorio ECR primero:

   ```bash
   cd infra/terraform
   terraform init
   terraform apply -target=aws_ecr_repository.app -target=aws_dynamodb_table.franquicias -target=aws_iam_role.execution_role -target=aws_iam_role.task_role -target=aws_iam_policy.dynamodb_access -target=aws_iam_role_policy_attachment.execution_role -target=aws_iam_role_policy_attachment.task_role_dynamodb_access
   ```

2. Construye la imagen Docker y súbela al repositorio ECR recién creado:

   ```bash
   cd ../..
   mvn clean package -DskipTests
   docker build -t franquicias-api .

   ECR_URL=$(cd infra/terraform && terraform output -raw ecr_repository_url)
   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ECR_URL
   docker tag franquicias-api:latest $ECR_URL:latest
   docker push $ECR_URL:latest
   ```

3. Despliega el servicio ECS Fargate con la imagen ya disponible:

   ```bash
   cd infra/terraform
   terraform apply
   ```

4. Obtén la IP pública de la tarea mediante un script aparte. Terraform no expone esta IP mediante un output.

5. Verifica el despliegue:
   ```bash
   curl http://18.232.53.87:8080/health
   ```

### Destruir y reconstruir ECS Fargate

Para eliminar únicamente el servicio Fargate, el cluster, la definición de tarea, el grupo de logs y el security group:

```bash
cd infra/terraform
terraform destroy -target=aws_ecs_service.app -target=aws_ecs_cluster.app -target=aws_ecs_task_definition.app -target=aws_cloudwatch_log_group.app -target=aws_security_group.app
```

Para volver a construirlos cuando sea necesario, ejecuta nuevamente `terraform apply` desde el mismo directorio:

```bash
cd infra/terraform
terraform apply
```

### Destruir la infraestructura

```bash
cd infra/terraform
terraform destroy
```

## Decisiones de diseño

- **Agregado único en DynamoDB**: cada franquicia (con sus sucursales y productos anidados) se persiste como un solo ítem, priorizando consistencia y simplicidad sobre el volumen de datos que maneja este reto. Esto implica que operaciones concurrentes sobre la misma franquicia podrían sobrescribirse entre sí (patrón _read-modify-write_); en un escenario de producción con alta concurrencia, se resolvería con _optimistic locking_ mediante un atributo de versión.
- **Router Functions en vez de `@RestController`**: se optó por el estilo funcional de WebFlux, coherente con el punto extra de programación funcional/reactiva del reto.
- **Amazon ECS Fargate sin balanceador**: para este proyecto de prueba técnica con tráfico bajo, se usa ECS Fargate con una IP pública directa para minimizar costos de infraestructura.
- **Dominio rico con validación en el constructor**: los objetos de dominio (`Franquicia`, `Sucursal`, `Producto`) no pueden construirse en un estado inválido — las reglas de negocio (nombres no vacíos, stock no negativo) se aplican en el constructor y en métodos explícitos como `actualizarStock(...)`.
