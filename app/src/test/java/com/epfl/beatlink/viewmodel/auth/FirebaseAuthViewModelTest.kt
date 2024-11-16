package com.epfl.beatlink.viewmodel.auth

import com.epfl.beatlink.model.auth.AuthState
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
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
class FirebaseAuthViewModelTest {

  private val testDispatcher = TestCoroutineDispatcher()
  private lateinit var firebaseAuthRepository: FirebaseAuthRepository
  private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    firebaseAuthRepository = mock(FirebaseAuthRepository::class.java)
    firebaseAuthViewModel = FirebaseAuthViewModel(firebaseAuthRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun signUpSuccess() {
    `when`(firebaseAuthRepository.signUp(any(), any(), any(), any(), any())).thenAnswer { invocation
      ->
      val onSuccess = invocation.getArgument(3) as () -> Unit
      onSuccess() // Simulate a successful sign-up callback
    }

    firebaseAuthViewModel.signUp("test@example.com", "password123", "testuser")

    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Success))
    verify(firebaseAuthRepository)
        .signUp(eq("test@example.com"), eq("password123"), eq("testuser"), any(), any())
  }

  @Test
  fun signUpFailure() {
    `when`(firebaseAuthRepository.signUp(any(), any(), any(), any(), any())).thenAnswer { invocation
      ->
      val onFailure = invocation.getArgument(4) as (Exception) -> Unit
      onFailure(Exception("Sign up error")) // Simulate a sign-up failure
    }

    firebaseAuthViewModel.signUp("test@example.com", "password123", "testuser")

    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Sign up failed: Sign up error"))
    verify(firebaseAuthRepository)
        .signUp(eq("test@example.com"), eq("password123"), eq("testuser"), any(), any())
  }

  @Test
  fun loginSuccess() {
    `when`(firebaseAuthRepository.login(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument(2) as () -> Unit
      onSuccess() // Simulate a successful login callback
    }

    firebaseAuthViewModel.login("test@example.com", "password123")

    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Success))
    verify(firebaseAuthRepository).login(eq("test@example.com"), eq("password123"), any(), any())
  }

  @Test
  fun loginFailure() {
    `when`(firebaseAuthRepository.login(any(), any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument(3) as (Exception) -> Unit
      onFailure(Exception("Login error")) // Simulate a login failure
    }

    firebaseAuthViewModel.login("test@example.com", "password123")

    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Login failed: Login error"))
    verify(firebaseAuthRepository).login(eq("test@example.com"), eq("password123"), any(), any())
  }

  @Test
  fun resetState() {
    firebaseAuthViewModel.resetState()

    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Idle))
  }
}
