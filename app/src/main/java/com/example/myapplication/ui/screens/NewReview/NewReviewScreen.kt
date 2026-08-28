package com.example.myapplication.ui.screens.NewReview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.ui.screens.NewReview.componentes.BotonesNuevaResena
import com.example.myapplication.ui.screens.NewReview.componentes.CampoCorreo
import com.example.myapplication.ui.screens.NewReview.componentes.CampoEtiquetas
import com.example.myapplication.ui.screens.NewReview.componentes.CampoPuntuacion
import com.example.myapplication.ui.screens.NewReview.componentes.CampoResena
import com.example.myapplication.ui.screens.NewReview.componentes.CampoTitulo
import com.example.myapplication.ui.screens.NewReview.componentes.CampoUsuario
import com.example.myapplication.ui.screens.NewReview.componentes.FotosAdjuntas
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun NewReviewScreen(
    modifier: Modifier = Modifier,
    producto: Producto,
    onPublicar: (
        puntuacion: Int,
        titulo: String,
        resena: String,
        usuario: String,
        etiquetas: Set<String>
    ) -> Unit,
    onCancelar: () -> Unit
) {
    var puntuacion by remember { mutableIntStateOf(4) }
    var titulo by remember { mutableStateOf("") }
    var resena by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var etiquetasSeleccionadas by remember { mutableStateOf(emptySet<String>()) }

    val puedePublicar = puntuacion > 0 && usuario.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BotonesNuevaResena(
            puedePublicar = puedePublicar,
            onPublicar = { onPublicar(puntuacion, titulo, resena, usuario, etiquetasSeleccionadas) },
            onCancelar = onCancelar
        )

        CampoPuntuacion(
            puntuacion = puntuacion,
            onPuntuacionChange = { puntuacion = it }
        )

        CampoTitulo(titulo = titulo, onTituloChange = { titulo = it })

        CampoResena(resena = resena, onResenaChange = { resena = it })

        FotosAdjuntas()

        CampoEtiquetas(
            etiquetasSeleccionadas = etiquetasSeleccionadas,
            onEtiquetaToggle = { etiqueta ->
                etiquetasSeleccionadas = if (etiqueta in etiquetasSeleccionadas) {
                    etiquetasSeleccionadas - etiqueta
                } else {
                    etiquetasSeleccionadas + etiqueta
                }
            }
        )

        CampoUsuario(usuario = usuario, onUsuarioChange = { usuario = it })

        CampoCorreo(correo = correo, onCorreoChange = { correo = it })
    }
}

@Preview(showBackground = true, name = "NewReviewScreen - Preview")
@Composable
fun NewReviewScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        NewReviewScreen(
            producto = DatosLocales.productoDestacado,
            onPublicar = { _, _, _, _, _ -> },
            onCancelar = {}
        )
    }
}
