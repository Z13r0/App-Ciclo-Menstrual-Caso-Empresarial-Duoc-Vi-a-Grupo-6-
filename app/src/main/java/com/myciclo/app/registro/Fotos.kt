package com.myciclo.app.registro

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object Fotos {

    private fun carpeta(context: Context): File {
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun archivoNuevo(context: Context): File {
        return File(carpeta(context), "patron_" + System.currentTimeMillis() + ".jpg")
    }

    fun uriDe(context: Context, archivo: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            archivo
        )
    }

    fun copiarDesdeGaleria(context: Context, origen: Uri): File? {
        val entrada = context.contentResolver.openInputStream(origen) ?: return null
        val destino = archivoNuevo(context)
        return try {
            entrada.use { desde ->
                destino.outputStream().use { hacia ->
                    desde.copyTo(hacia)
                }
            }
            destino
        } catch (_: Exception) {
            null
        }
    }
}