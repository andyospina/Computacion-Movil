# Sprint 16 — Compartir reseña, Reportar reseña y correcciones de UI

## Compartir reseña
- Ícono de compartir (`BotonCompartir`) en el pie de **`TarjetaResena`**, así que aparece en Detalle de reseña,
  Lista de reseñas, Siguiendo, «Me gustaron» y Conversación.
- `ui/share/ReviewShare.kt`: construye el texto (autor, producto, estrellas ★★★★☆, título y cuerpo recortado a
  280 caracteres) con la plantilla `R.string.share_review_text` y abre el selector de Android (`ACTION_SEND`,
  `text/plain`). Sin apps disponibles muestra un aviso.
- Pruebas: `ReviewShareTest` (10, corridas en JVM).

## Reportar reseña
- Botón «Reportar reseña» en el detalle de una reseña **ajena** (no aparece en la propia ni sin sesión). Abre un
  diálogo con el motivo (spam, ofensivo, falsa, otro); tras enviar queda «Ya reportaste esta reseña».
- Datos: colección `reports/{userId_reviewId}` (un reporte por usuario y reseña), mismo patrón que `saved`:
  `ReportRemoteDataSource` → `ReportFirestoreDataSource` → `ReportRepository` → `ReviewDetailViewModel`.
- `firestore.rules`: solo se crea el propio reporte, con motivo válido, sobre una reseña existente que no sea
  tuya; se puede leer el propio; no se edita ni se borra. 11 casos nuevos en `tools/emulator-tests/test.js`
  (verificados contra el emulador de Firestore).
- Pruebas: `ReportRepositoryTest` (7) y `ReviewDetailViewModelTest` (7).

## Correcciones de UI
| Pantalla | Problema | Corrección |
|---|---|---|
| Login | Sin scroll: en pantallas bajas, horizontal o con teclado el formulario no cabía | `verticalScroll` |
| Perfil | Columna fija: los 5 botones dejaban «Mis reseñas» aplastada o fuera de la vista | Todo en un `LazyColumn` |
| Inicio | Buscador + chips + título fijos dejaban la lista sin espacio en horizontal / letra grande | Un solo `LazyColumn` |
| Lista de reseñas | Encabezado y filtros fijos con el mismo problema | Un solo `LazyColumn` |
| Tarjeta de reseña | Autor largo o pie con fecha + «Recomendado» + acciones se salía de la tarjeta | `weight` + elipsis |
| Mi reseña (Perfil) | Nombre de producto largo empujaba las estrellas fuera; pie con fecha/like/estado | `weight` + elipsis |
| Reseñas destacadas | Autor largo empujaba las estrellas | `weight` + elipsis |
| Barra superior | Título largo | Elipsis |
| Tema claro | `primary` era lima eléctrico: texto de `OutlinedButton`/`TextButton`, spinner y borde enfocado casi invisibles sobre fondo claro | `primary = DeepLime` (los botones rellenos fijan lima + tinta) |
| Detalle de reseña | Sin scroll | `verticalScroll` |

> Estos cambios se escribieron sin poder compilar ni correr la app (el entorno de desarrollo no tenía Android SDK):
> hay que compilar y probar en el emulador, ver `./gradlew testDebugUnitTest`.
