# Instrucciones para el Agente — Microservicio de Franquicias

## Contexto del negocio

API para administrar franquicias. Jerarquía del dominio:

- **Franquicia**: tiene un `nombre` y una lista de `Sucursales`.
- **Sucursal**: tiene un `nombre` y una lista de `Productos`.
- **Producto**: tiene un `nombre` y una `cantidadStock`.

## Stack técnico (no negociable)

- Java 21, Spring Boot 3.x, **Spring WebFlux** (100% reactivo, prohibido `.block()` en cualquier capa).
- Maven como gestor de build.
- Persistencia: **DynamoDB**, usando `DynamoDbAsyncClient` (AWS SDK v2), envuelto en `Mono`/`Flux` con `Mono.fromFuture(...)`. Nunca usar el cliente síncrono.
- Docker para empaquetar la aplicación.
- Terraform para aprovisionar la tabla de DynamoDB (y cualquier otro recurso de AWS necesario).
- Despliegue final en AWS mediante **Amazon ECS Express Mode** (`aws_ecs_express_gateway_service` en Terraform), a partir de la imagen Docker de la aplicación. No usar App Runner (dejó de aceptar clientes nuevos desde el 30 de abril de 2026) ni Lambda.
- El servicio requiere tres roles IAM distintos: `execution_role` (principal `ecs-tasks.amazonaws.com`, policy `AmazonECSTaskExecutionRolePolicy`, para pull de ECR y logs), `infrastructure_role` (principal `ecs.amazonaws.com`, policy `AmazonECSInfrastructureRoleforExpressGatewayServices`, para gestionar el ALB/target groups/auto-scaling), y `task_role` (principal `ecs-tasks.amazonaws.com`, con la policy custom de acceso a DynamoDB, para que la aplicación en ejecución tenga permisos sobre la tabla).
- El servicio valida salud vía HTTP a través de un Application Load Balancer (`health_check_path`), a diferencia de App Runner que solo validaba TCP. Por eso existe el endpoint `GET /health`, que debe mantenerse siempre disponible y respondiendo 200 sin dependencias externas (no debe consultar DynamoDB ni ningún otro servicio).

## Arquitectura: Hexagonal (puertos y adaptadores)

Estructura de paquetes obligatoria:

```
src/main/java/com/<empresa>/franquicias/
├── domain/
│   ├── model/              # Franquicia, Sucursal, Producto (POJOs puros, sin anotaciones de framework)
│   └── exception/          # Excepciones de negocio
├── application/
│   ├── port/in/            # Interfaces de casos de uso (ej. AgregarFranquiciaUseCase)
│   ├── port/out/           # Interfaces de repositorio (ej. FranquiciaRepositoryPort)
│   └── usecase/            # Implementación de los casos de uso, orquestan el dominio
└── infrastructure/
    ├── adapter/in/web/     # Controllers WebFlux (RouterFunction o @RestController), DTOs de entrada/salida
    └── adapter/out/persistence/  # Implementación de los puertos out contra DynamoDB, mappers entidad<->dominio
```

### Reglas de dependencia (estrictas)

- `domain` no importa nada de Spring, WebFlux ni AWS SDK. Es Java puro.
- `application` depende solo de `domain` y define los puertos (interfaces). No conoce DynamoDB ni HTTP.
- `infrastructure` es la única capa que conoce frameworks externos e implementa los puertos definidos en `application`.
- Los DTOs de entrada/salida HTTP viven en `infrastructure/adapter/in/web`, nunca se exponen las entidades de dominio directamente en la API.
- Los modelos de persistencia (entidades DynamoDB) viven en `infrastructure/adapter/out/persistence`, nunca se filtran hacia el dominio.

## Endpoints requeridos

| Método | Ruta (sugerida) | Descripción |
|---|---|---|
| GET | `/health` | Health check del servicio, usado por el Application Load Balancer de ECS Express Mode. Debe responder 200 sin depender de DynamoDB ni ningún otro servicio externo. |
| POST | `/franquicias` | Crear franquicia |
| POST | `/franquicias/{franquiciaId}/sucursales` | Agregar sucursal a una franquicia |
| POST | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos` | Agregar producto a una sucursal |
| DELETE | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}` | Eliminar producto de una sucursal |
| PATCH | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock` | Modificar stock de un producto |
| GET | `/franquicias/{franquiciaId}/productos/top-stock` | Producto con más stock por sucursal, indicando a qué sucursal pertenece cada uno |
| PATCH | `/franquicias/{franquiciaId}` | (Plus) Actualizar nombre de franquicia |
| PATCH | `/franquicias/{franquiciaId}/sucursales/{sucursalId}` | (Plus) Actualizar nombre de sucursal |
| PATCH | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}` | (Plus) Actualizar nombre de producto |

## Convenciones reactivas

- Todo método de puerto/repositorio retorna `Mono<T>` o `Flux<T>`.
- Manejo de errores con operadores reactivos (`onErrorResume`, `switchIfEmpty`), nunca `try/catch` bloqueante alrededor de una cadena reactiva.
- Validación de entrada con Bean Validation (`@Valid`) en los DTOs de la capa web.
- Usar `WebExceptionHandler` o `@RestControllerAdvice` reactivo para mapear excepciones de dominio a respuestas HTTP.

## Testing

- Unit tests para `domain` y `application` con JUnit 5 + Mockito, sin levantar contexto de Spring.
- Tests de los adaptadores web con `WebTestClient`.
- Tests de persistencia contra DynamoDB Local (vía Testcontainers), no contra AWS real.

## Qué NO hacer

- No poner lógica de negocio en los controllers ni en los adaptadores de persistencia.
- No usar el modelo de persistencia de DynamoDB como modelo de dominio.
- No bloquear el event loop de Netty con llamadas síncronas.
- No hardcodear credenciales de AWS; usar variables de entorno o el proveedor de credenciales por defecto del SDK.
