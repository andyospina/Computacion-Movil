# Contexto del proyecto — ReviewLab

Resumen de todos los avances, pensado para retomar el trabajo (o dárselo a otra persona/IA) sin releer
el repositorio. Estado a la rama `sprint-12-emulator-faker` (commit `01e7ff0`).

## 1. Qué es

**ReviewLab**: app Android nativa (Kotlin 2.2, Jetpack Compose + Material 3) para descubrir productos y
leer/escribir reseñas. Proyecto académico de Desarrollo Móvil (carpeta `Computacion-Movil`; el backend
Express/PostgreSQL de apoyo vive en `../Back-Movil`).

- Paquete/applicationId: `com.example.myapplication` · minSdk 24 · compile/targetSdk 37.
- Proyecto Firebase real: `reviewlab-b60be` (Firestore en `nam5`, plan **Blaze**).
- Arquitectura: **MVVM + StateFlow**, Hilt para inyección, navegación centralizada
  (`navigation/Routes.kt`, `AppNavigation.kt`), un único `Scaffold`.
- Capas de datos: `ViewModel → Repository → RemoteDataSource (interfaz) → implementación`
  (Firestore actual; Retrofit conservado como alternativa). Los Repository devuelven
  `Result<T>` (`Success`/`Failure(exception, messageRes)`) vía `runCatchingApi`/`asResult`.

## 2. Historia por sprint

| Rama / commit | Entregado |
|---|---|
| Sprint 2–5 | 10–11 pantallas Compose, navegación centralizada, MVVM con `StateFlow`, pantalla Register, documentación (README, diagramas, requisitos) |
| Sprint 6 (`619fcc1`) | Login y registro con Firebase Authentication, corrutinas |
| Sprint 7 (`f47aac6`) | Foto de perfil: Coil, Photo Picker, Firebase Storage |
| Sprint 9 (`bb153f9`) | Consumo del backend Express con Retrofit (productos, reseñas, usuarios) — `docs/sprint-9-retrofit.md` |
| Sprint 10 (`8f6622b`) | Firestore como backend: usuarios, productos, reseñas; reglas de seguridad — `docs/sprint-firestore.md` |
| Sprint 11 (`2aef52b`) | Likes, seguidores, feed «Siguiendo», tiempo real (snapshot listeners), notificaciones FCM + Cloud Functions — `docs/sprint-11-social-fcm.md` |
| **Sprint 12** (`01e7ff0`) | Emulator Suite, Faker, pruebas de integración y productos guardados — `docs/sprint-12-emulator-faker-tests.md` |

Ramas locales: `main`, `sprint-6`, `sprint-7`, `sprint-9`, `sprint-10-firestore`,
`sprint-11-social-fcm` (en `2aef52b`, intacta), `sprint-12-emulator-faker` (actual, **sin push**).

## 3. Modelo de datos (Firestore)

```
users/{uid}                       username, name, email, country, bio, profileImage, createdAt
  ├─ fcmTokens/{token}            token, platform, updatedAt        (privado)
  └─ notifications/{id}           type, actorId, reviewId, read, …  (la escriben las Functions)
products/{id}                     name, category                    (solo lectura desde la app)
reviews/{id}                      userId, productId, productName, rating, title, body, recommended,
                                  user{name,username,profileImage}, likedBy[], createdAt
follows/{followerId_followedId}   followerId, followedId, createdAt
saved/{userId_productId}          userId, productId, createdAt      (privado; Sprint 12)
```

Reglas en `firestore.rules` (probadas en `tools/emulator-tests`). Funciones en `functions/index.js`
(`onReviewLiked`, `onReviewCreated`, `onUserProfileUpdated`, follow → notificación).

## 4. Sprint 12 en detalle

**Entorno debug ↔ producción.** `data/di/FirebaseEnvironment.kt` es el único punto que decide el
entorno: con `BuildConfig.DEBUG && BuildConfig.USE_FIREBASE_EMULATOR` conecta Firestore (8080), Auth
(9099) y Storage (9199) a `10.0.2.2`; en release nunca. Debug contra Firebase real:
`-PuseEmulator=false`. El flag se define en `app/build.gradle.kts`.

**Emulador.** `firebase.emulator.json` + `firestore.emulator.rules` / `storage.emulator.rules`
(reglas abiertas **solo para el emulador**, nunca desplegar). `firebase.json` y `firestore.rules` son los de
producción.

**Faker.**
- Kotlin (solo debug, `src/debug/.../debugtools`): `Seeder` (usuarios, productos, reseñas con likes y
  copia del autor, seguidores, guardados; UUID; cantidades configurables; semilla opcional),
  `SeedLoader` (escribe en lotes, informa por colección, guarda de producción: exige repetir el id del
  proyecto), `SeedClients` (una `FirebaseApp` por destino) y `SeedActivity` (ícono «Seed (debug)»).
- Node: `tools/seed-firestore/seed-faker.js` (Admin SDK; `--target=emulator|production`,
  `--project`, `--confirm=reviewlab-b60be`). Para Firebase real hace falta clave de servicio.
  Limitación: cargar producción desde la app pasa por las reglas (rechaza `products` y usuarios ajenos).

**Pruebas** (`app/src/androidTest/...`, contra el emulador; base `support/FirestoreEmulatorTest.kt`
con `@Before`/`@After` que vacían las colecciones y cliente propio sin tocar producción):

| Clase | # |
|---|---|
| `ReviewFirestoreDataSourceTest` | 12 |
| `UserProductFollowDataSourceTest` | 6 |
| `SavedFirestoreDataSourceTest` | 5 |
| `RepositoryIntegrationTest` | 11 |
| `SavedRepositoryIntegrationTest` | 6 |
| `SeederTest` (JVM) | 6 |
| Reglas `saved` (`tools/emulator-tests`) | 9 |

Resultado: 40 de integración + 29 unitarias pasando; 6 pruebas de Cloud Functions de
`tools/emulator-tests` fallan en local («function killed»), preexistentes y no analizadas.

**Funcionalidad adicional: productos guardados.** Botón Guardar/Guardado en la ficha del producto,
pantalla `Saved` (Perfil → Productos guardados), `SavedRepository` (lista en vivo, orden por último
guardado, omite productos borrados), reglas privadas. Verificada a mano en la app. Extra menor: orden
«Más gustadas» en la lista de reseñas.

## 5. Cómo ejecutar

```bash
# Emulador de Firebase (otra terminal)
firebase emulators:start --config firebase.emulator.json --only firestore,auth
# UI del emulador: http://localhost:4000

# Pruebas
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest      # necesita Android Emulator + emulador de Firebase
cd tools/emulator-tests && npm test       # reglas y funciones

# Datos Faker
cd tools/seed-firestore && npm install
node seed-faker.js --target=emulator --project=reviewlab-b60be --users=10 --products=6
```

## 6. Gotchas importantes

- **Android 17 + targetSdk 37**: hablar con `10.0.2.2` exige el permiso `ACCESS_LOCAL_NETWORK`
  (declarado en `src/debug`). Sin concederlo, Firestore queda «offline» sin error claro. Las pruebas lo
  conceden solas; a mano: `adb shell pm grant com.example.myapplication android.permission.ACCESS_LOCAL_NETWORK`.
- Teléfono físico: cambiar `FirebaseEnvironment.EMULATOR_HOST` por la IP del PC.
- Con el emulador, Auth está vacío: hay que registrar usuarios nuevos.
- Los métodos `@Test` deben devolver `Unit` (por eso `test { }` en la base devuelve `Unit`).
- Plan de pruebas con `connectedDebugAndroidTest` cuelga (sin error) si falta el permiso de red local.
- Windows + OneDrive: el repo está bajo OneDrive; puede haber avisos CRLF de Git (inofensivos).
- `.idea/misc.xml` y `.idea/vcs.xml` aparecen modificados y no se commitean.

## 7. Pendiente / siguiente

1. **Publicar `firestore.rules`** en Firebase real (la consola aún tiene la versión del 22 oct, sin
   `saved`): pegar en Consola → Firestore → Reglas, o `firebase deploy --only firestore:rules`.
2. Desplegar Cloud Functions (`firebase deploy --only functions`; Blaze ya activo). Revisar las 6
   pruebas de Functions que fallan en local.
3. Cargar datos Faker en Firebase real con `seed-faker.js --target=production` (requiere clave de
   servicio fuera del repo) y tomar la evidencia.
4. Capturas de evidencia: panel del emulador (Auth + Firestore), reporte HTML de pruebas
   (`app/build/reports/androidTests/connected/debug/index.html`), pantalla de guardados.
5. Presupuesto de facturación en Firebase (Blaze sin presupuesto configurado).
6. Hacer push de `sprint-12-emulator-faker` (`git push -u origin sprint-12-emulator-faker`) y abrir PR.

## 8. Mapa de archivos clave

| Tema | Archivos |
|---|---|
| Entorno | `data/di/FirebaseEnvironment.kt`, `FirestoreModule.kt`, `AppModule.kt`, `StorageModule.kt`, `RemoteDataSourceModule.kt` |
| Datos | `data/datasource/*RemoteDataSource.kt`, `data/datasource/impl/*Firestore*.kt`, `data/dto/Firestore*Dto.kt`, `data/repository/*Repository.kt` |
| UI nueva | `ui/screens/Saved/*`, `ProductDetail` (botón Guardar), `Profile` (acceso a guardados) |
| Faker/seed | `app/src/debug/.../debugtools/*`, `tools/seed-firestore/seed-faker.js` |
| Pruebas | `app/src/androidTest/...`, `app/src/test/.../SeederTest.kt`, `tools/emulator-tests/test.js` |
| Config Firebase | `firebase.json`, `firebase.emulator.json`, `firestore.rules`, `firestore.emulator.rules`, `storage.emulator.rules`, `.firebaserc` |
| Docs | `README.md`, `docs/*.md` (este archivo, `sprint-*.md`, diagramas, requisitos) |
