package com.ursolgleb.appblocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "El dispositivo se ha reiniciado, iniciando servicio...")

            context?.let {
                val serviceIntent = Intent(it, AppUsageService::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.startForegroundService(serviceIntent) // Android 8+ requiere esto
                } else {
                    it.startService(serviceIntent) // Versiones anteriores usan startService
                }
            }
        }
    }
}


