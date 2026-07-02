package com.kotlin.dvijaypatient.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ClassConnectionDetector(private val _context: Context) {
    //        if (_context != null)
//            ClassGlobal.showErrorDialog(_context, "No Internet Connection..!", null);
    val isConnectingToInternet: Boolean
        get() {
            val connectivity =
                _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }

            //        if (_context != null)
            //            ClassGlobal.showErrorDialog(_context, "No Internet Connection..!", null);
            return false
        }
}
