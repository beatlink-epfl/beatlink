import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.spotify.SpotifyAuthRepository
import com.epfl.beatlink.ui.authentication.AuthState
import com.epfl.beatlink.ui.authentication.SpotifyAuthViewModel
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
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SpotifyFirebaseAuthViewModelTest {

  @get:Rule
  val instantExecutorRule = InstantTaskExecutorRule() // Allows LiveData to update instantly

  @Mock private lateinit var application: Application

  @Mock private lateinit var context: Context

  @Mock private lateinit var repository: SpotifyAuthRepository

  private lateinit var viewModel: SpotifyAuthViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    MockitoAnnotations.openMocks(this)
    viewModel = SpotifyAuthViewModel(application, repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `initial authState is Idle when no token exists`() {
    // Arrange: Set repository to return nulls for initial tokens
    `when`(repository.getAccessToken(context)).thenReturn(null)
    `when`(repository.getRefreshToken(context)).thenReturn(null)
    `when`(repository.getExpiryTime(context)).thenReturn(null)

    // Act
    viewModel.loadTokens(context)
    viewModel.loadAuthState()

    // Assert
    assertEquals(AuthState.Idle, viewModel.authState.value)
  }

  @Test
  fun `authState is Authenticated if access token exists and is valid`() =
      runTest(testDispatcher) {
        // Arrange: Set up repository to return a valid token with a future expiry time
        `when`(repository.getAccessToken(context)).thenReturn("valid_access_token")
        `when`(repository.getRefreshToken(context)).thenReturn("valid_refresh_token")
        `when`(repository.getExpiryTime(context)).thenReturn(System.currentTimeMillis() + 10_000)

        // Act
        viewModel.loadTokens(context)
        viewModel.loadAuthState()

        // Assert
        assertEquals(AuthState.Authenticated, viewModel.authState.value)
      }

  @Test
  fun `authState becomes Authenticated on successful access token refresh`() = runTest {
    // Arrange: Set up initial state for the ViewModel directly
    viewModel._refreshToken.value = "valid_refresh_token" // Set initial refresh token directly
    `when`(repository.refreshAccessToken("valid_refresh_token", context))
        .thenReturn(Result.success(Unit))

    // Act
    viewModel.refreshAccessToken(context)

    advanceUntilIdle()

    // Assert: Verify authState is set to Authenticated
    assertEquals(AuthState.Authenticated, viewModel.authState.value)
  }

  @Test
  fun `clearAuthData sets authState to Idle`() {
    // Act: Clear authentication data
    viewModel.clearAuthData(context)

    // Assert
    verify(repository).clearAuthData(context)
    assertEquals(AuthState.Idle, viewModel.authState.value)
  }

  @Test
  fun `handleAuthorizationResponse sets authState to Authenticated on success`() =
      runTest(testDispatcher) {
        val intent = mock(Intent::class.java)
        val uri = mock(Uri::class.java)

        // Set up intent and URI for a successful response
        `when`(intent.data).thenReturn(uri)
        `when`(uri.getQueryParameter("code")).thenReturn("auth_code")
        `when`(repository.requestAccessToken("auth_code", context)).thenReturn(Result.success(Unit))

        // Act
        viewModel.handleAuthorizationResponse(intent, context)

        advanceUntilIdle()

        // Assert
        assertEquals(AuthState.Authenticated, viewModel.authState.value)
      }

  @Test
  fun `handleAuthorizationResponse sets authState to Idle on failure`() =
      runTest(testDispatcher) {
        val intent = mock(Intent::class.java)
        val uri = mock(Uri::class.java)

        // Mock the intent and URI to simulate an error in the authorization code retrieval
        `when`(intent.data).thenReturn(uri)
        `when`(uri.getQueryParameter("code")).thenReturn(null) // No code

        // Act
        viewModel.handleAuthorizationResponse(intent, context)

        // Assert
        assertEquals(AuthState.Idle, viewModel.authState.value)
      }
}
