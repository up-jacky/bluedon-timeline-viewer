# made this lang because I code on powershell HAHAHAHA for compile + running without IDE 
$JAVA_FX = "C:\javafx-sdk-17.0.16"
$SRC = "src"
$BIN = "bin"
$RES = "resources"
# Always run from repo root
Set-Location -Path (Split-Path -Parent $MyInvocation.MyCommand.Definition)

if (!(Test-Path $BIN)) {
    New-Item -ItemType Directory -Path $BIN | Out-Null
}
Write-Output "Compiling..."
#javac --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web `
 #     -d $BIN (Get-ChildItem -Recurse -Filter *.java $SRC | ForEach-Object { $_.FullName })

# javac --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web `
#       -cp "src\libs\*" -d bin src\application\Main.java src\viewer\*.java src\oauthServices\*.java

javac --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web `
  -cp "src/libs/*" -d bin src\application\*.java src\viewer\*.java src\oauthServices\*.java


if ($LASTEXITCODE -ne 0) {
    Write-Output "Compilation failed."
    exit $LASTEXITCODE
}

Write-Output "Copying resources..."
Copy-Item -Recurse -Path $RES\* -Destination $BIN -Force

Write-Output "Running..."
java --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web `
  -cp "bin;src/libs/*" application.Main


