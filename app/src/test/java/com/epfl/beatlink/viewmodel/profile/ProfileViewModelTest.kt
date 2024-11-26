package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule() // Rule to allow LiveData testing

  private lateinit var viewModel: ProfileViewModel
  private lateinit var mockRepository: ProfileRepositoryFirestore

  @Before
  fun setUp() {
    // Setup the test coroutine dispatcher
    Dispatchers.setMain(StandardTestDispatcher())

    // Create mock repository
    mockRepository = mock(ProfileRepositoryFirestore::class.java)

    // Create the ViewModel with the mocked repository
    viewModel = ProfileViewModel(mockRepository)
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
    viewModel.fetchProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
    assertEquals(expectedProfile, actualProfile)
  }

  @Test
  fun `fetchProfile does not update profile when userId is null`() = runTest {
    // Arrange
    `when`(mockRepository.getUserId()).thenReturn(null)

    // Act
    viewModel.fetchProfile()

    // Assert
    assertEquals(null, viewModel.profile.value)
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
    viewModel.addProfile(newProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
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
    viewModel.addProfile(newProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
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
    viewModel.updateProfile(updatedProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
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
    viewModel.updateProfile(updatedProfile)

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
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

    viewModel.addProfile(existingProfile) // Adding an initial profile

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Act
    viewModel.deleteProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
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

    viewModel.addProfile(existingProfile) // Adding an initial profile

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Act
    viewModel.deleteProfile()

    // Advance time to let the coroutine run
    advanceUntilIdle()

    // Assert
    val actualProfile = viewModel.profile.value
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
        viewModel.uploadProfilePicture(mockContext, mockUri)
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
        viewModel.uploadProfilePicture(mockContext, mockUri)

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
    viewModel.handlePermissionResult(true, mockGalleryLauncher, mockContext)

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
      viewModel.handlePermissionResult(false, mockGalleryLauncher, mockContext)

      // Assert
      verify(mockGalleryLauncher, never()).launch(any()) // Ensure galleryLauncher is NOT called
      mockedToast.verify {
        Toast.makeText(eq(mockContext), eq("Permission denied"), eq(Toast.LENGTH_SHORT))
      }
      verify(toastMock).show() // Verify the Toast was displayed
    }
  }
}
