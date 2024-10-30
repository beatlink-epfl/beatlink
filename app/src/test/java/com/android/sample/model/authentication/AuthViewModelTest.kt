package com.android.sample.model.authentication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@ExperimentalCoroutinesApi
class AuthViewModelTest {

  private val testDispatcher = TestCoroutineDispatcher()
  private lateinit var authRepository: AuthRepository
  private lateinit var authViewModel: AuthViewModel

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    authRepository = mock(AuthRepository::class.java)
    authViewModel = AuthViewModel(authRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun signUpSuccess() {
    `when`(authRepository.signUp(any(), any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument(3) as () -> Unit
      onSuccess() // Simulate a successful sign-up callback
    }

    authViewModel.signUp("test@example.com", "password123", "testuser")

    assertThat(authViewModel.authState.value, `is`(AuthState.Success))
    verify(authRepository)
        .signUp(eq("test@example.com"), eq("password123"), eq("testuser"), any(), any())
  }

  @Test
  fun signUpFailure() {
    `when`(authRepository.signUp(any(), any(), any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument(4) as (Exception) -> Unit
      onFailure(Exception("Sign up error")) // Simulate a sign-up failure
    }

    authViewModel.signUp("test@example.com", "password123", "testuser")

    assertThat(authViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (authViewModel.authState.value as AuthState.Error).message,
        `is`("Sign up failed: Sign up error"))
    verify(authRepository)
        .signUp(eq("test@example.com"), eq("password123"), eq("testuser"), any(), any())
  }

  @Test
  fun loginSuccess() {
    `when`(authRepository.login(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument(2) as () -> Unit
      onSuccess() // Simulate a successful login callback
    }

    authViewModel.login("test@example.com", "password123")

    assertThat(authViewModel.authState.value, `is`(AuthState.Success))
    verify(authRepository).login(eq("test@example.com"), eq("password123"), any(), any())
  }

  @Test
  fun loginFailure() {
    `when`(authRepository.login(any(), any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument(3) as (Exception) -> Unit
      onFailure(Exception("Login error")) // Simulate a login failure
    }

    authViewModel.login("test@example.com", "password123")

    assertThat(authViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (authViewModel.authState.value as AuthState.Error).message,
        `is`("Login failed: Login error"))
    verify(authRepository).login(eq("test@example.com"), eq("password123"), any(), any())
  }

  @Test
  fun resetState() {
    authViewModel.resetState()

    assertThat(authViewModel.authState.value, `is`(AuthState.Idle))
  }
}
