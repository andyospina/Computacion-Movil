# Pendientes para hacer en un computador con el entorno completo

Lo que se hizo en el Sprint 16 se escribió **sin poder compilar la app Android** (el entorno no tenía Android SDK) y el
proyecto Flutter se probó solo con `flutter test`, no en un emulador. Esta lista es para cerrarlo bien.

## 0. Requisitos del computador
- Android Studio (Narwhal o más nuevo) con Android SDK 37, un emulador (Pixel, API 34+) y JDK 17+.
- Node 20+ y `firebase-tools` (para las pruebas de reglas).
- Flutter SDK reciente (3.47+ / Dart 3.14+; el proyecto usa `package:material_ui`) con las extensiones Flutter y Dart.
- `app/google-services.json` del proyecto `reviewlab-b60be` (no está en el repo si es privado).

## 1. Android (rama `sprint-15-comentarios`, commit `0aee56b`)
```bash
git pull origin sprint-15-comentarios
./gradlew testDebugUnitTest          # 24+ pruebas nuevas: ReviewShareTest, ReportRepositoryTest, ReviewDetailViewModelTest
./gradlew assembleDebug
```
1. **Compilar.** Si falla, los archivos más probables (se escribieron sin compilar):
   - `ui/screens/Profile/ProfileScreen.kt` (ahora es un solo `LazyColumn`; `when` con `item`/`items`)
   - `ui/screens/Home/HomeScreen.kt` y `ui/screens/Reviews/ReviewsListScreen.kt` (mismo patrón)
   - `ui/screens/ReviewDetail/*` (`ReviewDetailViewModel` ahora recibe `ReportRepository` y `CurrentUserIdProvider`)
   - `ui/share/ReviewShare.kt` y `ui/screens/Reviews/componentes/TarjetaResena.kt`
2. **Pruebas unitarias en verde** (`testDebugUnitTest`).
3. **Reglas de Firestore** (ya pasaron aquí, repetir si se tocan):
   ```bash
   cd tools/emulator-tests && npm install && npm test
   ```
4. **Pruebas instrumentadas** (con el emulador de Firebase levantado, ver `docs/sprint-12-emulator-faker-tests.md`):
   `./gradlew connectedDebugAndroidTest`. Revisar que ninguna dependa del Perfil/Inicio de antes (ahora son `LazyColumn`).
5. **Desplegar reglas** a Firebase real (necesario para que Reportar funcione fuera del emulador):
   ```bash
   firebase deploy --only firestore:rules --project reviewlab-b60be
   ```

## 2. Revisión manual de UI en el emulador (lo que se arregló y no se vio funcionando)
Probar cada punto en un teléfono **pequeño (320×480 / Nexus S)**, uno normal, **horizontal**, y con **Tamaño de fuente = Más grande**
(Ajustes ▸ Pantalla). Anotar cualquier desborde o texto cortado.

| Pantalla | Qué comprobar |
|---|---|
| Login | Se puede hacer scroll; con teclado abierto se ve el botón |
| Registro | Igual |
| Inicio | Con letra grande/horizontal se ve la lista de tendencias (scroll) |
| Perfil | «Mis reseñas» visible haciendo scroll; botones con texto legible (`DeepLime`) |
| Lista de reseñas | Encabezado, filtros y tarjetas en una sola lista; «Cargar más» funciona |
| Tarjeta de reseña | Autor largo («Valentina Rodríguez Castellanos»), fecha + Recomendado + Comentar + Compartir + Like sin salirse |
| Detalle de reseña | Scroll; botón «Reportar reseña» |
| Modo oscuro | Los botones con borde y el spinner siguen viéndose |
| Tema claro | Texto de OutlinedButton/TextButton ahora verde oscuro; confirmar que se ve bien (cambio de diseño: `primary = DeepLime`) |

## 3. Probar las dos funcionalidades nuevas
**Compartir reseña**
- Tocar el ícono de compartir en una tarjeta → aparece el selector de Android → enviar a Mensajes/WhatsApp/Gmail y ver el texto.
- Probar con reseña sin nombre de producto y con cuerpo > 280 caracteres (se recorta con «…»).

**Reportar reseña**
- Con la cuenta A, abrir una reseña de la cuenta B ▸ «Reportar reseña» ▸ elegir motivo ▸ Enviar → aviso de agradecimiento y botón «Ya reportaste esta reseña».
- Cerrar y reabrir la reseña: sigue en «Ya reportaste…».
- Abrir una reseña **propia**: no debe aparecer el botón.
- En la consola de Firebase ▸ Firestore ▸ `reports/{uidA_reviewId}` debe existir con `reason` y `createdAt`.
- Sin internet: debe mostrar el aviso de error y mantener el diálogo abierto.

## 4. Flutter (`flutter_reviewlab/`)
> Está en `Computacion-Movil/flutter_reviewlab/` con su propio git (1 commit) y **no** está subido a ningún remoto.
```bash
# 1) Crear en GitHub un repositorio vacío, p. ej. reviewlab-flutter, y luego:
cd flutter_reviewlab
git remote add origin https://github.com/<usuario>/reviewlab-flutter.git
git push -u origin main

# 2) Probar
flutter pub get
flutter analyze
flutter test
flutter run --dart-define=SCREEN=1     # repetir con 2..6 en el emulador
```
- Comparar cada pantalla con su captura de Android (guardar las 6 capturas reales en `flutter_reviewlab/referencias_android/`
  y completar la tabla Pantalla/Referencia/Estado del plan de entrega).
- Ajustar diferencias de tamaño/color/espaciado; las capturas de `screenshots/` se hicieron con la fuente Roboto de las pruebas,
  no con un teléfono real.
- Si el SDK es anterior a Flutter 3.47 y falla `package:material_ui`, cambiar los imports a `package:flutter/material.dart`
  y quitar `material_ui` de `pubspec.yaml`.
- Quitar `flutter_reviewlab/` de `.git/info/exclude` solo si se decide meterlo dentro del repo de Android (no recomendado:
  la entrega pide un repositorio independiente).

## 5. Entrega
- [ ] `./gradlew testDebugUnitTest` y `assembleDebug` en verde
- [ ] Reglas desplegadas y probadas con datos reales
- [ ] Revisión de UI en 3 tamaños + letra grande sin desbordes
- [ ] Repositorio Flutter independiente creado y subido, README con instrucciones
- [ ] Las 6 capturas de referencia guardadas y comparadas
- [ ] Rama Android subida (ya está: `sprint-15-comentarios`); abrir PR a `main` si se pide
