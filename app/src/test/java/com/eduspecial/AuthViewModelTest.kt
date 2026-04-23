package com.eduspecial

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.presentation.auth.AuthViewModel
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        whenever(authRepository.isLoggedIn()).thenReturn(false)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Initial State ────────────────────────────────────────────────────────

    @Test
    fun `initial state is login mode`() {
        viewModel.uiState.value.isLoginMode.shouldBeTrue()
    }

    @Test
    fun `initial state is not authenticated when not logged in`() {
        viewModel.uiState.value.isAuthenticated.shouldBeFalse()
    }

    @Test
    fun `initial state is authenticated when already logged in`() {
        whenever(authRepository.isLoggedIn()).thenReturn(true)
        val vm = AuthViewModel(authRepository)
        vm.uiState.value.isAuthenticated.shouldBeTrue()
    }

    // ─── Mode Switching ───────────────────────────────────────────────────────

    @Test
    fun `switchToRegister changes mode`() {
        viewModel.switchToRegister()
        viewModel.uiState.value.isLoginMode.shouldBeFalse()
    }

    @Test
    fun `switchToLogin restores login mode`() {
        viewModel.switchToRegister()
        viewModel.switchToLogin()
        viewModel.uiState.value.isLoginMode.shouldBeTrue()
    }

    @Test
    fun `switchToLogin clears error`() {
        viewModel.switchToRegister()
        viewModel.switchToLogin()
        viewModel.uiState.value.error shouldBe null
    }

    // ─── Input Changes ────────────────────────────────────────────────────────

    @Test
    fun `onEmailChange updates email`() {
        viewModel.onEmailChange("test@example.com")
        viewModel.uiState.value.email shouldBe "test@example.com"
    }

    @Test
    fun `onEmailChange clears emailError`() {
        viewModel.login() // trigger validation error
        viewModel.onEmailChange("new@email.com")
        viewModel.uiState.value.emailError shouldBe null
    }

    @Test
    fun `onPasswordChange updates password`() {
        viewModel.onPasswordChange("secret123")
        viewModel.uiState.value.password shouldBe "secret123"
    }

    @Test
    fun `onDisplayNameChange updates displayName`() {
        viewModel.onDisplayNameChange("Ahmed")
        viewModel.uiState.value.displayName shouldBe "Ahmed"
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    @Test
    fun `login with empty email sets emailError`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        viewModel.uiState.value.emailError.shouldNotBeNull()
    }

    @Test
    fun `login with invalid email sets emailError`() {
        viewModel.onEmailChange("not-an-email")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        viewModel.uiState.value.emailError.shouldNotBeNull()
    }

    @Test
    fun `login with short password sets passwordError`() {
        viewModel.onEmailChange("valid@email.com")
        viewModel.onPasswordChange("123")
        viewModel.login()
        viewModel.uiState.value.passwordError.shouldNotBeNull()
    }

    @Test
    fun `login with valid credentials calls repository`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.success(Unit))
        viewModel.onEmailChange("valid@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        verify(authRepository).login("valid@email.com", "password123")
    }

    @Test
    fun `successful login sets isAuthenticated`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.success(Unit))
        viewModel.onEmailChange("valid@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        viewModel.uiState.value.isAuthenticated.shouldBeTrue()
    }

    @Test
    fun `failed login sets error message`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.failure(Exception("INVALID_LOGIN_CREDENTIALS")))
        viewModel.onEmailChange("valid@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        viewModel.uiState.value.error.shouldNotBeNull()
    }

    @Test
    fun `failed login does not set isAuthenticated`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.failure(Exception("wrong-password")))
        viewModel.onEmailChange("valid@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        viewModel.uiState.value.isAuthenticated.shouldBeFalse()
    }

    // ─── Password Reset ───────────────────────────────────────────────────────

    @Test
    fun `sendPasswordReset with empty email sets emailError`() {
        viewModel.onEmailChange("")
        viewModel.sendPasswordReset()
        viewModel.uiState.value.emailError.shouldNotBeNull()
    }

    @Test
    fun `sendPasswordReset with valid email calls repository`() = runTest {
        whenever(authRepository.sendPasswordReset(any()))
            .thenReturn(Result.success(Unit))
        viewModel.onEmailChange("valid@email.com")
        viewModel.sendPasswordReset()
        verify(authRepository).sendPasswordReset("valid@email.com")
    }
}
