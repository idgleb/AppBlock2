# 📱 AppBlocker

AppBlocker es una aplicación Android que **monitorea el uso de aplicaciones en tiempo real** y **bloquea automáticamente aquellas que no estén permitidas**, redirigiendo al usuario a la pantalla de inicio. Ideal para control parental o control de productividad personal.

## 🚀 Características

- 🧠 Detección en segundo plano de la app en primer plano usando `UsageStatsManager`
- 🚫 Bloqueo automático de apps no autorizadas
- ⚙️ Arranque automático tras reiniciar el dispositivo (`BOOT_COMPLETED`)
- 🛡️ Requiere permisos especiales:
  - Acceso a estadísticas de uso
  - Superposición de pantalla
  - Administrador de dispositivo
  - Permisos de servicio en primer plano y accesibilidad
- 🔋 Opciones para evitar cierre por optimización de batería
- 📲 Interfaz con botones para facilitar la configuración de permisos por parte del usuario

## 🧱 Tecnologías usadas

- Kotlin
- Android SDK (API 21+)
- `UsageStatsManager`, `AlarmManager`, `NotificationManager`
- `DevicePolicyManager`, `AccessibilityService` (estructura base)
- `BroadcastReceiver`, `ForegroundService`, `JobScheduler`

## 📦 Instalación y ejecución

### Requisitos

- Android Studio (Flamingo o superior)
- Mínimo SDK: 21 (Android 5.0)
- Target SDK: 33+

### Pasos

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tuusuario/appblocker.git
   ```
2. Abre el proyecto en Android Studio
3. Concede los permisos necesarios desde la aplicación:
   - Acceso de uso
   - Superposición de pantalla
   - Administrador del dispositivo
   - Ignorar optimización de batería
4. Ejecuta la app en tu dispositivo físico (recomendado) o emulador con soporte para UsageStats

## 🛡️ Permisos requeridos

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.BIND_DEVICE_ADMIN" />
<uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE" />
```

## ⚠️ Advertencias

- Algunas funciones pueden variar según la versión del sistema operativo o el fabricante del dispositivo
- En Android 10+ es necesario conceder acceso manualmente a estadísticas de uso en Configuración > Apps > Acceso especial
- Algunas marcas (como Xiaomi, Huawei, Vivo) requieren configuración adicional para permitir el arranque en segundo plano

## 👨‍💻 Autor

**Gleb Ursol**  
🛠️ Desarrollador Android / Linux Sysadmin  
📫 Contacto: idgleb646807@gmail.com

## 📝 Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo LICENSE para más detalles. 