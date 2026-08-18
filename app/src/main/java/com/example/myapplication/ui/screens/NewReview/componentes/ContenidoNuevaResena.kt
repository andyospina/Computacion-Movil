package com.example.myapplication.ui.screens.NewReview.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ContenidoNuevaResena(
    modifier: Modifier = Modifier,
    puntuacion: Int,
    titulo: String,
    resena: String,
    usuario: String,
    correo: String,
    onPuntuacionChange: (Int) -> Unit,
    onTituloChange: (String) -> Unit,
    onResenaChange: (String) -> Unit,
    onUsuarioChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onPublicar: () -> Unit,
    onCancelar: () -> Unit
) {

    Column(
        modifier = modifier
    ) {

        BotonesNuevaResena(
            modifier = Modifier.fillMaxWidth(),
            onPublicar = onPublicar,
            onCancelar = onCancelar
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        CampoPuntuacion(
            modifier = Modifier.fillMaxWidth(),
            puntuacion = puntuacion,
            onPuntuacionChange = onPuntuacionChange
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        CampoTitulo(
            modifier = Modifier.fillMaxWidth(),
            titulo = titulo,
            onTituloChange = onTituloChange
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        CampoResena(
            modifier = Modifier.fillMaxWidth(),
            resena = resena,
            onResenaChange = onResenaChange
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        CampoEtiquetas(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        CampoUsuario(
            modifier = Modifier.fillMaxWidth(),
            usuario = usuario,
            onUsuarioChange = onUsuarioChange
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        CampoCorreo(
            modifier = Modifier.fillMaxWidth(),
            correo = correo,
            onCorreoChange = onCorreoChange
        )
    }
}

@Preview(showBackground = true, name = "ContenidoNuevaResena - Preview")
@Composable
fun ContenidoNuevaResenaPreview() {
    ContenidoNuevaResena(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        puntuacion = 3,
        titulo = "Muy buena calidad de audio",
        resena = "Excelente producto, superó mis expectativas.",
        usuario = "Camila R.",
        correo = "camila@example.com",
        onPuntuacionChange = {},
        onTituloChange = {},
        onResenaChange = {},
        onUsuarioChange = {},
        onCorreoChange = {},
        onPublicar = {},
        onCancelar = {}
    )
}
