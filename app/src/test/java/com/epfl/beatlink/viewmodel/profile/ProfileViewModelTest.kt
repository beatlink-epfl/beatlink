package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.utils.ImageUtils.handlePermissionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule() // Rule to allow LiveData testing

  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var mockRepository: ProfileRepositoryFirestore

  @Before
  fun setUp() {
    // Setup the test coroutine dispatcher
    Dispatchers.setMain(StandardTestDispatcher())

    // Create mock repository
    mockRepository = mock(ProfileRepositoryFirestore::class.java)

    // Create the ViewModel with the mocked repository
    profileViewModel = ProfileViewModel(mockRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset dispatcher to avoid affecting other tests
  }

  @Test
  fun `fetchProfile updates profile state when successful`() = runTest {
    // Arrange
    val userId = "testUserId"
    val expectedProfile =
        ProfileData(
            bio = "Sample bio",
            links = 5,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.fetchProfile(userId)).thenReturn(expectedProfile)

    // Act
    profileViewModel.fetchProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(expectedProfile, actualProfile)
  }

  @Test
  fun `fetchProfile does not update profile when userId is null`() = runTest {
    // Arrange
    `when`(mockRepository.getUserId()).thenReturn(null)

    // Act
    profileViewModel.fetchProfile()

    // Assert
    assertEquals(null, profileViewModel.profile.value)
  }

  @Test
  fun `addProfile updates profile state when successful`() = runTest {
    // Arrange
    val userId = "testUserId"
    val newProfile =
        ProfileData(
            bio = "New bio",
            links = 3,
            name = "Jane Doe",
            profilePicture = null,
            username = "janedoe")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.addProfile(userId, newProfile)).thenReturn(true)

    // Act
    profileViewModel.addProfile(newProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(newProfile, actualProfile)
  }

  @Test
  fun `addProfile does not update profile when operation fails`() = runTest {
    // Arrange
    val userId = "testUserId"
    val newProfile =
        ProfileData(
            bio = "New bio",
            links = 3,
            name = "Jane Doe",
            profilePicture = null,
            username = "janedoe")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.addProfile(userId, newProfile)).thenReturn(false)

    // Act
    profileViewModel.addProfile(newProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(null, actualProfile)
  }

  @Test
  fun `updateProfile updates profile state when successful`() = runTest {
    // Arrange
    val userId = "testUserId"
    val existingProfile =
        ProfileData(
            bio = "Existing bio",
            links = 3,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe")
    val updatedProfile = existingProfile.copy(bio = "Updated bio", name = "John Doe Updated")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.updateProfile(userId, updatedProfile)).thenReturn(true)

    // Act
    profileViewModel.updateProfile(updatedProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(updatedProfile, actualProfile)
  }

  @Test
  fun `updateProfile does not update profile when operation fails`() = runTest {
    // Arrange
    val userId = "testUserId"
    val updatedProfile =
        ProfileData(
            bio = "Existing bio",
            links = 3,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.updateProfile(userId, updatedProfile)).thenReturn(false)

    // Act
    profileViewModel.updateProfile(updatedProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(null, actualProfile)
  }

  @Test
  fun `deleteProfile clears profile state when successful`() = runTest {
    // Arrange
    val userId = "testUserId"
    val existingProfile =
        ProfileData(
            bio = "Existing bio",
            links = 3,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.addProfile(userId, existingProfile)).thenReturn(true)
    `when`(mockRepository.deleteProfile(userId)).thenReturn(true)

    profileViewModel.addProfile(existingProfile) // Adding an initial profile

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Act
    profileViewModel.deleteProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(null, actualProfile)
  }

  @Test
  fun `deleteProfile does not clear profile state when operation fails`() = runTest {
    // Arrange
    val userId = "testUserId"
    val existingProfile =
        ProfileData(
            bio = "Existing bio",
            links = 3,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.addProfile(userId, existingProfile)).thenReturn(true)
    `when`(mockRepository.deleteProfile(userId)).thenReturn(false)

    profileViewModel.addProfile(existingProfile) // Adding an initial profile

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Act
    profileViewModel.deleteProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = profileViewModel.profile.value
    assertEquals(existingProfile, actualProfile)
  }

  @Test
  fun `uploadProfilePicture should call repository uploadProfilePicture when userId is valid`() =
      runTest {
        // Arrange
        val mockContext = mock(Context::class.java)
        val mockUri = mock(Uri::class.java)
        val userId = "testUserId"

        `when`(mockRepository.getUserId()).thenReturn(userId)
        doNothing().`when`(mockRepository).uploadProfilePicture(any(), any(), eq(userId))

        // Act
        profileViewModel.uploadProfilePicture(mockContext, mockUri)
        runCurrent() // Ensure the coroutine block executes

        // Assert
        verify(mockRepository).getUserId()
        verify(mockRepository).uploadProfilePicture(mockUri, mockContext, userId)
      }

  @Test
  fun `uploadProfilePicture should not call repository uploadProfilePicture when userId is null`() =
      runTest {
        // Arrange
        val mockContext = mock(Context::class.java)
        val mockUri = mock(Uri::class.java)

        `when`(mockRepository.getUserId()).thenReturn(null)

        // Act
        profileViewModel.uploadProfilePicture(mockContext, mockUri)

        // Assert
        verify(mockRepository).getUserId()
        verify(mockRepository, never()).uploadProfilePicture(any(), any(), any())
      }

  @Suppress("UNCHECKED_CAST")
  @Test
  fun `handlePermissionResult launches gallery when permission is granted`() {
    // Arrange
    val mockGalleryLauncher =
        mock(ManagedActivityResultLauncher::class.java)
            as ManagedActivityResultLauncher<String, Uri?>
    val mockContext = mock(Context::class.java)

    // Act
    handlePermissionResult(true, mockGalleryLauncher, mockContext)

    // Assert
    verify(mockGalleryLauncher).launch("image/*") // Verify galleryLauncher was called
  }

  @Suppress("UNCHECKED_CAST")
  @Test
  fun `handlePermissionResult shows toast when permission is denied`() {
    // Arrange
    val mockGalleryLauncher =
        mock(ManagedActivityResultLauncher::class.java)
            as ManagedActivityResultLauncher<String, Uri?>
    val mockContext = mock(Context::class.java)

    // Mock Toast behavior
    val toastMock = mock(Toast::class.java)
    mockStatic(Toast::class.java).use { mockedToast ->
      whenever(Toast.makeText(eq(mockContext), eq("Permission denied"), eq(Toast.LENGTH_SHORT)))
          .thenReturn(toastMock)

      // Act
      handlePermissionResult(false, mockGalleryLauncher, mockContext)

      // Assert
      verify(mockGalleryLauncher, never()).launch(any()) // Ensure galleryLauncher is NOT called
      mockedToast.verify {
        Toast.makeText(eq(mockContext), eq("Permission denied"), eq(Toast.LENGTH_SHORT))
      }
      verify(toastMock).show() // Verify the Toast was displayed
    }
  }

  @Test
  fun `getUsername should return username when repository returns a username`() = runTest {
    // Arrange
    val userId = "testUserId"
    val expectedUsername = "testUsername"
    val onResult: (String?) -> Unit = mock()

    `when`(mockRepository.getUsername(userId)).thenReturn(expectedUsername)

    // Act
    val result = profileViewModel.getUsername(userId, onResult)
    advanceUntilIdle()

    // Assert
    verify(mockRepository).getUsername(userId) // Verify repository method was called
    verify(onResult).invoke(expectedUsername)
  }

  @Test
  fun `getUsername should return null and log an error when repository throws an exception`() =
      runTest {
        // Arrange
        val userId = "testUserId"
        val exception = RuntimeException("Error fetching username")
        val onResult: (String?) -> Unit = mock()

        `when`(mockRepository.getUsername(userId)).thenThrow(exception)

        // Act
        val result = profileViewModel.getUsername(userId, onResult)
        advanceUntilIdle()

        // Assert
        verify(mockRepository).getUsername(userId) // Verify repository method was called
        verify(onResult).invoke(null)
      }

  @Test
  fun `getUserIdByUsername should invoke onResult with user ID when repository returns user ID`() =
      runTest {
        val username = "testUsername"
        val expectedUserId = "testUserId"
        val onResult: (String?) -> Unit = mock() // Mock the callback

        // Mock repository behavior
        `when`(mockRepository.getUserIdByUsername(username)).thenReturn(expectedUserId)

        // Act
        profileViewModel.getUserIdByUsername(username, onResult)
        advanceUntilIdle() // Advance the coroutine execution

        // Assert
        verify(mockRepository).getUserIdByUsername(username)
        verify(onResult).invoke(expectedUserId)
      }

  @Test
  fun `getUserIdByUsername should invoke onResult with null when repository throws an exception`() =
      runTest {
        val username = "testUsername"
        val exception = RuntimeException("Firestore error")
        val onResult: (String?) -> Unit = mock() // Mock the callback

        // Mock repository behavior to throw an exception
        `when`(mockRepository.getUserIdByUsername(username)).thenThrow(exception)

        // Act
        profileViewModel.getUserIdByUsername(username, onResult)
        advanceUntilIdle() // Advance the coroutine execution

        // Assert
        verify(mockRepository).getUserIdByUsername(username)
        verify(onResult).invoke(null)
      }

  @Test
  fun `verifyUsername returns invalid result when username is too short`() = runTest {
    // Arrange
    val username = ""
    val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

    // Act
    profileViewModel.verifyUsername(username, onResult)
    advanceUntilIdle()

    // Assert
    verify(onResult)
        .invoke(
            ProfileViewModel.UsernameValidationResult.Invalid(
                "Username must be between 1 and 30 characters"))
  }

  @Test
  fun `verifyUsername returns invalid result when username is too long`() = runTest {
    // Arrange
    val username = "a".repeat(31)
    val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

    // Act
    profileViewModel.verifyUsername(username, onResult)
    advanceUntilIdle()

    // Assert
    verify(onResult)
        .invoke(
            ProfileViewModel.UsernameValidationResult.Invalid(
                "Username must be between 1 and 30 characters"))
  }

  @Test
  fun `verifyUsername returns invalid result when username contains invalid characters`() =
      runTest {
        // Arrange
        val username = "invalid@username" // Invalid character '@'
        val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

        // Act
        profileViewModel.verifyUsername(username, onResult)
        advanceUntilIdle()

        // Assert
        verify(onResult)
            .invoke(
                ProfileViewModel.UsernameValidationResult.Invalid(
                    "Username can only contain letters, numbers, dots and underscores"))
      }

  @Test
  fun `verifyUsername returns invalid result when username starts with a dot`() = runTest {
    // Arrange
    val username = ".invalidusername"
    val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

    // Act
    profileViewModel.verifyUsername(username, onResult)
    advanceUntilIdle()

    // Assert
    verify(onResult)
        .invoke(
            ProfileViewModel.UsernameValidationResult.Invalid(
                "Username cannot start or end with a dot"))
  }

  @Test
  fun `verifyUsername returns invalid result when username contains consecutive dots`() = runTest {
    // Arrange
    val username = "user..name"
    val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

    // Act
    profileViewModel.verifyUsername(username, onResult)
    advanceUntilIdle()

    // Assert
    verify(onResult)
        .invoke(
            ProfileViewModel.UsernameValidationResult.Invalid(
                "Username cannot have consecutive dots"))
  }

  @Test
  fun `verifyUsername returns invalid result when username contains more than two consecutive underscores`() =
      runTest {
        // Arrange
        val username = "user___name"
        val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

        // Act
        profileViewModel.verifyUsername(username, onResult)
        advanceUntilIdle()

        // Assert
        verify(onResult)
            .invoke(
                ProfileViewModel.UsernameValidationResult.Invalid(
                    "Username cannot have more than two consecutive underscores"))
      }

  @Test
  fun `verifyUsername returns invalid result when username is already taken`() = runTest {
    // Arrange
    val username = "takenUsername"
    val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

    // Mock the repository to return that the username is not available
    `when`(mockRepository.isUsernameAvailable(username)).thenReturn(false)

    // Act
    profileViewModel.verifyUsername(username, onResult)
    advanceUntilIdle()

    // Assert
    verify(onResult)
        .invoke(ProfileViewModel.UsernameValidationResult.Invalid("Username is already taken"))
  }

  @Test
  fun `verifyUsername returns valid result when username is valid and available`() = runTest {
    // Arrange
    val username = "valid_username"
    val onResult: (ProfileViewModel.UsernameValidationResult) -> Unit = mock()

    // Mock the repository to return that the username is available
    `when`(mockRepository.isUsernameAvailable(username)).thenReturn(true)

    // Act
    profileViewModel.verifyUsername(username, onResult)
    advanceUntilIdle()

    // Assert
    verify(onResult).invoke(ProfileViewModel.UsernameValidationResult.Valid)
  }

  @Test
  fun `test searchUsers updates LiveData with search results`(): Unit = runTest {
    // Arrange
    val query = "john"
    val mockUsers =
        listOf(
            ProfileData(
                bio = "Bio 1",
                links = 2,
                name = "John Doe",
                profilePicture = null,
                username = "john_doe",
                favoriteMusicGenres = listOf("Rock")),
            ProfileData(
                bio = "Bio 2",
                links = 3,
                name = "John Smith",
                profilePicture = null,
                username = "john_smith",
                favoriteMusicGenres = listOf("Pop")))
    `when`(mockRepository.searchUsers(query)).thenReturn(mockUsers)

    // Act
    profileViewModel.searchUsers(query)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val result = profileViewModel.searchResult.getOrAwaitValue() // Use a LiveData test helper
    assertNotNull(result)
    assertEquals(2, result.size)
    assert(result.containsAll(mockUsers))
  }

  @Test
  fun `test searchUsers updates LiveData with empty list when no matches found`() = runTest {
    // Arrange
    val query = "nonexistent"
    `when`(mockRepository.searchUsers(query)).thenReturn(emptyList())

    // Act
    profileViewModel.searchUsers(query)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val result = profileViewModel.searchResult.getOrAwaitValue()
    assertNotNull(result)
    assert(result.isEmpty())
  }

  @Test
  fun `test searchUsers updates LiveData with empty list on error`() = runTest {
    // Arrange
    val query = "john"
    `when`(mockRepository.searchUsers(query)).thenThrow(RuntimeException("Mocked error"))

    // Act
    profileViewModel.searchUsers(query)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val result = profileViewModel.searchResult.getOrAwaitValue()
    assertNotNull(result)
    assert(result.isEmpty())
  }

  @Test
  fun `selectSelectedUser updates selectedUserUserId`() {
    // Arrange
    val userId = "testUserId"

    // Act
    profileViewModel.selectSelectedUser(userId)

    // Assert
    assertEquals(userId, profileViewModel.selectedUserUserId.value)
  }

  @Test
  fun `unselectSelectedUser clears selectedUserUserId`() {
    // Arrange
    profileViewModel.selectSelectedUser("testUserId")

    // Act
    profileViewModel.unselectSelectedUser()

    // Assert
    assertEquals("", profileViewModel.selectedUserUserId.value)
  }

  @Test
  fun `unreadyProfile sets profileReady to false`() {
    // Arrange
    profileViewModel.unreadyProfile()

    // Assert
    assertEquals(false, profileViewModel.profileReady.value)
  }

  @Test
  fun `fetchUserProfile updates selectedUserProfile and profileReady`() = runTest {
    // Arrange
    val userId = "testUserId"
    val expectedProfile =
        ProfileData(
            bio = "Bio",
            links = 1,
            name = "Test User",
            profilePicture = null,
            username = "testuser")
    profileViewModel.selectSelectedUser(userId)
    `when`(mockRepository.fetchProfile(userId)).thenReturn(expectedProfile)

    // Act
    profileViewModel.fetchUserProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    assertEquals(expectedProfile, profileViewModel.selectedUserProfile.value)
    assertEquals(true, profileViewModel.profileReady.value)
  }

  @Test
  fun `loadSelectedUserProfilePicture calls repository loadProfilePicture with selected userId`() {
    // Arrange
    val userId = "testUserId"
    val onBitmapLoaded: (Bitmap?) -> Unit = mock()
    profileViewModel.selectSelectedUser(userId)

    doNothing().`when`(mockRepository).loadProfilePicture(eq(userId), any())

    // Act
    profileViewModel.loadSelectedUserProfilePicture(userId, onBitmapLoaded)

    // Assert
    verify(mockRepository).loadProfilePicture(eq(userId), any())
  }

  @Test
  fun `loadSelectedUserProfilePicture does not call repository when selected userId is null`() {
    // Arrange
    val onBitmapLoaded: (Bitmap?) -> Unit = mock()

    // Act
    profileViewModel.loadSelectedUserProfilePicture(null, onBitmapLoaded)

    // Assert
    verify(mockRepository, never()).loadProfilePicture(any(), any())
  }

  @Test
  fun `profileReady default value is false`() {
    // Assert
    assertEquals(false, profileViewModel.profileReady.value)
  }

  @Test
  fun `selectedUserUserId default value is empty string`() {
    // Assert
    assertEquals("", profileViewModel.selectedUserUserId.value)
  }

  @Test
  fun `selectedUserProfile default value is initialProfile`() {
    // Assert
    assertEquals(null, profileViewModel.selectedUserProfile.value)
  }
}

fun <T> LiveData<T>.getOrAwaitValue(): T {
  var data: T? = null
  val latch = CountDownLatch(1)

  val observer =
      object : Observer<T> {
        override fun onChanged(value: T) {
          data = value
          latch.countDown()
          this@getOrAwaitValue.removeObserver(this)
        }
      }

  this.observeForever(observer)
  latch.await(2, TimeUnit.SECONDS)

  @Suppress("UNCHECKED_CAST") return data as T
}
