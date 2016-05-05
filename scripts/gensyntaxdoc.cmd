@echo off
rem Here are instructions how to generate HTML with Caesar syntax definitions
rem -------------------------------------------------------------------------
rem Download ANTLR from http://www.antlr.org/ 
rem Adjust ANTLRDIR in the script
rem Use build.xml on target "scanner" to generate "CaesarTokenTypes.txt"
rem Copy src\org\caesarj\compiler\CaesarTokenTypes.txt to the script directory
rem Copy cc\Caesar.g to the script directory
rem Run the script
rem Remove or fix the problematic lines in Caesar.g 
rem Run the script again

set ANTLRDIR=C:\Java\antlr-2.7.5rc2

set CLASSPATH=%ANTLRDIR%\antlr.jar

java antlr.Tool -html Caesar.g

pause