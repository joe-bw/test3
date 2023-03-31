/**
 * Create by jhong on 2022. 4. 1.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.utils

import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.sorizava.asrplayer.extension.connectivityManager
import javax.inject.Inject

/**
 * Injectable class which returns information about the network connection state.
 */
class NetworkHandler (private val context: Context): NetworkConnectivity {
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.connectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}

interface NetworkConnectivity {
    fun isNetworkAvailable(): Boolean
}
