@echo off
REM echo Are you sure you want to update the workbench files?
REM pause

SET ECLIPSEDIR=c:\programme\eclipse

REM we need KOPIDIR with backslashes
SET KOPIDIR=c:\eigene~1\diplom\kopi\kopi-andreas
REM and with slashes
SET KOPIDIR_=c:/eigene~1/diplom/kopi/kopi-andreas

SET CYGBINDIR=c:\programme\cygwin\bin
SET CYGBINDIR_=c:/programme/cygwin/bin
SET LIBEXTDIR=c:\eigene~1\diplom\kopi\libext
SET JAVADIR=c:/programme/jdk-1.4.0

SET RESULTFILE=%ECLIPSEDIR%\workspace\familyj\cc\out.txt

REM echo *************************
echo update cc src-files
REM echo *************************

copy %ECLIPSEDIR%\workspace\familyj\cc\*.* %KOPIDIR%\src\familyj > %RESULTFILE%

REM echo ********************************
echo update familyj class-files
REM echo ********************************

rem w2000 rd %KOPIDIR%\classes\familyj /s /q
deltree /y %KOPIDIR%\classes\familyj >> %RESULTFILE%
mkdir %KOPIDIR%\classes\familyj  >> %RESULTFILE%
xcopy %ECLIPSEDIR%\workspace\familyj\bin\familyj %KOPIDIR%\classes\familyj /S  >> %RESULTFILE%

REM echo *****************************
echo update kopi class-files
REM echo *****************************

rem w2000 rd %KOPIDIR%\classes\at /s /q
deltree /y %KOPIDIR%\classes\at >> %RESULTFILE%
mkdir %KOPIDIR%\classes\at >> %RESULTFILE%
xcopy %ECLIPSEDIR%\workspace\familyj\bin\at %KOPIDIR%\classes\at /S >> %RESULTFILE%
copy %ECLIPSEDIR%\workspace\familyj\cc\skeleton.shared %KOPIDIR%\classes\at\dms\compiler >> %RESULTFILE%

REM echo *******************
echo perform build
REM echo *******************

SET PATH=%CYGBINDIR%;%PATH%
SET SHELL=%CYGBINDIR_%/sh.exe

SET CLASSROOT=%KOPIDIR_%/classes
REM important: use jflex 1.3.2 - not greater!
SET CLASSPATH=%CLASSROOT%;%LIBEXTDIR%\jflex.jar;%LIBEXTDIR%\java-getopt-1.0.9.jar

SET JAVA=%JAVADIR%/bin/java
SET JAVAC=%JAVADIR%/bin/javac

cd %KOPIDIR%\src
make SHELL=%SHELL%
REM >> %RESULTFILE%

REM echo ****************************
echo update workbench files
REM echo ****************************

copy %KOPIDIR%\src\familyj\*.java %ECLIPSEDIR%\workspace\familyj\src\familyj\compiler >> %RESULTFILE%
REM start notepad %RESULTFILE%