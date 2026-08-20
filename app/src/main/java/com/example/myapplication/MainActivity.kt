package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.EstadoResena
import com.example.myapplication.model.Perfil
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.AvatarIniciales
import com.example.myapplication.ui.components.BarraInferior
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.IconoInicial
import com.example.myapplication.ui.components.ItemBarraInferior
import com.example.myapplication.ui.screens.Buscar.BuscarScreen
import com.example.myapplication.ui.screens.Home.HomeScreen
import com.example.myapplication.ui.screens.HomeFeed.HomeFeedScreen
import com.example.myapplication.ui.screens.Login.LoginScreen
import com.example.myapplication.ui.screens.NewReview.NewReviewScreen
import com.example.myapplication.ui.screens.Notificaciones.NotificacionesScreen
import com.example.myapplication.ui.screens.Perfil.PerfilScreen
import com.example.myapplication.ui.screens.Producto.ProductoScreen
import com.example.myapplication.ui.screens.PublishedReview.PublishedReviewScreen
import com.example.myapplication.ui.screens.Reviews.ReviewsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val esOscuroPorDefecto = isSystemInDarkTheme()
            var modoOscuro by remember { mutableStateOf(esOscuroPorDefecto) }

            MyApplicationTheme(darkTheme = modoOscuro) {
                ReviewLabApp(
                    modoOscuro = modoOscuro,
                    onModoOscuroChange = { modoOscuro = it }
                )
            }
        }
    }
}

private val ITEMS_BARRA_INFERIOR = listOf(
    ItemBarraInferior(Rutas.HOME, "Inicio", "🏠"),
    ItemBarraInferior(Rutas.BUSCAR, "Buscar", "🔍"),
    ItemBarraInferior(Rutas.NUEVA_RESENA, "Reseñar", "✍️"),
    ItemBarraInferior(Rutas.PERFIL, "Perfil", "👤")
)

/** Rutas que muestran la barra inferior (pestañas raíz de la app autenticada). */
private val RUTAS_CON_BARRA_INFERIOR = setOf(Rutas.HOME, Rutas.BUSCAR, Rutas.PERFIL)

/** Rutas con cabecera propia (oscura) — no usan el BarraSuperior compartido. */
private val RUTAS_SIN_TOPBAR = setOf(Rutas.LOGIN, Rutas.PERFIL)

/**
 * Punto único de composición de la app: declara el único Scaffold
 * (topBar + bottomBar + NavHost) que existe en toda la aplicación. Todo el
 * estado que debe sobrevivir a la navegación (autenticación, perfil,
 * reseñas, notificaciones, modo oscuro) se hospeda (state hoisting) aquí y
 * baja como parámetros a cada pantalla.
 */
@Composable
fun ReviewLabApp(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val productos = DatosLocales.productos

    var estaAutenticado by remember { mutableStateOf(false) }
    var perfil by remember { mutableStateOf(DatosLocales.perfilUsuario) }
    var productoSeleccionado by remember { mutableStateOf(DatosLocales.productoDestacado) }
    var ultimaResenaPublicada by remember { mutableStateOf(DatosLocales.resenas.first()) }

    val todasLasResenas = remember {
        val idsPublicos = DatosLocales.resenas.map { it.id }.toSet()
        val extra = DatosLocales.misResenas.filter { it.id !in idsPublicos }
        mutableStateListOf(*(DatosLocales.resenas + extra).toTypedArray())
    }
    val misResenasIds = remember {
        mutableStateListOf(*DatosLocales.misResenas.map { it.id }.toTypedArray())
    }
    val notificaciones = remember {
        mutableStateListOf(*DatosLocales.notificaciones.toTypedArray())
    }

    val misResenas = todasLasResenas.filter { it.id in misResenasIds }

    fun irAProducto(producto: Producto) {
        productoSeleccionado = producto
        navController.navigate(Rutas.PRODUCTO)
    }

    fun exigirLogin(destinoTrasLogin: () -> Unit) {
        if (estaAutenticado) {
            destinoTrasLogin()
        } else {
            navController.navigate(Rutas.LOGIN)
        }
    }

    Scaffold(
        topBar = {
            if (rutaActual !in RUTAS_SIN_TOPBAR) {
                BarraSuperiorPorRuta(
                    rutaActual = rutaActual,
                    perfil = perfil,
                    onVolverClick = { navController.popBackStack() },
                    onLoginClick = { navController.navigate(Rutas.LOGIN) },
                    onNotificacionesClick = { navController.navigate(Rutas.NOTIFICACIONES) },
                    onCerrarConfirmacion = {
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.HOME) { inclusive = true }
                        }
                    },
                    onMarcarLeidas = {
                        for (i in notificaciones.indices) {
                            notificaciones[i] = notificaciones[i].copy(leida = true)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (rutaActual in RUTAS_CON_BARRA_INFERIOR) {
                BarraInferior(
                    items = ITEMS_BARRA_INFERIOR,
                    rutaActual = rutaActual,
                    onItemClick = { ruta ->
                        if (ruta == Rutas.NUEVA_RESENA) {
                            exigirLogin {
                                productoSeleccionado = DatosLocales.productoDestacado
                                navController.navigate(Rutas.NUEVA_RESENA)
                            }
                        } else {
                            navController.navigate(ruta) {
                                popUpTo(Rutas.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Rutas.HOME_INVITADO,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Rutas.HOME_INVITADO) {
                HomeScreen(
                    onEscribirResena = {
                        exigirLogin {
                            navController.navigate(Rutas.HOME) {
                                popUpTo(Rutas.HOME_INVITADO) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Rutas.LOGIN) {
                LoginScreen(
                    onIniciarSesion = {
                        estaAutenticado = true
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.HOME_INVITADO) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.HOME) {
                HomeFeedScreen(
                    productos = productos,
                    onBuscarClick = { navController.navigate(Rutas.BUSCAR) },
                    onProductoClick = ::irAProducto
                )
            }

            composable(Rutas.PRODUCTO) {
                val resenasDelProducto = todasLasResenas.filter {
                    it.productoId == productoSeleccionado.id && it.estado == EstadoResena.PUBLICADA
                }
                ProductoScreen(
                    producto = productoSeleccionado,
                    resenasDestacadas = resenasDelProducto,
                    onEscribirResena = { navController.navigate(Rutas.NUEVA_RESENA) },
                    onVerTodas = { navController.navigate(Rutas.REVIEWS) }
                )
            }

            composable(Rutas.REVIEWS) {
                val resenasDelProducto = todasLasResenas.filter {
                    it.productoId == productoSeleccionado.id && it.estado == EstadoResena.PUBLICADA
                }
                ReviewsScreen(
                    producto = productoSeleccionado,
                    resenas = resenasDelProducto
                )
            }

            composable(Rutas.NUEVA_RESENA) {
                NewReviewScreen(
                    producto = productoSeleccionado,
                    onPublicar = { puntuacion, titulo, resena, usuario, etiquetas ->
                        val nuevoId = (todasLasResenas.maxOfOrNull { it.id } ?: 0) + 1
                        val autor = usuario.ifBlank { perfil.nombre }

                        val nuevaResena = Resena(
                            id = nuevoId,
                            productoId = productoSeleccionado.id,
                            autor = autor,
                            compradorVerificado = true,
                            puntuacion = puntuacion,
                            titulo = titulo.ifBlank { "Sin título" },
                            comentario = resena,
                            fecha = "Hoy",
                            votosUtiles = 0,
                            recomendado = puntuacion >= 4
                        )

                        todasLasResenas.add(nuevaResena)
                        misResenasIds.add(nuevoId)
                        ultimaResenaPublicada = nuevaResena

                        navController.navigate(Rutas.CONFIRMACION) {
                            popUpTo(Rutas.NUEVA_RESENA) { inclusive = true }
                        }
                    },
                    onCancelar = { navController.popBackStack() }
                )
            }

            composable(Rutas.CONFIRMACION) {
                PublishedReviewScreen(
                    resena = ultimaResenaPublicada,
                    producto = productoSeleccionado,
                    onVerMiResena = {
                        navController.navigate(Rutas.PERFIL) {
                            popUpTo(Rutas.HOME)
                        }
                    },
                    onVolver = {
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.BUSCAR) {
                BuscarScreen(
                    productos = productos,
                    busquedasRecientes = DatosLocales.busquedasRecientes,
                    onProductoClick = ::irAProducto
                )
            }

            composable(Rutas.PERFIL) {
                PerfilScreen(
                    perfil = perfil,
                    misResenas = misResenas,
                    productos = productos,
                    modoOscuro = modoOscuro,
                    onModoOscuroChange = onModoOscuroChange
                )
            }

            composable(Rutas.NOTIFICACIONES) {
                NotificacionesScreen(notificaciones = notificaciones)
            }
        }
    }
}

@Composable
private fun BarraSuperiorPorRuta(
    rutaActual: String?,
    perfil: Perfil,
    onVolverClick: () -> Unit,
    onLoginClick: () -> Unit,
    onNotificacionesClick: () -> Unit,
    onCerrarConfirmacion: () -> Unit,
    onMarcarLeidas: () -> Unit
) {
    when (rutaActual) {
        Rutas.HOME_INVITADO, Rutas.NUEVA_RESENA -> BarraSuperior(
            titulo = "ReviewLab",
            iconoInicial = IconoInicial.HAMBURGUESA
        ) {
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }

        Rutas.HOME -> BarraSuperior(
            titulo = "ReviewLab",
            iconoInicial = IconoInicial.HAMBURGUESA
        ) {
            Text(
                text = "🔔",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onNotificacionesClick() }
            )
            AvatarIniciales(iniciales = perfil.iniciales)
        }

        Rutas.PRODUCTO -> BarraSuperior(
            titulo = "Producto",
            iconoInicial = IconoInicial.VOLVER,
            onVolverClick = onVolverClick
        ) {
            Text("⋮", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Rutas.REVIEWS -> BarraSuperior(
            titulo = "ReviewLab",
            iconoInicial = IconoInicial.VOLVER,
            onVolverClick = onVolverClick
        ) {
            Text("⋮", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Rutas.CONFIRMACION -> BarraSuperior(
            titulo = "ReviewLab",
            iconoInicial = IconoInicial.NINGUNO
        ) {
            Text(
                text = "✕",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable { onCerrarConfirmacion() }
            )
        }

        Rutas.BUSCAR -> BarraSuperior(
            titulo = "ReviewLab",
            iconoInicial = IconoInicial.VOLVER,
            onVolverClick = onVolverClick
        ) {
            Text("🔍", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Rutas.NOTIFICACIONES -> BarraSuperior(
            titulo = "ReviewLab",
            iconoInicial = IconoInicial.VOLVER,
            onVolverClick = onVolverClick
        ) {
            Text(
                text = "Marcar leídas",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable { onMarcarLeidas() }
            )
        }

        else -> BarraSuperior(titulo = "ReviewLab")
    }
}
