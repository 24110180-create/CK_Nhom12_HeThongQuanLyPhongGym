@echo off
chcp 65001 > nul
echo ====================================================================
echo   KHỞI CHẠY HỆ THỐNG QUẢN LÝ PHÒNG GYM FITLIFE (JAVA 17 COMPATIBLE)
echo ====================================================================
setlocal enabledelayedexpansion

set "CLASSES_DIR=%~dp0target\classes"
set "M2_REPO=%USERPROFILE%\.m2\repository"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$jars = (Get-ChildItem -Path '%M2_REPO%' -Filter '*.jar' -Recurse | Where-Object { $_.FullName -notmatch '5\.6\.15' -and $_.FullName -notmatch '6\.2\.7' -and $_.FullName -notmatch '5\.1\.2' } | Select-Object -ExpandProperty FullName) -join ';'; ^
  $cp = '%CLASSES_DIR%;' + $jars; ^
  $java17 = 'C:\Program Files\Java\jdk-17\bin\java.exe'; ^
  if (Test-Path $java17) { ^
    & $java17 '-Dfile.encoding=UTF-8' -cp $cp com.nhom12.gym.gui.LoginForm; ^
  } else { ^
    java '-Dfile.encoding=UTF-8' -cp $cp com.nhom12.gym.gui.LoginForm; ^
  }"

pause

