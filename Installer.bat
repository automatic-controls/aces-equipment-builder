@echo off

:: Get all parameters passed to the script
set "install=%*"

:: If an installation directory is not specified, use the default
if "%install%" EQU "" (
  set "install=%LocalAppData%\Programs\ACES Equipment Builder"
)

:: Ensure software developers don't try to update from the WIP instance of ACES EB
if "%install%" EQU "%~dp0install" (
  echo ACES EB is already up to date!
  echo Press any key to exit.
  pause >nul
  exit
)

setlocal EnableDelayedExpansion

  :: Retrieve properties from the previous configuration file
  set "conf=%install%\config.txt"
  set "exists=0"
  set "Developer="
  set "VersionTag="
  set "WebCTRLPath="
  set "AutoSync="
  set "SyncLibrary="
  set "SyncFavorites="
  set "SyncScripts="
  if exist "%conf%" (
    set "exists=1"
    for /f "usebackq tokens=1,* delims==" %%i in ("%conf%") do (
      set "%%i=%%j"
    )
  )

  :: Copy installation files to the specified directory
  robocopy "%~dp0install" "%install%" /MIR /XD "%~dp0install\lib" "%install%\lib" /XF "%~dp0install\log.txt" "%install%\log.txt"

  :: Write a few of the old properties to the new configuration file
  if "%exists%" EQU "1" (
    echo.
    if "!WebCTRLPath!" NEQ "" echo WebCTRLPath=!WebCTRLPath!
    if "!Developer!" NEQ "" echo Developer=!Developer!
    if "!VersionTag!" NEQ "" echo VersionTag=!VersionTag!
    if "!AutoSync!" NEQ "" echo AutoSync=!AutoSync!
    if "!SyncLibrary!" NEQ "" echo SyncLibrary=!SyncLibrary!
    if "!SyncFavorites!" NEQ "" echo SyncFavorites=!SyncFavorites!
    if "!SyncScripts!" NEQ "" echo SyncScripts=!SyncScripts!
  ) >> "%conf%"

endlocal

:: Create a shortbut to "ACES Equipment Builder.exe" on the desktop
set "shortcut=%USERPROFILE%\Desktop\ACES Equipment Builder.lnk"
set "shortcutVBS=%Temp%\ShortcutCreator.vbs"
if exist "%shortcut%" (
  del /f /q "%shortcut%"
)
call :createShortcut "%shortcut%" "%install%\ACES Equipment Builder.exe"

echo Operation completed.

:: Launch the application
echo CreateObject^("WScript.Shell"^).Run """%install%\jre\bin\java"" -cp ""%install%\ACES Equipment Builder.jar"" ACESEquipmentBuilder", 0, FALSE > "%install%\launcher.vbs"
wscript "%install%\launcher.vbs"
del /F "%install%\launcher.vbs" >nul 2>nul

timeout /t 1 >nul
exit


:: Create a shortbut to "ACES Equipment Builder.exe" on the desktop
:createShortcut
  (
    echo Set ws = WScript.CreateObject^("WScript.Shell"^)
    echo Set link = ws.CreateShortcut^("%~1"^)
    echo link.TargetPath = "%~2"
    echo link.Save
  )>"%shortcutVBS%"
  wscript "%shortcutVBS%"
  if exist "%shortcutVBS%" (
    del /f /q "%shortcutVBS%"
  )
exit /b

:: OPTIONAL function which could be used to auto-update the corresponding VSCode extension
:vscode
  call code --version >nul 2>nul
  if "%ERRORLEVEL%" EQU "0" (
    echo Examining VSCode installation...
    setlocal EnableDelayedExpansion
      set "ver="
      for /f "tokens=2 delims=@" %%i in ('code --list-extensions --show-versions ^| findstr /L "ACES.aces-eb-language-support@"') do (
        set "ver=%%i"
      )
      :: Specify the target version here
      if "!ver!" NEQ "1.0.2" (
        if "!ver!" NEQ "" (
          call code --uninstall-extension ACES.aces-eb-language-support
        )
        call code --install-extension "%~dp0aces-eb-language-support.vsix"
      )
    endlocal
  )
exit /b