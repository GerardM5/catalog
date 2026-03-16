# Catalog API

API REST construida con Spring Boot para gestionar y consultar un catalogo de productos almacenado en PostgreSQL. La aplicacion expone operaciones para crear productos, obtener un producto por id y buscar por cualquier campo del modelo con paginacion y ordenacion.

## Que hace la aplicacion

- Gestiona productos normalizados en una tabla `product`.
- Permite altas de productos mediante `POST /api/products`.
- Permite consultar un producto concreto mediante `GET /api/products/{id}`.
- Permite buscar por multiples filtros combinables en `GET /api/products`.
- Expone documentacion OpenAPI y Swagger UI.

## Stack tecnico

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation
- MapStruct
- springdoc-openapi
- Maven Wrapper

## Modelo de datos

La tabla principal es `product` y contiene estos campos:

| Campo | Tipo |
| --- | --- |
| `id` | `BIGINT` |
| `referencia` | `TEXT` |
| `nombre` | `TEXT` |
| `descripcion` | `TEXT` |
| `peso` | `BIGINT` |
| `volumen` | `BIGINT` |
| `color` | `TEXT` |
| `ancho` | `BIGINT` |
| `largo` | `BIGINT` |
| `alto` | `BIGINT` |
| `precio` | `DECIMAL(10,2)` |
| `empresa` | `TEXT` |
| `categoria` | `TEXT` |
| `origen_pdf` | `TEXT` |

El esquema SQL base esta en [`src/main/resources/schemaBD.sql`](/Users/gerardmartinezalcocer/dev/catalog/src/main/resources/schemaBD.sql).

## Requisitos

- JDK 21
- PostgreSQL en ejecucion
- Base de datos `catalog` creada

## Configuracion

La aplicacion lee estas variables de entorno y define valores por defecto para desarrollo local:

| Variable | Valor por defecto |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/catalog` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `postgres` |

Configuracion actual en [`src/main/resources/application.properties`](/Users/gerardmartinezalcocer/dev/catalog/src/main/resources/application.properties).

## Arranque local

1. Crea la base de datos `catalog` en PostgreSQL.
2. Ejecuta el SQL de [`src/main/resources/schemaBD.sql`](/Users/gerardmartinezalcocer/dev/catalog/src/main/resources/schemaBD.sql).
3. Lanza la aplicacion:

```bash
./mvnw spring-boot:run
```

Si quieres compilar sin arrancar:

```bash
./mvnw -DskipTests compile
```

## Documentacion API

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Endpoints principales

### `POST /api/products`

Crea un producto nuevo. El `id` no se genera automaticamente: debe enviarse en el body y debe ser unico.

Ejemplo:

```json
{
  "id": 1001,
  "referencia": "SILLA-001",
  "nombre": "Silla nordica",
  "descripcion": "Silla de comedor de madera",
  "peso": 7000,
  "volumen": 12000,
  "color": "roble",
  "ancho": 45,
  "largo": 52,
  "alto": 82,
  "precio": 79.90,
  "empresa": "Acme Furniture",
  "categoria": "sillas",
  "origenPdf": "catalogo-marzo-2026.pdf"
}
```

Reglas de validacion relevantes:

- `id` obligatorio
- `referencia`, `nombre`, `empresa`, `categoria` y `origenPdf` obligatorios
- `precio` obligatorio y mayor que cero

### `GET /api/products/{id}`

Devuelve un producto por su identificador. Si no existe, responde `404`.

### `GET /api/products`

Busqueda flexible por query params. Todos los filtros son opcionales y se combinan entre si.

Filtros soportados:

- `id`
- `referencia`
- `nombre`
- `descripcion`
- `peso`
- `volumen`
- `color`
- `ancho`
- `largo`
- `alto`
- `precio`
- `empresa`
- `categoria`
- `origenPdf`

Campos de texto como `referencia`, `nombre`, `descripcion`, `color`, `empresa`, `categoria` y `origenPdf` se buscan con `LIKE` case-insensitive.

Ejemplo:

```bash
curl "http://localhost:8080/api/products?nombre=silla&empresa=acme&page=0&size=10&sort=precio,asc"
```

La respuesta devuelve:

- `content`: lista de productos
- `pagination`: metadatos de pagina, total de elementos y total de paginas

## Estructura del proyecto

- [`src/main/java/com/example/catalog/controller/ProductController.java`](/Users/gerardmartinezalcocer/dev/catalog/src/main/java/com/example/catalog/controller/ProductController.java): capa HTTP
- [`src/main/java/com/example/catalog/service/ProductService.java`](/Users/gerardmartinezalcocer/dev/catalog/src/main/java/com/example/catalog/service/ProductService.java): logica de negocio
- [`src/main/java/com/example/catalog/repository/ProductRepository.java`](/Users/gerardmartinezalcocer/dev/catalog/src/main/java/com/example/catalog/repository/ProductRepository.java): acceso a datos
- [`src/main/java/com/example/catalog/repository/ProductSpecifications.java`](/Users/gerardmartinezalcocer/dev/catalog/src/main/java/com/example/catalog/repository/ProductSpecifications.java): filtros dinamicos
- [`src/main/java/com/example/catalog/mapper/ProductMapper.java`](/Users/gerardmartinezalcocer/dev/catalog/src/main/java/com/example/catalog/mapper/ProductMapper.java): conversion DTO <-> entidad
- [`src/main/java/com/example/catalog/exception/GlobalExceptionHandler.java`](/Users/gerardmartinezalcocer/dev/catalog/src/main/java/com/example/catalog/exception/GlobalExceptionHandler.java): manejo centralizado de errores

## Limitaciones actuales

- No hay migraciones versionadas; el esquema se aplica manualmente desde `schemaBD.sql`.
- El identificador del producto se gestiona externamente.
- Solo existe creacion y lectura; no hay actualizacion ni borrado.
- No hay autenticacion ni autorizacion.
