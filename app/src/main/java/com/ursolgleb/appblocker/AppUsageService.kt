package com.ursolgleb.appblocker

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStats
import android.provider.Settings
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat


class AppUsageService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 10000L // Verificar cada 5 segundos
    private val allowedApps = listOf("com.ursolgleb.appblocker", "com.android.chrome", "null")

    override fun onCreate() {
        super.onCreate()
        Log.d("AppUsageService", "Servicio creado")
        startForegroundServiceProperly()
        handler.post(checkAppRunnable)
    }

    // 🔹 1. Método para iniciar Foreground Service correctamente
    private fun startForegroundServiceProperly() {
        val channelId = "app_usage_service_channel"
        val channelName = "App Usage Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("App Blocker en ejecución")
            .setContentText("Monitoreando el uso de aplicaciones")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification) // Inicia el servicio en primer plano
    }

    // 🔹 2. Código mejorado para monitorear la app en primer plano usando `Handler`
    private val checkAppRunnable = object : Runnable {
        override fun run() {
            val currentApp = getForegroundApp(this@AppUsageService)
            Log.d("AppUsageService", "App en primer plano: $currentApp")

            if (!allowedApps.contains(currentApp.toString())) {
                Log.d("AppUsageService", "Bloqueando app: $currentApp")
                bringUserToHomeScreen()
            }

            handler.postDelayed(this, checkInterval) // Ejecutar de nuevo después de 5s
        }
    }

    // 🔹 3. Obtener la app en primer plano
    private fun getForegroundApp(context: Context): String? {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val usageStatsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time
        )

        return usageStatsList.maxByOrNull { it.lastTimeUsed }?.packageName
    }

    // 🔹 4. Llevar al usuario a la pantalla de inicio si usa una app bloqueada
    private fun bringUserToHomeScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        } else {
            Log.e("AppUsageService", "No se tienen permisos para superponer ventanas")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("AppUsageService", "Servicio destruido")
        handler.removeCallbacks(checkAppRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("AppUsageService", "555 La app fue cerrada desde la multitarea")

        val restartServiceIntent = Intent(applicationContext, BootReceiver::class.java)
        val restartServicePendingIntent = PendingIntent.getBroadcast(
            applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 5000, // Reiniciar después de 5 segundos
            restartServicePendingIntent
        )

        super.onTaskRemoved(rootIntent)
    }

}
