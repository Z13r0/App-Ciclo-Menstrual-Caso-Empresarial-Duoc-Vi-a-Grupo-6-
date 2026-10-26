package com.myciclo.app.notificaciones

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * Gestiona el canal de notificaciones y programa el recordatorio diario
 * para observar EVA (exigencia del caso empresarial).
 *
 * La notificación se muestra todos los días a las 09:00.
 * El diario nunca se envía a internet; solo se muestra un recordatorio local.
 */
object NotificacionHelper {

    const val CANAL_ID = "eva_recordatorio"
    const val NOTIF_ID = 1001
    const val HORA_RECORDATORIO = 9   // 09:00 AM
    const val MINUTO_RECORDATORIO = 0

    /** Crea el canal de notificaciones (obligatorio desde Android 8). */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorio EVA",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorio diario para observar el patrón en EVA"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    /**
     * Programa una alarma que se repite todos los días a la hora configurada.
     * Si la hora de hoy ya pasó, programa para mañana.
     */
    fun programarRecordatorioDiario(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RecordatorioReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, HORA_RECORDATORIO)
            set(Calendar.MINUTE, MINUTO_RECORDATORIO)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pending
        )
    }

    /** Cancela el recordatorio diario (por si más adelante agregan un switch en Configuración). */
    fun cancelarRecordatorio(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RecordatorioReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }
}