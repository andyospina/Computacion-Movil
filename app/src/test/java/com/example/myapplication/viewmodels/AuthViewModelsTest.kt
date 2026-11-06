package com.example.myapplication.viewmodels

import com.example.myapplication.R
import com.example.myapplication.data.Result
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.ui.screens.Login.LoginViewModel
import com.example.myapplication.ui.screens.Register.RegisterViewModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** ViewModels de Login y Registro con los Repository simulados (MockK). */
class AuthViewModelsTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = mockk<AuthRepository>()
    private val users = mockk<UserRepository>()

    private fun loginViewModel() = LoginViewModel(auth)
    private fun registerViewModel() = RegisterViewModel(auth, users)

    private fun fillRegister(vm: RegisterViewModel, password: String) {
        vm.updateName("Ana Pérez")
        vm.updateUsername("ana")
        vm.updateEmail("ana@test.com")
        vm.updateCellphone("3001234567")
        vm.updatePassword(password)
    }

    // ---------------- Login ----------------

    @Test
    fun login_blankFields_showsRequiredErrorWithoutCallingTheRepository() {
        val vm = loginViewModel()

        vm.loginButtonPress()

        assertTrue(vm.uiState.value.showError)
        assertEquals(R.string.error_all_fields_required, vm.uiState.value.errorMessageRes)
        coVerify(exactly = 0) { auth.signIn(any(), any()) }
    }

    @Test
    fun login_success_navigatesAndStopsLoading() {
        coEvery { auth.signIn("ana@test.com", "123456") } returns Result.Success(Unit)
        val vm = loginViewModel()
        vm.updateEmail("ana@test.com")
        vm.updatePassword("123456")

        vm.loginButtonPress()

        assertTrue(vm.uiState.value.navigate)
        assertFalse(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.showError)
    }

    @Test
    fun login_wrongCredentials_showsTheRepositoryErrorAndDoesNotNavigate() {
        coEvery { auth.signIn(any(), any()) } returns
            Result.Failure(Exception("bad"), R.string.error_invalid_credentials)
        val vm = loginViewModel()
        vm.updateEmail("ana@test.com")
        vm.updatePassword("equivocada")

        vm.loginButtonPress()

        assertTrue(vm.uiState.value.showError)
        assertEquals(R.string.error_invalid_credentials, vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.navigate)
        assertFalse(vm.uiState.value.isLoading)
    }

    // ---------------- Registro ----------------

    @Test
    fun register_missingFields_showsRequiredError() {
        val vm = registerViewModel()
        vm.updateEmail("ana@test.com")

        vm.registerButtonPress()

        assertTrue(vm.uiState.value.showError)
        assertEquals(R.string.error_all_fields_required, vm.uiState.value.errorMessageRes)
    }

    @Test
    fun register_passwordOf1234_isTooShortAndNeverReachesFirebase() {
        val vm = registerViewModel()
        fillRegister(vm, password = "1234")

        vm.registerButtonPress()

        assertEquals(R.string.error_password_too_short, vm.uiState.value.errorMessageRes)
        assertTrue(vm.uiState.value.showError)
        coVerify(exactly = 0) { auth.signUp(any(), any()) }
    }

    @Test
    fun register_editingAFieldHidesThePreviousError() {
        val vm = registerViewModel()
        fillRegister(vm, password = "1234")
        vm.registerButtonPress()
        assertTrue(vm.uiState.value.showError)

        vm.updatePassword("123456")

        assertFalse(vm.uiState.value.showError)
    }

    @Test
    fun register_emailAlreadyInUse_showsTheAuthErrorAndSavesNoProfile() {
        coEvery { auth.signUp("ana@test.com", "123456") } returns
            Result.Failure(Exception("collision"), R.string.error_email_already_registered)
        val vm = registerViewModel()
        fillRegister(vm, password = "123456")

        vm.registerButtonPress()

        assertEquals(R.string.error_email_already_registered, vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.navigate)
        coVerify(exactly = 0) { users.registerUser(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun register_success_createsTheProfileWithTheNewUidAndNavigates() {
        val firebaseUser = mockk<FirebaseUser> { every { uid } returns "uid-1" }
        coEvery { auth.signUp("ana@test.com", "123456") } returns Result.Success(Unit)
        every { auth.currentUser } returns firebaseUser
        coEvery { users.registerUser(any(), any(), any(), any(), any(), any()) } returns Result.Success(Unit)
        val vm = registerViewModel()
        fillRegister(vm, password = "123456")

        vm.registerButtonPress()

        assertTrue(vm.uiState.value.navigate)
        assertFalse(vm.uiState.value.isLoading)
        coVerify(exactly = 1) {
            users.registerUser(
                userId = "uid-1", username = "ana", name = "Ana Pérez",
                email = "ana@test.com", country = "", bio = ""
            )
        }
    }

    @Test
    fun register_whenTheProfileCannotBeSaved_rollsBackTheAccount() {
        val firebaseUser = mockk<FirebaseUser> { every { uid } returns "uid-1" }
        coEvery { auth.signUp(any(), any()) } returns Result.Success(Unit)
        every { auth.currentUser } returns firebaseUser
        coEvery { users.registerUser(any(), any(), any(), any(), any(), any()) } returns
            Result.Failure(Exception("firestore"), R.string.error_api_unreachable)
        coEvery { auth.deleteCurrentUser() } returns Result.Success(Unit)
        every { auth.signOut() } just runs
        val vm = registerViewModel()
        fillRegister(vm, password = "123456")

        vm.registerButtonPress()

        coVerify(exactly = 1) { auth.deleteCurrentUser() }
        assertEquals(R.string.error_api_unreachable, vm.uiState.value.errorMessageRes)
        assertFalse(vm.uiState.value.navigate)
        verify(exactly = 0) { auth.signOut() }
    }
}
