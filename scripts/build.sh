#!/bin/sh
#---
# Sets the variables.
#---
CCI_HOME="$HOME"/java/eclipse/workspace/CaesarCI
KOPI_HOME="$HOME"/java/kopi-2.1B
CLASSROOT="$KOPI_HOME"/classes
JAVA="$JAVA_HOME"/bin/java
JAVAC="$JAVA_HOME"/bin/javac
#---
# Updates the kopi.
#---
rm "$KOPI_HOME"/src/caesar/*.*
cp "$CCI_HOME"/cc/*.* "$KOPI_HOME"/src/caesar/
cp -R "$CCI_HOME"/bin/caesar "$CLASSROOT"
cp -R "$CCI_HOME"/bin/familyj "$CLASSROOT"
cp -R "$CCI_HOME"/bin/at "$CLASSROOT"
cp -R "$CCI_HOME"/cc/skeleton.shared "$CLASSROOT"/at/dms/compiler
#---
# Sets Class path
#---
CLASSPATH="$CLASSROOT":"$CCI_HOME"/lib/JFlex.jar:"$CCI_HOME"/lib/java-getopt-1.0.9.jar
export CLASSPATH
#---
# Generates the code.
#---
export CLASSROOT
export JAVA
export JAVAC
cd "$KOPI_HOME"/src
if make
  then
  	cp "$KOPI_HOME"/src/caesar/*.java "$CCI_HOME"/src/caesar/ci/compiler
    echo "______________________________"
    echo "OK, files generated sucefully."
    echo "------------------------------"
  else
    echo "__________________________________________"
    echo "OPS! Errors occured during the generation."
    echo "------------------------------------------"
fi
