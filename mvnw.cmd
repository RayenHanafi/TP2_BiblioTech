@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper script for Windows
@echo off
setlocal

set WRAPPER_JAR="%~dp0\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
set MAVEN_PROJECTBASEDIR=%~dp0

@REM Download maven-wrapper.jar if not present
if not exist %WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL:"=%', '%WRAPPER_JAR:"=%')"
)

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome
set JAVA_EXE=java.exe
goto execute

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:execute
@REM Simple approach: download Maven and run it
set MAVEN_DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
set MAVEN_HOME_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6
set MVN_CMD=%MAVEN_HOME_DIR%\bin\mvn.cmd

if exist "%MVN_CMD%" goto runMaven

echo Downloading Apache Maven 3.9.6...
mkdir "%MAVEN_HOME_DIR%" 2>nul
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_DIST_URL%' -OutFile '%TEMP%\maven.zip' -UseBasicParsing; Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"
@REM The zip extracts to apache-maven-3.9.6 inside the dists folder
set MAVEN_HOME_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6
set MVN_CMD=%MAVEN_HOME_DIR%\bin\mvn.cmd

:runMaven
"%MVN_CMD%" %*
