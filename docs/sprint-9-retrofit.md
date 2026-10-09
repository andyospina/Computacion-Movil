# Sprint 9 — Consumo del backend con Retrofit

La app (ReviewLab: productos y reseñas) consume el backend Express/PostgreSQL de `Back-Movil`.
El plan original estaba redactado para tweets; aquí se aplicó a las entidades equivalentes que
existen en la app y en el backend: **Product**, **Review** y **User**.

## Mapa endpoint → capas

| Endpoint | Service | Data source | Repository | ViewModel |
|---|---|---|---|---|
| `GET /products` | `ProductRetrofitService.getProducts` | `ProductRemoteDataSource` | `ProductRepository.getProducts` | Home, Search |
| `GET /products/:id` | `getProductById` | `ProductRemoteDataSource` | `getProductById` | ProductDetail, ReviewsList, ReviewPublished |
| `GET /reviews` (filtrado en cliente) | `ReviewRetrofitService.getReviews` | `ReviewRemoteDataSource` | `getReviewsByProduct` / `getReviewsByUser` | ProductDetail, ReviewsList, Profile |
| `GET /reviews/:id` | `getReviewById` | `ReviewRemoteDataSource` | `getReviewById` | NewReview (editar) |
| `POST /reviews` | `createReview` | `ReviewRemoteDataSource` | `createReview` | NewReview |
| `PUT /reviews/:id` | `updateReview` | `ReviewRemoteDataSource` | `updateReview` | NewReview (editar) |
| `DELETE /reviews/:id` (204) | `deleteReview` | `ReviewRemoteDataSource` | `deleteReview` | Profile |
| `GET /users/:id` | `UserRetrofitService.getUserById` | `UserRemoteDataSource` | `UserRepository.getUserById` | Profile, UserProfile (perfil de otro usuario, solo lectura; se abre tocando el autor de una reseña) |

## Decisiones

- **Red:** base `http://10.0.2.2:3000/` en `ApiConfig`; HTTP en claro permitido solo para `10.0.2.2`
  (`res/xml/network_security_config.xml`). JSON con `kotlinx.serialization` (`ignoreUnknownKeys`).
- **Hilt:** `AppModule` provee `OkHttpClient`, `Retrofit` y los 3 servicios; `RemoteDataSourceModule`
  enlaza cada interfaz de data source con su implementación Retrofit.
- **DTOs:** IDs `Int` en el JSON y `String` en UI/navegación; la conversión está en los mapeos
  (`toProduct`, `toReview`, `toUser`). Los repositorios nunca exponen `retrofit2.Response`.
- **Errores:** `runCatchingApi` convierte excepciones en `Result.Failure` con un mensaje amigable
  (400 / 404 / 5xx / sin conexión / inesperado). El código HTTP solo se escribe en Logcat (tag `Api`).
- **Estados de UI:** `isLoading` + `errorMessageRes` en Home, Search, ProductDetail, ReviewsList y
  Profile, mostrados con `CargaOError` (progreso, error + «Reintentar», o contenido).
- **Crear/editar:** al guardar con éxito se navega por el flag `publicado` observado con
  `LaunchedEffect`; si falla se conserva el formulario y se muestra el error.
- **Eliminar:** tras el 204 se filtra la reseña de la lista local sin volver a pedirla.

## Limitaciones conocidas

- **Usuario actual temporal:** `ApiConfig.TEMP_CURRENT_USER_ID = "1"`. El uid de Firebase Auth aún no
  está vinculado a `users.id` de PostgreSQL, por lo que las reseñas creadas y el perfil usan ese usuario.
- **Sin filtros en el backend:** `GET /reviews` no filtra por producto ni por usuario y `GET /products`
  no busca por nombre; se filtra en la app. Lo ideal es añadir `?productId=`, `?userId=` y `?q=`.
- **Campos que el backend no tiene** (se muestran con valor neutro): comprador verificado (`false`),
  votos útiles (0), reseñas con foto (`false`), `recommended` se calcula como `rating >= 4` al crear/editar.
  `reviewCount`, `recommendPercent` y el desglose por estrellas se calculan con las reseñas del producto.
- **Flujo «Escribir reseña» desde el hub** (sin producto elegido): usa el primer producto del catálogo.
- Las categorías de los chips pasaron a las del backend (Electrónica, Hogar, Deportes, Libros, Moda).
- Notificaciones siguen usando datos locales (fuera del alcance del sprint). `Local*Provider` de
  productos/reseñas/usuario quedan solo como datos de `@Preview`.

## Cómo ejecutar

```bash
cd ../Back-Movil && npm install && npm run dev     # backend en :3000 (o DB_DIALECT=sqlite)
./gradlew :app:assembleDebug                       # compilar la app
./gradlew :app:testDebugUnitTest                   # DtoMappingTest con JSON reales del backend
```

Ejecutar en el emulador (Android Studio); `10.0.2.2` apunta a la máquina host.

## Pruebas realizadas

- Compilación `assembleDebug` correcta (grafo de Hilt resuelto).
- `DtoMappingTest` (JSON reales del backend → DTO → modelos de UI): pasa.
- Prueba temporal con Retrofit/JVM contra el backend real (SQLite local): listar productos, producto,
  usuario, listar/crear/obtener/editar/eliminar reseña, 404 y 400 como `HttpException`, y DELETE 204 con
  retorno `Unit`. Pasó y se eliminó.
- **No probado:** ejecución en emulador/dispositivo (no disponible aquí). Falta verificar a mano:
  carga y error con el servidor apagado, flujo crear → publicada, editar y eliminar desde Perfil.
