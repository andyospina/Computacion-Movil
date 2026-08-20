package com.example.myapplication.ui.screens.Producto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Producto
import com.example.myapplication.model.Resena
import com.example.myapplication.ui.components.ImagenPlaceholder
import com.example.myapplication.ui.components.TarjetaResena
import com.example.myapplication.ui.screens.Producto.componentes.BotonesProducto
import com.example.myapplication.ui.screens.Producto.componentes.EncabezadoProducto
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun ProductoScreen(
    modifier: Modifier = Modifier,
    producto: Producto,
    resenasDestacadas: List<Resena>,
    onEscribirResena: () -> Unit,
    onVerTodas: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ImagenPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            esquinas = 0.dp,
            texto = "Foto del producto"
        )

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EncabezadoProducto(producto = producto)

            BotonesProducto(
                modifier = Modifier.fillMaxWidth(),
                onEscribirResena = onEscribirResena
            )

            HorizontalDivider()

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reseñas destacadas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ver todas ›",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable { onVerTodas() }
                    )
                }

                if (resenasDestacadas.isEmpty()) {
                    Text(
                        text = "Aún no hay reseñas para este producto.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    resenasDestacadas.take(2).forEach { resena ->
                        TarjetaResena(resena = resena)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ProductoScreen - Preview")
@Composable
fun ProductoScreenPreview() {
    MyApplicationTheme(darkTheme = false) {
        ProductoScreen(
            producto = DatosLocales.productoDestacado,
            resenasDestacadas = DatosLocales.resenas,
            onEscribirResena = {},
            onVerTodas = {}
        )
    }
}
