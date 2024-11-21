package com.epfl.beatlink.repository.authentication

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class FirebaseAuthRepositoryFirestoreTest {

  @Mock private lateinit var mockAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser
  @Mock private lateinit var mockAuthResult: AuthResult

  private lateinit var firebaseAuthRepositoryFirestore: FirebaseAuthRepositoryFirestore

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Set up FirebaseAuth and FirebaseUser
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")
    `when`(mockFirebaseUser.email).thenReturn("test@example.com")

    firebaseAuthRepositoryFirestore = FirebaseAuthRepositoryFirestore(mockAuth)
  }

  @Test
  fun signUp_shouldCallCreateUserWithEmailAndPassword() {
    `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(Tasks.forResult(mockAuthResult))

    firebaseAuthRepositoryFirestore.signUp(
        email = "test@example.com",
        password = "password123",
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that createUserWithEmailAndPassword is called
    verify(mockAuth).createUserWithEmailAndPassword("test@example.com", "password123")
  }

  @Test
  fun login_shouldCallSignInWithEmailAndPassword() {
    `when`(mockAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(Tasks.forResult(mockAuthResult))

    firebaseAuthRepositoryFirestore.login(
        email = "test@example.com",
        password = "password123",
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify signInWithEmailAndPassword is called
    verify(mockAuth).signInWithEmailAndPassword("test@example.com", "password123")
  }

  @Test
  fun verifyPassword_shouldReauthenticateUserSuccessfully() {
    val testPassword = "testPassword"

    // Mock FirebaseUser's email and reauthenticate behavior
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.email).thenReturn("test@example.com")
    `when`(mockFirebaseUser.reauthenticate(any()))
        .thenReturn(Tasks.forResult(null)) // Ignore the credential reference

    // Call the method
    val result = runBlocking { firebaseAuthRepositoryFirestore.verifyPassword(testPassword) }

    // Assert success result
    assert(result.isSuccess)

    // Verify reauthenticate was called
    verify(mockFirebaseUser).reauthenticate(any())
  }

  @Test
  fun verifyPassword_shouldReturnFailureIfReauthenticationFails() {
    val testPassword = "wrongPassword"

    // Mock FirebaseAuth's currentUser and FirebaseUser's behavior
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.email).thenReturn("test@example.com")
    `when`(mockFirebaseUser.reauthenticate(any()))
        .thenReturn(Tasks.forException(Exception("Reauthentication failed")))

    // Call the method
    val result = runBlocking { firebaseAuthRepositoryFirestore.verifyPassword(testPassword) }

    // Assert failure result
    assert(result.isFailure)
    assert(result.exceptionOrNull()?.message == "Reauthentication failed")

    // Verify that reauthenticate was called
    verify(mockFirebaseUser).reauthenticate(any())
  }

  @Test
  fun changePassword_shouldUpdatePasswordSuccessfully() {
    val newPassword = "newPassword123"

    // Mock the password update process
    `when`(mockFirebaseUser.updatePassword(newPassword)).thenReturn(Tasks.forResult(null))

    val result = runBlocking { firebaseAuthRepositoryFirestore.changePassword(newPassword) }

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the result is successful
    assert(result.isSuccess)
    verify(mockFirebaseUser).updatePassword(newPassword)
  }

  @Test
  fun changePassword_shouldReturnFailureIfUpdateFails() {
    val newPassword = "newPassword123"

    // Mock the password update process to throw an exception
    `when`(mockFirebaseUser.updatePassword(newPassword))
        .thenReturn(Tasks.forException(Exception("Password update failed")))

    val result = runBlocking { firebaseAuthRepositoryFirestore.changePassword(newPassword) }

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assert that the result is a failure
    assert(result.isFailure)
    assert(result.exceptionOrNull()?.message == "Password update failed")
    verify(mockFirebaseUser).updatePassword(newPassword)
  }

  @Test
  fun verifyPassword_shouldReturnFailureIfUserNotAuthenticated() {
    val testPassword = "testPassword"

    // Mock FirebaseAuth to return null for the current user
    `when`(mockAuth.currentUser).thenReturn(null)

    // Call the method
    val result = runBlocking { firebaseAuthRepositoryFirestore.verifyPassword(testPassword) }

    // Assert failure result
    assert(result.isFailure)
    assert(result.exceptionOrNull()?.message == "User not authenticated")
  }

  @Test
  fun changePassword_shouldReturnFailureIfUserNotAuthenticated() {
    val newPassword = "newPassword123"

    // Mock FirebaseAuth to return null for the current user
    `when`(mockAuth.currentUser).thenReturn(null)

    // Call the method
    val result = runBlocking { firebaseAuthRepositoryFirestore.changePassword(newPassword) }

    // Assert failure result
    assert(result.isFailure)
    assert(result.exceptionOrNull()?.message == "User not authenticated")
  }

  @Test
  fun signOut_shouldCallFirebaseAuthSignOut() {
    // Mock FirebaseAuth to not throw an exception when signOut is called
    doNothing().`when`(mockAuth).signOut()

    // Call signOut
    var onSuccessCalled = false
    firebaseAuthRepositoryFirestore.signOut(
        onSuccess = { onSuccessCalled = true },
        onFailure = { fail("onFailure should not be called") })

    // Verify signOut was called and onSuccess was triggered
    verify(mockAuth).signOut()
    assertTrue(onSuccessCalled)
  }
}
