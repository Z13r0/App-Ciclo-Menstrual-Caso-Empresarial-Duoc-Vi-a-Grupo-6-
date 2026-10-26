@echo off
REM Script para compilar el backend Spring Boot con Java 21
REM Este script configura el JAVA_HOME temporalmente para esta sesión

set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using Java:
java -version
echo.

cd backend-myciclo

echo Building backend with Maven...
if exist mvnw.cmd (
    mvnw.cmd clean package
) else (
    echo Maven wrapper not found. Please install Maven or run: mvn clean package
    echo You can download Maven from: https://maven.apache.org/download.cgi
)

cd ..
pause
