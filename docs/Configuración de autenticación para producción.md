# Configuración de autenticación para producción

La app ya incluye registro, inicio de sesión, recuperación de contraseña, almacenamiento cifrado de sesión y retorno mediante `myciclo://auth-callback`. Para activarlo, define `SUPABASE_URL` y `SUPABASE_ANON_KEY` como propiedades de Gradle o variables de entorno antes de compilar. La clave debe ser la clave pública `anon`/publishable; nunca uses `service_role` dentro de Android.

En Supabase, activa la confirmación de correo en **Authentication → Providers → Email**. Agrega `myciclo://auth-callback` en **Authentication → URL Configuration → Additional Redirect URLs**. Para recuperación de contraseña, configura el template de email para redirigir al mismo callback y prueba el enlace en un dispositivo real. El servicio de correo predeterminado sirve para pruebas, pero la documentación de Supabase recomienda SMTP propio para producción por límites y disponibilidad.

La sesión se cifra con AES-GCM y una clave no exportable del Android Keystore. La app no debe registrar tokens en logs, analytics, crash reports ni mensajes de soporte. Si se incorpora sincronización de registros, cada fila debe tener `user_id` y políticas RLS que exijan `auth.uid() = user_id`; nunca se debe confiar en un `user_id` enviado por el cliente.

El diario local ahora queda aislado por `userId` mediante una clave Room compuesta (`userId + fecha`). La migración conserva los registros existentes como `legacy-local` y los reclama una sola vez en la primera cuenta autenticada del dispositivo. La aplicación todavía no sincroniza ni respalda los registros entre dispositivos. Antes de presentar esa función como disponible, hay que implementar una API de registros con RLS, subida privada de fotos, borrado de cuenta y exportación de datos.

Para compilar en una máquina con Android SDK:

```bash
export SUPABASE_URL=https://tu-proyecto.supabase.co
export SUPABASE_ANON_KEY=tu-clave-publica
./gradlew test
./gradlew assembleDebug
```

No se agregó `local.properties` al repositorio porque contiene rutas específicas del equipo. Si Gradle informa `SDK location not found`, configura `ANDROID_HOME` o crea `local.properties` localmente con `sdk.dir=...`.
