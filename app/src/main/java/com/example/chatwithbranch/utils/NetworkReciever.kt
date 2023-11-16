package com.example.chatwithbranch.utils

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.chatwithbranch.R
import javax.inject.Inject

/**
 * BroadcastReceiver for monitoring network connectivity changes.
 * Displays an alert dialog when both mobile data and Wi-Fi are disconnected.
 */
class NetworkReceiver @Inject constructor() : BroadcastReceiver() {

    /**
     * Called when network connectivity changes. Displays an alert dialog if both mobile data and Wi-Fi are disconnected.
     *
     * @param context The context in which the receiver is running.
     * @param intent The intent being received.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.DISCONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.DISCONNECTED
            ) {
                showAlertDialog(context)
            }
        } catch (t: Throwable) {
            Log.e("Network", "Network Status Failed: ", t)
        }
    }

    /**
     * Displays an alert dialog with options to check mobile data or Wi-Fi settings.
     *
     * @param context The context in which the alert dialog is displayed.
     */
    private fun showAlertDialog(context: Context){
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_alert_circle)
            .setTitle("Internet Connection Alert")
            .setCancelable(false)
            .setMessage("Please Check Your Internet Connection")
            .setPositiveButton(
                "Mobile Data"
            ) { _, i ->
                try {
                    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
                    } else {
                        val i = Intent()
                        i.component = ComponentName("com.android.settings", "com.android.settings.Settings\$DataUsageSummaryActivity")
                        i
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(
                "Wifi"
            ){ _, i ->
                try {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .show()
    }
}