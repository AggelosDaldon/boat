@echo off
setlocal
cd /d "%~dp0"
echo === Boatism Forge Stage 13 build ===
where java >nul 2>nul || (echo ERROR: Java missing & exit /b 1)
java -version
where gradle >nul 2>nul || (echo ERROR: Gradle missing from PATH. Install Gradle 8.x. & exit /b 1)
if not exist "build.gradle" (echo ERROR: build.gradle missing & exit /b 1)
gradle --no-daemon clean build --stacktrace > stage13-build.log 2>&1
set CODE=%ERRORLEVEL%
type stage13-build.log
if not "%CODE%"=="0" (
 echo BUILD FAILED. Send stage13-build.log for diagnosis.
 exit /b %CODE%
)
echo BUILD SUCCESSFUL. Check build\libs\
dir build\libs\*.jar
