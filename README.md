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
├── registro/            INTEGRANTE 1: formulario diario + room
├── calendario/          INTEGRANTE 2: calendario + ventana fertil
└── contenido/           INTEGRANTE 3: guias educativas + retrofit
```

### Integrante 1 - Registro
- `Nivel.kt`, `RegistroEntity.kt`, `RegistroDao.kt`, `RegistroRepository.kt`
- `RegistroViewModel.kt`, `RegistroScreen.kt`
- Pendiente: foto del patron con camara o galeria (guia 13).

### Integrante 2 - Calendario
- `VentanaFertil.kt` (regla: primer helecho + 8 dias), `CalendarioViewModel.kt`, `CalendarioScreen.kt`
- Test en `app/src/test/.../VentanaFertilTest.kt`
- Pendiente: tocar un dia y ver el detalle del registro.

### Integrante 3 - Contenido
- `Guia.kt`, `ContenidoRepository.kt` (interfaz + guias locales), `ContenidoApi.kt` (retrofit)
- `ContenidoViewModel.kt`, `ContenidoScreen.kt`
- Pendiente: backend spring boot con `GET /api/guias` y notificaciones diarias.

## Privacidad
- El diario solo se guarda en room, no se manda a ningun servidor.
- `allowBackup="false"` en el manifest para que no se respalde en la nube.

## Api
La app pide `GET http://10.0.2.2:8080/api/guias` (10.0.2.2 = el pc desde el emulador).
Respuesta: `[{ "id": 1, "titulo": "...", "contenido": "...", "categoria": "..." }]`.
Si la api no responde se muestran las guias locales.
