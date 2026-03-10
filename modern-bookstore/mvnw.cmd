@echo off
REM Maven Wrapper script for Windows
REM This script downloads and runs Maven with the correct version

setlocal

set MAVEN_VERSION=3.9.6
set MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip

cd /d "%~dp0"

if not exist ".mvn\wrapper\maven" (
    echo Downloading Maven %MAVEN_VERSION%...
    powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '.mvn\wrapper\maven.zip'"
    powershell -Command "Expand-Archive -Path '.mvn\wrapper\maven.zip' -DestinationPath '.mvn\wrapper' -Force"
    move ".mvn\wrapper\apache-maven-%MAVEN_VERSION%" ".mvn\wrapper\maven"
    del ".mvn\wrapper\maven.zip"
)

".mvn\wrapper\maven\bin\mvn.cmd" %*
