```markdown
# myCiclo - Bitácora EVA (DSY1105, Grupo 6)

App Android para el caso de myCiclo: registrar lo que se ve en EVA, ver el
calendario del ciclo y leer guías educativas.

Kotlin + Jetpack Compose + Material 3 + MVVM + Room + Retrofit + Spring Boot.

## Estructura

Cada carpeta es una parte del trabajo, así cada uno trabaja en lo suyo sin pisarse.

```
com.myciclo.app/
├── MainActivity.kt
├── navegacion/          Screen.kt (rutas) y AppNavegacion.kt (barra de abajo)   -> compartido
├── data/AppDatabase.kt  base de datos room                                     -> compartido
├── ui/                  tema y AvisoEva                                         -> compartido
├── notificaciones/      recordatorio diario local                              -> compartido
├── registro/            INTEGRANTE 1: formulario diario + room + foto + historial
├── calendario/          INTEGRANTE 2: calendario + ventana fértil + fase + promedio
└── contenido/           INTEGRANTE 3: guías educativas + retrofit

backend/                 API Spring Boot con GET /api/guias
```
```
### Integrante 1 - Registro
- `Nivel.kt`, `RegistroEntity.kt`, `RegistroDao.kt`, `RegistroRepository.kt`
- `RegistroViewModel.kt`, `RegistroScreen.kt`
- `Fotos.kt`: guarda la foto del patrón dentro de la carpeta de la app (guía 13).
- Listo: foto con cámara (TakePicture + FileProvider) o galería (GetContent).
- Listo: historial de registros con edición y borrado (confirmación antes de eliminar).
- Listo: al editar se carga el registro en el formulario y se actualiza con REPLACE.

### Integrante 2 - Calendario
- `VentanaFertil.kt` (regla: primer helecho + 8 días), `CalendarioViewModel.kt`, `CalendarioScreen.kt`
- `FaseCiclo.kt`: día del ciclo y fase (menstruación, folicular, ventana, lútea).
- Tests en `app/src/test/.../VentanaFertilTest.kt` y `FaseCicloTest.kt`
- Listo: tocar un día abre un diálogo con el nivel, las notas y la foto de ese día.
- Listo: duración promedio del ciclo (necesita al menos 2 inicios de período distintos).

### Integrante 3 - Contenido
- `Guia.kt`, `ContenidoRepository.kt` (interfaz + guías locales), `ContenidoApi.kt` (retrofit)
- `ContenidoViewModel.kt`, `ContenidoScreen.kt`
- Listo: backend Spring Boot en carpeta `backend/` con `GET /api/guias`.
- Listo: si la API no responde se muestran las guías locales (modo offline).
- Pendiente: ampliar un poco más el texto de las guías locales para que coincidan con el backend.

### Notificaciones (compartido)
- `NotificacionHelper.kt`: canal y alarma diaria a las 09:00.
- `RecordatorioReceiver.kt`: muestra la notificación local.
- Listo: permiso `POST_NOTIFICATIONS` en runtime (Android 13+).
- Listo: al tocar la notificación se abre la app.
- Pendiente (opcional): pantalla de configuración para activar/desactivar el recordatorio.

## Base de datos
Room, versión 2. La versión 2 agregó la columna `foto` a la tabla `registros`.
Mientras sea prototipo usamos `fallbackToDestructiveMigration()`, o sea al subir
de versión la tabla se crea de nuevo. Los datos son de prueba, no se pierde nada real.

## Privacidad
- El diario solo se guarda en Room, no se manda a ningún servidor.
- Las fotos del patrón van a la carpeta interna de la app (`files/images`), no a la
  galería del celular, así no aparecen en otras apps.
- `allowBackup="false"` en el manifest para que no se respalde en la nube.
- Solo se descarga contenido educativo (guías); nunca datos del ciclo.

## API
La app pide `GET http://10.0.2.2:8080/api/guias` (10.0.2.2 = el PC desde el emulador).
Respuesta: `[{ "id": 1, "titulo": "...", "contenido": "...", "categoria": "..." }]`.
Si la API no responde se muestran las guías locales.

### Cómo levantar el backend
```
bash
cd backend
mvn spring-boot:run
```
Luego abrir en el navegador: `http://localhost:8080/api/guias`

## Cómo probar
- Tests de lógica: `./gradlew test` (no necesita emulador, son JUnit puros).
- App completa: abrir en Android Studio y correr en el emulador.
- Para probar la foto con cámara hay que usar un emulador con cámara activada.
- Para probar las guías del servidor: dejar el backend corriendo en el puerto 8080.
- Para probar notificaciones: aceptar el permiso y, si hace falta, adelantar la hora del emulador.

## Qué falta / mejoras opcionales
- Ampliar textos de las guías locales para que coincidan con las del backend.
- Pantalla de configuración (activar/desactivar notificaciones).
- Onboarding simple al primer uso (fecha del último período).
- Más tests unitarios (promedio de ciclo, borrar registro).
- Generar APK de release firmado para la entrega formal.
- Mantener el tablero Trello al día.
```