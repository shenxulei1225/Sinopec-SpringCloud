@echo off
REM platform 资源库：编译本模块（路径已迁入 cheers-module-platform）
cd /d "%~dp0"
call mvn clean install -DskipTests
exit /b %ERRORLEVEL%
