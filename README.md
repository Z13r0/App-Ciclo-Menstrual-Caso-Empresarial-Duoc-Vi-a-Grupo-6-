# myCiclo - Bitácora EVA (DSY1105, Grupo 6)

Aplicación Android nativa desarrollada para la solución empresarial **myCiclo**: permite a las usuarias registrar las observaciones diarias del patrón microscópico de la saliva (EVA), visualizar la ventana fértil y fases del ciclo menstrual en un calendario interactivo, recibir un recordatorio diario local y consultar guías educativas sobre el uso del dispositivo y el método.

Desarrollada en **Kotlin** con **Jetpack Compose**, **Material 3**, **MVVM**, **Room Database** y **Retrofit**.

---

## 🚀 Arquitectura y Tecnologías

- **Lenguaje:** Kotlin 2.0+ (Target JVM 17)
- **UI:** Jetpack Compose + Material 3 Design (Navigation Compose)
- **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Data Layer (Repositories & DAOs)
- **Persistencia Local:** Room Database (con soporte para fotos en almacenamiento interno)
- **Red / API:** Retrofit 2 + Gson Converter (con estrategia de respaldo a guías locales offline)
- **Notificaciones Locales:** AlarmManager + NotificationChannel + BroadcastReceiver (Recordatorio diario 09:00 AM)
- **Asincronía:** Kotlin Coroutines & StateFlow

---

## 📁 Estructura del Proyecto

Organizado por módulos funcionales para trabajo colaborativo y mantenible:

```
com.myciclo.app/
├── MainActivity.kt                  # Punto de entrada, permisos runtime (Android 13+) y notificaciones
├── navegacion/                      # Rutas (Screen.kt) y barra de navegación inferior (AppNavegacion.kt)
├── data/                            # Base de datos local (AppDatabase.kt)
├── ui/                              # Tema de la app (MyCicloTheme) y componentes compartidos (AvisoEva.kt)
├── util/                            # Utilidades de fecha y formato (FechaUtils.kt)
├── notificaciones/                  # AlarmManager y NotificationChannel (NotificacionHelper.kt, RecordatorioReceiver.kt)
│
├── registro/                        # [Módulo Registro Diario]
│   ├── Nivel.kt                     # Enum de observaciones EVA (Nulo, Bajo, Medio, Alto)
│   ├── RegistroEntity.kt            # Entidad Room para observaciones diarias
│   ├── RegistroDao.kt               # Consultas Room
│   ├── RegistroRepository.kt        # Repositorio de datos de registros
│   ├── RegistroViewModel.kt         # Lógica de presentación y estado de registros
│   ├── RegistroScreen.kt            # Formulario, cámara/galería e historial interactivo (Editar/Eliminar)
│   └── Fotos.kt                     # Manejo de imágenes en la carpeta privada de la app
│
├── calendario/                      # [Módulo Calendario & Estimación]
│   ├── VentanaFertil.kt             # Regla EVA: primer helecho + 8 días de ventana fértil
│   ├── FaseCiclo.kt                 # Cálculo de fases del ciclo (Menstruación, Folicular, Ventana EVA, Lútea)
│   ├── CalendarioViewModel.kt       # Cálculo de estado del calendario y duración promedio del ciclo
│   └── CalendarioScreen.kt          # Vista de calendario mensual y diálogo de detalle por día
│
└── contenido/                       # [Módulo Contenido Educativo]
    ├── Guia.kt                      # Modelo de datos de las guías
    ├── ContenidoApi.kt              # Interfaz Retrofit para API REST
    ├── ContenidoRepository.kt       # Repositorio con soporte híbrido (API Remote + Local Fallback)
    ├── ContenidoViewModel.kt        # Gestión del estado de la pantalla de contenido
    └── ContenidoScreen.kt           # Vista de guías educativas y preguntas frecuentes
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
- [ ] **Visor de Foto Ampliada:** Permitir hacer clic en la imagen dentro de la tarjeta del historial para verla a pantalla completa.
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

- **Diario 100% Local:** Toda la información del ciclo se guarda exclusivamente en la base de datos local (Room) dentro del dispositivo. No se envían datos sensibles a ningún servidor.
- **Aislamiento de Imágenes:** Las fotografías tomadas se guardan en el directorio privado de la aplicación (`context.filesDir/images`), impidiendo que aparezcan en la galería pública del celular o sean accesibles por otras aplicaciones.
- **Sin Respaldos en Nube:** Atributo `android:allowBackup="false"` en el archivo `AndroidManifest.xml` para proteger la información íntima de la usuaria.

---

## 🔌 Conexión a la API Backend

La aplicación está preparada para consumir la API de guías educativas:
- **URL Base (Emulador Android):** `http://10.0.2.2:8080/`
- **Endpoint:** `GET /api/guias`
- **Estructura JSON:**
  ```json
  [
    {
      "id": 1,
      "titulo": "Cómo usar EVA en 4 pasos",
      "contenido": "...",
      "categoria": "Manual"
    }
  ]
  ```

---

## 🧪 Pruebas y Validación

### Requisitos de Java
**IMPORTANTE:** Este proyecto requiere Java 17 o superior.
- **Versión de compilación:** la fija `jvmToolchain(17)` en `app/build.gradle.kts`, no hay que configurar nada.
- **Nota:** Java 25 NO es compatible con Kotlin 2.0.21 - se debe usar Java 17-22.
- **Si tu Java por defecto no es 17-22:** pon la ruta en tu archivo personal `C:\Users\<tu usuario>\.gradle\gradle.properties`, **no** en el `gradle.properties` del proyecto. Esa ruta cambia en cada PC y si se sube rompe el build de los demás (`Java home supplied is invalid`).

### Compilación del Proyecto Android
Para compilar el proyecto Android desde la línea de comandos:
```bash
# En Windows (PowerShell o CMD) - RECOMENDADO
compile-android.bat

# O manualmente (evita problemas de lint):
./gradlew assembleDebug -x lint

# Solo pruebas unitarias:
./gradlew test
```

### Compilación del Backend Spring Boot
El backend requiere Maven. Si no está instalado, descárgalo desde https://maven.apache.org/download.cgi
```bash
# En Windows (PowerShell o CMD)
compile-backend.bat

# O manualmente con Maven instalado:
cd backend-myciclo
mvn clean package
java -jar target/api-guias-1.0.0.jar
```

### Prueba en Dispositivo o Emulador
1. Abrir el proyecto en **Android Studio**.
2. Seleccionar un emulador o dispositivo físico con Android 13+ (API 33+).
3. Ejecutar la aplicación (`Run 'app'`).
4. Para probar la captura con cámara en emulador, asegurar que el AVD tenga configurada la cámara en las opciones del emulador (*VirtualScene* o *Emulated*).
