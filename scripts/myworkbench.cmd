@echo off
REM echo Are you sure you want to update the workbench files?
REM pause

SET ECLIPSEDIR=E:\andreas\eclipse

REM we need KOPIDIR with backslashes
SET KOPIDIR=E:\andreas\kopi\kopi-andreas
REM and with slashes
SET KOPIDIR_=E:/andreas/kopi/kopi-andreas

SET CYGBINDIR=f:/eliasdir/cygwin/bin
SET LIBEXTDIR=e:\andreas\kopi\libext
SET JAVADIR=e:/andreas/jdk-1.4.0

echo *************************
echo ** update cc src-files **
echo *************************

copy %ECLIPSEDIR%\workspace\familyj\cc\*.* %KOPIDIR%\src\familyj

echo ********************************
echo ** update familyj class-files **
echo ********************************

rd %KOPIDIR%\classes\familyj /s /q
mkdir %KOPIDIR%\classes\familyj
xcopy %ECLIPSEDIR%\workspace\familyj\bin\familyj %KOPIDIR%\classes\familyj /S

echo *****************************
echo ** update kopi class-files **
echo *****************************

rd %KOPIDIR%\classes\at /s /q
mkdir %KOPIDIR%\classes\at
xcopy %ECLIPSEDIR%\workspace\familyj\bin\at %KOPIDIR%\classes\at /S
copy %ECLIPSEDIR%\workspace\familyj\cc\skeleton.shared %KOPIDIR%\classes\at\dms\compiler

echo *******************
echo ** perform build **
echo *******************

SET PATH=%CYGBINDIR%;%PATH%
SET SHELL=%CYGBINDIR%/sh.exe

SET CLASSROOT=%KOPIDIR_%/classes
REM important: use jflex 1.3.2 - not greater!
SET CLASSPATH=%CLASSROOT%;%LIBEXTDIR%\jflex.jar;%LIBEXTDIR%\java-getopt-1.0.9.jar

SET JAVA=%JAVADIR%/bin/java
SET JAVAC=%JAVADIR%/bin/javac

cd %KOPIDIR%\src
make SHELL=%SHELL%

echo ****************************
echo ** update workbench files **
echo ****************************

copy %KOPIDIR%\src\familyj\*.java %ECLIPSEDIR%\workspace\familyj\src\familyj\compiler

pause