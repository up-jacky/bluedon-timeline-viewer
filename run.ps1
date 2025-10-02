# made this lang because I code on powershell HAHAHAHA for compile + running without IDE 
$JAVA_FX = "C:\javafx-sdk-17.0.16"
$SRC = "src"
$BIN = "bin"
$RES = "resources"

if (!(Test-Path $BIN)) {
    New-Item -ItemType Directory -Path $BIN | Out-Null
}

Write-Output "Compiling..."
#javac --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web `
 #     -d $BIN (Get-ChildItem -Recurse -Filter *.java $SRC | ForEach-Object { $_.FullName })

javac --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web -d bin src\application\Main.java src\viewer\*.java




if ($LASTEXITCODE -ne 0) {
    Write-Output "Compilation failed."
    exit $LASTEXITCODE
}

Write-Output "Copying resources..."
Copy-Item -Recurse -Path $RES\* -Destination $BIN -Force

Write-Output "Running..."
java --module-path "$JAVA_FX\lib" --add-modules javafx.controls,javafx.fxml,javafx.web `
     -cp $BIN application.Main