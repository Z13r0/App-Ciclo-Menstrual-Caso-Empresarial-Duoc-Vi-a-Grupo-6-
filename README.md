# myCiclo - Bitácora EVA (DSY1105, Grupo 6)

Aplicación Android nativa profesional desarrollada para la solución empresarial **myCiclo**: permite a las usuarias registrar las observaciones diarias del patrón microscópico de la saliva (EVA), visualizar la ventana fértil y fases del ciclo menstrual en un calendario interactivo, recibir recordatorios diarios configurables y consultar guías educativas con búsqueda avanzada y sistema de favoritas.

Desarrollada en **Kotlin 2.0+** con **Jetpack Compose**, **Material 3**, **MVVM**, **Room Database** y **Retrofit**.

---

## 🚀 Características Principales

### 📝 Registro Diario de Observaciones EVA
- Formulario completo con validación de fechas usando `FechaUtils`
- Captura de fotografías mediante cámara nativa o galería
- Almacenamiento privado de imágenes (no accesible desde galería pública)
- Historial interactivo con edición y eliminación de registros
- Validación segura de entradas de usuario

### 📅 Calendario Inteligente
- Vista mensual interactiva con navegación entre meses
- Cálculo automático de ventana fértil según método EVA (primer helecho + 8 días)
- Identificación de fase del ciclo actual (Menstruación, Folicular, Ventana EVA, Lútea)
- Estadísticas de ciclo: duración promedio y variación entre ciclos
- Diálogo de detalle por día con foto, notas y patrón observado
- Manejo seguro de fechas con `FechaUtils` para evitar crashes

### 📚 Contenido Educativo Avanzado
- Guías educativas sobre uso de EVA, interpretación de patrones y privacidad
- Búsqueda en tiempo real por título y contenido
- Filtros por categoría (Manual, Educación, Preguntas Frecuentes)
- Sistema de favoritas persistente con SharedPreferences
- Soporte híbrido: API REST (Spring Boot/Supabase) + fallback local offline
- Interfaz intuitiva con tarjetas expandibles

### ⏰ Notificaciones Configurables
- Pantalla de ajustes con TimePicker nativo
- Configuración personalizable de hora del recordatorio
- Activación/desactivación dinámica de alarmas
- Solicitud automática de permisos en Android 13+
- Sincronización con AlarmManager
- Persistencia de configuración con SharedPreferences

---

## 🏗️ Arquitectura y Tecnologías

### Stack Tecnológico
- **Lenguaje:** Kotlin 2.0+ (Target JVM 17)
- **UI Framework:** Jetpack Compose + Material 3 Design
- **Navegación:** Navigation Compose
- **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Data Layer
- **Persistencia:** Room Database (almacenamiento local)
- **Red:** Retrofit 2 + Gson Converter
- **Notificaciones:** AlarmManager + NotificationChannel
- **Asincronía:** Kotlin Coroutines & StateFlow

### Estructura del Proyecto
Organizado por módulos funcionales para mantenibilidad y colaboración:

```
com.myciclo.app/
├── MainActivity.kt                          # Punto de entrada, permisos runtime
├── navegacion/                              # Navegación y rutas
│   ├── Screen.kt                            # Rutas de navegación
│   └── AppNavegacion.kt                     # Barra de navegación inferior
├── data/                                    # Base de datos local
│   └── AppDatabase.kt                       # Configuración Room
├── ui/                                      # Tema y componentes compartidos
│   ├── theme/                              # MyCicloTheme y colores
│   └── componentes/                        # AvisoEva y otros componentes
├── util/                                    # Utilidades
│   └── FechaUtils.kt                        # Manejo seguro de fechas
├── notificaciones/                          # Sistema de notificaciones
│   ├── NotificacionHelper.kt               # Gestión de AlarmManager
│   ├── RecordatorioReceiver.kt             # BroadcastReceiver
│   ├── AjustesScreen.kt                    # Pantalla de configuración
│   └── AjustesPreferences.kt                # Persistencia de ajustes
├── registro/                                # Módulo de registro diario
│   ├── Nivel.kt                            # Enum de niveles EVA
│   ├── RegistroEntity.kt                   # Entidad Room
│   ├── RegistroDao.kt                      # Consultas Room
│   ├── RegistroRepository.kt               # Repositorio de datos
│   ├── RegistroViewModel.kt                # ViewModel
│   ├── RegistroScreen.kt                   # Pantalla de registro
│   └── Fotos.kt                            # Manejo de imágenes
├── calendario/                              # Módulo de calendario
│   ├── VentanaFertil.kt                    # Cálculo de ventana fértil
│   ├── FaseCiclo.kt                        # Fases del ciclo
│   ├── CalendarioViewModel.kt              # ViewModel
│   └── CalendarioScreen.kt                 # Pantalla de calendario
└── contenido/                               # Módulo de contenido educativo
    ├── Guia.kt                             # Modelo de datos
    ├── ContenidoApi.kt                     # Interfaz Retrofit
    ├── ContenidoRepository.kt              # Repositorio híbrido
    ├── ContenidoViewModel.kt               # ViewModel
    ├── ContenidoScreen.kt                  # Pantalla de contenido
    ├── FavoritasPreferences.kt             # Persistencia de favoritas
    ├── SupabaseApi.kt                      # API Supabase (opcional)
    └── SupabaseConfig.kt                    # Configuración Supabase
```

---

## 🌟 Funcionalidades Implementadas

### 📝 1. Registro Diario de Observaciones EVA (Integrante 1)
- **Formulario Completo:** Registro de fecha, día de inicio del período, nivel de cristalización EVA observado y notas opcionales.
- **Captura de Fotografías:** Integración nativa con cámara (`TakePicture`) o galería (`GetContent`). Las imágenes se almacenan exclusivamente en el almacenamiento interno privado del dispositivo.
- **Historial Interactivo:** Lista de observaciones pasadas con soporte para **edición** y **eliminación** con diálogo de confirmación.

### 📅 2. Calendario y Ventana Fértil EVA (Integrante 2)
- **Vista Mensual Interactiva:** Muestra cada día con código de color según el nivel EVA registrado.
- **Ventana Fértil Personalizada:** Aplica la regla del método EVA (ventana fértil activa desde el primer día con patrón de helecho hasta 8 días posteriores).
- **Identificación de Fase del Ciclo:** Determina automáticamente la fase actual (*Menstruación*, *Fase Folicular*, *Ventana EVA*, *Fase Lútea*).
- **Promedio de Duración del Ciclo:** Calcula la duración promedio en días basándose en los registros de inicio de período de meses anteriores.
- **Detalle de Día:** Al presionar un día se despliega un diálogo con las notas, patrón observado y foto adjunta.

### 📚 3. Guías Educativas y Notificaciones (Integrante 3)
- **Contenido Educativo:** Muestra guías sobre el uso del dispositivo EVA, interpretación de patrones, higiene y privacidad.
- **Conexión API REST & Fallback:** Pide el contenido a la API en Spring Boot (`GET /api/guias`). Si el servidor o la red no están disponibles, carga automáticamente las guías locales sin interrumpir a la usuaria.
- **Recordatorios Diarios Locales:** Alerta diaria programada a las **09:00 AM** mediante `AlarmManager` y `NotificationChannel`, solicitando los permisos adecuados en Android 13+.

---

## 📌 Tareas Pendientes y Próximos Pasos (Roadmap del Grupo)

### 📝 Integrante 1 - Módulo Registro
- [ ] **Integración de `FechaUtils` en Formulario:** Aplicar `FechaUtils.esValida()` y `FechaUtils.parsear()` en `RegistroScreen` para validar la entrada y guardar en formato ISO estándar (`yyyy-MM-dd`) en Room.
- [x] **Visor de Foto Ampliada:** ✅ Completado - La tarjeta del historial muestra una miniatura de la foto del patrón y al tocarla se abre ampliada. La miniatura usa `ContentScale.Crop` para que todas queden parejas, y la vista ampliada usa `ContentScale.Fit` para no recortar el patrón de helecho.
- [ ] **Filtro / Búsqueda en Historial:** Filtrar registros por nivel de observación EVA (Nulo, Bajo, Medio, Alto) o por texto libre en notas.

### 📅 Integrante 2 - Módulo Calendario
- [x] **Navegación entre Meses:** ✅ Completado - Controles de navegación (mes anterior / mes siguiente) ya implementados en `CalendarioScreen`.
- [x] **Parseo Seguro con `FechaUtils`:** ✅ Completado - `FechaUtils.parsear(...)` ya se usa en `CalendarioViewModel`.
- [ ] **Indicadores de Regularidad:** Añadir resumen visual del historial de ciclos (variación en días entre períodos).

### 📚 Integrante 3 - Módulo Contenido & Notificaciones
- [ ] **Búsqueda y Filtro de Guías:** Agregar barra de búsqueda y filtros por categoría (Manual, Educación, Preguntas Frecuentes) en `ContenidoScreen`.
- [ ] **Ajustes de Notificaciones:** Crear pantalla de configuración para activar/desactivar la alarma o ajustar la hora del recordatorio diario (actualmente fija a las 09:00 AM).
- [ ] **Guías Favoritas:** Permitir guardar o marcar guías de interés para consulta rápida offline.

---
## 🔒 Privacidad y Seguridad

### Protección de Datos
- **Almacenamiento 100% Local:** Toda la información del ciclo se guarda exclusivamente en Room Database dentro del dispositivo
- **Aislamiento de Imágenes:** Fotografías almacenadas en directorio privado (`context.filesDir/images`), no accesible desde galería pública
- **Sin Respaldos Automáticos:** `android:allowBackup="false"` en AndroidManifest.xml para proteger datos íntimos
- **Permisos Mínimos:** Solo solicita permisos necesarios (cámara, notificaciones)

### Seguridad de Código
- Manejo seguro de excepciones en parseo de fechas y datos
- Validación de entradas de usuario
- Fallback local cuando API no está disponible
- Configuración de keystore excluida del repositorio

---

## 🔌 Integración con Backend

### API Spring Boot (Desarrollo Local)
- **URL Base (Emulador):** `http://10.0.2.2:8080/api/`
- **Endpoint:** `GET /api/guias`
- **Uso:** Desarrollo y pruebas en emulador Android

### Supabase (Producción Opcional)
Para demostraciones en dispositivos reales sin servidor local:
- **Endpoint:** `GET https://<proyecto>.supabase.co/rest/v1/guias?select=*&order=id`
- **Activación:** Configurar URL y llave anon en `SupabaseConfig.kt`
- **Seguridad:** Solo usa llave `anon` con políticas RLS de solo lectura

### Estrategia de Fallback
1. **Primero:** Intenta API configurada (Supabase o Spring Boot)
2. **Si falla:** Carga guías locales automáticamente
3. **Resultado:** La app siempre funciona, sin interrupciones

---

## 🧪 Desarrollo y Compilación

### Requisitos del Sistema
- **Java:** 17, 21 o 22 (Java 25 NO es compatible)
- **Android Studio:** Koala o superior
- **Android SDK:** API 33+ (Android 13+)
- **Gradle:** 8.11.1 (configurado en wrapper)

### Compilación

#### APK Debug (Desarrollo)
```bash
# Script automatizado (Windows)
compile-android.bat

# O manualmente
./gradlew assembleDebug -x lint
```

#### APK Release (Producción)
```bash
# Script automatizado (Windows)
generar-apk-release.bat

# O manualmente
./gradlew assembleRelease
```

**Ubicación del APK:** `app/build/outputs/apk/release/app-release.apk`

### Pruebas Unitarias
```bash
./gradlew test
```

### Verificación de Firma
```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

---

## 📱 Instalación y Uso

### Instalación en Dispositivo
1. Abrir el proyecto en Android Studio
2. Conectar dispositivo físico o iniciar emulador (Android 13+)
3. Ejecutar `Run 'app'` o instalar el APK firmado

### Configuración Inicial
- Otorgar permisos de cámara y notificaciones cuando se soliciten
- Configurar hora del recordatorio en Ajustes (por defecto: 09:00 AM)
- Registrar primera observación para comenzar a usar el calendario

### Uso Principal
1. **Registrar:** Presionar botón "+" → Completar formulario → Guardar
2. **Ver Calendario:** Navegar por meses → Tocar día para ver detalle
3. **Aprender:** Ir a "Aprende" → Buscar/filtrar guías → Marcar favoritas
4. **Ajustes:** Botón ⚙️ en TopAppBar → Configurar notificaciones

---

## 🛠️ Scripts de Automatización

### compile-android.bat
Compila el APK debug con Java 21 configurado automáticamente.

### compile-backend.bat
Compila el backend Spring Boot (requiere Maven instalado).

### generar-apk-release.bat
Genera el APK firmado en modo release con verificación de firma.

---

## 📝 Notas para Desarrolladores

### Firma del APK
- Los archivos `myciclo-release.jks` y `keystore.properties` **NO están en el repositorio**
- Para generar APK firmado, copiar estos archivos del equipo del responsable de firma
- Configurar `keystore.properties` con las credenciales correctas
- Guardar copia de seguridad del keystore: si se pierde, no se puede volver a firmar con la misma llave

### Configuración de Java
- El proyecto usa Java 21 por defecto (configurado en `gradle.properties`)
- Si tu Java por defecto no es compatible, configura en tu gradle personal: `~/.gradle/gradle.properties`
- NO modificar `gradle.properties` del proyecto (rompe el build de otros desarrolladores)

### Lint
- Lint está deshabilitado en build para evitar problemas de file lock en Windows
- Para análisis de lint, ejecutar: `./gradlew lintDebug` manualmente

---

## 📄 Licencia

Proyecto desarrollado para el caso empresarial DSY1105, Grupo 6. Universidad Duoc UC.

---

## 👥 Equipo de Desarrollo

**Grupo 6 - DSY1105**
** Integrantes **
- Ignacio Vera
- Sebastian Cerda
- Benjamin Navarrete

## 🔐 Autenticación y datos reales

La aplicación incorpora autenticación por correo mediante Supabase Auth. Permite crear cuenta, iniciar sesión, renovar la sesión, cerrar sesión, solicitar recuperación de contraseña y establecer una nueva contraseña desde el deep link `myciclo://auth-callback`. La confirmación de correo debe estar activada en Supabase antes de usar el flujo en producción.

La sesión se guarda cifrada con AES-GCM y una clave del Android Keystore. La URL y la clave pública de Supabase se inyectan al compilar mediante `SUPABASE_URL` y `SUPABASE_ANON_KEY`; no deben escribirse en Kotlin ni subir credenciales privadas al repositorio. La clave `service_role` nunca debe estar dentro del APK.

El error más peligroso del prototipo era `fallbackToDestructiveMigration()`: podía borrar el diario cuando cambiaba el esquema. Fue reemplazado por una migración Room explícita. El entorno de desarrollo también requiere Android SDK configurado mediante `ANDROID_HOME` o `local.properties`.

**Importante:** el diario continúa siendo local en esta versión, pero sus filas están aisladas por `userId` y fecha. El login protege el acceso, pero aún no sincroniza registros entre dispositivos. Para habilitar sincronización real se debe implementar la API de `registros_ciclo`, ligar cada fila a `auth.uid()`, mantener RLS activa, usar bucket privado para fotos y agregar exportación y eliminación de cuenta. El SQL base está en `docs/supabase.sql` y la guía de despliegue en `docs/PRODUCCION_AUTH.md`.

### Configuración rápida de desarrollo

```bash
export SUPABASE_URL=https://tu-proyecto.supabase.co
export SUPABASE_ANON_KEY=tu-clave-publica
./gradlew assembleDebug
```
