
This folder contains the files that are used to
generate the familyj lexer

Fj.t
Fj.flex.in

which result in

FjScanner.java
FjTokenTypes.java.

And the file that is used to generate the familyj 
parser

Fj.g

which results in

FjParser.java.

And the file that is used to generate additional
fj-compiler messages

FjMessages.msg

which results in

FjMessages.java.

There had to be some patches within kopi sources
to enable our features. Although I tried to keep
such changes as small as possible, the following
files had to be updated:

at/dms/classfile/Constants.java
at/dms/kjc/CClassContext.java
at/dms/kjc/CInterfaceContext.java
at/dms/kjc/CModifier.java
at/dms/kjc/CVoidType.java
at/dms/kjc/JTypeDeclaration.java
at/dms/kjc/JClassDeclaration.java
at/dms/kjc/JInterfaceDeclaration.java
at/dms/kjc/JExpression.java
at/dms/kjc/JNameExpression.java
at/dms/kjc/JUnaryPromote.java
at/dms/kjc/JOwnerExpression.java
at/dms/kjc/KjcPrettyPrinter.java
at/dms/kjc/KjcOptions.java
at/dms/kjc/Main.java
at/dms/optimize/InstructionHandle.java
at/dms/optimize/Optimizer.java

Please note that all changes I introduced are
marked with my name, so just have a look for
"andreas" to find them.