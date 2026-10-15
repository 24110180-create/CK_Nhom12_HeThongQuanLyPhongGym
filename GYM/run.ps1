# FitLife Gym Management - Khoi dong ung dung truc tuyen (Online SQL Server)
$ErrorActionPreference = 'Stop'
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

$userProfile = $env:USERPROFILE
$allJars = Get-ChildItem -Path "$userProfile\.m2\repository" -Filter *.jar -Recurse

# Loc bo cac thu vien Hibernate 5 va javax.persistence cu bi xung dot de dam bao ket noi truc tiep vao SQL Server
$filtered = $allJars | Where-Object {
    $p = $_.FullName.Replace('\', '/')
    -not ($p -match '/hibernate-core/5\.' -or 
          $p -match '/hibernate-core/6\.2\.' -or
          $p -match 'hibernate-commons-annotations/5\.' -or
          $p -match 'javax/persistence' -or
          $p -match 'jakarta\.persistence-api/2\.')
}

$jars = ($filtered | ForEach-Object { $_.FullName.Replace('\', '/') }) -join ';'
$cp = "src/main/resources;target/classes;$jars"

& java "-Dfile.encoding=UTF-8" -cp "$cp" com.nhom12.gym.gui.LoginForm
