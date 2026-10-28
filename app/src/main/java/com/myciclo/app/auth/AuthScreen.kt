package com.myciclo.app.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de autenticación de myCiclo.
 *
 * Soporta:
 * - Inicio de sesión
 * - Creación de cuenta
 * - Recuperación de contraseña
 * - Establecer nueva contraseña cuando se llega por el deep link de recovery
 *
 * La sesión nunca se guarda en texto plano (lo hace SecureSessionStore).
 */
@Composable
fun AuthScreen(
    repository: AuthRepository,
    resetPassword: Boolean = false,
    onAuthenticated: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var creandoCuenta by remember { mutableStateOf(false) }
    var nuevaPassword by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun ejecutar(accion: suspend () -> AuthResult) {
        scope.launch {
            procesando = true
            mensaje = ""
            when (val resultado = accion()) {
                is AuthResult.Success -> {
                    mensaje = resultado.message
                    if (resetPassword) {
                        // Tras cambiar la contraseña, limpiamos el flag y entrarmos a la app
                        repository.clearPasswordResetPending()
                        onAuthenticated()
                    } else if (repository.hasSession()) {
                        onAuthenticated()
                    }
                }
                is AuthResult.Error -> mensaje = resultado.message
            }
            procesando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "myCiclo",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (resetPassword) {
                "Crea una nueva contraseña para continuar."
            } else {
                "Tu bitácora empieza con una cuenta protegida."
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        if (resetPassword) {
            OutlinedTextField(
                value = nuevaPassword,
                onValueChange = { nuevaPassword = it },
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                ejecutar {
                    when {
                        resetPassword -> repository.updatePassword(nuevaPassword)
                        creandoCuenta -> repository.signUp(email, password)
                        else -> repository.signIn(email, password)
                    }
                }
            },
            enabled = !procesando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    resetPassword -> "Guardar contraseña"
                    creandoCuenta -> "Crear cuenta"
                    else -> "Iniciar sesión"
                }
            )
        }

        if (!resetPassword) {
            TextButton(
                onClick = {
                    creandoCuenta = !creandoCuenta
                    mensaje = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (creandoCuenta) "Ya tengo una cuenta" else "Crear una cuenta nueva")
            }

            TextButton(
                onClick = { ejecutar { repository.recover(email) } },
                enabled = !procesando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Olvidaste tu contraseña?")
            }
        }

        if (mensaje.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tus registros de salud son privados. No compartas tu contraseña.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}