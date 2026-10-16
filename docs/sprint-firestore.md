# Sprint — Cloud Firestore como backend de datos

El plan original hablaba de tweets; en ReviewLab las entidades equivalentes son **User**, **Product**
y **Review** (no existen respuestas anidadas, así que no hay eliminación recursiva). Retrofit se
conserva como alternativa y se selecciona en un único punto.

## Selección Retrofit / Firestore

`data/di/RemoteDataSourceModule.kt` enlaza cada interfaz `*RemoteDataSource` con su implementación y
`CurrentUserIdProvider` con su proveedor de identidad:

| Interfaz | Firestore (actual) | Retrofit |
|---|---|---|
| `UserRemoteDataSource` | `UserFirestoreDataSource` | `UserRetrofitDataSource` |
| `ProductRemoteDataSource` | `ProductFirestoreDataSource` | `ProductRetrofitDataSource` |
| `ReviewRemoteDataSource` | `ReviewFirestoreDataSource` | `ReviewRetrofitDataSource` |
| `CurrentUserIdProvider` | `FirebaseCurrentUserIdProvider` (uid de Auth) | `RetrofitCurrentUserIdProvider` (usuario "1") |

Ningún ViewModel ni pantalla conoce la tecnología. Verificado: la app compila con los bindings de
Firestore y con los de Retrofit (sin ejecutar ninguna de las dos en dispositivo).

### Diferencias reales (no son 100 % intercambiables)
- **Registro:** Retrofit no puede guardar el perfil de un usuario de Firebase (`POST /users` exige
  contraseña y crea su propio id numérico), así que `registerUser` lanza `UnsupportedOperationException`
  y registrarse en modo Retrofit falla (se revierte la cuenta de Auth).
- **Identidad:** con Retrofit el "usuario actual" es el usuario sembrado `1`, no el de Firebase Auth.
- **País:** el backend Express no tiene campo `country`; editar el perfil solo envía nombre y bio.
- **Ids:** Retrofit usa enteros en el JSON; Firestore usa `String`. La UI y la navegación siempre usan `String`.

## Modelo de datos en Firestore

```
users/{uid}        username, name, email, country, bio, profileImage, createdAt
products/{id}      name, category                     (rating y estadísticas se calculan)
reviews/{id}       userId, productId, productName, rating, title, body, recommended,
                   user{name, username, profileImage}, createdAt
```

- `users/{uid}`: el id del documento es el uid de Firebase Authentication. La contraseña no se guarda.
- **Desnormalización:** cada reseña lleva una copia del autor (`user`) y del nombre del producto para
  pintar la tarjeta sin más consultas. **La copia no se sincroniza**: si el autor cambia su nombre
  o foto, las reseñas ya publicadas conservan los valores antiguos.
- **Estadísticas de producto** (promedio, % recomendado, desglose por estrellas) se calculan en el
  cliente con las reseñas. Con muchas reseñas habría que guardarlas agregadas (p. ej. con Cloud Functions).
- **Consultas:** reseñas por producto/usuario con `whereEqualTo` y orden más reciente primero en el
  cliente (where + orderBy exigiría un índice compuesto).

## Flujo de datos

`Pantalla → ViewModel → Repository → *RemoteDataSource (interfaz) → Firestore / Retrofit`

Los data sources devuelven abstracciones (`UserDto`, `ProductData`, `ReviewData`) con `toXxx()` hacia
el modelo de dominio; cada backend tiene sus DTO (`FirestoreUserDto`, `FirestoreReviewDto`, …, con
valores por defecto para que Firestore pueda deserializar y toleren campos ausentes).
Los errores pasan por `runCatchingApi` → `Result.Failure(messageRes)`; `FirebaseFirestoreException`
se traduce a mensajes (sin permiso, sin conexión), y un documento inexistente a `NotFoundException`.

## Funcionalidades

| Requisito | Dónde |
|---|---|
| Guardar info en Firestore al registrar | `RegisterViewModel` → `AuthRepository.signUp` → `UserRepository.registerUser`. Si falla el guardado se borra la cuenta de Auth para poder reintentar |
| Ver mi perfil | `ProfileViewModel` (uid de `CurrentUserIdProvider`) |
| Modificar mi perfil | Botón «Editar perfil» (nombre, país, bio) y foto, que también se copia a `users/{uid}.profileImage` |
| Ver perfil de otro usuario | Tocar el autor de una reseña → `UserProfile/{userId}` (solo lectura) |
| Ver todos los artículos / detalle | `ProductRepository` (Home, Search, ProductDetail) |
| Ver reseñas de un artículo | `ReviewRepository.getReviewsByProduct` (ProductDetail, ReviewsList) |
| Escribir reseña | `NewReviewViewModel` → `ReviewRepository.createReview` (copia autor y producto) |

Editar y eliminar reseñas propias solo se ofrece en «Mi perfil»; los perfiles ajenos son de solo
lectura. Ocultar botones no es seguridad: las reglas exigen que `userId` de la reseña sea el uid de
quien la modifica, y que `userId` y `productId` no cambien al editar.

## Sincronización de pantallas

Home, ProductDetail, ReviewsList y Profile se recargan cada vez que la pantalla vuelve a componerse
(`LaunchedEffect`), con refresco silencioso si ya hay datos: sin indicador de carga y un fallo no
oculta lo que ya se ve. Home dejó de cargar en `init` para no duplicar la petición inicial.

## Seguridad (`firestore.rules`)

Todo requiere sesión iniciada. `users`: cualquiera autenticado lee; solo el dueño crea/edita su
documento y no puede cambiar `username` ni `email`. `products`: solo lectura. `reviews`: lectura
para autenticados; crear/editar/borrar solo el autor, con rating 1–5. Los perfiles son públicos entre
usuarios autenticados por diseño (necesario para «ver perfil de otro usuario»).

**Para producción:** publicar estas reglas (Console → Firestore → Reglas, o `firebase deploy --only
firestore:rules`), activar App Check, hacer único el `username` (colección `usernames/{name}` con
transacción), guardar estadísticas agregadas y poner presupuesto/alertas en Blaze.

## Datos semilla

`tools/seed-firestore/` (Admin SDK, idempotente): 3 usuarios semilla (sin cuenta de Auth), 5
productos y 9 reseñas.

```bash
cd tools/seed-firestore && npm install
# PowerShell: $env:GOOGLE_APPLICATION_CREDENTIALS = "C:\ruta\clave-de-servicio.json"
node seed.js
```

La clave de servicio se genera en Console → Configuración del proyecto → Cuentas de servicio. No se
sube al repositorio (`.gitignore`).

## Pruebas

Ejecutadas: `assembleDebug` y `testDebugUnitTest` (mapeo de DTO de Retrofit y de Firestore, valores
por defecto, desnormalización del autor, estadísticas de producto) con los dos conjuntos de bindings.
**No ejecutadas** (requieren emulador y proyecto Firebase): registro real, edición de perfil, CRUD de
reseñas contra Firestore, navegación entre perfiles, estados de carga/error, reglas de seguridad.

## Limitaciones
- El campo «celular» del registro se pide pero no se guarda.
- `username` no es único.
- Los índices/consultas se resuelven en el cliente (catálogo pequeño).
- Si el autor cambia su perfil, sus reseñas anteriores conservan el nombre y la foto antiguos.
