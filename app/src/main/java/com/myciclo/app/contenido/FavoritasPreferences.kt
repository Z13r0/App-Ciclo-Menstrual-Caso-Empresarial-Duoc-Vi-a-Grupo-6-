package com.myciclo.app.contenido

import android.content.Context
import android.content.SharedPreferences

/**
 * Almacena los IDs de las guías favoritas de forma persistente entre sesiones con SharedPreferences.
 */
class FavoritasPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("guias_favoritas_prefs", Context.MODE_PRIVATE)

    fun obtenerFavoritas(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_FAVORITAS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun guardarFavoritas(ids: Set<Int>) {
        val stringSet = ids.map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_FAVORITAS, stringSet).apply()
    }

    companion object {
        private const val KEY_FAVORITAS = "favoritas_ids"
    }
}
