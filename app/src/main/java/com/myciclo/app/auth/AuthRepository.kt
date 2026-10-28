package com.myciclo.app.auth

import android.content.Context
import android.net.Uri
import com.myciclo.app.BuildConfig
import com.myciclo.app.data.AppDatabase
import java.util.Locale

sealed interface AuthResult {
    data class Success(val userId: String? = null, val message: String = "") : AuthResult
    data class Error(val message: String) : AuthResult
}

class AuthRepository(context: Context) {
    private val appContext = context.applicationContext
    private val store = SecureSessionStore(appContext)

    fun hasSession(): Boolean = !store.accessToken().isNullOrBlank()
    fun currentUserId(): String? = store.userId()
    fun passwordResetPending(): Boolean = store.passwordResetPending()
    fun clearPasswordResetPending() = store.clearPasswordResetPending()
    fun clearSession() = store.clear()

    /**
     * Procesa el deep link que llega desde el correo de confirmación o recuperación.
     * Soporta tanto query parameters como fragment (#access_token=...).
     */
    fun acceptCallback(uri: Uri): Boolean {
        val access = uri.getQueryParameter("access_token") ?: uri.fragmentParams()["access_token"]
        val refresh = uri.getQueryParameter("refresh_token") ?: uri.fragmentParams()["refresh_token"]
        if (access.isNullOrBlank() || refresh.isNullOrBlank()) return false

        if (uri.getQueryParameter("type") == "recovery" || uri.fragmentParams()["type"] == "recovery") {
            store.markPasswordResetPending()
        }
        store.save(access, refresh, decodeJwtSubject(access))
        return true
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        val validation = validate(email, password)
        if (validation != null) return AuthResult.Error(validation)

        return runCatching {
            AuthClient.api.signUp(
                BuildConfig.SUPABASE_ANON_KEY,
                SignupRequest(
                    email = email.trim().lowercase(Locale.ROOT),
                    password = password,
                    options = SignupOptions(AuthConfig.redirectUrl)
                )
            )
        }.fold(
            onSuccess = {
                AuthResult.Success(
                    userId = it.user?.id,
                    message = "Revisa tu correo para activar tu cuenta antes de iniciar sesión."
                )
            },
            onFailure = {
                AuthResult.Error("No fue posible crear la cuenta. Revisa tu conexión e inténtalo nuevamente.")
            }
        )
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        val validation = validate(email, password)
        if (validation != null) return AuthResult.Error(validation)

        return runCatching {
            AuthClient.api.signIn(
                BuildConfig.SUPABASE_ANON_KEY,
                email.trim().lowercase(Locale.ROOT),
                password
            )
        }.fold(
            onSuccess = { response ->
                if (response.access_token.isNullOrBlank() || response.refresh_token.isNullOrBlank()) {
                    AuthResult.Error("No se pudo iniciar sesión. Confirma tu correo y revisa tus credenciales.")
                } else {
                    store.save(
                        response.access_token,
                        response.refresh_token,
                        response.user?.id ?: decodeJwtSubject(response.access_token)
                    )
                    AuthResult.Success(response.user?.id, "Sesión iniciada")
                }
            },
            onFailure = {
                // Mensaje genérico a propósito: no revelamos si el correo existe o no
                AuthResult.Error("No se pudo iniciar sesión. Confirma tu correo y revisa tus credenciales.")
            }
        )
    }

    /**
     * Siempre devolvemos el mismo mensaje de éxito para no filtrar si el correo está registrado.
     */
    suspend fun recover(email: String): AuthResult {
        return runCatching {
            AuthClient.api.recover(
                BuildConfig.SUPABASE_ANON_KEY,
                mapOf(
                    "email" to email.trim().lowercase(Locale.ROOT),
                    "redirect_to" to AuthConfig.redirectUrl
                )
            )
            AuthResult.Success(
                message = "Si el correo está registrado, recibirás instrucciones para recuperar tu contraseña."
            )
        }.getOrElse {
            AuthResult.Success(
                message = "Si el correo está registrado, recibirás instrucciones para recuperar tu contraseña."
            )
        }
    }

    suspend fun restoreSession(): Boolean {
        val access = store.accessToken()
        val refresh = store.refreshToken()
        if (access.isNullOrBlank() || refresh.isNullOrBlank()) return false

        return runCatching {
            val response = AuthClient.api.refresh(BuildConfig.SUPABASE_ANON_KEY, refresh)
            val newAccess = response.access_token ?: return false
            val newRefresh = response.refresh_token ?: refresh
            store.save(
                newAccess,
                newRefresh,
                response.user?.id ?: store.userId() ?: decodeJwtSubject(newAccess)
            )
            true
        }.getOrDefault(false)
    }

    suspend fun updatePassword(newPassword: String): AuthResult {
        if (newPassword.length < 8) {
            return AuthResult.Error("La contraseña debe tener al menos 8 caracteres.")
        }
        val access = store.accessToken()
            ?: return AuthResult.Error("La sesión expiró. Inicia sesión nuevamente.")

        return runCatching {
            AuthClient.api.updateUser(
                BuildConfig.SUPABASE_ANON_KEY,
                "Bearer $access",
                mapOf("password" to newPassword)
            )
            AuthResult.Success(message = "Contraseña actualizada correctamente.")
        }.getOrElse {
            AuthResult.Error("No se pudo actualizar la contraseña.")
        }
    }

    suspend fun logout() {
        val access = store.accessToken()
        runCatching {
            if (!access.isNullOrBlank()) {
                AuthClient.api.logout(BuildConfig.SUPABASE_ANON_KEY, "Bearer $access")
            }
        }
        store.clear()
    }

    /**
     * Si la usuaria tenía registros antiguos (antes de la autenticación),
     * los asociamos una sola vez a su userId actual.
     */
    suspend fun claimLegacyRecordsIfNeeded() {
        val userId = currentUserId() ?: return
        if (store.legacyClaimed()) return
        AppDatabase.obtener(appContext).registroDao()
            .reclamarRegistrosHeredados(userId)
        store.markLegacyClaimed()
    }

    private fun validate(email: String, password: String): String? {
        if (!AuthConfig.enabled) {
            return "La autenticación aún no está configurada. Define SUPABASE_URL y SUPABASE_ANON_KEY."
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Ingresa un correo válido."
        }
        if (password.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres."
        }
        return null
    }

    private fun decodeJwtSubject(token: String): String? = runCatching {
        val payload = token.split('.')[1]
        val json = String(
            android.util.Base64.decode(
                payload,
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
            )
        )
        Regex("\\\"sub\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"")
            .find(json)
            ?.groupValues
            ?.get(1)
    }.getOrNull()
}

private fun Uri.fragmentParams(): Map<String, String> =
    fragment.orEmpty()
        .split('&')
        .mapNotNull {
            val p = it.split('=', limit = 2)
            if (p.size == 2) p[0] to Uri.decode(p[1]) else null
        }
        .toMap()