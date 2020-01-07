package com.example.koininjectionsampleproject

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

class AppConnectivityManager(context: Context, pollInterval: Long = 1_000L) {
    private val connected = AtomicBoolean(false)
    private val mConnectedState: MutableLiveData<ConnectionState> = MutableLiveData()
    val connectionState: LiveData<ConnectionState> = mConnectedState
    private val connectivityService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connected.set(true)
            mConnectedState.postValue(ConnectionState.CONNECTED)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            connected.set(false)
        }
    }

    init {
        connectivityService.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
        (CoroutineScope(Dispatchers.Default)).launch {
            pollConnectionState(pollInterval).collect {
                mConnectedState.postValue(it)
            }
        }
    }

    fun isConnected(): Boolean {
        return connected.get()
    }

    /**
     * Attempt to open a connection to Google 8.8.8.8 DNS server
     */
    fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val inetSocketAddress = InetSocketAddress("8.8.8.8", 53)

            sock.connect(inetSocketAddress, timeoutMs)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun pollConnectionState(interval: Long): Flow<ConnectionState> {
        return flow {
            while (true){
                val toEmit = when {
                    !isConnected() -> ConnectionState.NO_NETWORK
                    !isInternetAvailable() -> ConnectionState.NO_INTERNET
                    else -> ConnectionState.CONNECTED
                }
                emit(toEmit)
                delay(interval)
            }
        }
    }

}