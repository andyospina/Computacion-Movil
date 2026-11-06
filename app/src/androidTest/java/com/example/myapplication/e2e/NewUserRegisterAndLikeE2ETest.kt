package com.example.myapplication.e2e

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithTag
import com.example.myapplication.R
import com.example.myapplication.debugtools.SeedDataset
import org.junit.Before
import org.junit.Test
import java.util.UUID

/**
 * E2E 1 — Usuario nuevo:
 * 1. Se registra con la contraseña «1234» y la app lo rechaza (se verifica el mensaje).
 * 2. Corrige la contraseña a «123456» y entra a la app.
 * 3. Abre la primera publicación y se verifica el detalle.
 * 4. Va a las reseñas y da like a la primera: el contador sube.
 * 5. Regresa, vuelve a abrir la publicación y quita el like: el contador baja.
 */
class NewUserRegisterAndLikeE2ETest : E2ETest() {

    private val email = "nuevo-${UUID.randomUUID().toString().take(8)}@test.com"

    @Before
    fun seedScenario() {
        seed(
            SeedDataset(
                users = listOf(E2EData.ana, E2EData.luis),
                products = listOf(E2EData.product),
                reviews = listOf(E2EData.anaReview, E2EData.luisReview),
                follows = emptyList()
            )
        )
    }

    @Test
    fun newUser_registersAfterFixingPassword_thenLikesAndUnlikesTheFirstReview() {
        launchApp()

        // ---- Registro: primero con «1234» (inválida) ----
        waitForTag("loginScreen")
        click("linkRegister")
        waitForTag("registerScreen")
        type("txtNombre", "Carla Nueva")
        type("txtUsuario", "carla_e2e")
        type("txtEmail", email)
        type("txtCelular", "3001234567")
        type("txtPassword", "1234")
        type("txtPais", "Colombia")
        clickScrolling("btnRegister")

        waitForTag("errorMessage")
        node("errorMessage").assertTextEquals(string(R.string.error_password_too_short))
        composeRule.onAllNodesWithTag("homeScreen").assertCountEquals(0)

        // ---- Corrige la contraseña a «123456» y entra ----
        clearAndType("txtPassword", "123456")
        clickScrolling("btnRegister")
        waitForTag("homeScreen")

        // ---- Primera publicación: detalle correcto ----
        waitForTag("productItem")
        composeRule.onAllNodesWithTag("productItem")[0].assertTextContains(E2EData.PRODUCT_NAME)
        click("productItem")

        waitForTag("productDetailScreen")
        waitForText("productName", E2EData.PRODUCT_NAME)
        waitForText(
            "productSummary",
            string(R.string.product_info_summary, E2EData.AVERAGE_RATING, E2EData.REVIEW_COUNT, E2EData.RECOMMEND_PERCENT)
        )
        waitForTagCount("reviewCard", E2EData.REVIEW_COUNT)
        // La primera reseña es la más reciente (la de Ana)
        waitForText("reviewAuthor", E2EData.ANA_NAME)

        // ---- Sección de reseñas: like al primer comentario ----
        clickScrolling("linkVerTodas")
        waitForTag("reviewsListScreen")
        waitForTag("btnLike")
        waitForText("reviewAuthor", E2EData.ANA_NAME)
        nodeAt("btnLike").assertTextEquals("0") // sin likes todavía
        click("btnLike")
        waitForText("btnLike", "1") // aumentó

        // ---- Atrás, vuelve a la publicación y quita el like ----
        goBack() // ficha del producto
        waitForTag("productDetailScreen")
        goBack() // Home
        waitForTag("homeScreen")
        waitForTag("productItem")
        click("productItem")

        waitForTag("productDetailScreen")
        waitForText("btnLike", "1") // el like sigue ahí
        clickScrolling("btnLike")
        waitForText("btnLike", "0") // disminuyó
    }
}
