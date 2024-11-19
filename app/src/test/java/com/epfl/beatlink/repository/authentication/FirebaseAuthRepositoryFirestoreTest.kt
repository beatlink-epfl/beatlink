package com.epfl.beatlink.repository.authentication

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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
}
