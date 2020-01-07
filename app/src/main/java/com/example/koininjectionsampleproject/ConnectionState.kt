package com.example.koininjectionsampleproject

import androidx.annotation.DrawableRes

enum class ConnectionState(val isConnected: Boolean, val message: String, @DrawableRes val drawableRes: Int) {
    NO_NETWORK(false, "No Network Found, Please turn on Wifi", R.drawable.ic_signal_wifi_off_black_48dp),
    NO_INTERNET(false, "Network Available, but no internet, Please check Settings", R.drawable.ic_signal_cellular_connected_no_internet_0_bar_black_48dp),
    CONNECTED(true, "Internet Available!", R.drawable.ic_signal_wifi_4_bar_black_48dp)
}