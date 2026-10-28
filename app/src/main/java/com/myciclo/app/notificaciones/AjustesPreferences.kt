package com.myciclo.app.notificaciones

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestiona el almacenamiento persistente de la configuración de notificaciones con SharedPreferences.
 */
class AjustesPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("myciclo_prefs", Context.MODE_PRIVATE)

    var notificacionesActivadas: Boolean
        get() = prefs.getBoolean(KEY_ACTIVADAS, true)
        set(value) = prefs.edit().putBoolean(KEY_ACTIVADAS, value).apply()

    var horaRecordatorio: Int
        get() = prefs.getInt(KEY_HORA, 9)
        set(value) = prefs.edit().putInt(KEY_HORA, value).apply()

    var minutoRecordatorio: Int
        get() = prefs.getInt(KEY_MINUTO, 0)
        set(value) = prefs.edit().putInt(KEY_MINUTO, value).apply()

    companion object {
        private const val KEY_ACTIVADAS = "notif_activadas"
        private const val KEY_HORA = "notif_hora"
        private const val KEY_MINUTO = "notif_minuto"
    }
}
