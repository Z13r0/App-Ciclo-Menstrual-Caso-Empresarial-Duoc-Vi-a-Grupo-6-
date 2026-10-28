package com.myciclo.app.registro

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// todas las fotos del patron se guardan dentro de la carpeta privada de la app
// asi no quedan en la galeria del celular, que es lo que pide el caso en privacidad
object Fotos {

    // carpeta interna donde van las fotos, la creamos la primera vez
    // es la misma ruta que declaramos en res/xml/file_paths.xml
    private fun carpeta(context: Context): File {
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    // un nombre distinto para cada foto usando la hora del sistema
    // si usaramos un nombre fijo cada foto nueva borraria la anterior
    fun archivoNuevo(context: Context): File {
        return File(carpeta(context), "patron_" + System.currentTimeMillis() + ".jpg")
    }

    // la camara no puede escribir directo en nuestra carpeta, necesita un uri del fileprovider
    // el authority tiene que ser igual al que pusimos en el manifest
    fun uriDe(context: Context, archivo: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)
    }

    // cuando la foto viene de la galeria la copiamos a nuestra carpeta
    // si guardaramos solo el uri de la galeria, al reiniciar la app se pierde el permiso y no carga
    fun copiarDesdeGaleria(context: Context, origen: Uri): File? {
        // abrimos la imagen que eligio la usuaria
        val entrada = context.contentResolver.openInputStream(origen) ?: return null
        // y la escribimos en un archivo nuestro
        val destino = archivoNuevo(context)
        return try {
            entrada.use { desde -> destino.outputStream().use { hacia -> desde.copyTo(hacia) } }
            destino
        } catch (_: Exception) {
            // si algo falla preferimos no guardar nada antes que dejar una ruta rota
            null
        }
    }
}
