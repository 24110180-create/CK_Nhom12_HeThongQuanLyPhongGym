# FitLife Gym Management System - Đã hoàn thành 99%
if (!(Test-Path "target\classes")) {
    New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null
}
$jars = (Get-ChildItem -Path "$env:USERPROFILE\.m2\repository" -Filter *.jar -Recurse | ForEach-Object { $_.FullName.Replace('\', '/') }) -join ';'
$sources = Get-ChildItem -Path "src\main\java" -Filter *.java -Recurse | ForEach-Object { $_.FullName.Replace('\', '/') }

$argsFile = "$PSScriptRoot/javac_args.txt"
$argsList = [System.Collections.Generic.List[string]]::new()
$argsList.Add("-encoding")
$argsList.Add("UTF-8")
$argsList.Add("-cp")
$argsList.Add("`"$jars;target/classes`"")
$argsList.Add("-d")
$argsList.Add("target/classes")
foreach ($s in $sources) {
    $argsList.Add("`"$s`"")
}

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllLines($argsFile, $argsList, $utf8NoBom)

& javac "@$argsFile"
$ec = $LASTEXITCODE
Remove-Item $argsFile -Force -ErrorAction SilentlyContinue
if ($ec -eq 0) {
    Write-Host "COMPILE SUCCESSFUL!"
} else {
    Write-Host "COMPILE FAILED with code $ec"
}
