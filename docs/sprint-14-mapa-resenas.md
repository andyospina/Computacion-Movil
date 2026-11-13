# Sprint 14 — Mapa de reseñas de las últimas 24 horas

Requisito del cliente: *«una pantalla con un mapa donde se vean marcadores en la ubicación donde se publicaron
los últimos reviews; para no saturar, solo los de las últimas 24 horas; al tocar un marcador debe aparecer
información del review»*.

## Qué se entregó

1. **Captura de ubicación al publicar** (antes no existía ningún dato de ubicación): en «Nueva reseña» hay un
   interruptor *«Incluir mi ubicación aproximada»*, **desactivado por defecto** (la ubicación es un dato
   personal). Al activarlo se pide el permiso `ACCESS_COARSE_LOCATION` en ejecución; si se niega, la opción
   queda apagada y se avisa. Si el GPS no responde, la reseña se publica igual, sin ubicación.
2. **Pantalla «Reseñas cerca de ti»** (`Routes.ReviewsMap`, ícono de mapa en la barra superior de Inicio):
   mapa con un marcador por reseña, coloreado según la puntuación (verde 4-5, ámbar 3, rojo 1-2).
   Al tocar un marcador (se agranda) aparece una tarjeta con producto, estrellas, título, texto, autor,
   «hace X», likes y botones *Ver producto* / *Ver autor*. Tocar el mapa o la ✕ la cierra. El mapa se encuadra
   solo sobre los marcadores y muestra un contador («N reseñas en las últimas 24 h») o el estado vacío.
3. **Tiempo real y ventana móvil de 24 h**: la consulta se suscribe con un *snapshot listener* (una reseña nueva
   aparece sin recargar) y cada minuto se vuelve a filtrar por antigüedad, así una reseña sale del mapa al
   cumplir 24 h aunque la pantalla siga abierta (y la tarjeta se cierra si era la seleccionada).

## Decisiones de diseño

| Tema | Decisión | Por qué |
|---|---|---|
| Librería de mapas | **osmdroid** (OpenStreetMap) | No requiere API key ni cuenta de facturación: funciona al clonar el repo. Todo osmdroid vive en un solo composable (`OsmMapView`); migrar a Google Maps Compose solo toca ese archivo |
| Dónde se filtra | Servidor: `createdAt >= ahora-24h`, `orderBy createdAt desc`, `limit(200)`. Cliente: descarta las que no tienen `location` y re-filtra cada minuto | Un único rango sobre un solo campo no necesita índice compuesto; el límite evita saturar el mapa y la lectura |
| Modelo | `location: GeoPoint?` en `reviews/{id}`; dominio `GeoLocation(lat, lng)` | Las reseñas viejas (sin campo) siguen funcionando; el dominio no depende de Firebase |
| Permiso | Solo ubicación **aproximada** | Para ubicar una reseña en un mapa de ciudad no hace falta precisión exacta |
| Reglas | `location` ausente, `null` o `latlng` al crear; el autor **no puede editarla** después | Evita falsificar/mover la ubicación; el update del autor ya solo permite `rating/title/body/recommended` |
| Reloj | `TimeProvider` inyectable | Permite probar la regla de 24 h sin esperar |

## Arquitectura (misma que el resto de la app)

```
ReviewsMapScreen → ReviewsMapViewModel (StateFlow) → RecentReviewsRepository → ReviewRemoteDataSource.observeReviewsSince
                                                                            └→ TimeProvider
NewReviewScreen  → NewReviewViewModel → LocationRepository → DeviceLocationDataSource (Fused Location)
                                      └→ ReviewRepository.createReview(location) → ReviewRemoteDataSource
```

- `ReviewRemoteDataSource.observeReviewsSince` tiene implementación por defecto que lanza
  `UnsupportedOperationException` (como los likes): el backend Retrofit no tiene ubicación.
- Errores como `Result.Failure` con mensaje traducible; el mapa reutiliza `CargaOError` (cargando / error / reintentar).

## Pruebas

| Clase | # | Tipo |
|---|---|---|
| `RecentReviewsRepositoryTest` | 7 | JVM + MockK: filtro 24 h, sin ubicación, reseña pendiente del servidor, ventana móvil, backend no soportado |
| `ReviewsMapViewModelTest` | 5 | JVM + MockK: carga, selección, cierre de tarjeta si la reseña vence, vacío ≠ error, reintentar |
| `NewReviewLocationTest` | 4 | JVM + MockK: opt-in, GPS sin respuesta, permiso denegado |
| `ReviewLocationMappingTest` | 6 | JVM: DTO ↔ dominio, coordenadas inválidas descartadas |
| `SeederTest` (+1) | 7 | JVM: el Faker genera reseñas recientes con ubicación y otras que el mapa debe descartar |
| `ReviewFirestoreDataSourceTest` (+4) | — | Emulador (androidTest): guarda `GeoPoint`, ventana de 24 h, `limit`, tiempo real |
| Reglas `location` (`tools/emulator-tests`) | 4 | Emulador de Firestore |

Estado verificado: `testDebugUnitTest` 93/93 pasan; las 4 pruebas de reglas nuevas pasan en el emulador;
`compileDebugAndroidTestKotlin` compila. **Pendiente de ejecutar:** las 4 pruebas nuevas de `androidTest` y la
revisión visual del mapa en un dispositivo/emulador Android (no había `adb` disponible en esta sesión).

## Cómo probarlo a mano

1. Levantar el emulador de Firebase y cargar datos (el Faker ahora genera reseñas con ubicación, ~1/3 de las
   últimas 24 h): `node seed-faker.js --target=emulator --project=reviewlab-b60be --users=10 --products=6`
   (o el ícono «Seed (debug)» de la app).
2. Inicio → ícono de mapa → tocar un marcador.
3. Publicar una reseña con el interruptor de ubicación activo (en el emulador de Android fijar una ubicación en
   *Extended controls → Location*) y ver el marcador aparecer en vivo.

## Antes de producción

- **Publicar `firestore.rules`** (regla `validLocation`): `firebase deploy --only firestore:rules`.
- El mapa descarga teselas de `tile.openstreetmap.org`: para mucho tráfico real conviene un proveedor de teselas
  propio (política de uso de OSM). Ya se envía un *User-Agent* propio y la atribución © OpenStreetMap.
