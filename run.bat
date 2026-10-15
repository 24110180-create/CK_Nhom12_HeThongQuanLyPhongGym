@echo off
chcp 65001 >nul
title FitLife Gym Management System
cd /d "%~dp0"
if exist "GYM" cd /d "%~dp0GYM"

echo ========================================================
echo   DANG KHOI DONG FITLIFE GYM MANAGEMENT SYSTEM (ONLINE)
echo ========================================================

powershell -NoProfile -ExecutionPolicy Bypass -File "run.ps1"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [LOI] Khong the khoi dong ung dung!
    pause
)
