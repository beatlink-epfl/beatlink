package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.net.Uri
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
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

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

  @Test
  fun `getUsername should return username when repository returns a username`() = runTest {
    // Arrange
    val userId = "testUserId"
    val expectedUsername = "testUsername"
    val onResult: (String?) -> Unit = mock()

    `when`(mockRepository.getUsername(userId)).thenReturn(expectedUsername)

    // Act
    val result = viewModel.getUsername(userId, onResult)
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
        val result = viewModel.getUsername(userId, onResult)
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
        viewModel.getUserIdByUsername(username, onResult)
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
        viewModel.getUserIdByUsername(username, onResult)
        advanceUntilIdle() // Advance the coroutine execution

        // Assert
        verify(mockRepository).getUserIdByUsername(username)
        verify(onResult).invoke(null)
      }
}
