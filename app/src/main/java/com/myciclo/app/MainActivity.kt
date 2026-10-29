package com.myciclo.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.myciclo.app.auth.AuthRepository
import com.myciclo.app.auth.AuthScreen
import com.myciclo.app.navegacion.AppNavegacion
import com.myciclo.app.notificaciones.NotificacionHelper
import com.myciclo.app.registro.RegistroDao
import com.myciclo.app.ui.theme.FondoFresco
import com.myciclo.app.ui.theme.MyCicloTheme
import com.myciclo.app.ui.theme.RosaPrimario
import kotlinx.coroutines.launch
import kotlin.text.compareTo

class MainActivity : ComponentActivity() {

    private lateinit var auth: AuthRepository

    // Estado de la UI (observable por Compose)
    private val autenticado = mutableStateOf(false)
    private val cargandoSesion = mutableStateOf(true)
    private val userId = mutableStateOf<String?>(null)
    private val restableciendoPassword = mutableStateOf(false)

    /**
     * Launcher para pedir el permiso POST_NOTIFICATIONS en runtime (obligatorio desde Android 13).
     * Si la usuaria lo concede, programamos el recordatorio diario.
     */
    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            NotificacionHelper.sincronizarNotificacion(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = AuthRepository(this)

        // Si la app se abrió desde el correo (deep link), procesamos el callback de inmediato
        if (intent?.data != null) {
            auth.acceptCallback(intent.data!!)
        }
        restableciendoPassword.value = auth.passwordResetPending()

        // Intentamos renovar la sesión con el refresh_token guardado
        lifecycleScope.launch {
            // si no hay supabase configurado la app entra en modo local en vez de
            // quedarse pegada en el login: el diario igual funciona porque se guarda
            // en room, que es lo que pide el caso. cuando se definan SUPABASE_URL y
            // SUPABASE_ANON_KEY al compilar, el inicio de sesion vuelve solo.
            if (!hayAuthConfigurada()) {
                // usamos el mismo id que la migracion, asi estos registros quedan
                // listos para que claimLegacyRecordsIfNeeded los reclame el dia que
                // alguien inicie sesion de verdad
                userId.value = RegistroDao.LEGACY_USER_ID
                autenticado.value = true
                cargandoSesion.value = false
                return@launch
            }

            val sesionValida = auth.restoreSession()
            // Solo consideramos autenticada si hay sesión Y no estamos en medio de un recovery
            autenticado.value = sesionValida && !restableciendoPassword.value
            userId.value = auth.currentUserId()

            // Si había registros locales antiguos (antes de la auth), los asociamos a esta cuenta
            if (autenticado.value && userId.value != null) {
                auth.claimLegacyRecordsIfNeeded()
            }
            cargandoSesion.value = false
        }

        // Canal de notificaciones (requerido desde Android 8)
        NotificacionHelper.crearCanal(this)
        pedirPermisoYProgramar()

        setContent {
            MyCicloTheme {
                when {
                    // Mientras renovamos la sesión: fondo de marca + indicador rosa
                    cargandoSesion.value -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(FondoFresco),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = RosaPrimario)
                        }
                    }

                    // Sesión válida → navegación principal (calendario, registro, etc.)
                    autenticado.value && userId.value != null -> {
                        AppNavegacion(
                            userId = userId.value!!,
                            onLogout = {
                                lifecycleScope.launch {
                                    auth.logout()
                                    // en modo local no hay sesion que cerrar: si dejaramos
                                    // autenticado en false la app se iria al login y ya no
                                    // se podria volver a entrar
                                    if (hayAuthConfigurada()) {
                                        autenticado.value = false
                                        userId.value = null
                                    }
                                }
                            }
                        )
                    }

                    // Sin sesión o en recovery → pantalla de autenticación
                    else -> {
                        AuthScreen(
                            repository = auth,
                            resetPassword = restableciendoPassword.value
                        ) {
                            // Callback cuando el login / cambio de contraseña fue exitoso
                            lifecycleScope.launch {
                                auth.claimLegacyRecordsIfNeeded()
                                userId.value = auth.currentUserId()
                                autenticado.value = userId.value != null
                                restableciendoPassword.value = false
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Se llama cuando la app ya estaba abierta y llega un nuevo Intent
     * (por ejemplo, el deep link de recovery mientras la app está en segundo plano).
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.data != null && auth.acceptCallback(intent.data!!)) {
            userId.value = auth.currentUserId()
            restableciendoPassword.value = auth.passwordResetPending()
            autenticado.value = userId.value != null && !restableciendoPassword.value
        }
    }

    /**
     * Dice si la autenticacion esta lista para usarse.
     * Las dos llaves entran al compilar como buildConfigField, asi que si vienen
     * vacias no hay servidor contra el cual iniciar sesion.
     */
    private fun hayAuthConfigurada(): Boolean =
        BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    /**
     * Pide el permiso de notificaciones si es necesario y programa el recordatorio diario.
     * En Android 12 e inferior el permiso no se pide en runtime.
     */
    private fun pedirPermisoYProgramar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    NotificacionHelper.sincronizarNotificacion(this)
                }
                else -> {
                    permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            NotificacionHelper.sincronizarNotificacion(this)
        }
    }
}