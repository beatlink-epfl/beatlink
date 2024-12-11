package com.epfl.beatlink.viewmodel.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.epfl.beatlink.repository.network.NetworkStatusTracker

/**
 * ViewModel that provides the network status to the UI.
 *
 * @param networkStatusTracker The network status tracker to get the network status from
 */
open class NetworkViewModel(networkStatusTracker: NetworkStatusTracker) : ViewModel() {
  open val isConnected: LiveData<Boolean> = networkStatusTracker.isConnected
}
