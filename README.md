# RENT LIBRARY API REST
> Este es mi primer proyecto usando el entorno de Java con Spring Boot. \
> Este proyecto es solo educativo, no está listo para un entorno de producción real. \
> Cualquier bug o mejora puede notificarlo.

API REST para la gestión de una biblioteca fisica: catálogo de libros, copias físicas, préstamos y reservas, 
con autenticación basada en JWT y control de sesiones.

## Funcionalidades

- Autenticación con JWT (access token y refresh token)
- Catálogo de libros y control de copias
- Préstamos y reservas por usuario autenticado
- Reglas de negocio configurables: máximo de préstamos activos y de reservas activas por usuario
- Paginación con límite máximo configurable

## Stack
- Java, Spring Boot
- Spring Security + JWT
- Spring Data JPA / Hibernate
- Base de datos relacional (PostgreSQL)

## Infraestructura
- Docker, Docker Compose
## Configuración

Variables de entorno requeridas:

| Variable                         | Descripción                       | Por defecto |
|----------------------------------|-----------------------------------|-------------|
| `SPRING_DB_URL`                  | URL de la base de datos           | -           |
| `SPRING_DB_USERNAME`             | Usuario de la BD                  | -           |
| `SPRING_DB_PASSWORD`             | Contraseña de la BD               | -           |
| `JWT_SECRET_KEY`                 | Clave de firma del JWT            | -           |
| `JWT_EXPIRATION_ACCESS_TOKEN`    | Expiración del access token (ms)  | 240000      |
| `JWT_EXPIRATION_REFRESH_TOKEN`   | Expiración del refresh token (ms) | 604800000   |
| `LIBRARY_MAX_ACTIVE_LOANS`       | Máximo de préstamos activos       | 3           |
| `LIBRARY_MAX_ACTIVE_RESERVATION` | Máximo de reservas activas        | 3           |
| `SPRING_PROFILES_ACTIVE`         | Profile activo (`dev`, `prod`)    | dev         |

# Endpoints
Rutas disponibles de mi API REST

## AUTH
| Método | Endpoint                       | Descripción              | Autenticación |      Estado      |
|:------:|:-------------------------------|:-------------------------|:-------------:|:----------------:|
|  POST  | `/api/auth/register`           | Registrar usuario        |    Publico    |  `201 Created`   |
|  POST  | `/api/auth/login`              | Iniciar Sesión           |    Publico    |     `200 OK`     |
|  POST  | `/api/auth/refresh`            | Refrescar Access Token   |      JWT      |     `200 OK`     |
|  GET   | `/api/auth/sessions`           | Ver sessiones activas    |      JWT      |     `200 OK`     |
| DELETE | `/api/auth/logout`             | Cerrar sesión            |      JWT      | `204 No Content` |
| DELETE | `/api/auth/sessions`           | Cerrar sesiónes          |      JWT      | `204 No Content` |
| DELETE | `/api/auth/sessions/{tokenId}` | Cerrar sesión especifica |      JWT      | `204 No Content` |

## AUTHOR
| Método | Endpoint            | Descripción          | Autenticación  |        Estado        |
|:------:|:--------------------|:---------------------|:--------------:|:--------------------:|
|  GET   | `/api/authors`      | Ver autores          |    Publico     |       `200 OK`       |
|  GET   | `/api/authors/{id}` | Ver autor especifico |    Publico     |       `200 OK`       |
|  POST  | `/api/authors`      | Crear autor          | Admin, Librero |    `201 Created`     |
| PATCH  | `/api/authors/{id}` | Actualizar autor     | Admin, Librero |       `200 OK`       |
| DELETE | `/api/authors/{id}` | Eliminar autor       | Admin, Librero |   `204 No Content`   |

## BOOK
| Método | Endpoint                 | Descripción          | Autenticación  |        Estado        |
|:------:|:-------------------------|:---------------------|:--------------:|:--------------------:|
|  GET   | `/api/books`             | Ver Libros           |    Publico     |       `200 OK`       |
|  GET   | `/api/books/{id}`        | Ver libro especifico |    Publico     |       `200 OK`       |
|  POST  | `/api/book`              | Crear libro          | Admin, Librero |    `201 Created`     |
| PATCH  | `/api/book/{id}`         | Actualizar libro     | Admin, Librero |       `200 OK`       |
| DELETE | `/api/book/{id}`         | Eliminar libro       | Admin, Librero |   `204 No Content`   |
| PATCH  | `/api/book/{id}/restore` | Restaurar libro      | Admin, Librero |       `200 OK`       |

## CATEGORY
| Método | Endpoint               | Descripción                   | Autenticación  |        Estado        |
|:------:|:-----------------------|:------------------------------|:--------------:|:--------------------:|
|  GET   | `/api/categories`      | Ver categorias                |    Publico     |       `200 OK`       |
|  GET   | `/api/categories/{id}` | Ver categorias especifico     |    Publico     |       `200 OK`       |
|  POST  | `/api/categories`      | Crear categoria               | Admin, Librero |    `201 Created`     |
|  PUT   | `/api/categories/{id}` | Actualizar categoria (nombre) | Admin, Librero |       `200 OK`       |
| DELETE | `/api/categories/{id}` | Eliminar categoria            | Admin, Librero |   `204 No Content`   |

## COPY
| Método | Endpoint                   | Descripción          |    Autenticación    |        Estado        |
|:------:|:---------------------------|:---------------------|:-------------------:|:--------------------:|
|  GET   | `/api/copies`              | Ver copias           |   Admin, Librero    |       `200 OK`       |
|  GET   | `/api/copies/{id}`         | Ver copia especifico |   Admin, Librero    |       `200 OK`       |
|  POST  | `/api/copies`              | Crear copias         |   Admin, Librero    |    `201 Created`     |
| PATCH  | `/api/copies/{id}`         | Actualizar copia     |   Admin, Librero    |       `200 OK`       |
| PATCH  | `/api/copies/{id}/restore` | Restaurar copia      |   Admin, Librero    |       `200 OK`       |
| DELETE | `/api/copies/{id}`         | Eliminar copias      |   Admin, Librero    |   `204 No Content`   |

## LOAN
### ADMIN, LIBRARIAN
| Método | Endpoint                  | Descripción                |    Autenticación    |        Estado        |
|:------:|:--------------------------|:---------------------------|:-------------------:|:--------------------:|
|  GET   | `/api/loans`              | Ver prestamos              |   Admin, Librero    |       `200 OK`       |
|  GET   | `/api/loans/{id}`         | Ver prestamo especifico    |   Admin, Librero    |       `200 OK`       |
|  POST  | `/api/loans`              | Crear prestamos            |   Admin, Librero    |    `201 Created`     |
| PATCH  | `/api/loans/{id}`         | Actualizar prestamos       |   Admin, Librero    |       `200 OK`       |
| PATCH  | `/api/loans/{id}/restore` | Restaurar prestamo         |   Admin, Librero    |       `200 OK`       |
| PATCH  | `/api/loans/{id}/return`  | Devolver prestamo          |   Admin, Librero    |       `200 OK`       |
| PATCH  | `/api/loans/{id}/extend`  | Extender plazo de prestamo |   Admin, Librero    |       `200 OK`       |
| DELETE | `/api/loans/{id}`         | Eliminar prestamos         |   Admin, Librero    |   `204 No Content`   |

### JWT ME
| Método | Endpoint                  | Descripción                         | Autenticación |        Estado        |
|:------:|:--------------------------|:------------------------------------|:-------------:|:--------------------:|
|  GET   | `/api/loans/me`           | Ver prestamos del usuario           |      JWT      |       `200 OK`       |
|  GET   | `/api/loans/me/{id}`      | Ver prestamo especifico del usuario |      JWT      |       `200 OK`       |

## PUBLISHER
| Método | Endpoint               | Descripción              | Autenticación  |        Estado        |
|:------:|:-----------------------|:-------------------------|:--------------:|:--------------------:|
|  GET   | `/api/publishers`      | Ver editoriales          |    Publico     |       `200 OK`       |
|  GET   | `/api/publishers/{id}` | Ver editorial especifico |    Publico     |       `200 OK`       |
|  POST  | `/api/publishers`      | Crear editoriales        | Admin, Librero |    `201 Created`     |
|  PUT   | `/api/publishers/{id}` | Actualizar editorial     | Admin, Librero |       `200 OK`       |
| DELETE | `/api/publishers/{id}` | Eliminar editorial       | Admin, Librero |   `204 No Content`   |

## RESERVATION
### ADMIN, LIBRARIAN
| Método | Endpoint                        | Descripción                |    Autenticación     |      Estado      |
|:------:|:--------------------------------|:---------------------------|:--------------------:|:----------------:|
|  GET   | `/api/reservations`             | Ver reservaciones          |    Admin, Librero    |     `200 OK`     |
|  GET   | `/api/reservations/{id}`        | Ver reservación especifico |    Admin, Librero    |     `200 OK`     |
|  POST  | `/api/reservations`             | Crear reservación          |    Admin, Librero    |  `201 Created`   |
| PATCH  | `/api/reservations/{id}/cancel` | Cancelar reservación       |    Admin, Librero    | `204 No Content` |
| PATCH  | `/api/reservations/{id}/extend` | Extender reservación       |    Admin, Librero    |     `200 OK`     |
| DELETE | `/api/reservations/{id}`        | Eliminar reservación       |    Admin, Librero    | `204 No Content` |
### JWT ME
| Método | Endpoint                           | Descripción                            | Autenticación |         Estado          |
|:------:|:-----------------------------------|:---------------------------------------|:-------------:|:-----------------------:|
|  GET   | `/api/reservations/me`             | Ver reservaciones del usuario          |      JWT      |        `200 OK`         |
|  GET   | `/api/reservations/me/{id}`        | Ver reservación especifico del usuario |      JWT      |        `200 OK`         |
|  POST  | `/api/reservations/me`             | Crear reservación por el usuario       |      JWT      |      `201 Created`      |
| PATCH  | `/api/reservations/me/{id}/cancel` | cancelar reservación por el usuario    |      JWT      |    `204 No Content`     |
| DELETE | `/api/reservations/me/{id}`        | Eliminar reservación  del usuario      |      JWT      |    `204 No Content`     |

## USER
### ADMIN, LIBRARIAN
| Método | Endpoint                   | Descripción                                         | Autenticación  |      Estado       |
|:------:|:---------------------------|:----------------------------------------------------|:--------------:|:-----------------:|
|  GET   | `/api/users`               | Obtener lista paginada y filtrada de usuarios       | Admin, Librero |     `200 OK`      |
|  GET   | `/api/users/{id}`          | Obtener usuario por ID                              | Admin, Librero |     `200 OK`      |
|  POST  | `/api/users`               | Crear un nuevo usuario                              | Admin, Librero |   `201 Created`   |
| PATCH  | `/api/users/{id}`          | Actualizar parcialmente datos de un usuario         | Admin, Librero |     `200 OK`      |
| PATCH  | `/api/users/{id}/email`    | Cambiar correo de un usuario como administrador     | Admin, Librero |     `200 OK`      |
| PATCH  | `/api/users/{id}/password` | Cambiar contraseña de un usuario como administrador | Admin, Librero |     `200 OK`      |
| PATCH  | `/api/users/{id}/role`     | Cambiar rol de un usuario                           |     Admin      |     `200 OK`      |
| PATCH  | `/api/users/{id}/restore`  | Restaurar un usuario eliminado o desactivado        |     Admin      |     `200 OK`      |
| DELETE | `/api/users/{id}`          | Eliminar usuario                                    |     Admin      | `204 No Content`  |

### JWT ME
| Método | Endpoint                                | Descripción                                           | Autenticación |      Estado      |
|:------:|:----------------------------------------|:------------------------------------------------------|:-------------:|:----------------:|
|  GET   | `/api/users/me`                         | Obtener perfil del usuario autenticado                |      JWT      |     `200 OK`     |
| PATCH  | `/api/users/me`                         | Actualizar perfil del usuario autenticado             |      JWT      |     `200 OK`     |
| PATCH  | `/api/users/me/email/change-request`    | Solicitar código para cambio de correo                |      JWT      |  `202 Accepted`  |
| PATCH  | `/api/users/me/email/confirm`           | Confirmar cambio de correo con código de verificación |      JWT      |     `200 OK`     |
| PATCH  | `/api/users/me/password/change-request` | Solicitar cambio de contraseña                        |      JWT      |  `202 Accepted`  |
| PATCH  | `/api/users/me/password/confirm`        | Confirmar cambio de contraseña                        |      JWT      |  `202 Accepted`  |
| DELETE | `/api/users/me`                         | Eliminar el propio usuario                            |      JWT      | `204 No Content` |


## Licencia

Este proyecto está bajo la Licencia MIT.

---

## Autor

Josue huayapa julca

- GitHub: @Huayapa
- LinkedIn: [Josue huayapa](www.linkedin.com/in/josue-huayapa-630a19316)
- Portfolio: [huayapadev](https://portafolio-huayapa.vercel.app)