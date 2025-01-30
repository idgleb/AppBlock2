package com.ursolgleb.appblocker

import android.accessibilityservice.AccessibilityService
import android.app.AppOpsManager
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //scheduleRestartService(this)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, DeviceAdminReceiver::class.java)

        val enableAdminButton: Button = findViewById(R.id.enable_admin_button)
        enableAdminButton.setOnClickListener {
            if (devicePolicyManager.isAdminActive(compName)) {
                Toast.makeText(this, "Permisos de administrador ya habilitados.", Toast.LENGTH_SHORT).show()
            } else {
                solicitarPermisoAdmin(this)
            }

        }

        val openAutoStartSettingsButton: Button = findViewById(R.id.openAutoStartSettingsButton)
        openAutoStartSettingsButton.setOnClickListener {
            openAutoStartSettings(this)
        }

        val accesoDeUsoButton: Button = findViewById(R.id.accesoDeUsoButton)
        accesoDeUsoButton.setOnClickListener {
            if (!isUsageAccessGranted(this)) {
                solicitarPermisoAccesoUso(this)
            }
        }

        val canDrawOverlaysButton: Button = findViewById(R.id.canDrawOverlaysButton)
        canDrawOverlaysButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                solisitarPermisoSuperponerVentanas(this)
            }
        }

        val requestIgnoreBatteryOptimizationsButton: Button = findViewById(R.id.requestIgnoreBatteryOptimizationsButton)
        requestIgnoreBatteryOptimizationsButton.setOnClickListener {
            requestIgnoreBatteryOptimizations(this)
        }

        val openManufacturerSettingsButton: Button = findViewById(R.id.openManufacturerSettingsButton)
        openManufacturerSettingsButton.setOnClickListener {
            openManufacturerSettings(this)
        }

        val requestAccessibilityPermissionButton: Button = findViewById(R.id.requestAccessibilityPermissionButton)
        requestAccessibilityPermissionButton.setOnClickListener {
            //
        }

        // Iniciar el servicio en segundo plano
        val serviceAppUsageIntent = Intent(this, AppUsageService::class.java)
        startService(serviceAppUsageIntent)

    }

    private fun solicitarPermisoAdmin(context: Context) {
        if (devicePolicyManager.isAdminActive(compName)) {
            Toast.makeText(this, "Permisos de administrador ya habilitados.", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Necesitamos permisos de administrador.")
            }
            context.startActivity(intent)
        }
    }

    private fun solisitarPermisoSuperponerVentanas(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        context.startActivity(intent)
    }

    fun solicitarPermisoAccesoUso(context: Context) {
        val intent = Intent(
            Settings.ACTION_USAGE_ACCESS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        context.startActivity(intent)
    }

    fun openAutoStartSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        context.startActivity(intent)
    }

    fun requestIgnoreBatteryOptimizations(context: Context) {
        showBatteryOptimizationDialog(context)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,
                    Uri.parse("package:" + context.packageName)
                )
                // Verificar si existe una actividad que pueda manejar el Intent
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    openBatterySettings(context) // Abrir configuración general de batería si no está disponible
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            openBatterySettings(context) // Como alternativa, abre la configuración de batería general
        }
    }

    fun openBatterySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            openAppSettings(context) // Abrir configuración de la aplicación si no está disponible
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context.packageName))
        context.startActivity(intent)
    }



    fun openManufacturerSettings(context: Context) {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            when {
                manufacturer.equals("xiaomi", ignoreCase = true) -> {
                    intent.setComponent(
                        android.content.ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                    )
                }
                manufacturer.equals("huawei", ignoreCase = true) -> {
                    intent.setComponent(
                        android.content.ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.process.ProtectActivity"
                        )
                    )
                }
                manufacturer.equals("oppo", ignoreCase = true) -> {
                    intent.setComponent(
                        android.content.ComponentName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                        )
                    )
                }
                manufacturer.equals("vivo", ignoreCase = true) -> {
                    intent.setComponent(
                        android.content.ComponentName(
                            "com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                        )
                    )
                }
                manufacturer.equals("samsung", ignoreCase = true) -> {
                    intent.setComponent(
                        android.content.ComponentName(
                            "com.samsung.android.lool",
                            "com.samsung.android.sm.ui.battery.BatteryActivity"
                        )
                    )
                }
                else -> {
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.parse("package:" + context.packageName)
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        }
    }


    fun isUsageAccessGranted(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun showBatteryOptimizationDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Evitar Cierre de la App")
            .setMessage("Para asegurarte de que esta app funcione correctamente, desactiva la optimización de batería.")
            .setPositiveButton("Configurar") { _, _ ->
                requestIgnoreBatteryOptimizations(context)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun scheduleRestartService(context: Context) {
        val componentName = ComponentName(context, BootReceiver::class.java)
        val jobInfo = JobInfo.Builder(123, componentName)
            .setPersisted(true) // Mantiene el servicio incluso después de reiniciar
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // Solo requiere cualquier red
            .build()

        val jobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(jobInfo)
    }




}
