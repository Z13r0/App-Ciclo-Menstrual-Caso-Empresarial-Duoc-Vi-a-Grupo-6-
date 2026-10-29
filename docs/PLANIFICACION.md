# Planificación del equipo — myCiclo (DSY1105, Grupo 6)

Tablero en vivo: https://app.notion.com/p/a8743e87c537413c9450b2b77d31cd80

Esta copia existe para que la planificación quede versionada junto al código.

## Reparto

| Integrante | Tareas |
| --- | --- |
| Benjamín Navarrete | 16 |
| Ignacio Vera | 14 |
| Sebastián Cerda | 20 |
| Todo el equipo | 2 |

## En curso (3)

| Responsable | Módulo | Tarea |
| --- | --- | --- |
| Benjamín Navarrete | Entrega | Grabar y montar los videos del pitch y la defensa técnica |
| Sebastián Cerda | Entrega | Grabar la narración de los bloques de la defensa |
| Todo el equipo | Entrega | Revisar el README y dejarlo al día antes de la entrega |

## Por hacer (5)

| Responsable | Módulo | Tarea |
| --- | --- | --- |
| Sebastián Cerda | Autenticación | Llevar la autenticación al patrón MVVM: hoy AuthScreen habla directo con el repositorio |
| Ignacio Vera | Notificaciones | Llevar la pantalla de Ajustes a MVVM: hoy lee las preferencias dentro del Composable |
| Ignacio Vera | Diseño visual | Completar el modo oscuro: las barras superior e inferior usan colores fijos |
| Benjamín Navarrete | Registro | Ampliar las pruebas unitarias al módulo de registro |
| Ignacio Vera | Contenido educativo | Ampliar las pruebas unitarias al módulo de contenido |

## Backlog (5)

| Responsable | Módulo | Tarea |
| --- | --- | --- |
| Benjamín Navarrete | Registro | Exportar el diario a PDF o CSV para que la usuaria pueda llevárselo |
| Ignacio Vera | Diseño visual | Permitir elegir el tema claro u oscuro a mano desde Ajustes |
| Sebastián Cerda | Autenticación | Sincronizar los registros con el servidor para que viajen entre dispositivos |
| Sebastián Cerda | Registro | Borrar la foto del disco cuando se elimina el registro que la usaba |
| Ignacio Vera | Diseño visual | Traducir los textos fijos a strings.xml para poder cambiar el idioma |

## Hecho (39)

| Responsable | Módulo | Tarea |
| --- | --- | --- |
| Sebastián Cerda | Entorno y arquitectura | Configurar el proyecto en Android Studio con el catálogo de versiones |
| Sebastián Cerda | Entorno y arquitectura | Definir la arquitectura MVVM y la organización por funcionalidad |
| Sebastián Cerda | Entorno y arquitectura | Montar la navegación con NavHost y las tres pestañas |
| Benjamín Navarrete | Registro | Crear la base de datos con Room y la entidad de registros |
| Benjamín Navarrete | Registro | Escribir el DAO con las consultas y OnConflictStrategy REPLACE |
| Benjamín Navarrete | Registro | Migraciones de Room para no perder los datos al cambiar el esquema |
| Benjamín Navarrete | Registro | Formulario de registro diario con los tres niveles de observación EVA |
| Benjamín Navarrete | Registro | Validación de fechas con FechaUtils, fuera del componente visual |
| Benjamín Navarrete | Registro | Retroalimentación visual de errores con isError y supportingText |
| Benjamín Navarrete | Registro | Botón Guardar deshabilitado mientras falte el nivel de observación |
| Benjamín Navarrete | Registro | Integrar la cámara con TakePicture y FileProvider |
| Benjamín Navarrete | Registro | Integrar la galería copiando la imagen a la carpeta privada de la app |
| Benjamín Navarrete | Registro | Mostrar la foto en el detalle del día con esquinas redondeadas |
| Benjamín Navarrete | Registro | Filtrar el historial por nivel de observación |
| Sebastián Cerda | Calendario | Calcular la ventana fértil con la regla del primer helecho más ocho días |
| Sebastián Cerda | Calendario | Calcular el día y la fase del ciclo |
| Sebastián Cerda | Calendario | Calcular el ciclo promedio y la variación en días |
| Sebastián Cerda | Calendario | Calendario mensual con navegación entre meses y leyenda de colores |
| Sebastián Cerda | Calendario | Exponer el estado del calendario como StateFlow desde el Flow de Room |
| Benjamín Navarrete | Calendario | Pruebas unitarias con JUnit para la ventana fértil y la fase del ciclo |
| Ignacio Vera | Diseño visual | Paleta de colores propia en Color.kt y tema Material 3 |
| Ignacio Vera | Diseño visual | Desactivar dynamicColor para conservar la identidad visual de la marca |
| Ignacio Vera | Contenido educativo | Pantalla de guías educativas con las categorías del manual |
| Ignacio Vera | Contenido educativo | Buscador en tiempo real sobre título y contenido de las guías |
| Ignacio Vera | Contenido educativo | Filtros por categoría combinados con la búsqueda usando combine |
| Ignacio Vera | Contenido educativo | Guías favoritas persistentes con FavoritasPreferences |
| Ignacio Vera | Contenido educativo | Animación de despliegue de las tarjetas con AnimatedVisibility |
| Ignacio Vera | Diseño visual | Hacer deslizable la fila de filtros para que no se aplasten en pantallas angostas |
| Sebastián Cerda | Contenido educativo | Respaldo local de las guías cuando el servidor no responde |
| Sebastián Cerda | Contenido educativo | Conectar las guías a la API REST en Spring Boot |
| Sebastián Cerda | Contenido educativo | Segunda fuente remota de guías con Supabase |
| Sebastián Cerda | Notificaciones | Notificación diaria programada con AlarmManager y canal de notificación |
| Sebastián Cerda | Notificaciones | Pedir el permiso POST_NOTIFICATIONS en tiempo de ejecución |
| Sebastián Cerda | Notificaciones | Pantalla de Ajustes para cambiar la hora del recordatorio o desactivarlo |
| Sebastián Cerda | Autenticación | Autenticación por correo con Supabase Auth |
| Sebastián Cerda | Autenticación | Guardar la sesión cifrada con AES-GCM y clave del Android Keystore |
| Benjamín Navarrete | Entrega | Firmar el APK de release con un keystore fuera del repositorio |
| Ignacio Vera | Diseño visual | Icono y logo de la aplicación con la paleta de la marca |
| Todo el equipo | Entrega | README con descripción, funcionalidades y pasos para ejecutar |
