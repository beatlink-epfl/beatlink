package com.epfl.beatlink.repository.authentication

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.repository.profile.ProfileData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
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

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
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

    // Set up Firestore collection and document
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)

    firebaseAuthRepositoryFirestore = FirebaseAuthRepositoryFirestore(mockFirestore, mockAuth)
  }

  @Test
  fun signUp_shouldCallCreateUserWithEmailAndPassword() {
    `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(Tasks.forResult(mockAuthResult))
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    firebaseAuthRepositoryFirestore.signUp(
        email = "test@example.com",
        password = "password123",
        username = "testUser",
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that createUserWithEmailAndPassword is called
    verify(mockAuth).createUserWithEmailAndPassword("test@example.com", "password123")
  }

  @Test
  fun signUp_shouldCallAddUsernameOnSuccess() {
    `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(Tasks.forResult(mockAuthResult))
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    firebaseAuthRepositoryFirestore.signUp(
        email = "test@example.com",
        password = "password123",
        username = "testUser",
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that Firestore's set method is called on the document
    verify(mockDocumentReference).set(any())
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
  fun addUsername_shouldSetProfileDataInFirestore() {
    val profileData =
        ProfileData(
            username = "testUser", name = null, bio = null, links = 0, profilePicture = null)

    `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(Tasks.forResult(mockAuthResult))
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    firebaseAuthRepositoryFirestore.signUp(
        email = "test@example.com",
        password = "password123",
        username = "testUser",
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that set is called with the correct ProfileData
    verify(mockDocumentReference).set(profileData)
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
