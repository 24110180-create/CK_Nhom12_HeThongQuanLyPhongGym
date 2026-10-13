@echo off
chcp 65001 >nul
title FITLIFE GYM - KHÔNG GIAN HUẤN LUYỆN VIÊN (PT)
echo ========================================================
echo   KHỞI CHẠY ỨNG DỤNG RIÊNG: KHÔNG GIAN HUẤN LUYỆN VIÊN
echo ========================================================
echo.
cd /d "%~dp0\GYM"
call mvn compile exec:java -Dexec.mainClass="com.nhom12.gym.AppHLV"
pause
