@echo off
chcp 65001 >nul
title FitLife Gym Management System
cd /d "%~dp0"
if exist "GYM" cd /d "%~dp0GYM"

echo ========================================================
echo   DANG KHOI DONG FITLIFE GYM MANAGEMENT SYSTEM
echo ========================================================

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$cp = (Get-ChildItem -Path '%USERPROFILE%\.m2\repository' -Filter '*.jar' -Recurse | Select-Object -ExpandProperty FullName) -join ';'; " ^
  "$cp = 'src\main\resources;target\classes;' + $cp; " ^
  "java -cp $cp com.nhom12.gym.gui.LoginForm"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [LOI] Khong the khoi dong ung dung!
    pause
)