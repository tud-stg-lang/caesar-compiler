REM @echo off
REM echo Are you sure you want to update the workbench files?
REM pause

SET ECLIPSEDIR=C:\java\eclipse

REM we need KOPIDIR with backslashes
SET KOPIDIR=C:\java\kopi
REM and with slashes
SET KOPIDIR_=C:/java/kopi

SET CYGBINDIR=C:/Programme/cygwin/bin
SET LIBEXTDIR=C:\java\eclipse\workspace\CaesarCI\lib
SET JAVADIR=c:/j2sdk1.4.2

echo *************************
echo ** update cc src-files **
echo *************************

copy %ECLIPSEDIR%\workspace\CaesarCI\cc\*.* %KOPIDIR%\src\caesar

echo ********************************
echo ** update caesar class-files **
echo ********************************

rd %KOPIDIR%\classes\org /s /q
mkdir %KOPIDIR%\classes\org
xcopy %ECLIPSEDIR%\workspace\CaesarCI\bin\org %KOPIDIR%\classes\org /S

copy %ECLIPSEDIR%\workspace\CaesarCI\cc\skeleton.shared %KOPIDIR%\classes\org\caesarj\compiler

echo *******************
echo ** perform build **
echo *******************

SET PATH=C:\Programme\cygwin\bin;%PATH%
SET SHELL=%CYGBINDIR%/sh.exe

SET CLASSROOT=%KOPIDIR_%/classes
REM important: use jflex 1.3.2 - not greater!
SET CLASSPATH=%CLASSROOT%;%LIBEXTDIR%\jflex.jar;%LIBEXTDIR%\java-getopt-1.0.9.jar;%LIBEXTDIR%\aspectjtools.jar

SET JAVA=%JAVADIR%/bin/java
SET JAVAC=%JAVADIR%/bin/javac

cd %KOPIDIR%\src
make SHELL=%SHELL%

echo ****************************
echo ** update workbench files **
echo ****************************

copy %KOPIDIR%\src\caesar\*.java %ECLIPSEDIR%\workspace\CaesarCI\src\org\caesarj\compiler

pause