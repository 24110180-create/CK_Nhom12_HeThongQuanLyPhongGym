@echo off
chcp 65001 >nul
title FITLIFE GYM - ĐIỀU HÀNH QUẢN LÝ CHI NHÁNH
echo ========================================================
echo   KHỞI CHẠY ỨNG DỤNG RIÊNG: ĐIỀU HÀNH QUẢN LÝ CHI NHÁNH
echo ========================================================
echo.
cd /d "%~dp0\GYM"
call mvn compile exec:java -Dexec.mainClass="com.nhom12.gym.AppQuanLy"
pause
