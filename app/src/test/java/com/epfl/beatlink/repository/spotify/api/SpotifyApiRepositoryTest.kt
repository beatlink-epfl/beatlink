package com.epfl.beatlink.repository.spotify.api

import android.content.SharedPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class SpotifyApiRepositoryTest {

  @Mock private lateinit var mockClient: OkHttpClient
  @Mock private lateinit var mockSharedPreferences: SharedPreferences

  private lateinit var repository: SpotifyApiRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    repository = SpotifyApiRepository(mockClient, mockSharedPreferences)
  }

  // === GET ===
  @Test
  fun `GET request success`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)
    val mockResponseBody = "{\"key\":\"value\"}".toResponseBody(null)

    whenever(mockResponse.isSuccessful).thenReturn(true)
    whenever(mockResponse.body).thenReturn(mockResponseBody)
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.get("test_endpoint")

    assertTrue(result.isSuccess)
    assertEquals("value", result.getOrNull()?.getString("key"))
  }

  @Test
  fun `GET request failure`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)

    whenever(mockResponse.isSuccessful).thenReturn(false)
    whenever(mockResponse.code).thenReturn(400)
    whenever(mockResponse.body).thenReturn("Error".toResponseBody(null))
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.get("test_endpoint")

    assertTrue(result.isFailure)
    assertEquals("API call failed with code 400", result.exceptionOrNull()?.message)
  }

  // === POST ===
  @Test
  fun `POST request success`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)
    val mockResponseBody = "{\"key\":\"value\"}".toResponseBody(null)
    val requestBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())

    whenever(mockResponse.isSuccessful).thenReturn(true)
    whenever(mockResponse.body).thenReturn(mockResponseBody)
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.post("test_endpoint", requestBody)

    assertTrue(result.isSuccess)
    assertEquals("value", result.getOrNull()?.getString("key"))
  }

  @Test
  fun `POST request failure`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)
    val requestBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())

    whenever(mockResponse.isSuccessful).thenReturn(false)
    whenever(mockResponse.code).thenReturn(400)
    whenever(mockResponse.body).thenReturn("Error".toResponseBody(null))
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.post("test_endpoint", requestBody)

    assertTrue(result.isFailure)
    assertEquals("API call failed with code 400", result.exceptionOrNull()?.message)
  }

  // === PUT ===
  @Test
  fun `PUT request success`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)
    val mockResponseBody = "{\"key\":\"value\"}".toResponseBody(null)
    val requestBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())

    whenever(mockResponse.isSuccessful).thenReturn(true)
    whenever(mockResponse.body).thenReturn(mockResponseBody)
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.put("test_endpoint", requestBody)

    assertTrue(result.isSuccess)
    assertEquals("value", result.getOrNull()?.getString("key"))
  }

  @Test
  fun `PUT request failure`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)
    val requestBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())

    whenever(mockResponse.isSuccessful).thenReturn(false)
    whenever(mockResponse.code).thenReturn(400)
    whenever(mockResponse.body).thenReturn("Error".toResponseBody(null))
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.put("test_endpoint", requestBody)

    assertTrue(result.isFailure)
    assertEquals("API call failed with code 400", result.exceptionOrNull()?.message)
  }

  // === DELETE ===
  @Test
  fun `DELETE request success`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)
    val mockResponseBody = "{\"key\":\"value\"}".toResponseBody(null)

    whenever(mockResponse.isSuccessful).thenReturn(true)
    whenever(mockResponse.body).thenReturn(mockResponseBody)
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.delete("test_endpoint", null)

    assertTrue(result.isSuccess)
    assertEquals("value", result.getOrNull()?.getString("key"))
  }

  @Test
  fun `DELETE request failure`() = runBlocking {
    whenever(mockSharedPreferences.getString("access_token", null)).thenReturn("test_token")

    val mockCall = mock(Call::class.java)
    val mockResponse = mock(Response::class.java)

    whenever(mockResponse.isSuccessful).thenReturn(false)
    whenever(mockResponse.code).thenReturn(400)
    whenever(mockResponse.body).thenReturn("Error".toResponseBody(null))
    whenever(mockCall.execute()).thenReturn(mockResponse)

    doAnswer { mockCall }.whenever(mockClient).newCall(any())

    val result = repository.delete("test_endpoint", null)

    assertTrue(result.isFailure)
    assertEquals("API call failed with code 400", result.exceptionOrNull()?.message)
  }
}
