# myCiclo - Bitacora EVA (DSY1105, Grupo 6)

App Android para el caso de myCiclo: registrar lo que se ve en EVA, ver el
calendario del ciclo y leer guias educativas.

Kotlin + Jetpack Compose + Material 3 + MVVM + Room + Retrofit.

## Estructura

Cada carpeta es una parte del trabajo, asi cada uno trabaja en lo suyo sin pisarse.

```
com.myciclo.app/
├── MainActivity.kt
├── navegacion/          Screen.kt (rutas) y AppNavegacion.kt (barra de abajo)   -> compartido
├── data/AppDatabase.kt  base de datos room                                     -> compartido
├── ui/                  tema y AvisoEva                                         -> compartido
├── registro/            INTEGRANTE 1: formulario diario + room + foto
├── calendario/          INTEGRANTE 2: calendario + ventana fertil + fase
└── contenido/           INTEGRANTE 3: guias educativas + retrofit
```

### Integrante 1 - Registro
- `Nivel.kt`, `RegistroEntity.kt`, `RegistroDao.kt`, `RegistroRepository.kt`
- `RegistroViewModel.kt`, `RegistroScreen.kt`
- `Fotos.kt`: guarda la foto del patron dentro de la carpeta de la app (guia 13).
- Listo: foto con camara (TakePicture + FileProvider) o galeria (GetContent).
- Pendiente: poder editar o borrar un registro ya guardado.

### Integrante 2 - Calendario
- `VentanaFertil.kt` (regla: primer helecho + 8 dias), `CalendarioViewModel.kt`, `CalendarioScreen.kt`
- `FaseCiclo.kt`: dia del ciclo y fase (menstruacion, folicular, ventana, lutea).
- Tests en `app/src/test/.../VentanaFertilTest.kt` y `FaseCicloTest.kt`
- Listo: tocar un dia abre un dialogo con el nivel, las notas y la foto de ese dia.
- Pendiente: mostrar la duracion promedio del ciclo con varios meses registrados.

### Integrante 3 - Contenido
- `Guia.kt`, `ContenidoRepository.kt` (interfaz + guias locales), `ContenidoApi.kt` (retrofit)
- `ContenidoViewModel.kt`, `ContenidoScreen.kt`
- Pendiente: backend spring boot con `GET /api/guias` y notificaciones diarias.

## Base de datos
Room, version 2. La version 2 agrego la columna `foto` a la tabla `registros`.
Mientras sea prototipo usamos `fallbackToDestructiveMigration()`, o sea al subir
de version la tabla se crea de nuevo. Los datos son de prueba, no se pierde nada real.

## Privacidad
- El diario solo se guarda en room, no se manda a ningun servidor.
- Las fotos del patron van a la carpeta interna de la app (`files/images`), no a la
  galeria del celular, asi no aparecen en otras apps.
- `allowBackup="false"` en el manifest para que no se respalde en la nube.

## Api
La app pide `GET http://10.0.2.2:8080/api/guias` (10.0.2.2 = el pc desde el emulador).
Respuesta: `[{ "id": 1, "titulo": "...", "contenido": "...", "categoria": "..." }]`.
Si la api no responde se muestran las guias locales.

## Como probar
- Tests de logica: `gradlew test` (no necesita emulador, son junit puros).
- App completa: abrir en Android Studio y correr en el emulador.
- Para probar la foto con camara hay que usar un emulador con camara activada.
