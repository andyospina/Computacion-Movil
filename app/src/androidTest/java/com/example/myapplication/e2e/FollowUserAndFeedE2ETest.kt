package com.example.myapplication.e2e

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import com.example.myapplication.R
import com.example.myapplication.data.dto.FirestoreUserDto
import com.example.myapplication.debugtools.SeedDataset
import com.example.myapplication.support.EmulatorApp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

/**
 * E2E 2 — Usuario ya registrado (no sigue a nadie al empezar):
 * 1. Inicia sesión.
 * 2. Abre el perfil de un usuario y se verifica su información.
 * 3. Lo sigue: la cantidad de seguidores aumenta.
 * 4. Vuelve al Home y abre «Siguiendo»: aparece una publicación del usuario que acaba de seguir.
 */
class FollowUserAndFeedE2ETest : E2ETest() {

    private val email = "seguidor-${UUID.randomUUID().toString().take(8)}@test.com"
    private val password = "123456"

    @Before
    fun seedScenario() {
        // El usuario de la prueba ya está registrado: cuenta en Auth + perfil en users/{uid}
        val uid = runBlocking { EmulatorApp.createAuthUser(email, password) }
        val me = FirestoreUserDto(id = uid, username = "daniel_e2e", name = "Daniel Seguidor", email = email)
        seed(
            SeedDataset(
                users = listOf(me, E2EData.ana, E2EData.luis),
                products = listOf(E2EData.product),
                reviews = listOf(E2EData.anaReview, E2EData.luisReview),
                follows = emptyList() // no sigue a nadie
            )
        )
    }

    @Test
    fun registeredUser_followsAnotherUser_andSeesTheirReviewInTheFollowingFeed() {
        launchApp()

        // ---- Login ----
        waitForTag("loginScreen")
        type("txtCorreo", email, scroll = false)
        type("txtPassword", password, scroll = false)
        click("btnLogin")
        waitForTag("homeScreen")

        // ---- Perfil de Ana (se llega desde el autor de la primera reseña del producto) ----
        waitForTag("productItem")
        click("productItem")
        waitForTag("productDetailScreen")
        waitForText("reviewAuthor", E2EData.ANA_NAME)
        clickScrolling("reviewAuthor")

        waitForTag("userProfileScreen")
        waitForText("perfilNombre", E2EData.ANA_NAME)
        node("perfilUsuario").assertTextEquals("@${E2EData.ANA_USERNAME}")
        node("perfilBio").assertTextEquals(E2EData.ANA_BIO)
        node("perfilPais").assertTextEquals(E2EData.ANA_COUNTRY)
        assertEquals("0", countOf("perfilSeguidores"))
        node("btnFollow").assertTextEquals(string(R.string.action_follow))

        // ---- Seguir: sube el contador de seguidores ----
        click("btnFollow")
        composeRule.waitUntil(30_000) { runCatching { countOf("perfilSeguidores") == "1" }.getOrDefault(false) }
        assertEquals("1", countOf("perfilSeguidores"))
        waitForText("btnFollow", string(R.string.action_unfollow))

        // ---- De vuelta al Home → pestaña «Siguiendo» ----
        goBack() // ficha del producto
        waitForTag("productDetailScreen")
        goBack() // Home
        waitForTag("homeScreen")
        click("nav_feed")

        waitForTag("feedScreen")
        waitForTag("reviewCard")
        composeRule.waitUntil(30_000) {
            composeRule.onAllNodes(hasTestTag("reviewAuthor") and hasText(E2EData.ANA_NAME))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText(E2EData.ANA_REVIEW_TITLE).assertCountEquals(1)
        // Solo aparecen publicaciones de quien sigue: la de Luis no
        composeRule.onAllNodesWithText(E2EData.LUIS_REVIEW_TITLE).assertCountEquals(0)
    }
}
