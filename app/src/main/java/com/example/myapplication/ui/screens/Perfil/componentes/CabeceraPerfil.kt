package com.example.myapplication.ui.screens.Perfil.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.DatosLocales
import com.example.myapplication.model.Perfil
import com.example.myapplication.ui.theme.ElectricLime
import com.example.myapplication.ui.theme.Gray
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.Paper
import com.example.myapplication.ui.theme.Sora

/** Cabecera fija y siempre oscura, igual que en Login (momento de marca). */
@Composable
fun CabeceraPerfil(
    perfil: Perfil,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Ink)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(ElectricLime, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = perfil.iniciales,
                fontFamily = Sora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Ink
            )
        }

        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = perfil.nombre,
                fontFamily = Sora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Paper
            )
            Text(
                text = "Miembro desde ${perfil.miembroDesde}",
                fontSize = 12.sp,
                color = Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CabeceraPerfilPreview() {
    MyApplicationTheme(darkTheme = false) {
        CabeceraPerfil(perfil = DatosLocales.perfilUsuario)
    }
}
