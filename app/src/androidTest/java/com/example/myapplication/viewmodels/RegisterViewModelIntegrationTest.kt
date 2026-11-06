package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.datasource.AuthRemoteDataSource
import com.example.myapplication.data.datasource.impl.UserFirestoreDataSource
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.support.EmulatorApp
import com.example.myapplication.ui.screens.Register.RegisterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

/**
 * Integración de [RegisterViewModel]: el ViewModel real con los Repository y DataSource reales contra
 * Firebase Emulator Suite (Auth + Firestore). Nada se simula: se comprueba lo que de verdad queda
 * guardado en el emulador.
 */
class RegisterViewModelIntegrationTest : com.example.myapplication.support.FirestoreEmulatorTest() {

    private val auth get() = EmulatorApp.auth()
    private val email = "vm-${UUID.randomUUID().toString().take(8)}@test.com"

    @Before
    fun signedOut() {
        auth.signOut()
    }

    @After
    fun signOutAgain() {
        auth.signOut()
    }

    private fun viewModel() = RegisterViewModel(
        AuthRepository(AuthRemoteDataSource(auth)),
        UserRepository(UserFirestoreDataSource(firestore))
    )

    private fun RegisterViewModel.fill(password: String) {
        updateName("Carla Nueva")
        updateUsername("carla_vm")
        updateEmail(email)
        updateCellphone("3001234567")
        updateCountry("Colombia")
        updateBio("Hola desde la prueba")
        updatePassword(password)
    }

    @Test
    fun register_validData_createsTheAuthAccountAndTheProfileDocument() = test {
        val vm = viewModel()
        vm.fill(password = "123456")

        vm.registerButtonPress()
        val state = vm.uiState.first { it.navigate || it.showError }

        assertTrue("el registro debe terminar en navegación al Home", state.navigate)
        assertFalse(state.showError)
        val uid = auth.currentUser?.uid
        assertNotNull("Auth debe haber iniciado sesión con la cuenta nueva", uid)
        val profile = firestore.collection("users").document(uid!!).get().await()
        assertTrue("debe existir users/{uid}", profile.exists())
        assertEquals("Carla Nueva", profile.getString("name"))
        assertEquals("carla_vm", profile.getString("username"))
        assertEquals(email, profile.getString("email"))
        assertEquals("Colombia", profile.getString("country"))
    }

    @Test
    fun register_emailAlreadyRegistered_showsTheErrorAndCreatesNoProfile() = test {
        EmulatorApp.createAuthUser(email, "123456")
        val vm = viewModel()
        vm.fill(password = "654321")

        vm.registerButtonPress()
        val state = vm.uiState.first { it.navigate || it.showError }

        assertFalse(state.navigate)
        assertTrue(state.showError)
        assertEquals(R.string.error_email_already_registered, state.errorMessageRes)
        assertTrue("no se debe crear ningún perfil", firestore.collection("users").get().await().isEmpty)
    }
}
