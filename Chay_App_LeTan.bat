@echo off
chcp 65001 >nul
title FITLIFE GYM - TRẠM LỄ TÂN & BÁN HÀNG
echo ========================================================
echo   KHỞI CHẠY ỨNG DỤNG RIÊNG: TRẠM LỄ TÂN & BÁN HÀNG
echo ========================================================
echo.
cd /d "%~dp0\GYM"
call mvn compile exec:java -Dexec.mainClass="com.nhom12.gym.AppLeTan"
pause
