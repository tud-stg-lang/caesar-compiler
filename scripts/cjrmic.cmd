@echo off
rem CaesarJ RMI compiler

if not "%JAVA_HOME%" == "" goto ok1
echo please set JAVA_HOME environment variable
goto end

:ok1
if not "%CAESAR_HOME%" == "" goto ok2
echo please set CAESAR_HOME environment variable
goto end

:ok2
set CAESAR_LIBS=%CAESAR_HOME%\lib\caesar-runtime.jar;%CAESAR_HOME%\lib\cj-rmic.jar

"%JAVA_HOME%\bin\java" -classpath %CAESAR_LIBS% org.caesarj.rmi.Compiler %*

:end