package com.eduspecial

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eduspecial.data.remote.secure.RuntimeConfig
import com.eduspecial.data.remote.secure.RuntimeConfigProvider
import com.eduspecial.data.remote.secure.FirebaseConfigDto
import com.eduspecial.data.repository.AuthRepository
import com.eduspecial.presentation.auth.AuthViewModel
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    private lateinit var runtimeConfigProvider: RuntimeConfigProvider
    private lateinit var runtimeConfigFlow: MutableStateFlow<RuntimeConfig?>
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        runtimeConfigProvider = mock()
        runtimeConfigFlow = MutableStateFlow(null)
        whenever(authRepository.isLoggedIn()).thenReturn(false)
        whenever(runtimeConfigProvider.config).thenReturn(runtimeConfigFlow)
        viewModel = AuthViewModel(authRepository, runtimeConfigProvider)
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
        val vm = AuthViewModel(authRepository, runtimeConfigProvider)
        vm.uiState.value.isAuthenticated.shouldBeTrue()
    }

    @Test
    fun `google sign in disabled when client id is missing`() {
        viewModel.isGoogleSignInEnabled.value.shouldBeFalse()
    }

    @Test
    fun `google sign in enabled when client id is valid`() {
        runtimeConfigFlow.value = RuntimeConfig(
            firebase = FirebaseConfigDto(
                webClientId = "demo.apps.googleusercontent.com"
            )
        )
        viewModel.isGoogleSignInEnabled.value.shouldBeTrue()
    }

    @Test
    fun `google sign in disabled when client id is placeholder`() {
        runtimeConfigFlow.value = RuntimeConfig(
            firebase = FirebaseConfigDto(
                webClientId = "REQUIRED_ANDROID_CLIENT_ID.apps.googleusercontent.com"
            )
        )
        viewModel.isGoogleSignInEnabled.value.shouldBeFalse()
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
            .thenReturn(Result.success("uid-1"))
        viewModel.onEmailChange("valid@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        verify(authRepository).login("valid@email.com", "password123")
    }

    @Test
    fun `successful login sets isAuthenticated`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.success("uid-1"))
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

    @Test
    fun `sendPasswordReset success sets password reset flag`() = runTest {
        whenever(authRepository.sendPasswordReset(any()))
            .thenReturn(Result.success(Unit))
        viewModel.onEmailChange("valid@email.com")
        viewModel.sendPasswordReset()
        viewModel.uiState.value.isPasswordResetSent.shouldBeTrue()
    }

    @Test
    fun `clearPasswordResetState clears success flag and error`() {
        viewModel.onGoogleSignInFailed("temporary error")
        viewModel.clearPasswordResetState()
        viewModel.uiState.value.isPasswordResetSent.shouldBeFalse()
        viewModel.uiState.value.error shouldBe null
    }

    // ─── Register / Google / Guest ────────────────────────────────────────────

    @Test
    fun `register with valid data calls repository`() = runTest {
        whenever(authRepository.register(any(), any(), any()))
            .thenReturn(Result.success("uid-2"))
        viewModel.switchToRegister()
        viewModel.onDisplayNameChange("Ahmed")
        viewModel.onEmailChange("register@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.register()
        verify(authRepository).register("register@email.com", "password123", "Ahmed")
    }

    @Test
    fun `register failure sets mapped error and keeps unauthenticated`() = runTest {
        whenever(authRepository.register(any(), any(), any()))
            .thenReturn(Result.failure(Exception("email-already-in-use")))
        viewModel.switchToRegister()
        viewModel.onDisplayNameChange("Ahmed")
        viewModel.onEmailChange("register@email.com")
        viewModel.onPasswordChange("password123")
        viewModel.register()
        viewModel.uiState.value.error shouldBe "هذا البريد مسجّل بالفعل"
        viewModel.uiState.value.isAuthenticated.shouldBeFalse()
    }

    @Test
    fun `google sign in success authenticates user`() = runTest {
        whenever(authRepository.signInWithGoogle(any()))
            .thenReturn(Result.success("uid-google"))
        viewModel.signInWithGoogle("token123")
        viewModel.uiState.value.isAuthenticated.shouldBeTrue()
    }

    @Test
    fun `google sign in failure maps error into ui state`() = runTest {
        whenever(authRepository.signInWithGoogle(any()))
            .thenReturn(Result.failure(Exception("network down")))
        viewModel.signInWithGoogle("token123")
        viewModel.uiState.value.error.shouldNotBeNull()
    }

    @Test
    fun `onGoogleSignInFailed surfaces message`() {
        viewModel.onGoogleSignInFailed("google flow failed")
        viewModel.uiState.value.error shouldBe "google flow failed"
    }

    @Test
    fun `continueAsGuest success authenticates user`() = runTest {
        whenever(authRepository.signInAnonymously())
            .thenReturn(Result.success("uid-guest"))
        viewModel.continueAsGuest()
        viewModel.uiState.value.isAuthenticated.shouldBeTrue()
    }

    // ─── Email Verification ────────────────────────────────────────────────────

    @Test
    fun `sendEmailVerification success clears loading without error`() = runTest {
        whenever(authRepository.sendEmailVerification())
            .thenReturn(Result.success(Unit))
        viewModel.sendEmailVerification()
        viewModel.uiState.value.isLoading.shouldBeFalse()
        viewModel.uiState.value.error shouldBe null
    }

    @Test
    fun `sendEmailVerification failure sets mapped error`() = runTest {
        whenever(authRepository.sendEmailVerification())
            .thenReturn(Result.failure(Exception("network unavailable")))
        viewModel.sendEmailVerification()
        viewModel.uiState.value.error.shouldNotBeNull()
    }

    @Test
    fun `checkEmailVerification updates verification flag and current user`() = runTest {
        whenever(authRepository.isEmailVerified()).thenReturn(true)
        whenever(authRepository.getCurrentUserId()).thenReturn("uid-verified")
        whenever(authRepository.getCurrentUserEmail()).thenReturn("verified@email.com")
        whenever(authRepository.getCurrentDisplayName()).thenReturn("Verified User")
        whenever(authRepository.reloadUser()).thenReturn(Result.success(Unit))

        viewModel.checkEmailVerification()

        viewModel.uiState.value.isEmailVerified.shouldBeTrue()
        viewModel.uiState.value.currentUser.shouldNotBeNull()
        viewModel.uiState.value.currentUser?.uid shouldBe "uid-verified"
    }
}
