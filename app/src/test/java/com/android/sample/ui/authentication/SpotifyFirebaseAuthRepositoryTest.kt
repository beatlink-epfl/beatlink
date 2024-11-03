package com.epfl.beatlink.ui.authentication

import android.content.Context
import android.content.SharedPreferences
import com.epfl.beatlink.model.spotify.CLIENT_ID
import com.epfl.beatlink.model.spotify.REDIRECT_URI
import com.epfl.beatlink.model.spotify.SpotifyAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SpotifyFirebaseAuthRepositoryTest {

  @Mock private lateinit var context: Context

  @Mock private lateinit var client: OkHttpClient

  @Mock private lateinit var sharedPreferences: SharedPreferences

  @Mock private lateinit var editor: SharedPreferences.Editor

  private lateinit var repository: SpotifyAuthRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    client = mock()
    context = mock()
    editor = mock()

    repository = SpotifyAuthRepository(client)

    // Mock shared preferences behavior
    `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
    `when`(sharedPreferences.edit()).thenReturn(editor)
    `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
  }

  @Test
  fun `refreshAccessToken returns success on valid response`() = runBlocking {
    // Arrange
    val refreshToken = "valid_refresh_token"
    val accessToken = "new_access_token"
    val expiresIn = 3600
    val expiryTime = System.currentTimeMillis() + expiresIn * 1000

    // Create the mock JSON response with access_token and expires_in
    val jsonResponse =
        JSONObject().apply {
          put("access_token", accessToken)
          put("expires_in", expiresIn)
        }

    val response = mock<Response>()
    val responseBody = mock<ResponseBody>()
    val call = mock<Call>()

    whenever(response.isSuccessful).thenReturn(true)
    whenever(response.body).thenReturn(responseBody)
    whenever(responseBody.string()).thenReturn(jsonResponse.toString())
    whenever(call.execute()).thenReturn(response)

    // Use `doAnswer` to intercept and return the mocked `Call` regardless of request details
    doAnswer { call }.whenever(client).newCall(any())

    // Act
    val result = repository.refreshAccessToken(refreshToken, context)

    // Assert
    assertEquals(Result.success(Unit), result)
  }

  @Test
  fun `requestAccessToken returns success on valid response`() = runBlocking {
    // Arrange
    val code = "authorization_code"
    val codeVerifier = "valid_code_verifier"
    val accessToken = "new_access_token"
    val refreshToken = "new_refresh_token"
    val expiresIn = 3600
    val expiryTime = System.currentTimeMillis() + expiresIn * 1000

    // Mock the fetch function to return a valid code verifier
    whenever(repository.fetch(context, "code_verifier")).thenReturn(codeVerifier)

    // Create the mock JSON response with access_token, refresh_token, and expires_in
    val jsonResponse =
        JSONObject().apply {
          put("access_token", accessToken)
          put("refresh_token", refreshToken)
          put("expires_in", expiresIn)
        }

    val response = mock<Response>()
    val responseBody = mock<ResponseBody>()
    val call = mock<Call>()

    whenever(response.isSuccessful).thenReturn(true)
    whenever(response.body).thenReturn(responseBody)
    whenever(responseBody.string()).thenReturn(jsonResponse.toString())
    whenever(call.execute()).thenReturn(response)

    // Use `doAnswer` to intercept and return the mocked `Call` regardless of request details
    doAnswer { call }.whenever(client).newCall(any())

    // Act
    val result = repository.requestAccessToken(code, context)

    // Assert
    assertEquals(Result.success(Unit), result)
  }

  @Test
  fun `buildSpotifyAuthUrl generates correct URL`() {
    // Act
    val authUrl = repository.buildSpotifyAuthUrl(context)

    // Assert
    val expectedScope = "user-read-private user-read-email"
    assert(authUrl.contains("client_id=$CLIENT_ID"))
    assert(authUrl.contains("redirect_uri=$REDIRECT_URI"))
    assert(authUrl.contains("scope=$expectedScope"))
    assert(authUrl.contains("response_type=code"))
    assert(authUrl.contains("code_challenge_method=S256"))
  }

  @Test
  fun `clearAuthData clears shared preferences`() {
    // Act
    repository.clearAuthData(context)

    // Assert
    verify(editor).clear()
    verify(editor).apply()
  }

  @Test
  fun `getExpiryTime returns correct expiry time when present`() {
    // Arrange
    val expectedExpiryTime = System.currentTimeMillis() + 3600 * 1000 // 1 hour from now
    val expiryTimeString = expectedExpiryTime.toString()

    // Mock fetch to return the expiry time as a string
    whenever(repository.fetch(context, "expiry_time")).thenReturn(expiryTimeString)

    // Act
    val result = repository.getExpiryTime(context)

    // Assert
    assertEquals(expectedExpiryTime, result)
  }

  @Test
  fun `getExpiryTime returns null when expiry time is not present`() {
    // Arrange
    // Mock fetch to return null, simulating the absence of "expiry_time" in SharedPreferences
    whenever(repository.fetch(context, "expiry_time")).thenReturn(null)

    // Act
    val result = repository.getExpiryTime(context)

    // Assert
    assertEquals(null, result)
  }
}
