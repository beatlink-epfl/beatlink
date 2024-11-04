package com.epfl.beatlink.model.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule() // Rule to allow LiveData testing

  private lateinit var viewModel: ProfileViewModel
  private lateinit var mockRepository: ProfileRepository

  @Before
  fun setUp() {
    // Setup the test coroutine dispatcher
    Dispatchers.setMain(StandardTestDispatcher())

    // Create mock repository
    mockRepository = mock(ProfileRepository::class.java)

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
    `when`(mockRepository.getProfile(userId)).thenReturn(expectedProfile)

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
}
