@echo off
setlocal EnableDelayedExpansion

:: Location of the JDK bin
set "jdkBin=C:\Program Files\Java\jdk-16.0.1\bin"

:: Location of the Launch4j executable
set "launch4j=C:\Program Files (x86)\Launch4j\launch4j.exe"

set "name=ACES Equipment Builder"
set "src=%~dp0src"
set "res=%~dp0resources"
set "classes=%~dp0classes"
set "install=%~dp0install"
set "jar=%install%\%name%.jar"
set "jre=%install%\jre"
set "wrapConfig=%~dp0launch4j_config.xml"

title %name% Development Utility
:main
cls
echo.
echo %name% Development Utility
echo.
echo Enter '?' for a list of commands.
echo.
:loop
  set "cmd="
  set /p "cmd=>"
  for /f "tokens=1,*" %%a in ("%cmd%") do (
    if /i "%%a" EQU "cls" (
      goto main
    ) else if /i "%%a" EQU "?" (
      call :help %%b
    ) else if /i "%%a" EQU "clean" (
      call :clean %%b
    ) else if /i "%%a" EQU "build" (
      call :build %%b
    ) else if /i "%%a" EQU "pack" (
      call :pack %%b
    ) else if /i "%%a" EQU "make" (
      call :make %%b
    ) else if /i "%%a" EQU "exec" (
      call :exec %%b
    ) else if /i "%%a" EQU "jre" (
      call :jre %%b
    ) else if /i "%%a" EQU "wrap" (
      call :wrap %%b
    ) else (
      call %%a %%b
    )
  )
  goto loop

:help
  echo clean    -  deletes .class files
  echo build    -  calls clean and compiles .java files
  echo pack     -  packages class files and resources into .jar archive
  echo wrap     -  uses Launch4j to create executable wrapper
  echo make     -  calls build and pack
  echo exec     -  executes the jar file
  echo jre      -  creates a custom JRE
  exit /b

:wrap
  "%launch4j%" "%wrapConfig%"
  exit /b

::optional parameter to clean only the specified subdirectory
:clean
  if exist "%classes%\%*\*" (
    rmdir /Q /S "%classes%\%*" 2>nul
  )
  exit /b

::optional parameter to clean and build only a specified file or subdirectory
::omit file extensions
:build
  call :clean %*
  setlocal
  if exist "%src%\%*\*" (
    set "param=%src%\%*"
    set "match=*.java"
  ) else if exist "%src%\%*.java" (
    set "param=%src%\%*.java"
    set "match=."
  )
  for /r "%param%" %%i in (%match%) do (
    "%jdkBin%\javac" -implicit:none -d "%classes%" -cp "%src%" "%%~fi"
  )
  endlocal
  exit /b

::creates the jar archive
:pack
  copy /y "%~dp0LICENSE" "%install%\LICENSE.txt" >nul
  if exist "%classes%" (
    del /F "%jar%" 2>nul
    "%jdkBin%\jar" -c -M -f "%jar%" -C "%classes%" . -C "%res%" .
  ) else (
    echo Please use the 'build' command to compile source code.
  )
  exit /b

::parameters passed to build
:make
  call :build %*
  call :pack
  exit /b

::executes the jar file
:exec
  if not exist "%jar%" (
    echo Please use the 'pack' command to create a .jar archive.
    exit /b
  )
  if not exist "%jre%\bin" (
    echo Please use the 'jre' command to create a custom runtime image.
    exit /b
  )
  if exist "%install%\%name%.exe" (
    "%install%\%name%.exe"
  ) else (
    echo Please use the 'wrap' command to generate an executable wrapper.
    "%jre%\bin\java" -cp "%install%\*" ACESEquipmentBuilder
  )
  exit /b

::create a custom JRE
:jre
  if exist "%jar%" (
    rmdir /Q /S "%jre%" 2>nul
    setlocal
    for /f "delims=" %%i in ('""%jdkBin%\jdeps" --print-module-deps "%jar%""') do set "modules=%%i"
    "%jdkBin%\jlink" --output "%jre%" --add-modules !modules!
    endlocal
  ) else (
    echo Please use the 'pack' command to create a .jar archive.
  )
  exit /b