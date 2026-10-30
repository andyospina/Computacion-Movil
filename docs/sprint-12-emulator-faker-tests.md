# Sprint 12 — Firebase Emulator Suite, datos con Faker y pruebas de integración

## Qué se agregó y dónde

| Requisito | Implementación |
|---|---|
| Emulador + Firebase CLI | `firebase.emulator.json` (Firestore 8080, Auth 9099, Storage 9199, UI 4000) con reglas abiertas **solo para el emulador** (`firestore.emulator.rules`, `storage.emulator.rules`). `firebase.json` sigue siendo el de producción y también define Auth 9099 para `tools/emulator-tests` |
| Detección de entorno | `FirebaseEnvironment` (`data/di`): si `BuildConfig.DEBUG && BuildConfig.USE_FIREBASE_EMULATOR` conecta Firestore, Auth y Storage a `10.0.2.2`; en release nunca. Los módulos de Hilt piden los clientes ahí, así que ningún DataSource decide el entorno. Debug contra Firebase real: `-PuseEmulator=false` |
| Seeder con Faker | `Seeder` (`src/debug/.../debugtools`): usuarios, productos, reseñas (con `likedBy` y copia del autor) y seguidores con UUID, referencias válidas, cantidades configurables y semilla opcional. Solo existe en debug (JavaFaker es `debugImplementation`) |
| Carga y selección de destino | `SeedLoader` + `SeedClients`: cada destino usa su propia `FirebaseApp` (emulador / producción), y producción exige repetir el id del proyecto. Pantalla `SeedActivity` (ícono «Seed (debug)») muestra insertados por colección y errores. Para Firebase real con Admin SDK: `tools/seed-firestore/seed-faker.js` |
| Pruebas DataSource (≥ 8) | `ReviewFirestoreDataSourceTest` (12) y `UserProductFollowDataSourceTest` (6) |
| Pruebas Repository (≥ 8) | `RepositoryIntegrationTest` (11) |
| Before / After | `FirestoreEmulatorTest`: `@Before` conecta y vacía las colecciones, `@After` vuelve a activar la red y vuelve a vaciar |
| Funcionalidad adicional | **Productos guardados (favoritos)**, de punta a punta: colección `saved/{uid_producto}` con reglas privadas, `SavedRemoteDataSource` + implementación Firestore, `SavedRepository` (listado en vivo que cruza ids con productos, orden por último guardado, omite productos borrados), botón «Guardar/Guardado» en la ficha del producto y pantalla «Productos guardados» (Perfil → Productos guardados). Además, orden «Más gustadas» en la lista de reseñas |

## Cómo ejecutarlo

```bash
firebase emulators:start --config firebase.emulator.json --only firestore,auth
```

Con un Android Emulator encendido (en otra terminal):

```bash
./gradlew connectedDebugAndroidTest
./gradlew testDebugUnitTest
```

Datos de prueba:

```bash
# En la app: abrir «Seed (debug)», elegir destino y pulsar «Generar y cargar»
cd tools/seed-firestore && npm install
node seed-faker.js --target=emulator --users=10 --products=6 --reviews=3
node seed-faker.js --target=production --confirm=reviewlab-b60be   # necesita GOOGLE_APPLICATION_CREDENTIALS
```

## Cosas que conviene saber

- **Android 17 / targetSdk 37**: hablar con `10.0.2.2` cuenta como red local y exige el permiso
  `ACCESS_LOCAL_NETWORK` (declarado solo en `src/debug`). Sin él, Firestore queda «offline» sin error
  claro. Las pruebas con Gradle lo reciben al instalarse; al ejecutar la app a mano desde el IDE:
  `adb shell pm grant com.example.myapplication android.permission.ACCESS_LOCAL_NETWORK`.
- En un teléfono físico `10.0.2.2` no existe: cambiar `FirebaseEnvironment.EMULATOR_HOST` por la IP del PC.
- En el emulador de Auth hay que registrar usuarios nuevos; los de producción no existen allí.
- Las reglas reales (`firestore.rules`) no se aplican en el emulador con `firebase.emulator.json`; se siguen
  probando aparte con `tools/emulator-tests`.
- Cargar en producción desde la app (`SeedActivity`) pasa por las reglas de seguridad: `products` y
  los `users` ajenos serán rechazados. Para sembrar producción de verdad se usa el script con Admin SDK.
  Los documentos de Faker llevan el prefijo `faker_` solo en el script de Node; los de la app usan UUID.

## Productos guardados: cómo funciona

```
Ficha del producto → toggleGuardado → SavedRepository.setSaved → saved/{uid}_{productId}  (reglas: solo el dueño)
                                   │
   listener (observeIsSaved) ←─────┘  el botón cambia a «Guardado» cuando el servidor lo confirma
   listener (observeSavedProducts) → ids ordenados por createdAt → getProducts() → Product[] en ese orden
```

Decisiones: documento plano con id `uid_producto` (igual que `follows`: no duplica y borrar no requiere
consulta); orden en el cliente para no exigir un índice compuesto; los guardados son **privados**
(las reglas impiden leerlos a otros usuarios); si un producto guardado se borra, la lista lo omite.

## Resultados

Android Emulator Pixel_6 (API 37) + Firebase Emulator Suite 15.34, `./gradlew connectedDebugAndroidTest`:

| Clase | Pruebas | Resultado |
|---|---|---|
| `ReviewFirestoreDataSourceTest` | 12 | todas pasan |
| `UserProductFollowDataSourceTest` | 6 | todas pasan |
| `SavedFirestoreDataSourceTest` | 5 | todas pasan |
| `RepositoryIntegrationTest` | 11 | todas pasan |
| `SavedRepositoryIntegrationTest` | 6 | todas pasan |
| `SeederTest` (JVM, `testDebugUnitTest`) | 6 | todas pasan |
| Reglas de `saved` (`tools/emulator-tests`, `npm test`) | 9 | todas pasan |

Total de integración: 23 de DataSource y 17 de Repository. Verificación manual en la app (debug contra los
emuladores): registro en el Auth local, productos de Faker en Inicio, «Guardar» → «Guardado», Perfil →
Productos guardados, el guardado sobrevive a reiniciar la app y «Quitar» vacía la lista.

Nota: en `tools/emulator-tests` fallan 6 pruebas de Cloud Functions (notificaciones y sincronización del
autor) con «Your function was killed because it raised an unhandled error». No tocan este sprint
(`functions/` no cambió) y se ejecutan en el emulador de funciones local; conviene revisarlas aparte.

Mapeo con los casos propuestos — DataSource: inserción (`createReview_stores…`), consulta por id
(`getReviewById_returns…`), actualización, eliminación, consulta múltiple (`getReviewsByProduct…`),
inexistente (`…UnknownId_throwsNotFound`), campos inválidos (`…IncompleteDocument…WrongTypes`) y
concurrencia (`concurrentLikes…`, `concurrentCreates…`). Repository: sincronización con campos copiados
(`createReview_copiesAuthorAndProduct…`), pérdida de conexión (`offline_…`), mapeo (`getReviewById_maps…`),
inserciones inválidas (`createReview_withUnknownAuthorOrProduct…`), filtrado (`getReviewsByUser_filters…`),
observación en tiempo real (`toggleLike_isReflected…OnTheLiveFlow`, `observeSavedProducts_reflects…`) y
propagación de excepciones (`dataSourceErrors_areMappedToResultFailures`). No hay caché en memoria en los
Repository, así que la «actualización de caché» se cubre como consistencia tras una mutación.

## Qué hay que hacer en Firebase (una sola vez)

1. **Desplegar las reglas** (cambió `firestore.rules`: colección `saved`): `firebase deploy --only firestore:rules`.
   Sin esto, «Guardar» falla con permiso denegado en producción. Nunca desplegar `*.emulator.rules`.
2. **Cloud Functions** (likes/seguidores del Sprint 11): requieren plan Blaze. `firebase deploy --only functions`.
3. **Authentication → Sign-in method**: correo/contraseña habilitado.
4. **Firestore**: base creada en modo producción (las reglas de arriba son las que mandan).
5. **Datos de prueba en producción** (opcional): Consola → Configuración del proyecto → Cuentas de servicio →
   «Generar nueva clave privada»; guardar el JSON **fuera del repo** y ejecutar
   `GOOGLE_APPLICATION_CREDENTIALS=<ruta> node seed-faker.js --target=production --confirm=reviewlab-b60be`.
   Borrar la clave al terminar. Los documentos llevan el prefijo `faker_`.
6. **Productos**: la app no puede escribirlos (reglas), deben existir en `products` para poder guardarlos.
