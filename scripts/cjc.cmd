@echo off
rem CaesarJ compiler

if not "%JAVA_HOME%" == "" goto ok1
echo please set JAVA_HOME environment variable
goto end

:ok1
if not "%CAESAR_HOME%" == "" goto ok2
echo please set CAESAR_HOME environment variable
goto end

:ok2
set CAESAR_LIBS=%CAESAR_HOME%\lib\caesar-compiler.jar;%CAESAR_HOME%\lib\aspectjtools.jar

"%JAVA_HOME%\bin\java" -classpath %CAESAR_LIBS% org.caesarj.compiler.Main %*

:end