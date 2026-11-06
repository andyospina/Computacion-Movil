package com.example.myapplication.e2e

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.MainActivity
import com.example.myapplication.support.EmulatorApp
import com.example.myapplication.support.FirestoreEmulatorTest
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule

private const val DEFAULT_TIMEOUT_MS = 30_000L

/**
 * Base de las pruebas e2e: arranca la app real (`MainActivity` con su grafo de Hilt de producción), que
 * en debug habla con Firebase Emulator Suite. Los datos de partida se siembran con un cliente aparte
 * ([FirestoreEmulatorTest]) y cada prueba empieza con la sesión cerrada.
 */
abstract class E2ETest : FirestoreEmulatorTest() {

    // Regla «vacía» + ActivityScenario: la Activity se lanza cuando la prueba termina de sembrar sus
    // datos, no antes (el Splash decide a dónde ir según haya sesión o no).
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    protected val targetContext get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun prepareApp() {
        EmulatorApp.grantPermissions()
        // La sesión de la app persiste entre pruebas del mismo proceso: cada prueba parte sin sesión
        FirebaseAuth.getInstance().signOut()
    }

    @After
    fun closeApp() {
        scenario?.close()
        FirebaseAuth.getInstance().signOut()
    }

    protected fun launchApp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    protected fun string(resId: Int, vararg args: Any): String = targetContext.getString(resId, *args)

    // ---- Esperas y acciones ----

    protected fun waitForTag(tag: String, timeoutMs: Long = DEFAULT_TIMEOUT_MS) {
        composeRule.waitUntil(timeoutMs) { composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
    }

    protected fun waitForTagCount(tag: String, count: Int, timeoutMs: Long = DEFAULT_TIMEOUT_MS) {
        composeRule.waitUntil(timeoutMs) { composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().size == count }
    }

    /** Espera a que el nodo [index] de [tag] muestre exactamente [expected] (los datos llegan en tiempo real). */
    protected fun waitForText(tag: String, expected: String, index: Int = 0, timeoutMs: Long = DEFAULT_TIMEOUT_MS) {
        composeRule.waitUntil(timeoutMs) {
            runCatching { composeRule.onAllNodesWithTag(tag)[index].assertTextEquals(expected) }.isSuccess
        }
    }

    protected fun node(tag: String): SemanticsNodeInteraction = composeRule.onNodeWithTag(tag)

    /** El nodo número [index] cuando el tag aparece varias veces (una por tarjeta de reseña, por ejemplo). */
    protected fun nodeAt(tag: String, index: Int = 0): SemanticsNodeInteraction =
        composeRule.onAllNodesWithTag(tag)[index]

    protected fun click(tag: String, index: Int = 0) {
        composeRule.onAllNodesWithTag(tag)[index].performClick()
    }

    /** Para elementos dentro de una columna con scroll: los lleva a la vista antes de tocarlos. */
    protected fun clickScrolling(tag: String, index: Int = 0) {
        composeRule.onAllNodesWithTag(tag)[index].performScrollTo().performClick()
    }

    /** Escribe en un campo; [scroll] = false para pantallas sin scroll (p. ej. Login). */
    protected fun type(tag: String, text: String, scroll: Boolean = true) {
        val field = composeRule.onNodeWithTag(tag)
        if (scroll) field.performScrollTo()
        field.performTextInput(text)
    }

    protected fun clearAndType(tag: String, text: String) {
        composeRule.onNodeWithTag(tag).performScrollTo().performTextClearance()
        composeRule.onNodeWithTag(tag).performTextInput(text)
    }

    /** Número que muestra un contador compuesto por dos textos («1 », «Seguidores»): el primero, sin espacios. */
    protected fun countOf(tag: String): String =
        node(tag).fetchSemanticsNode().config[SemanticsProperties.Text].first().text.trim()

    protected fun goBack() {
        composeRule.onNodeWithTag("btnBack").performClick()
        composeRule.waitForIdle()
    }
}
