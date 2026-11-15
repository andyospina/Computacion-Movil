# Sprint 15 — Comentarios en reseñas

Funcionalidad: cada reseña tiene su **conversación**. Los usuarios preguntan o comentan sobre la experiencia
del autor («¿te duró la batería?») y el autor responde. Convierte la reseña de un monólogo en un hilo útil para
quien está decidiendo una compra.

## Qué se entregó

1. **Pantalla «Conversación»** (`Routes.ReviewComments`, `review_comments/{reviewId}`): la reseña arriba (con su
   like), los comentarios del más antiguo al más reciente y una caja para escribir con contador `n/500`.
   - Tiempo real: un comentario nuevo o borrado por otra persona aparece sin recargar; la lista baja sola al final.
   - Los comentarios propios se muestran como «Tú»; el autor de la reseña lleva la insignia «Autor».
   - Validación en el ViewModel: vacío/solo espacios → «Escribe algo antes de enviar»; más de 500 → error.
   - Doble toque en Enviar no duplica el comentario; si falla el envío se conserva el texto.
   - Borrar pide confirmación. Puede borrar **el autor del comentario** y **el autor de la reseña** (moderación).
   - La hora es relativa («hace 5 min»); menos de un minuto (o reloj del servidor adelantado) → «Justo ahora».
2. **Botón «Comentar»** en la tarjeta de reseña (`TarjetaResena`, parámetro opcional `onCommentsClick`): lista de
   reseñas del producto, feed «Siguiendo», «Me gustaron» y detalle de la reseña (mapa).
3. **Notificación «comentario»**: el autor de la reseña recibe aviso (bandeja + FCM) y al tocarlo abre la conversación.

## Arquitectura (la misma del resto de la app)

```
CommentsScreen → CommentsViewModel (StateFlow) → CommentRepository → CommentRemoteDataSource → CommentFirestoreDataSource
                                               └→ ReviewRepository (la reseña de arriba y su like)
```

| Capa | Archivos |
|---|---|
| Dominio | `data/Comment.kt` (`MAX_COMMENT_LENGTH = 500`) |
| DTO | `data/dto/FirestoreCommentDto.kt` (`CommentData`, `NewCommentDto`) |
| DataSource | `data/datasource/CommentRemoteDataSource.kt`, `impl/CommentFirestoreDataSource.kt`; binding en `RemoteDataSourceModule` |
| Repository | `data/repository/CommentRepository.kt` |
| UI | `ui/screens/Comments/` (`CommentsScreen`, `CommentsState`, `CommentsViewModel`, `componentes/TarjetaComentario`, `CampoComentario`, `DialogoBorrarComentario`), `ui/components/BotonComentarios.kt` |

Los cambios a pantallas existentes son **aditivos** (parámetros opcionales con valor por defecto, nuevo valor de
`NotificationType`); ningún comportamiento anterior cambia.

## Modelo de datos y reglas

```
comments/{id}   reviewId, userId, text, user{name,username,profileImage}, createdAt
```

Colección de primer nivel (como `follows` y `saved`): se consulta con un solo `whereEqualTo("reviewId")` sin índice
compuesto y se ordena en el cliente. Reglas (`firestore.rules`): lectura para autenticados; crear solo a nombre propio,
sobre una reseña existente, texto 1–500 sin solo espacios, sin campos extra y con `createdAt == request.time`; **no se
editan**; borra el autor del comentario o el de la reseña.

## Cloud Functions nuevas

| Función | Disparador | Qué hace |
|---|---|---|
| `onCommentCreated` | `comments/{id}` creado | Rehace la copia del autor con `users` (integridad), avisa al autor de la reseña (no a sí mismo); borra comentarios huérfanos; tolera que el comentario se borre antes de procesarlo |
| `onReviewDeleted` | `reviews/{id}` borrada | Borra la conversación de la reseña |
| `onUserProfileUpdatedComments` | `users/{uid}` actualizado | Propaga nombre/foto/username a sus comentarios |

## Pruebas

| Clase / suite | # | Tipo |
|---|---|---|
| `CommentRepositoryTest` | 10 | JVM + MockK: mapeo, permisos de borrado, sin sesión, errores traducibles, copia del autor |
| `CommentsViewModelTest` | 13 | JVM + MockK: carga, tiempo real, validación, doble envío, error conserva texto, borrar con confirmación |
| `SeederCommentsTest` | 3 | JVM: el Faker genera comentarios coherentes con las reglas |
| `CommentFirestoreDataSourceTest` | 5 | Emulador (androidTest): campos guardados, orden, tiempo real, borrado, Repository de punta a punta |
| Reglas `comments` (`tools/emulator-tests`) | 13 | Emulador de Firestore |
| Functions de comentarios (`tools/emulator-tests`) | 4 | Emulador: notificación, integridad, sin auto-aviso, borrado en cascada |

Resultado: `testDebugUnitTest` 133/133; androidTest de comentarios 5/5; reglas + Functions 63/66 — las 3 que fallan
son la cadena de *like* preexistente (el primer trigger absorbe el arranque en frío del worker y supera los 20 s de
espera). Verificado a mano en el Android Emulator (API 37): enviar, validar, respuesta en vivo del autor y borrar.

### Notas para correr las pruebas de Functions en local

- `firebase-admin` 14 necesita **Node ≥ 22** (con Node 20 falta `@google-cloud/firestore` y las funciones no cargan).
- El `firebase-tools` v13 de `tools/emulator-tests` no es compatible con `firebase-functions` v7 (`functions.config()`
  fue eliminado): usar la CLI global reciente:
  `firebase emulators:exec --config ../../firebase.json --only firestore,functions --project demo-reviewlab "node test.js ../.."`
- En Windows el proceso Java del emulador puede quedar vivo tras `emulators:exec`; si el puerto 8080 queda ocupado, cerrarlo.

## Datos Faker

`Seeder.dataset(maxCommentsPerReview = 3)` y `seed-faker.js --comments=3`: entre 0 y N comentarios por reseña,
posteriores a la reseña, a veces del propio autor.

## Despliegue

Publicado en `reviewlab-b60be`: `firebase deploy --only firestore:rules` y `firebase deploy --only functions`
(7 funciones, Node.js 22, 2.ª gen.). App contra Firebase real: `./gradlew installDebug -PuseEmulator=false`.
