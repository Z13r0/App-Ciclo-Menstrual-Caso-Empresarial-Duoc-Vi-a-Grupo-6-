package com.myciclo.app.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.myciclo.app.MainActivity

/**
 * Recibe la alarma programada y muestra la notificación diaria.
 * El texto recuerda observar EVA y deja claro que es solo autoconocimiento.
 */
class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Al tocar la notificación se abre la app
        val openApp = Intent(context, MainActivity::class.java)
        val pending = android.app.PendingIntent.getActivity(
            context,
            0,
            openApp,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                    android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, NotificacionHelper.CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass) // puedes cambiarlo por un ícono propio
            .setContentTitle("Recordatorio myCiclo")
            .setContentText("¿Ya observaste el patrón en EVA hoy? Regístralo en la bitácora.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "¿Ya observaste el patrón en EVA hoy? Regístralo en la bitácora. " +
                                "EVA es una herramienta de autoconocimiento, no un método anticonceptivo."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        // En Android 13+ se necesita el permiso POST_NOTIFICATIONS
        try {
            NotificationManagerCompat.from(context)
                .notify(NotificacionHelper.NOTIF_ID, notificacion)
        } catch (e: SecurityException) {
            // Permiso no concedido; se ignora para no crashear
        }
    }
}