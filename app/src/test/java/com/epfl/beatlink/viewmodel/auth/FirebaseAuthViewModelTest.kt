package com.epfl.beatlink.viewmodel.auth

import com.epfl.beatlink.model.auth.AuthState
import com.epfl.beatlink.repository.authentication.FirebaseAuthRepository
import com.google.firebase.firestore.util.Assert.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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
    `when`(firebaseAuthRepository.signUp(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument(2) as () -> Unit
      onSuccess() // Simulate a successful sign-up callback
    }

    firebaseAuthViewModel.signUp("test@example.com", "password123")

    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Success))
    verify(firebaseAuthRepository).signUp(eq("test@example.com"), eq("password123"), any(), any())
  }

  @Test
  fun signUpFailure() {
    `when`(firebaseAuthRepository.signUp(any(), any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument(3) as (Exception) -> Unit
      onFailure(Exception("Sign up error")) // Simulate a sign-up failure
    }

    firebaseAuthViewModel.signUp("test@example.com", "password123")

    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Sign up failed: Sign up error"))
    verify(firebaseAuthRepository).signUp(eq("test@example.com"), eq("password123"), any(), any())
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

  @Test
  fun verifyAndChangePasswordSuccess() = runTest {
    val currentPassword = "currentPassword123"
    val newPassword = "newPassword123"

    // Mock verifyPassword to return success
    `when`(firebaseAuthRepository.verifyPassword(eq(currentPassword)))
        .thenReturn(Result.success(Unit))

    // Mock changePassword to return success
    `when`(firebaseAuthRepository.changePassword(eq(newPassword))).thenReturn(Result.success(Unit))

    // Call the function
    firebaseAuthViewModel.verifyAndChangePassword(currentPassword, newPassword)

    // Assert that the authState is set to Success
    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Success))

    // Verify interactions with the repository
    verify(firebaseAuthRepository).verifyPassword(eq(currentPassword))
    verify(firebaseAuthRepository).changePassword(eq(newPassword))
  }

  @Test
  fun verifyAndChangePasswordVerificationFails() = runTest {
    val currentPassword = "wrongPassword"
    val newPassword = "newPassword123"

    // Mock verifyPassword to return failure
    `when`(firebaseAuthRepository.verifyPassword(eq(currentPassword)))
        .thenReturn(Result.failure(Exception("Verification failed")))

    // Call the function
    firebaseAuthViewModel.verifyAndChangePassword(currentPassword, newPassword)

    // Assert that the authState is set to Error with the correct message
    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Verification failed: Current password is incorrect"))

    // Verify that changePassword is not called
    verify(firebaseAuthRepository).verifyPassword(eq(currentPassword))
    verify(firebaseAuthRepository, org.mockito.Mockito.never()).changePassword(any())
  }

  @Test
  fun verifyAndChangePasswordChangeFails() = runTest {
    val currentPassword = "currentPassword123"
    val newPassword = "newPassword123"

    // Mock verifyPassword to return success
    `when`(firebaseAuthRepository.verifyPassword(eq(currentPassword)))
        .thenReturn(Result.success(Unit))

    // Mock changePassword to return failure
    `when`(firebaseAuthRepository.changePassword(eq(newPassword)))
        .thenReturn(Result.failure(Exception("Change password failed")))

    // Call the function
    firebaseAuthViewModel.verifyAndChangePassword(currentPassword, newPassword)

    // Assert that the authState is set to Error with the correct message
    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Change password failed: Change password failed"))

    // Verify interactions with the repository
    verify(firebaseAuthRepository).verifyPassword(eq(currentPassword))
    verify(firebaseAuthRepository).changePassword(eq(newPassword))
  }

  @Test
  fun deleteAccountSuccess() = runTest {
    val currentPassword = "testPassword"

    `when`(firebaseAuthRepository.deleteAccount(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument(1) as () -> Unit
      onSuccess() // Simulate a successful account deletion
    }

    var successCallbackTriggered = false
    firebaseAuthViewModel.deleteAccount(
        currentPassword = currentPassword,
        onSuccess = { successCallbackTriggered = true },
        onFailure = { fail("Failure callback should not be called") })

    assertThat(successCallbackTriggered, `is`(true))
    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Idle))
    verify(firebaseAuthRepository).deleteAccount(eq(currentPassword), any(), any())
  }

  @Test
  fun deleteAccountFailure() = runTest {
    val currentPassword = "testPassword"

    `when`(firebaseAuthRepository.deleteAccount(any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument(2) as (Exception) -> Unit
      onFailure(Exception("Account deletion failed")) // Simulate account deletion failure
    }

    var failureCallbackTriggered = false
    firebaseAuthViewModel.deleteAccount(
        currentPassword = currentPassword,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { exception ->
          failureCallbackTriggered = true
          assertThat(exception.message, `is`("Account deletion failed"))
        })

    assertThat(failureCallbackTriggered, `is`(true))
    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Account deletion failed: Account deletion failed"))
    verify(firebaseAuthRepository).deleteAccount(eq(currentPassword), any(), any())
  }

  @Test
  fun signOutSuccess() {
    `when`(firebaseAuthRepository.signOut(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument(0) as () -> Unit
      onSuccess() // Simulate a successful sign-out
    }

    var successCallbackTriggered = false
    firebaseAuthViewModel.signOut(
        onSuccess = { successCallbackTriggered = true },
        onFailure = { fail("Failure callback should not be called") })

    assertThat(successCallbackTriggered, `is`(true))
    assertThat(firebaseAuthViewModel.authState.value, `is`(AuthState.Idle))
    verify(firebaseAuthRepository).signOut(any(), any())
  }

  @Test
  fun signOutFailure() {
    `when`(firebaseAuthRepository.signOut(any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument(1) as (Exception) -> Unit
      onFailure(Exception("Sign out error")) // Simulate sign-out failure
    }

    var failureCallbackTriggered = false
    firebaseAuthViewModel.signOut(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { exception ->
          failureCallbackTriggered = true
          assertThat(exception.message, `is`("Sign out error"))
        })

    assertThat(failureCallbackTriggered, `is`(true))
    assertThat(firebaseAuthViewModel.authState.value is AuthState.Error, `is`(true))
    assertThat(
        (firebaseAuthViewModel.authState.value as AuthState.Error).message,
        `is`("Sign out failed: Sign out error"))
    verify(firebaseAuthRepository).signOut(any(), any())
  }

  @Test
  fun verifyPasswordSuccess() = runTest {
    val currentPassword = "correctPassword"

    // Mock verifyPassword to return success
    `when`(firebaseAuthRepository.verifyPassword(eq(currentPassword)))
        .thenReturn(Result.success(Unit))

    val result = firebaseAuthViewModel.verifyPassword(currentPassword)

    // Verify the result is success
    assertThat(result.isSuccess, `is`(true))
    verify(firebaseAuthRepository).verifyPassword(eq(currentPassword))
  }

  @Test
  fun verifyPasswordFailure() = runTest {
    val currentPassword = "wrongPassword"

    // Mock verifyPassword to return failure
    `when`(firebaseAuthRepository.verifyPassword(eq(currentPassword)))
        .thenReturn(Result.failure(Exception("Verification failed")))

    val result = firebaseAuthViewModel.verifyPassword(currentPassword)

    // Verify the result is failure
    assertThat(result.isFailure, `is`(true))
    assertThat(result.exceptionOrNull()?.message, `is`("Verification failed"))
    verify(firebaseAuthRepository).verifyPassword(eq(currentPassword))
  }
}
