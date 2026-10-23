# Sprint 11 — Likes, seguidores, tiempo real y notificaciones push (FCM)

El video aplica FCM a un clon de Twitter (tweets y seguidores). En ReviewLab lo equivalente es:
**like a una reseña**, **seguir a un usuario**, feed **«Siguiendo»** y una **notificación push cuando
alguien da like a tu reseña** (también cuando alguien empieza a seguirte, como en el video).

## Requisitos de la entrega → dónde están

| Requisito | Implementación |
|---|---|
| Dar / quitar like; la UI sabe si ya di like (0.2) | `BotonLike` (corazón lleno/vacío) en `TarjetaResena`, `ResenasDestacadas` y `TarjetaMiResena`. `ReviewRepository.toggleLike` decide like/unlike con `Review.likedByMe`, que sale de `likedBy` contiene mi uid |
| Cantidad de likes (0.05) | Número junto al corazón (`Review.likesCount = likedBy.size`). En el perfil, «Likes recibidos» = suma de likes de sus reseñas |
| Seguir / dejar de seguir (0.15) | `BotonSeguir` en `UserProfileScreen` → `FollowRepository.follow/unfollow` (oculto en el propio perfil) |
| Cantidad de seguidores (0.05) | `EstadisticasSeguidores` («N Seguidores · M Siguiendo») en Perfil y en el perfil de otro usuario, en tiempo real |
| Ver solo publicaciones de quienes sigo (0.2) | Pestaña **Siguiendo** de la barra inferior → `FeedScreen` / `FeedViewModel` |
| Ver seguidores y seguidos en el perfil (0.2) | Tocar los contadores → `FollowListScreen` (pestañas Seguidores / Siguiendo; cada fila abre ese perfil) |
| Tiempo real de reseñas (0.2) | Snapshot listeners (`Query.observe()` en `FirestoreFlows.kt`) en detalle de producto, lista de reseñas, feed, perfil de otro usuario, contadores y bandeja |
| Notificación cuando recibo un like (0.2) | Cloud Function `onReviewLiked` → bandeja `users/{uid}/notifications` + push FCM a todos los dispositivos del autor |
| **Adicional:** función en línea para la integridad | `onReviewCreated` (rehace la copia del autor y del producto con los datos reales) y `onUserProfileUpdated` (propaga nombre/foto/username a todas sus reseñas). Resuelve la limitación del Sprint 10: «la copia no se sincroniza» |
| **Adicional:** intercambiar Firebase / Express | Nuevas interfaces `FollowRemoteDataSource` y `NotificationRemoteDataSource` con implementación Firestore y Retrofit; se eligen en `RemoteDataSourceModule`. Verificado: la app compila con ambos conjuntos de bindings |

## Modelo de datos

```
reviews/{id}                      … + likedBy: [uid, …]          (likes = likedBy.size)
follows/{followerId_followedId}   followerId, followedId, createdAt
users/{uid}/fcmTokens/{token}     token, platform, updatedAt      (privado: solo el dueño)
users/{uid}/notifications/{id}    type (like|follow), actorId, actorName, reviewId, reviewTitle,
                                  productId, read, createdAt      (la escribe la Cloud Function)
```

Decisiones:
- **`likedBy` como arreglo** en la reseña: un solo documento basta para pintar el contador *y* saber
  si yo ya di like, el listener de reseñas trae los likes en vivo sin consultas extra, y
  `arrayUnion/arrayRemove` son atómicos. Límite: un documento de Firestore pesa máx. 1 MiB (decenas
  de miles de likes por reseña). A esa escala habría que pasar a `reviews/{id}/likes/{uid}` + contador.
- **`follows` como colección plana** con id `seguidor_seguido`: seguir es un único `set` (no hay dos
  documentos que mantener sincronizados), el id impide duplicados y las dos consultas
  (`followedId ==` / `followerId ==`) dan seguidores y seguidos. Los contadores son el tamaño de esas
  listas: no hay contador guardado que alguien pueda falsear.
- **Tokens en subcolección privada** y no en `users/{uid}` (que es público): nadie más puede leerlos
  y un usuario puede tener varios dispositivos. La función borra los tokens que FCM rechaza.

## Flujo de una notificación de like

```
Ana toca ♥  →  ReviewRepository.toggleLike  →  reviews/r1.likedBy += "ana"   (reglas: solo su uid)
                                               │
          listener de reseñas (todas las pantallas abiertas) ←┘  el corazón y el contador cambian al instante
                                               │
Cloud Function onReviewLiked  ←  reviews/r1 actualizada; uid nuevo en likedBy, distinto del autor
   ├─ users/{autor}/notifications/like_r1_ana   (id fijo: quitar y volver a dar like no repite el aviso)
   └─ FCM sendEachForMulticast a users/{autor}/fcmTokens/*  →  push «Nuevo me gusta»
                                               │
App del autor: en segundo plano la muestra el sistema; en primer plano MyFirebaseMessagingService
              → AppNotifications.show. La bandeja (campana en Home) se actualiza en vivo.
```

## Android (FCM)

| Pieza | Archivo |
|---|---|
| Dependencia `firebase-messaging` (Analytics ya estaba) | `gradle/libs.versions.toml`, `app/build.gradle.kts` |
| Servicio que recibe mensajes y `onNewToken` (Hilt `@AndroidEntryPoint`) | `notifications/MyFirebaseMessagingService.kt` |
| Registro del servicio, `POST_NOTIFICATIONS`, canal e ícono por defecto | `AndroidManifest.xml` |
| Canal «Actividad social» y notificación en primer plano | `notifications/AppNotifications.kt` (se crea en `BaseApplication`) |
| Pedir el permiso en Android 13+ (una vez, si no está concedido) | `ui/components/SolicitarPermisoNotificaciones.kt`, llamado en `HomeScreen` |
| Guardar el token al entrar con sesión / al rotar / borrarlo al cerrar sesión | `NotificationRepository` (`HomeViewModel`, servicio, `ProfileViewModel.logoutButtonPress`) |
| Bandeja real (antes datos locales) | `NotificationsScreen` / `NotificationsViewModel`; tocar un like abre el producto, un seguidor abre su perfil |

Capas: `Screen → ViewModel → Repository → *RemoteDataSource (interfaz) → Firestore / Retrofit`.
Toda escritura usa DTO (`FirestoreFollowDto`, `FirestoreDeviceTokenDto`, `FirestoreNotificationDto`);
los flujos en vivo se convierten en `Flow<Result<T>>` con `asResult()` (mismos mensajes de error que
`runCatchingApi`). Un fallo puntual (like, seguir) se muestra con un aviso breve (`MensajeErrorAccion`)
sin ocultar la pantalla.

## Seguridad (`firestore.rules`)

- Se quitó cualquier idea de regla general `/{document=**}`: las reglas se combinan con OR y anularía
  todo lo demás. Lo que no está permitido explícitamente queda denegado.
- **Likes:** cualquier usuario autenticado puede modificar `likedBy` de una reseña **solo** para
  agregar o quitar **su propio** uid, una vez; no puede tocar otro campo en la misma escritura.
- **Reseñas:** el autor edita solo `rating/title/body/recommended` (ya no puede inflar `likedBy`);
  se crean con `likedBy` vacío, título 1–120 caracteres, cuerpo ≤ 2000 y producto existente.
- **Perfil:** el dueño cambia solo `name/country/bio/profileImage` (nombre 1–80, bio ≤ 300).
- **Follows:** se crea solo como uno mismo, con el id `miUid_otro`, a un usuario existente, no a uno
  mismo y con `createdAt` del servidor; solo quien sigue puede borrarlo; no se edita.
- **Tokens:** solo el dueño. **Bandeja:** el dueño lee y cambia `read`; nadie crea notificaciones
  desde el cliente (solo la Cloud Function, que usa el Admin SDK y por eso valida lo que lee).

## Cloud Functions (`functions/index.js`, Node 22, API v2 de firebase-functions)

| Función | Disparador | Qué hace |
|---|---|---|
| `onReviewLiked` | `reviews/{id}` actualizada | Detecta uids nuevos en `likedBy` (ignora el like propio), notifica y envía push al autor |
| `onUserFollowed` | `follows/{id}` creado | Notifica al seguido «X ha comenzado a seguirte» |
| `onReviewCreated` | `reviews/{id}` creada | Integridad: reemplaza `user{}` y `productName` por los datos reales de `users` y `products` |
| `onUserProfileUpdated` | `users/{uid}` actualizado | Integridad: si cambió nombre, username o foto, actualiza la copia en todas sus reseñas (lotes de 450) |

## Despliegue

Cloud Functions exige el plan **Blaze** (pago por uso; el uso de este proyecto cabe en la capa
gratuita, pero conviene poner una alerta de presupuesto). Sin desplegar las funciones, likes,
seguidores y tiempo real funcionan; solo faltarían las notificaciones y las correcciones de integridad.

```bash
npm install -g firebase-tools
firebase login
cd functions && npm install && cd ..
firebase deploy --only firestore:rules,functions      # usa .firebaserc → reviewlab-b60be
```

Datos de ejemplo (ahora con likes y seguidores): `tools/seed-firestore` (igual que en el Sprint 10).

## Pruebas

Ejecutadas aquí:
- `assembleDebug` con los bindings de Firestore **y** con los de Retrofit.
- `testDebugUnitTest`: 23 pruebas (nuevas en `SocialMappingTest`: likes/likedByMe, conteo de
  seguidores, likes recibidos, mapeo de notificaciones, id de `follows`, Retrofit sin likes).
- **Emulador de Firebase** (`tools/emulator-tests`, proyecto `demo-reviewlab`, sin credenciales):
  36 verificaciones de reglas y de las 4 funciones, por ejemplo «no puedo quitar el like de otro»,
  «no puedo seguir a nombre de otro», «el like genera la notificación del autor», «quitar y volver a
  dar like no la duplica», «la copia falsificada del autor se corrige», «cambiar mi nombre actualiza
  mis reseñas».

  ```bash
  cd tools/emulator-tests && npm install && npm test
  ```

**No ejecutado** (requiere dispositivo/emulador Android y el proyecto real con Blaze):
- Recibir el push real en el teléfono (el emulador de Functions no entrega mensajes FCM; el envío
  se probó hasta «sin dispositivos registrados»).
- Diálogo del permiso en Android 13+, notificación en primer plano y navegación de la UI.

Prueba manual sugerida: iniciar sesión con la cuenta A en un dispositivo y la B en otro (o
emulador). Copiar el token de Logcat (`FCM_TOKEN`) y enviar un mensaje de prueba desde Firebase
Console → Messaging. Luego, con B: seguir a A y dar like a una reseña de A → A recibe dos push y las
ve en la campana; el contador y el corazón cambian en ambos dispositivos sin recargar.

## Limitaciones

- Con Retrofit no hay likes, seguidores ni notificaciones (el backend Express no los tiene): las
  listas quedan vacías y las acciones muestran «no disponible con el backend actual». El «tiempo
  real» con Retrofit es una consulta cada 15 s (`ApiConfig.POLL_INTERVAL_MS`).
- Dos toques muy rápidos al corazón antes de que llegue el cambio pueden dar «sin permiso» en el
  segundo (las reglas rechazan agregar dos veces el mismo uid); el estado final queda correcto.
- Los listeners siguen activos mientras la pantalla esté en la pila de navegación.
- `username` sigue sin ser único (pendiente del Sprint 10).
