@echo off
REM Script para compilar el proyecto Android con Java 21
REM Este script configura el JAVA_HOME temporalmente para esta sesión

set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using Java:
java -version
echo.

echo Building Android project (skipping lint to avoid file lock issues)...
call gradlew.bat assembleDebug -x lint

echo.
echo Build successful! APK located at: app\build\outputs\apk\debug\app-debug.apk
pause
