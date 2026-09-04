# ReviewLab

**ReviewLab** es una aplicación Android nativa (Jetpack Compose) para
descubrir productos y leer/escribir reseñas de compradores reales. Proyecto
académico de Desarrollo Móvil — Sprint 5: arquitectura **MVVM + StateFlow**.

## Descripción

ReviewLab permite a un usuario:

- Iniciar sesión o registrarse.
- Explorar productos en tendencia y filtrarlos por categoría.
- Buscar productos por nombre.
- Ver el detalle de un producto, sus reseñas destacadas y todas sus reseñas
  (con filtros de orden y "con fotos").
- Escribir una nueva reseña con puntuación por estrellas.
- Ver la confirmación de una reseña publicada.
- Revisar notificaciones y marcarlas como leídas.
- Ver su perfil, estadísticas y sus propias reseñas, y activar el modo oscuro.

## Pantallas (11)

| # | Pantalla | Ruta | MVVM |
|---|----------|------|------|
| 1 | Login | `login` | `LoginViewModel` / `LoginState` |
| 2 | Register | `register` | `RegisterViewModel` / `RegisterState` |
| 3 | Home | `home` | `HomeViewModel` / `HomeState` |
| 4 | Search | `search` | `SearchViewModel` / `SearchState` |
| 5 | Write Review Hub | `write_review_hub` | — (sin estado propio) |
| 6 | Product Detail | `product_detail/{productId}` | — (sin estado propio) |
| 7 | Reviews List | `reviews_list/{productId}` | `ReviewsListViewModel` / `ReviewsListState` |
| 8 | New Review | `new_review?productId={productId}` | `NewReviewViewModel` / `NewReviewState` |
| 9 | Review Published | `review_published/{productId}/{rating}` | — (sin estado propio) |
| 10 | Notifications | `notifications` | `NotificationsViewModel` / `NotificationsState` |
| 11 | Profile | `profile` | — (sin estado propio) |

## Arquitectura

MVVM con `MutableStateFlow`, siguiendo este flujo:

```
Usuario interactúa con la UI
        │
        ▼
   Screen (Composable)
        │  evento (onClick / onValueChange)
        ▼
   ViewModel
        │  valida reglas de negocio
        ▼
   Actualiza UiState (data class inmutable)
        │
        ▼
   MutableStateFlow emite el nuevo estado
        │
        ▼
   collectAsState() en la Screen
        │
        ▼
   Compose recompone la UI
```

Reglas seguidas en el proyecto:

- Cada pantalla con estado tiene su propio `ViewModel` y su propio `UiState`
  (`data class`), ubicados junto a la pantalla en `ui/screens/<Pantalla>/`.
- El estado se expone como `StateFlow` de solo lectura (`asStateFlow()`); la
  UI nunca lo modifica directamente, solo envía eventos al ViewModel.
- La navegación está centralizada en un único archivo,
  [`navigation/AppNavigation.kt`](app/src/main/java/com/example/myapplication/navigation/AppNavigation.kt),
  con las rutas declaradas en [`navigation/Routes.kt`](app/src/main/java/com/example/myapplication/navigation/Routes.kt).
- Existe un único `Scaffold` para toda la aplicación (en `AppNavigation`),
  que aloja la barra inferior de navegación; cada pantalla arma su propia
  barra superior (`BarraSuperior`) dentro de un `Column`.
- Los componentes pequeños (`ui/components/` y `<Pantalla>/componentes/`)
  reciben su estado y callbacks por parámetro (*state hosting*), no guardan
  estado propio.

Ver también:
- [Diagrama de clases](docs/diagrama-clases.md)
- [Diagrama entidad-relación](docs/diagrama-entidad-relacion.md)

## Estructura del proyecto

```
app/src/main/java/com/example/myapplication/
├── data/                     # Modelos y providers locales (Product, Review, User, ...)
├── navigation/                # Routes.kt, AppNavigation.kt (navegación centralizada)
├── ui/
│   ├── components/            # Componentes reutilizables entre pantallas
│   ├── theme/                 # Color.kt, Theme.kt, Type.kt
│   └── screens/
│       └── <Pantalla>/
│           ├── <Pantalla>Screen.kt
│           ├── <Pantalla>ViewModel.kt   # solo si la pantalla tiene estado
│           ├── <Pantalla>State.kt       # solo si la pantalla tiene estado
│           └── componentes/             # subcomponentes propios de la pantalla
└── MainActivity.kt
```

## Paleta de colores

Definida en [`ui/theme/Color.kt`](app/src/main/java/com/example/myapplication/ui/theme/Color.kt):

| Nombre | Hex | Uso |
|---|---|---|
| Electric Lime | `#D8FF3E` | Color primario / acento (barra superior, botones destacados) |
| Deep Lime | `#7A8F00` | Acento secundario (enlaces, textos destacados) |
| Ink | `#101012` | Fondo oscuro / texto principal |
| Paper | `#F4F4F2` | Fondo claro |
| Gray Secondary | `#8A8A90` | Texto secundario |
| Border Color | `#C9C9C6` | Bordes y separadores |

La app soporta modo oscuro (`MyApplicationTheme(darkTheme = ...)` en
`MainActivity.kt`), activable desde la pantalla de Perfil.

## Stack tecnológico

- Kotlin 2.2
- Jetpack Compose (BOM `2026.02.01`) + Material 3
- Navigation Compose
- Arquitectura MVVM: `androidx.lifecycle:lifecycle-viewmodel-compose` +
  `kotlinx.coroutines.flow.StateFlow`

## Cómo ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio (Ladybug o superior).
3. Sincronizar Gradle.
4. Ejecutar sobre un emulador o dispositivo con API 24+.

## Autor

Andy Ospina — Proyecto académico, curso de Desarrollo/Computación Móvil.
