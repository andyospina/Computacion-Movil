# Documento de requisitos — ReviewLab

## 1. Propósito

ReviewLab es una aplicación móvil Android que permite a los usuarios
descubrir productos, leer reseñas de otros compradores y publicar sus
propias reseñas.

## 2. Alcance (Sprint 5)

Arquitectura MVVM con `MutableStateFlow`, 11 pantallas en Jetpack Compose,
navegación centralizada y un único `Scaffold` para toda la aplicación.

## 3. Requisitos funcionales

| ID | Requisito | Pantalla(s) |
|----|-----------|-------------|
| RF-01 | El usuario debe poder iniciar sesión con correo y contraseña. | Login |
| RF-02 | El usuario debe poder crear una cuenta nueva (correo, celular, contraseña). | Register |
| RF-03 | El sistema debe validar que ningún campo obligatorio quede vacío. | Login, Register, New Review |
| RF-04 | El sistema debe validar que la contraseña tenga al menos 6 caracteres. | Register |
| RF-05 | El sistema debe rechazar el registro si el correo ya existe (`admin@admin.com`). | Register |
| RF-06 | El usuario debe poder ver una lista de productos en tendencia y filtrarlos por categoría. | Home |
| RF-07 | El usuario debe poder buscar productos por nombre. | Search |
| RF-08 | El usuario debe poder ver el detalle de un producto y sus reseñas destacadas. | Product Detail |
| RF-09 | El usuario debe poder ver todas las reseñas de un producto, ordenarlas y filtrarlas por "con fotos". | Reviews List |
| RF-10 | El usuario debe poder escribir una reseña con puntuación de 1 a 5 estrellas, título y comentario. | New Review |
| RF-11 | El sistema debe confirmar visualmente que la reseña fue publicada. | Review Published |
| RF-12 | El usuario debe poder ver y marcar como leídas sus notificaciones. | Notifications |
| RF-13 | El usuario debe poder ver su perfil, sus estadísticas y sus propias reseñas. | Profile |
| RF-14 | El usuario debe poder activar/desactivar el modo oscuro desde su perfil. | Profile |
| RF-15 | La aplicación debe navegar entre todas las pantallas mediante un componente de navegación centralizado. | Global |

## 4. Requisitos no funcionales

| ID | Requisito |
|----|-----------|
| RNF-01 | La interfaz debe estar construida enteramente con Jetpack Compose. |
| RNF-02 | Toda pantalla con estado debe seguir el patrón MVVM (`ViewModel` + `UiState` con `MutableStateFlow`). |
| RNF-03 | Debe existir un único `Scaffold` para toda la aplicación. |
| RNF-04 | La navegación debe estar centralizada en un solo archivo (`AppNavigation.kt`). |
| RNF-05 | Los componentes visuales pequeños deben recibir su estado por parámetro (state hosting), sin estado propio. |
| RNF-06 | El código debe usar nombres de clases, funciones y variables descriptivos, sin comentarios redundantes. |
| RNF-07 | La aplicación debe soportar Android API 24 en adelante. |

## 5. Reglas de negocio

- **Campos obligatorios**: si algún campo requerido está vacío, se muestra
  el mensaje "Todos los campos son obligatorios" y no se navega.
- **Contraseña mínima**: si la contraseña tiene menos de 6 caracteres, se
  muestra "La contraseña debe tener al menos 6 caracteres".
- **Correo ya registrado**: si el correo ingresado en Register es
  `admin@admin.com`, se muestra "Este correo ya está en uso".
- **Registro/Login exitoso**: si todas las validaciones pasan, el
  `ViewModel` marca `navigate = true` y la pantalla navega a Home.

## 6. Usuarios / roles

Por ahora existe un único rol: **usuario comprador**, que puede iniciar
sesión, registrarse, explorar productos y publicar reseñas. No hay rol de
administrador ni backend real: los datos se sirven desde providers locales
en memoria (`data/Local*Provider.kt`).
