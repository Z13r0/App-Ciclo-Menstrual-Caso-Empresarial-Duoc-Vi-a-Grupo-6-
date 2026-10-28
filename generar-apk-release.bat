@echo off
REM Script para generar el APK firmado en modo release
REM Este script requiere que existan myciclo-release.jks y keystore.properties

set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using Java:
java -version
echo.

echo ============================================
echo Generando APK firmado en modo release...
echo ============================================
echo.

REM Verificar que existan los archivos de firma
if not exist "keystore.properties" (
    echo ERROR: No se encontró keystore.properties
    echo El APK se generará SIN FIRMAR
    echo.
)

if not exist "myciclo-release.jks" (
    echo ERROR: No se encontró myciclo-release.jks
    echo El APK se generará SIN FIRMAR
    echo.
)

call gradlew.bat assembleRelease

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo BUILD EXITOSO!
    echo ============================================
    echo.
    echo APK ubicado en: app\build\outputs\apk\release\app-release.apk
    echo.
    
    if exist "keystore.properties" if exist "myciclo-release.jks" (
        echo Verificando firma del APK...
        jarsigner -verify -verbose -certs app\build\outputs\apk\release\app-release.apk 2>&1 | findstr "jar verified"
    ) else (
        echo WARNING: APK generado SIN FIRMAR (falta keystore.properties o myciclo-release.jks)
    )
    echo.
) else (
    echo.
    echo ============================================
    echo ERROR: El build falló
    echo ============================================
    echo.
)

pause
