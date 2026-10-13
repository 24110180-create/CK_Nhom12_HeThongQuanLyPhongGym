@echo off
chcp 65001 >nul
title FITLIFE GYM MANAGEMENT SYSTEM - HỆ THỐNG TOÀN DIỆN
echo ========================================================
echo   KHỞI CHẠY HỆ THỐNG QUẢN LÝ PHÒNG GYM FITLIFE
echo ========================================================
echo.
cd /d "%~dp0\GYM"
call mvn compile exec:java -Dexec.mainClass="com.nhom12.gym.gui.LoginForm"
pause
