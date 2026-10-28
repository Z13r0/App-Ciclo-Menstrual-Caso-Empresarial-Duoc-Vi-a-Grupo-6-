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
 * Soporta configuración personalizada de hora y activación/desactivación dinámica.
 */
object NotificacionHelper {

    const val CANAL_ID = "eva_recordatorio"
    const val NOTIF_ID = 1001

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
     * Sincroniza la alarma con las preferencias guardadas por el usuario.
     */
    fun sincronizarNotificacion(context: Context) {
        val prefs = AjustesPreferences(context)
        if (prefs.notificacionesActivadas) {
            programarRecordatorioDiario(context, prefs.horaRecordatorio, prefs.minutoRecordatorio)
        } else {
            cancelarRecordatorio(context)
        }
    }

    /**
     * Programa una alarma que se repite todos los días a la hora y minuto configurados.
     * Si la hora de hoy ya pasó, la programa para mañana.
     */
    fun programarRecordatorioDiario(
        context: Context,
        hora: Int = 9,
        minuto: Int = 0
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RecordatorioReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
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

    /** Cancela el recordatorio diario programado en AlarmManager. */
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
