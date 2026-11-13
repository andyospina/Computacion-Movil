# Informe del mapa de reseñas: qué usamos, dónde y cómo

## 1. Proveedor del mapa

**OpenStreetMap, capa «Mapnik»** (teselas desde `tile.openstreetmap.org`), dibujado con la librería
**osmdroid 6.1.20**.

- **No usa API key.** Por eso no hay ninguna clave en el repo, ni en `AndroidManifest.xml`, ni en
  `local.properties`. Lo único que OpenStreetMap exige es un *User-Agent* que identifique la app (`ReviewLab/1.0 (Android; academic project)`) y
  mostrar la atribución «© OpenStreetMap» (`CopyrightOverlay`). Ambas cosas están en `OsmMapView.kt`,
  `configureOsmdroid()` y `renderMap()`.
- El proveedor se define en **un solo lugar**: la constante `TILE_SOURCE` de `OsmMapView.kt`. Es también el único
  archivo que importa `org.osmdroid.*`.
- Si se quisiera Google Maps: dependencia `maps-compose`, la clave en `local.properties` inyectada con
  `manifestPlaceholders` en una `<meta-data com.google.android.geo.API_KEY>`, y reescribir solo `OsmMapView`.
- Para tráfico real alto, la política de OSM recomienda un proveedor de teselas propio o de pago.

> **Lección de las pruebas en el emulador:** con el *User-Agent* genérico `com.example.myapplication`, OpenStreetMap
> respondía `403 Access blocked` y el mapa salía lleno de carteles de error. Con un User-Agent que identifica la app
> carga normal. osmdroid guarda en caché hasta las teselas bloqueadas: tras cambiar el User-Agent hay que borrar
> `cache/osmdroid` (o reinstalar) en los dispositivos que ya abrieron el mapa.

## 2. Cumplimiento de los puntos de la rúbrica

| # | Punto | Estado | Dónde |
|---|---|---|---|
| 1 | Proveedor | OpenStreetMap vía osmdroid | `OsmMapView.kt` (`TILE_SOURCE`) |
| 2 | Manejo de la key | No hace falta; User-Agent y atribución configurados | `OsmMapView.kt` (`configureOsmdroid`) |
| 3 | Personalización | Pin propio teñido por puntuación (verde 4-5, ámbar 3, rojo 1-2) con el número de estrellas dentro, el seleccionado se agranda; burbujas oscuras para grupos; teselas en oscuro | `OsmMapView.kt` (`pinBitmap`, `clusterBitmap`), `res/drawable/ic_map_pin.xml` |
| 4 | Controles | Botones **+** y **−** (`SmallFloatingActionButton`); además se puede arrastrar y pellizcar | `OsmMapView.kt` |
| 5 | Modo claro / oscuro | El mapa sigue el tema real de la app (también el interruptor del Perfil): en oscuro se invierten los colores y se rota el tono 180° (agua azul, no marrón) | `OsmMapView.kt` (`darkMode`, `TilesOverlay.INVERT_COLORS`) |
| 6 | Arquitectura | DataSource (interfaz) + implementación + Repository + DTO | ver sección 3 |
| 7 | Manejo de errores | Si el usuario eligió compartir ubicación y no se obtiene, **no se publica**: se muestra el motivo y se conserva el formulario (ver sección 4) | `LocationRepository`, `NewReviewViewModel` |
| 8 | Muchos marcadores en una zona | Agrupación por cuadrícula según el zoom: burbuja con el número; al tocarla se acerca a sus reseñas; se separan al acercar | `ReviewClusterer.kt`, `OsmMapView.kt` |
| 9 | Permiso de GPS | Se pide el permiso al activar el interruptor; si el GPS está apagado no se activa y se ofrece abrir los ajustes; compartir ubicación exige permiso **y** GPS | `NewReviewScreen`, `CampoUbicacion`, `NewReviewViewModel` |
| 10 | Consulta solo de 24 h | Filtro **en la consulta de Firestore** (ver nota) | `ReviewFirestoreDataSource.observeReviewsSince` |
| 11 | Info poca al tocar un marcador | Tarjeta con producto, estrellas, título, texto (3 líneas), autor, «hace X» y likes | `TarjetaResenaMapa.kt` |
| 12 | Navegar desde esa info | «Ver detalle» → pantalla de detalle de la reseña (con like, autor y producto); «Ver producto» → ficha del producto | `ReviewDetailScreen.kt`, `Routes.ReviewDetail` |

**Nota sobre el punto 10 (honestidad):** la consulta es
`createdAt >= ahora − 24 h`, `orderBy createdAt desc`, `limit(200)` y se ejecuta en Firestore, así que el
dispositivo nunca descarga reseñas viejas. Quedan dos filtros livianos en la app que conviene saber explicar:
(a) descartar las reseñas **sin ubicación** (Firestore no permite combinar ese filtro con el rango de fecha sin un
índice compuesto adicional) y (b) un re-filtrado **cada minuto** sobre lo ya descargado, solo para que una reseña
salga del mapa al cumplir 24 h si la pantalla sigue abierta.

## 3. Arquitectura (dónde está cada pieza)

```
ReviewsMapScreen ─ ReviewsMapViewModel ─ RecentReviewsRepository ─ ReviewRemoteDataSource ─ ReviewFirestoreDataSource ─ FirestoreReviewDto
                                                                └ TimeProvider (reloj inyectable)
NewReviewScreen ─ NewReviewViewModel ─ LocationRepository ─ DeviceLocationDataSource ─ FusedDeviceLocationDataSource
                                     └ ReviewRepository.createReview(location) ─ ReviewFirestoreDataSource
ReviewDetailScreen ─ ReviewDetailViewModel ─ ReviewRepository.getReviewById / toggleLike
```

| Capa | Archivos |
|---|---|
| Interfaz DataSource | `data/datasource/ReviewRemoteDataSource.kt` (`observeReviewsSince`), `data/datasource/DeviceLocationDataSource.kt` |
| Implementación | `data/datasource/impl/ReviewFirestoreDataSource.kt`, `data/datasource/impl/FusedDeviceLocationDataSource.kt` |
| DTO | `data/dto/FirestoreReviewDto.kt` (campo `location: GeoPoint?`), `data/dto/DataContracts.kt` (`NewReviewDto.location`) |
| Modelo de dominio | `data/GeoLocation.kt`, `data/Review.kt` (`location`, `createdAtMillis`) |
| Repository | `data/repository/RecentReviewsRepository.kt`, `LocationRepository.kt`, `ReviewRepository.kt` |
| Errores | `data/LocationExceptions.kt`, `data/repository/ApiErrorMapper.kt`, `data/Result.kt` |
| Inyección | `data/di/RemoteDataSourceModule.kt`, `data/time/TimeProvider.kt` (`TimeModule`) |
| UI | `ui/screens/Map/*`, `ui/screens/ReviewDetail/*`, `ui/screens/NewReview/componentes/CampoUbicacion.kt` |
| Navegación | `navigation/Routes.kt`, `navigation/AppNavigation.kt` (acceso desde el ícono de mapa de Inicio) |
| Seguridad | `firestore.rules` (`validLocation`; la ubicación no se puede editar después) |

## 4. Manejo de errores de ubicación

| Situación | Excepción | Qué ve el usuario |
|---|---|---|
| Niega el permiso | `LocationPermissionDeniedException` | La opción queda apagada con el aviso de que sin permiso no se puede compartir |
| Permiso dado, GPS apagado | `LocationDisabledException` | La opción no se activa; aviso + botón «Abrir ajustes de ubicación» |
| GPS encendido pero sin posición a tiempo (8 s) | `LocationUnavailableException` | Error al publicar, **no se publica**, el formulario se conserva para reintentar o desactivar la opción |
| Retiran el permiso con la pantalla abierta | `SecurityException` → mismo mensaje de permiso | Igual que el primer caso |
| Falla de red/Firestore al publicar | `FirebaseFirestoreException` / `IOException` | Mensaje existente (`error_api_unreachable`, etc.) |

Si el usuario **no** activa el interruptor, la reseña se publica normal sin ubicación (opt-in).

## 5. Tecnologías usadas, dónde y cómo

| Tecnología (versión) | Dónde | Cómo se usa |
|---|---|---|
| **osmdroid 6.1.20** | `OsmMapView.kt` | `MapView` dentro de `AndroidView`: teselas Mapnik, `Marker` con íconos dibujados con `Canvas`, `MapEventsOverlay` (tocar el mapa cierra la tarjeta), `MapListener` (reagrupa al cambiar el zoom), `CopyrightOverlay`, `TilesOverlay.INVERT_COLORS` (modo oscuro) y ciclo de vida `onResume/onPause/onDetach` |
| **Google Play Services Location 21.3.0** | `FusedDeviceLocationDataSource.kt` | `FusedLocationProviderClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY)` con tope de 8 s y `lastLocation` como respaldo; solo permiso `ACCESS_COARSE_LOCATION` |
| **Firebase Firestore (BoM 33.7.0)** | `ReviewFirestoreDataSource.kt`, `FirestoreReviewDto.kt`, `FirestoreFlows.kt` | Snapshot listener (`Query.observe()` → `callbackFlow`) sobre `whereGreaterThanOrEqualTo + orderBy + limit`; ubicación como `GeoPoint` |
| **Reglas de seguridad de Firestore** | `firestore.rules` | `validLocation`: ausente, `null` o `latlng` al crear; el autor no puede cambiarla |
| **Kotlin 2.2.10 + Corrutinas / Flow** | Repositories y ViewModels | `Flow` + `combine` con un ticker de 1 min; `callbackFlow`; `suspend` + `withTimeoutOrNull` |
| **Jetpack Compose + Material 3 (BOM 2026.02.01)** | Pantallas `Map`, `ReviewDetail`, `CampoUbicacion` | `Card`, `Switch`, `SmallFloatingActionButton`, `Surface`; modo oscuro por luminancia de `MaterialTheme.colorScheme.background` |
| **Activity Result API** | `NewReviewScreen.kt` | `rememberLauncherForActivityResult(RequestPermission())` para el permiso en ejecución; `Settings.ACTION_LOCATION_SOURCE_SETTINGS` para abrir ajustes |
| **AndroidX Core (`LocationManagerCompat`)** | `FusedDeviceLocationDataSource.kt` | Saber si el GPS está encendido en cualquier versión de Android |
| **Navigation Compose 2.9.3** | `AppNavigation.kt`, `Routes.kt` | Rutas `reviews_map` y `review_detail/{reviewId}` |
| **Hilt / Dagger 2.60.1 + hilt-navigation-compose 1.2.0** | ViewModels y `RemoteDataSourceModule`, `TimeModule` | `@HiltViewModel`, `@Binds` de las interfaces |
| **MVVM + StateFlow** | `ReviewsMapViewModel`, `NewReviewViewModel`, `ReviewDetailViewModel` | Un `UiState` inmutable por pantalla; errores como `@StringRes` |
| **MockK 1.14.5 + kotlinx-coroutines-test** | `app/src/test` | Repository y ViewModel con dependencias simuladas, reloj falso para la regla de 24 h |
| **JavaFaker 1.0.2 y @faker-js/faker** | `Seeder.kt` (debug), `seed-faker.js` | Datos de ejemplo: reseñas con ubicación y ~1/3 de las últimas 24 h |
| **Firebase Emulator Suite + `@firebase/rules-unit-testing`** | `tools/emulator-tests/test.js`, `androidTest` | Pruebas de reglas e integración sin tocar producción |

## 6. Pruebas (estado verificado)

- `testDebugUnitTest`: **107 pruebas, 0 fallos** (incluye `RecentReviewsRepositoryTest`, `LocationRepositoryTest`,
  `ReviewsMapViewModelTest`, `NewReviewLocationTest`, `ReviewClustererTest`, `ReviewLocationMappingTest`).
- Reglas de Firestore (ubicación): 4 pruebas pasan en el emulador.
- `connectedDebugAndroidTest` en el Android Emulator (Pixel 6, API 37) con Firebase Emulator Suite: **49/49 pasan**
  (incluye las 4 nuevas de `observeReviewsSince`).
- **Verificado a mano en el emulador:** mapa con teselas reales, grupos que se separan al acercar, tocar un grupo,
  botones +/−, tarjeta, "Ver detalle", modo oscuro, permiso de ubicación aproximada, GPS apagado (aviso + botón de
  ajustes), "no se pudo obtener la ubicación" (no publica) y publicación con ubicación (aparece en el mapa: el
  contador pasó de 15 a 16 y se guardó el `GeoPoint`).
- **Limitación del emulador:** con solo permiso de ubicación *aproximada* el Fused Location depende de la red y el
  emulador no la simula, así que el camino feliz de la ubicación solo se pudo probar con un cambio temporal (permiso
  preciso + alta precisión + `adb emu geo fix`), ya revertido. En un teléfono real la ubicación aproximada funciona
  con Wi-Fi / antenas.

## 7. Guion para la demo

1. Cargar datos: `node seed-faker.js --target=emulator --project=reviewlab-b60be --users=15 --products=6`.
2. Inicio → ícono de mapa: se ve el contador y los grupos; tocar una burbuja acerca; **+ / −**.
3. Tocar un pin → tarjeta → **Ver detalle** → dar like / ir al autor o al producto.
4. Alternar modo oscuro en Perfil y volver al mapa.
5. Reseñar con el interruptor de ubicación: probar permiso denegado, GPS apagado (botón de ajustes) y éxito
   (el marcador aparece en vivo). En el emulador de Android: *Extended controls → Location*.
6. Mostrar la funcionalidad adicional (Productos guardados / «Me gustaron»).
