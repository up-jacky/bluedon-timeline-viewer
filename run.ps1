# made this lang because I code on powershell HAHAHAHA for compile + running without IDE 
<<<<<<< HEAD:run-java.ps1

$JAVA_FX = "$env:USERPROFILE\openjfx-17.0.16_windows-x64_bin-sdk\javafx-sdk-17.0.16"
=======
$JAVA_FX = "javafx-sdk-17.0.16"
>>>>>>> 0df47795e428df4134731bc26f7c984f9c4492d9:run.ps1
$SRC = "src"
$BIN = "bin"
$RES = "resources"

if (!(Test-Path $BIN)) {
    New-Item -ItemType Directory -Path $BIN | Out-Null
}

Write-Output "Compiling..."
javac --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml `
      -d $BIN (Get-ChildItem -Recurse -Filter *.java $SRC | ForEach-Object { $_.FullName })

if ($LASTEXITCODE -ne 0) {
    Write-Output "Compilation failed."
    exit $LASTEXITCODE
}

Write-Output "Copying resources..."
Copy-Item -Recurse -Path $RES\* -Destination $BIN -Force

Write-Output "Running..."
java --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml `
     -cp $BIN application.Main