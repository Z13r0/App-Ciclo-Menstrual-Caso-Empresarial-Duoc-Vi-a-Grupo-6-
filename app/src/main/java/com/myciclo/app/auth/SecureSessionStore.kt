package com.myciclo.app.auth

import android.content.Context
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Almacena access_token, refresh_token y userId de forma cifrada.
 * Usa AES-GCM con una clave no exportable del Android Keystore.
 * Nunca guarda credenciales en texto plano.
 */
class SecureSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("secure_auth_session", Context.MODE_PRIVATE)
    private val keyAlias = "myciclo_auth_aes"

    fun save(accessToken: String, refreshToken: String, userId: String? = null) {
        prefs.edit()
            .putString(KEY_ACCESS, encrypt(accessToken))
            .putString(KEY_REFRESH, encrypt(refreshToken))
            .apply {
                if (!userId.isNullOrBlank()) {
                    putString(KEY_USER, encrypt(userId))
                }
            }
            .apply()
    }

    fun accessToken(): String? = prefs.getString(KEY_ACCESS, null)?.let(::decrypt)
    fun refreshToken(): String? = prefs.getString(KEY_REFRESH, null)?.let(::decrypt)
    fun userId(): String? = prefs.getString(KEY_USER, null)?.let(::decrypt)

    fun legacyClaimed(): Boolean = prefs.getBoolean(KEY_LEGACY_CLAIMED, false)
    fun markLegacyClaimed() = prefs.edit().putBoolean(KEY_LEGACY_CLAIMED, true).apply()

    fun passwordResetPending(): Boolean = prefs.getBoolean(KEY_PASSWORD_RESET, false)
    fun markPasswordResetPending() = prefs.edit().putBoolean(KEY_PASSWORD_RESET, true).apply()
    fun clearPasswordResetPending() = prefs.edit().putBoolean(KEY_PASSWORD_RESET, false).apply()

    fun clear() = prefs.edit().clear().apply()

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(keyAlias, null) as? SecretKey)?.let { return it }

        return KeyGenerator.getInstance("AES", "AndroidKeyStore").apply {
            init(
                android.security.keystore.KeyGenParameterSpec.Builder(
                    keyAlias,
                    android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                            android.security.keystore.KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
        }.generateKey()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val data = Base64.encodeToString(cipher.doFinal(value.toByteArray()), Base64.NO_WRAP)
        return "$iv:$data"
    }

    private fun decrypt(value: String): String? = runCatching {
        val parts = value.split(":", limit = 2)
        if (parts.size != 2) return null
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            key(),
            GCMParameterSpec(128, Base64.decode(parts[0], Base64.NO_WRAP))
        )
        String(cipher.doFinal(Base64.decode(parts[1], Base64.NO_WRAP)))
    }.getOrNull()

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_USER = "user_id"
        private const val KEY_LEGACY_CLAIMED = "legacy_claimed"
        private const val KEY_PASSWORD_RESET = "password_reset_pending"
    }
}