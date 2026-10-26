# Información de Configuración del Proyecto

## Requisitos de Java
- **Versión requerida:** Java 17 o superior
- **Versión recomendada:** Java 21
- **Ruta de Java 21 en el sistema:** `C:\Program Files\Java\jdk-21.0.11`
- **Problema conocido:** Java 25.0.4 NO es compatible con Kotlin 2.0.21 - causa error `IllegalArgumentException: 25.0.4`

## Configuración del Proyecto Android
- **Archivo de configuración de Gradle:** `gradle.properties`
- **Línea agregada para forzar Java 21:** `org.gradle.java.home=C:\\Program Files\\Java\\jdk-21.0.11`
- **Permisos de cámara corregidos:** Se agregó `<uses-feature android:name="android.hardware.camera" android:required="false" />` en AndroidManifest.xml para cumplir con lint de Chrome OS

## Scripts de Compilación
### 1. `compile-android.bat`
Script para compilar el proyecto Android con Java 21 configurado automáticamente.
```batch
compile-android.bat
```

### 2. `compile-backend.bat`
Script para compilar el backend Spring Boot (requiere Maven instalado).
```batch
compile-backend.bat
```

## Comandos de Compilación Manual
### Android
```bash
./gradlew clean build
./gradlew test  # Solo pruebas unitarias
```

### Backend (requiere Maven)
```bash
cd backend-myciclo
mvn clean package
java -jar target/api-guias-1.0.0.jar
```

## Estructura del Proyecto
- **App Android:** `app/` - Kotlin + Jetpack Compose + Material 3
- **Backend:** `backend-myciclo/` - Spring Boot 3.2.5 + Java 17
- **Build:** Gradle 8.11.1 (Android), Maven (Backend)

## Dependencias Principales
- Android: Room, Retrofit, Jetpack Compose, Navigation Compose, Material 3
- Backend: Spring Boot Starter Web

## URL de la API (para emulador Android)
- **Base URL:** `http://10.0.2.2:8080/api/`
- **Endpoint:** `GET /api/guias`
- **Config de red:** `network_security_config.xml` permite HTTP en `10.0.2.2` para desarrollo

## Archivos de Seguridad
- **AndroidManifest.xml:** `android:allowBackup="false"` para proteger datos del ciclo
- **Almacenamiento de fotos:** Directorio privado `context.filesDir/images/`
- **FileProvider:** Configurado en `file_paths.xml` para acceso a fotos de cámara

## Estado de Implementación
### ✅ Completado
- Registro diario con cámara/galería
- Calendario con ventana fértil EVA
- Navegación entre meses en calendario
- Guías educativas con API fallback
- Notificaciones diarias (09:00 AM)
- Parseo seguro de fechas con FechaUtils
- Permisos de Android correctos

### ⚠️ Pendientes (según README)
- Visor de foto ampliada en historial
- Filtros/búsqueda en historial de registros
- Indicadores de regularidad del ciclo
- Búsqueda/filtros en guías educativas
- Ajustes de notificaciones (hora configurable)
- Guías favoritas
