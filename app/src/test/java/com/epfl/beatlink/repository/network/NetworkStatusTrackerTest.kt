package com.epfl.beatlink.repository.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NetworkStatusTrackerTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData testing

  private lateinit var context: Context
  private lateinit var connectivityManager: ConnectivityManager
  private lateinit var networkStatusTracker: NetworkStatusTracker

  @Before
  fun setUp() {
    context = mockk(relaxed = true)
    connectivityManager = mockk(relaxed = true)

    every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager

    networkStatusTracker =
        spyk(NetworkStatusTracker(context)) // Spy allows access to protected methods
  }

  @After
  fun tearDown() {
    networkStatusTracker.unregisterCallback()
  }

  @Test
  fun `initial state should reflect active network capabilities`() {
    val networkCapabilities: NetworkCapabilities = mockk(relaxed = true)
    every { connectivityManager.activeNetwork } returns mockk()
    every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
    every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

    networkStatusTracker = NetworkStatusTracker(context) // Reinitialize to use mocked capabilities

    val observer = mockk<Observer<Boolean>>(relaxed = true)
    networkStatusTracker.isConnected.observeForever(observer)

    verify { observer.onChanged(true) } // Initial state is connected
  }

  @Test
  fun `onAvailable should post true to isConnected`() {
    val network: Network = mockk()
    val observer = mockk<Observer<Boolean>>(relaxed = true)

    networkStatusTracker.isConnected.observeForever(observer)

    networkStatusTracker.getNetworkCallback().onAvailable(network)

    verify { observer.onChanged(true) }
  }

  @Test
  fun `onLost should post false to isConnected`() {
    val network: Network = mockk()
    val observer = mockk<Observer<Boolean>>(relaxed = true)

    networkStatusTracker.isConnected.observeForever(observer)

    networkStatusTracker.getNetworkCallback().onLost(network)

    verify { observer.onChanged(false) }
  }

  @Test
  fun `onCapabilitiesChanged should post true if network has internet`() {
    val network: Network = mockk()
    val capabilities: NetworkCapabilities = mockk(relaxed = true)
    val observer = mockk<Observer<Boolean>>(relaxed = true)

    every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

    networkStatusTracker.isConnected.observeForever(observer)

    networkStatusTracker.getNetworkCallback().onCapabilitiesChanged(network, capabilities)

    verify { observer.onChanged(true) }
  }

  @Test
  fun `onCapabilitiesChanged should post false if network lacks internet`() {
    val network: Network = mockk()
    val capabilities: NetworkCapabilities = mockk(relaxed = true)
    val observer = mockk<Observer<Boolean>>(relaxed = true)

    every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
    every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
    every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false

    networkStatusTracker.isConnected.observeForever(observer)

    networkStatusTracker.getNetworkCallback().onCapabilitiesChanged(network, capabilities)

    verify { observer.onChanged(false) }
  }

  @Test
  fun `unregisterCallback should unregister network callback`() {
    networkStatusTracker.unregisterCallback()

    verify {
      connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
    }
  }
}
