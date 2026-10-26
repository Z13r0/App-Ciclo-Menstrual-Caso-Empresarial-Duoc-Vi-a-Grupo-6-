package com.myciclo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.myciclo.app.navegacion.AppNavegacion
import com.myciclo.app.notificaciones.NotificacionHelper
import com.myciclo.app.ui.theme.MyCicloTheme

class MainActivity : ComponentActivity() {

    // Launcher para pedir el permiso de notificaciones (Android 13+)
    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            NotificacionHelper.programarRecordatorioDiario(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear el canal de notificaciones
        NotificacionHelper.crearCanal(this)

        // Pedir permiso y programar el recordatorio
        pedirPermisoYProgramar()

        setContent {
            MyCicloTheme {
                AppNavegacion()
            }
        }
    }

    private fun pedirPermisoYProgramar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 o superior: hay que pedir el permiso en runtime
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    NotificacionHelper.programarRecordatorioDiario(this)
                }
                else -> {
                    permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 12 o inferior: no necesita permiso especial
            NotificacionHelper.programarRecordatorioDiario(this)
        }
    }
}