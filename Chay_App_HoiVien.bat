@echo off
chcp 65001 >nul
title FITLIFE MEMBER APP - DÀNH RIÊNG CHO HỘI VIÊN
echo ========================================================
echo   KHỞI CHẠY ỨNG DỤNG RIÊNG DÀNH CHO HỘI VIÊN FITLIFE
echo ========================================================
echo.
cd /d "%~dp0\GYM"
call mvn compile exec:java -Dexec.mainClass="com.nhom12.gym.AppHoiVien"
pause
