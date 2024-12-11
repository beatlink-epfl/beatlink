package com.epfl.beatlink.repository.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Class that tracks the network status and provides it to the UI.
 *
 * @param context The application context
 */
open class NetworkStatusTracker(context: Context) {

  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val _isConnected = MutableLiveData<Boolean>()
  val isConnected: LiveData<Boolean>
    get() = _isConnected

  private val networkCallback =
      object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
          _isConnected.postValue(true) // Network became available
        }

        override fun onLost(network: Network) {
          _isConnected.postValue(false) // Network lost
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
          val hasInternet =
              capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                  capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                  capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
          _isConnected.postValue(hasInternet) // Update based on capabilities
        }
      }

  // Add a protected getter for testing
  fun getNetworkCallback(): ConnectivityManager.NetworkCallback {
    return networkCallback
  }

  init {
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    _isConnected.value =
        capabilities?.let {
          it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
              it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
              it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    connectivityManager.registerDefaultNetworkCallback(networkCallback)
  }

  /** Unregisters the network callback. */
  fun unregisterCallback() {
    connectivityManager.unregisterNetworkCallback(networkCallback)
  }
}
