package com.example.myapplication.debugtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de desarrollo (solo debug, con su propio ícono "Seed"): genera datos con [Seeder] y los
 * carga en el destino elegido. Producción pide escribir el id del proyecto antes de habilitar el botón.
 */
class SeedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) { SeedScreen() }
            }
        }
    }
}

@Composable
private fun SeedScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var target by remember { mutableStateOf(SeedTarget.EMULATOR) }
    var users by remember { mutableStateOf("5") }
    var products by remember { mutableStateOf("5") }
    var reviewsPerUser by remember { mutableStateOf("2") }
    var confirmation by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    val projectId = remember { SeedClients.projectId() }
    val canRun = !running && (target == SeedTarget.EMULATOR || confirmation == projectId)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Datos de prueba (Faker)", style = MaterialTheme.typography.titleLarge)
        SeedTarget.entries.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = target == option, onClick = { target = option })
                Text(option.label)
            }
        }
        if (target == SeedTarget.PRODUCTION) {
            Text(
                "Vas a escribir en el proyecto real. Las reglas de Firestore pueden rechazar parte de la carga. " +
                    "Escribe «$projectId» para habilitar el botón.",
                color = MaterialTheme.colorScheme.error
            )
            OutlinedTextField(confirmation, { confirmation = it }, label = { Text("Id del proyecto") }, modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(users, { users = it.filter(Char::isDigit) }, label = { Text("Usuarios") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(products, { products = it.filter(Char::isDigit) }, label = { Text("Productos") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(reviewsPerUser, { reviewsPerUser = it.filter(Char::isDigit) }, label = { Text("Reseñas por usuario") }, modifier = Modifier.fillMaxWidth())
        Button(
            enabled = canRun,
            onClick = {
                running = true
                result = "Cargando…"
                scope.launch {
                    result = try {
                        val dataset = Seeder().dataset(
                            users = users.toIntOrNull() ?: 0,
                            products = products.toIntOrNull() ?: 0,
                            reviewsPerUser = reviewsPerUser.toIntOrNull() ?: 0
                        )
                        val loader = SeedLoader(
                            firestore = SeedClients.firestore(context, target),
                            target = target,
                            projectId = projectId,
                            confirmedProjectId = confirmation.takeIf { target == SeedTarget.PRODUCTION }
                        )
                        loader.load(dataset).describe(target)
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                    running = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Generar y cargar") }
        Text(result)
    }
}

private fun SeedReport.describe(target: SeedTarget): String = buildString {
    appendLine(if (success) "Carga completa en ${target.label}: $totalInserted documentos." else "Carga con errores en ${target.label}.")
    inserted.forEach { (collection, count) -> appendLine("• $collection: $count") }
    errors.forEach { appendLine("✗ $it") }
}
