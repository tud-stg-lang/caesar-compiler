/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: Caesar.g,v 1.63 2011-11-03 16:06:01 gasiunas Exp $
 */

/*
 * This grammar is based on the Java 1.2 Recognizer from ANTLR
 *
 * Contributing authors:
 *		John Mitchell		johnm@non.net
 *		Terence Parr		parrt@magelang.com
 *		John Lilley		jlilley@empathy.com
 *		Scott Stanchfield	thetick@magelang.com
 *		Markus Mohnen           mohnen@informatik.rwth-aachen.de
 *		Peter Williams		pwilliams@netdynamics.com
 */

header { package org.caesarj.compiler; }
{
  import java.util.ArrayList;

  import org.caesarj.compiler.ast.*;
  import org.caesarj.compiler.ast.phylum.*;
  import org.caesarj.compiler.ast.phylum.expression.*;
  import org.caesarj.compiler.ast.phylum.expression.literal.*;
  import org.caesarj.compiler.ast.phylum.declaration.*;
  import org.caesarj.compiler.ast.phylum.statement.*;
  import org.caesarj.compiler.ast.phylum.variable.*;
  import org.caesarj.compiler.constants.*;
  import org.caesarj.compiler.aspectj.*;

  import org.caesarj.tools.antlr.extra.InputBuffer;
  import org.caesarj.compiler.context.*;
  import org.caesarj.compiler.types.*;
  import org.caesarj.compiler.export.*;
  import org.caesarj.util.*;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class CaesarParser extends Parser;
options {
  k = 2;                           // two token lookahead
  importVocab = Caesar;
  exportVocab = Caesar;
  codeGenMakeSwitchThreshold = 2;  // Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;     // Don't generate parser error handlers
  superClass = "org.caesarj.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}

{
  public CaesarParser(CompilerBase compiler, InputBuffer buffer, KjcEnvironment environment) {
    super(compiler, new CaesarScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.environment = environment;
  }

  private final KjcEnvironment  environment;
}

// Compilation Unit: this is the start rule for this parser
public jCompilationUnit []
  returns [JCompilationUnit self = null]
{
  JPackageName			pack;
  CParseCompilationUnitContext	context = CParseCompilationUnitContext.getInstance();
  TokenReference		sourceRef = buildTokenReference();
}
:
  pack = jPackageDeclaration[]
    { context.setPackage(pack); }
  jImportDeclarations[context]
  ( jTypeDefinition[context] )*
  EOF
    {
      self = new JCompilationUnit(sourceRef,
                                  environment,
				  pack,
				  context.getPackageImports(),
				  context.getClassImports(),
				  context.getTypeDeclarations());
      context.release();
    }
;

// JLS 7.4 Package Declarations
jPackageDeclaration []
returns [JPackageName self = JPackageName.UNNAMED]
{
  String	name;
}
:
  (
    "package" name = jIdentifier[] SEMI
      { self = new JPackageName(buildTokenReference(), name, getStatementComment(), false); }
  |
    "cclass" name = jIdentifier[] SEMI
      { self = new JPackageName(buildTokenReference(), name, getStatementComment(), true); }  
  )?
;


// JLS 7.5 Import Declarations
jImportDeclarations [CParseCompilationUnitContext context]
:
  jAutomaticImports[context]
  ( jImportDeclaration[context] )*
;

// JLS 7.5.3 Automatic Imports
jAutomaticImports [CParseCompilationUnitContext context]
:
{
  context.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
}
;

jImportDeclaration [CParseCompilationUnitContext context]
{
  StringBuffer	buffer = null;
  boolean	star = false;
  String	name = null;
}
:
  "import" i:IDENT
  (
    DOT j:IDENT
      { (buffer == null ? (buffer = new StringBuffer(i.getText())) : buffer).append('/').append(j.getText()); }
  )*
    { name = buffer == null ? i.getText() : buffer.toString(); }
  ( DOT STAR { star = true; } )?
  SEMI
    {
      if (star) {
	context.addPackageImport(new JPackageImport(buildTokenReference(), name, getStatementComment()));
      } else {
	context.addClassImport(new JClassImport(buildTokenReference(), name, getStatementComment()));
      }
    }
;

// A type definition in a file is either a class or interface definition.
jTypeDefinition [CParseCompilationUnitContext context]
{
  int              mods = 0;
  JTypeDeclaration decl = null;
  TokenReference   sourceRef = buildTokenReference();
}
:
  mods = jModifiers[]
  (
    decl = jClassDefinition[mods]
  |
    decl = jInterfaceDefinition[mods]
  |
  	decl = jCClassDefinition[mods]
  )
    {
       context.addTypeDeclaration(environment.getClassReader(), decl);
    }
  |
    SEMI
      { reportTrouble(new CWarning(sourceRef, KjcMessages.STRAY_SEMICOLON, null)); }
;

// JLS 14.4 : Local Variable Declaration Statement
// A declaration is the creation of a reference or primitive-type variable
// Create a separate Type/Var tree for each var in the var list.
jLocalVariableDeclaration [int modifiers]
  returns [JVariableDeclarationStatement self = null]
{
  CType			type;
  JVariableDefinition[] decl;
  TokenReference	sourceRef = buildTokenReference();
}
:
  type = jTypeSpec[] decl = jVariableDefinitions[modifiers, type]
   {
     self = new JVariableDeclarationStatement(sourceRef,
					      decl,
					      getStatementComment());
   }
;

// A list of zero or more modifiers.  We could have used (modifier)* in
// place of a call to modifiers, but I thought it was a good idea to keep
// this rule separate so they can easily be collected in a ArrayList if
// someone so desires
jModifiers []
  returns [int self = 0]
{
  int		mod;
}
:
  (
    mod = jModifier[]
      {
	if ((mod & self) != 0) {
	  reportTrouble(new PositionedError(buildTokenReference(),
					    KjcMessages.DUPLICATE_MODIFIER,
					    CModifier.getName(mod)));
	}

	if (!CModifier.checkOrder(self, mod)) {
	  reportTrouble(new CWarning(buildTokenReference(),
				     KjcMessages.MODIFIER_ORDER,
				     CModifier.getName(mod)));
	}
	self |= mod;
      }
  )*
    {
      //!!! 010428 move to JXxxDeclaration
      if (CModifier.getSubsetSize(self,
				  org.caesarj.classfile.ClassfileConstants2.ACC_PUBLIC
				  | org.caesarj.classfile.ClassfileConstants2.ACC_PROTECTED
				  | org.caesarj.classfile.ClassfileConstants2.ACC_PRIVATE) > 1) {
	reportTrouble(new PositionedError(buildTokenReference(),
					  KjcMessages.INCOMPATIBLE_MODIFIERS,
					  CModifier.toString(CModifier.getSubsetOf(self,
										   org.caesarj.classfile.ClassfileConstants2.ACC_PUBLIC
										   | org.caesarj.classfile.ClassfileConstants2.ACC_PROTECTED
										   | org.caesarj.classfile.ClassfileConstants2.ACC_PRIVATE))));
      }
    }
;


// A type specification is a type name with possible brackets afterwards
//   (which would make it an array type).
jTypeSpec []
  returns [CType self = null]
:
  self = jClassTypeSpec[]
|
  self = jBuiltInTypeSpec[]
;

// A class type specification is a class type with possible brackets afterwards
//   (which would make it an array type).
jClassTypeSpec []
  returns [CReferenceType self = null]
{
  String		name = null;
  int			bounds = 0;
}
:
  self = jTypeName[]
  (
    LBRACK RBRACK { bounds += 1; }
  )*
    {
      if (bounds > 0) {
	self = new CArrayType(self, bounds);
      }
    }
;

// A builtin type specification is a builtin type with possible brackets
// afterwards (which would make it an array type).
jBuiltInTypeSpec []
  returns [CType self = null]
{
  int			bounds = 0;
}
:
  self = jBuiltInType[]
  (
    LBRACK RBRACK { bounds += 1; }
  )*
    {
      if (bounds > 0) {
	self = new CArrayType(self, bounds);
      }
    }
;

jType []
  returns [CType type = null]
:
  type = jBuiltInType[]
|
  type = jTypeName[]
;

// !!!JLS The primitive types.
jBuiltInType []
  returns [CType self = null]
{
  TypeFactory factory = environment.getTypeFactory();
}
:
  "void" { self = factory.getVoidType(); }
|
  "boolean" { self = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN); }
|
  "byte" { self = factory.getPrimitiveType(TypeFactory.PRM_BYTE); }
|
  "char" { self = factory.getPrimitiveType(TypeFactory.PRM_CHAR); }
|
  "short" { self = factory.getPrimitiveType(TypeFactory.PRM_SHORT); }
|
  "int" { self = factory.getPrimitiveType(TypeFactory.PRM_INT); }
|
  "long" { self = factory.getPrimitiveType(TypeFactory.PRM_LONG); }
|
  "float" { self = factory.getPrimitiveType(TypeFactory.PRM_FLOAT); }
|
  "double" { self = factory.getPrimitiveType(TypeFactory.PRM_DOUBLE); }
;


// A (possibly-qualified) java identifier.  We start with the first IDENT
// and expand its name by adding dots and following IDENTS
jIdentifier []
  returns [String self = null]
{
  StringBuffer buffer = null;
}
:
  i:IDENT
  (
    DOT j:IDENT
      { (buffer == null ? (buffer = new StringBuffer(i.getText())) : buffer).append('/').append(j.getText()); }
  )*
    { self = buffer == null ? i.getText() : buffer.toString(); }
;

// modifiers for Java classes, interfaces, class/instance vars and methods
jModifier []
  returns [int self = 0]
:
  "public" { self = org.caesarj.classfile.ClassfileConstants2.ACC_PUBLIC; }
|
  "protected" { self = org.caesarj.classfile.ClassfileConstants2.ACC_PROTECTED; }
|
  "private" { self = org.caesarj.classfile.ClassfileConstants2.ACC_PRIVATE; }
|
  "static" { self = org.caesarj.classfile.ClassfileConstants2.ACC_STATIC; }
|
  "abstract" { self = org.caesarj.classfile.ClassfileConstants2.ACC_ABSTRACT; }
|
  "final" { self = org.caesarj.classfile.ClassfileConstants2.ACC_FINAL; }
|
  "native" { self = org.caesarj.classfile.ClassfileConstants2.ACC_NATIVE; }
|
  "strictfp" { self = org.caesarj.classfile.ClassfileConstants2.ACC_STRICT; }
|
  "synchronized" { self = org.caesarj.classfile.ClassfileConstants2.ACC_SYNCHRONIZED; }
|
  "transient" { self = org.caesarj.classfile.ClassfileConstants2.ACC_TRANSIENT; }
|
  "volatile" { self = org.caesarj.classfile.ClassfileConstants2.ACC_VOLATILE; }
|
	"privileged" { self = org.caesarj.classfile.ClassfileConstants2.ACC_PRIVILEGED; }
|
	"deployed" { self = org.caesarj.classfile.ClassfileConstants2.ACC_DEPLOYED; }
;



// Definition of a Java class
jClassDefinition [int modifiers]
  returns [JClassDeclaration self = null]
{
  CReferenceType     superClass    = null;
  CReferenceType[]   interfaces    = CReferenceType.EMPTY;
  ParseClassContext  context       = ParseClassContext.getInstance();
  TokenReference     sourceRef     = buildTokenReference();
  JavadocComment     javadoc       = getJavadocComment();
  JavaStyleComment[] comments      = getStatementComment();
}
:
  "class" ident:IDENT
  //This is like this for prevent non-determinism
  (superClass =  jSuperClassClause[])?

  (interfaces = jImplementsClause[])?
   
  jClassBlock[context]
  {
      JMethodDeclaration[]      methods;

      methods = context.getMethods();
      
          self = new JClassDeclaration(sourceRef,
				   modifiers,
				   ident.getText(),
				   superClass,	   
				   interfaces,
				   context.getFields(),
				   methods,
				   context.getInnerClasses(),
				   context.getBody(),
				   javadoc,
				   comments);
			
        context.release();
    }
;

// Definition of a Java class
jCClassDefinition [int modifiers]
  returns [CjClassDeclaration self = null]
{
  CReferenceType[]   superClasses = CReferenceType.EMPTY;
  CReferenceType[]   interfaces = CReferenceType.EMPTY;
  CReferenceType     wrappee = null;
  ParseClassContext  context = ParseClassContext.getInstance();
  TokenReference     sourceRef = buildTokenReference();
  JavadocComment     javadoc = getJavadocComment();
  JavaStyleComment[] comments = getStatementComment();
}
:
  "cclass" ident:IDENT
  //This is like this for prevent non-determinism
  (superClasses =  jCSuperClassClause[])?

  (interfaces = jImplementsClause[])?
  (wrappee = jWrapsClause[])?
   
  jCClassBlock[context]
  {
      JMethodDeclaration[]      methods;

      methods = context.getMethods();
      
          self = new CjVirtualClassDeclaration(sourceRef,
				   modifiers,
				   ident.getText(),
				   superClasses,
				   wrappee,			   
				   interfaces,
				   context.getFields(),
				   methods,
				   context.getInnerClasses(),
				   context.getBody(),
				   javadoc,
				   comments,				   
				   context.getPointcuts(),
				   context.getAdvices(),
				   context.getDeclares(),
				   true);
			
        context.release();
    }
;


jSuperClassClause []
  returns [CReferenceType self = null]
:
  ("extends" self =  jTypeName[])
;


jCSuperClassClause []
  returns [CReferenceType[] self = null]
{
  CReferenceType name;
  ArrayList	container = new ArrayList();
}
:
  ("extends" name =  jTypeName[]) { container.add(new CVirtualClassNameType(name.getQualifiedName())); }
  (
  	BAND
  	name = jTypeName[] { container.add(new CVirtualClassNameType(name.getQualifiedName())); }
  )*
  {
  	self = (CClassNameType[])container.toArray(new CClassNameType[container.size()]);
  }
;


// Definition of a Java Interface
jInterfaceDefinition [int modifiers]
  returns [JTypeDeclaration self = null]
{
  CReferenceType[]		interfaces =  CReferenceType.EMPTY;
  ParseClassContext	context = ParseClassContext.getInstance();
  TokenReference	sourceRef = buildTokenReference();
}
:
  "interface" ident:IDENT

  // it might extend some other interfaces
  interfaces = jInterfaceExtends[]
  // now parse the body of the interface (looks like a class...)
  jClassBlock[context]
    {

        self = new JInterfaceDeclaration(sourceRef,
                                           modifiers,
                                           ident.getText(),
                                           interfaces,
                                           context.getFields(),
                                           context.getMethods(),
                                           context.getInnerClasses(),
                                           context.getBody(),
                                           getJavadocComment(),
                                           getStatementComment());
      context.release();
    }
;


// This is the body of a class.  You can have fields and extra semicolons,
// That's about it (until you see what a field is...)
jClassBlock [ParseClassContext context]
:
  LCURLY
  (
    jMember[context]
  |
    SEMI
      { reportTrouble(new CWarning(buildTokenReference(), KjcMessages.STRAY_SEMICOLON, null)); }
  )*
  RCURLY
;

// Similar to jClassBlock, but this time cclass specific
jCClassBlock [ParseClassContext context]
:
  LCURLY
  (
    jCMember[context]
  |
    SEMI
      { reportTrouble(new CWarning(buildTokenReference(), KjcMessages.STRAY_SEMICOLON, null)); }
  )*
  RCURLY
;

// An interface can extend several other interfaces...
jInterfaceExtends []
  returns [CReferenceType[] self = CReferenceType.EMPTY]
:
  ( "extends" self = jNameList[] )?
;

jWrapsClause[]
  returns [CReferenceType self = null]
:
  ( "wraps" self = jTypeName[] )
;

// A class can implement several interfaces...
jImplementsClause[]
  returns [CReferenceType[] self = CReferenceType.EMPTY]
:
  ( "implements" self = jNameList[] )
;

// Now the various things that can be defined inside a class or interface:
// methods, constructors, or variable declaration
// Note that not all of these are really valid in an interface (constructors,
// for example), and if this grammar were used for a compiler there would
// need to be some semantic checks to make sure we're doing the right thing...
jMember [ParseClassContext context]
{
  int                   modifiers = 0;
  CType                 type;
  JMethodDeclaration    method;
  JTypeDeclaration      decl;
  JVariableDefinition[] vars;
  JStatement[]          body = null;
  TokenReference        sourceRef = buildTokenReference();
  JFormalParameter[]    parameters;
  JFormalParameter      extraParam = null;  
  CaesarAdviceKind      kind = null;
  TypeFactory           factory = environment.getTypeFactory();
  CReferenceType[]      throwsList = CReferenceType.EMPTY;  
}
:
  	(
   		modifiers = jModifiers[]
   		(
    		decl = jClassDefinition[modifiers]              // inner class
      		{ context.addInnerDeclaration(decl); }
  		|
    		decl = jInterfaceDefinition[modifiers]          // inner interface
      		{
        		context.addInnerDeclaration(decl);
      	}
  		|
    		method = jConstructorDefinition[context, modifiers]
      		{ context.addMethodDeclaration(method); }
  		|
    		type = jTypeSpec[]
	    	(
	      		method = jMethodDefinition [context, modifiers, type]
		        { context.addMethodDeclaration(method); }
	    	|
	      		vars = jVariableDefinitions[modifiers, type] SEMI
	        	{
		  			for (int i = 0; i < vars.length; i++) {
			    		context.addFieldDeclaration(new JFieldDeclaration(sourceRef,
																			vars[i],
																			getJavadocComment(),
																			getStatementComment()));
			  		}
				}
	    	)
		)
	)
	|
		// "static { ... }" class initializer
		"static" body = jCompoundStatement[]
    	{ context.addBlockInitializer(new JClassBlock(sourceRef, true, body)); }
	|
	  	// "{ ... }" instance initializer
	 	body = jCompoundStatement[]
    	{ context.addBlockInitializer(new JClassBlock(sourceRef, false, body)); }  	
;

// Similar to jMemeber, but this time cclass specific
jCMember [ParseClassContext context]
{
  int                   modifiers = 0;
  CType                 type;
  JMethodDeclaration    method;
  JTypeDeclaration      decl;
  JVariableDefinition[] vars;
  JStatement[]          body = null;
  TokenReference        sourceRef = buildTokenReference();
  JFormalParameter[]    parameters;
  JFormalParameter      extraParam = null;  
  CaesarAdviceKind      kind = null;
  TypeFactory           factory = environment.getTypeFactory();
  CReferenceType[]      throwsList = CReferenceType.EMPTY;  
  CjPointcutDeclaration pointcutDecl;
  CjAdviceDeclaration   adviceDecl;
}
:
  	(
   		modifiers = jModifiers[]
   		(
    		decl = jCClassDefinition[modifiers]              // inner class
      		{ context.addInnerDeclaration(decl); }
  		|
    		method = jConstructorDefinition[context, modifiers]
      		{ context.addMethodDeclaration(method); }
  		|
	  		//pointcut declaration  	
  			"pointcut" pointcutDecl = jPointcutDefinition[modifiers] 
  			{ 
  				context.addPointcutDeclaration(pointcutDecl);	
  			}
    	|	
        	"declare"
    		pattern : TYPE_PATTERN
    		{
    		
				try {

					CaesarPatternParser patternParser = new CaesarPatternParser(
															"declare "+pattern.getText(),
															new CaesarSourceContext(sourceRef) );
					context.addDeclare(patternParser.parseDeclare());		
					
				} catch(CaesarPatternParser.CaesarParserException e) {
					reportTrouble(new PositionedError(sourceRef, CaesarMessages.DECLARE_SYNTAX_ERROR, e.getMessage()));			
				} catch(RuntimeException e) {
			  		reportTrouble(new PositionedError(sourceRef, CaesarMessages.DECLARE_SYNTAX_ERROR, e.getMessage()));							
				}  			
			}  			
	  	|  	  	
  			//before advice declaration
  			"before"
	  		LPAREN  parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
	  		(throwsList = jThrowsClause[])?   	
 			adviceDecl = jCjAdviceDeclaration[CaesarAdviceKind.Before, modifiers, parameters, factory.getVoidType(), throwsList, null]
			{ 	
				context.addAdviceDeclaration(adviceDecl);
			}  	 		  		 	 
	  	|
  			//after advice declaration
  			 "after" {kind = CaesarAdviceKind.After;}
	  		LPAREN  parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
			(
				"returning"
				(LPAREN 
	  			extraParam = jParameterDeclaration[JLocalVariable.DES_GENERATED] 
  				RPAREN)?  
  				{kind = CaesarAdviceKind.AfterReturning;}
	  		|
  				"throwing" 
  				(LPAREN 
	  			extraParam = jParameterDeclaration[JLocalVariable.DES_GENERATED] 
  				RPAREN)? 
  				{kind = CaesarAdviceKind.AfterThrowing;}
			)?
			(throwsList = jThrowsClause[])?   	
 			adviceDecl = jCjAdviceDeclaration[kind, modifiers, parameters, factory.getVoidType(), throwsList, extraParam]   
			{ 
				context.addAdviceDeclaration(adviceDecl);			
			}  	 		  		 	 
	  	| 
  		  	// around advice declaration
    		type = jTypeSpec[]
	    	(
	  	  		"around"		  	
		      	LPAREN  parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN   	
	  		  	(throwsList = jThrowsClause[])?
	 	  		adviceDecl = jCjAdviceDeclaration[CaesarAdviceKind.Around, modifiers, parameters, type, throwsList, extraParam] 	
		  		{ 
		  			context.addAdviceDeclaration(adviceDecl);	  			
		  		}  	 		  		    
	    	|
	      		method = jMethodDefinition [context, modifiers, type]
		        { context.addMethodDeclaration(method); }
	    	|
	      		vars = jVariableDefinitions[modifiers, type] SEMI
	        	{
		  			for (int i = 0; i < vars.length; i++) {
			    		context.addFieldDeclaration(new JFieldDeclaration(sourceRef,
																			vars[i],
																			getJavadocComment(),
																			getStatementComment()));
			  		}
				}
	    	)
		)
	)
	|
		// "static { ... }" class initializer
		"static" body = jCompoundStatement[]
    	{ context.addBlockInitializer(new JClassBlock(sourceRef, true, body)); }
	|
	  	// "{ ... }" instance initializer
	 	body = jCompoundStatement[]
    	{ context.addBlockInitializer(new JClassBlock(sourceRef, false, body)); }  	
;

jConstructorDefinition [ParseClassContext context, int modifiers]
  returns [JConstructorDeclaration self = null]
{
  JFormalParameter[]	parameters;
  CReferenceType[]		throwsList = CReferenceType.EMPTY;
  JConstructorCall	constructorCall = null;
  ArrayList		body = new ArrayList();
  JStatement		stmt;
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
}
:
  name : IDENT
  LPAREN parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
  ( throwsList = jThrowsClause[] )?
  LCURLY
  (
    ( ( "this" |  "super") LPAREN ) =>
    constructorCall = jExplicitConstructorInvocation[]
  |
    (jPrimaryExpression[] DOT "super" LPAREN) =>
    constructorCall = jExplicitConstructorInvocation[]
  |
    // nothing
  )
  (
    stmt = jBlockStatement[]
      {
	if (stmt instanceof JEmptyStatement) {
	  reportTrouble(new CWarning(stmt.getTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
	}
	body.add(stmt);
      }
  )*
  RCURLY
    {
        self = new JConstructorDeclaration(sourceRef,
                                           modifiers,
                                           name.getText(),
                                           parameters,
                                           throwsList,
                                           new JConstructorBlock(sourceRef,
                                                                 constructorCall,
                                                                 (JStatement[]) body.toArray(new JStatement[body.size()])),
                                           javadoc,
                                           comments,
                                           environment.getTypeFactory());
    }
;


jExplicitConstructorInvocation []
  returns [JConstructorCall self = null]
{
  boolean		functorIsThis = false;
  JExpression[]		args = null;
  JExpression           expr = null;
  JavaStyleComment[]	comments = getStatementComment();
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    ("this") => "this"
      { functorIsThis = true; }
   |
     ("super") => "super"
      { functorIsThis = false; }
  |
    ( expr = jPrimaryExpression[] )? DOT "super"
      { functorIsThis = false; }
  )
  LPAREN args = jArgList[] RPAREN
  SEMI
    { self = new JConstructorCall(sourceRef, functorIsThis, expr, args); }
;


public jsMethodDefinition []
  returns [JMethodDeclaration self = null]
{
	int modifiers;
	CType type;
}
:
	modifiers = jModifiers[]
	type = jTypeSpec[]
	self = jMethodDefinition [ParseClassContext.getInstance(), modifiers, type]
;


private jMethodDefinition [ParseClassContext context, int modifiers, CType type]
  returns [JMethodDeclaration self = null]
{
  JFormalParameter[]	parameters;
  int			bounds = 0;
  CReferenceType[]		throwsList = CReferenceType.EMPTY;
  JStatement[]		body = null;
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
}
:
  name : IDENT
  LPAREN parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
  ( LBRACK RBRACK { bounds += 1; } )*
    {
      if (bounds > 0) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
	type = new CArrayType(type, bounds);
      }
    }
  ( throwsList = jThrowsClause[] )?
  (
    (
      body = jCompoundStatement[] 
    |
       SEMI
    )
  )

    {         
	     self = new JMethodDeclaration(sourceRef,
	                modifiers, type,
	                name.getText(), parameters, throwsList,
	                body == null 
	                    ? null 
	                    : new JBlock(sourceRef, body, null),
	                javadoc, comments);      
    }
;

jVariableDefinitions [int modifiers, CType type]
  returns [JVariableDefinition[] self = null]
{
  ArrayList		vars = new ArrayList();
  JVariableDefinition	decl;
}
:
  decl = jVariableDeclarator[modifiers, type]
    { vars.add(decl); }
  (
    COMMA decl = jVariableDeclarator[modifiers, type]
      { vars.add(decl); }
  )*
  { self = (JVariableDefinition[]) vars.toArray(new JVariableDefinition[vars.size()]); }
;

// JLS 8.3 Variable Declarator
jVariableDeclarator [int modifiers, CType type]
  returns [JVariableDefinition self = null]
{
  int			bounds = 0;
  JExpression		expr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ident : IDENT
  ( LBRACK RBRACK { bounds += 1; } )*
  ( ASSIGN expr = jVariableInitializer[] )?
    {
      if (bounds > 0) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
	type = new CArrayType(type, bounds);
      }
      self = new JVariableDefinition(sourceRef, modifiers, type, ident.getText(), expr);
    }
;

// JLS 8.3 Variable Initializer
jVariableInitializer []
  returns [JExpression self = null]
:
  self = jExpression[]
|
  self = jArrayInitializer[]
;

// JLS 10.6 Array Initializer
jArrayInitializer[]
  returns [JArrayInitializer self = null]
{
  JExpression		expr = null;
  ArrayList		vect = new ArrayList();
  TokenReference	sourceRef = buildTokenReference();
}
:
  LCURLY
  (
    expr = jVariableInitializer[]
      { vect.add(expr); }
    (
      // CONFLICT: does a COMMA after an initializer start a new
      //           initializer or start the option ',' at end?
      //           ANTLR generates proper code by matching
      //			 the comma as soon as possible.
      options { warnWhenFollowAmbig = false; } :
      COMMA expr = jVariableInitializer[]
        { vect.add(expr); }
    )*
  )?
  (
    COMMA
      { reportTrouble(new CWarning(sourceRef, KjcMessages.STRAY_COMMA, null)); }
  )?
  RCURLY
    {
      self = new JArrayInitializer(sourceRef,
				   (JExpression[]) vect.toArray(new JExpression[vect.size()]));
    }
;

// This is a list of exception classes that the method is declared to throw
jThrowsClause []
  returns [CReferenceType[] self]
:
  "throws" self = jNameList[]
;


// A list of formal parameters
jParameterDeclarationList [int desc]
 returns [JFormalParameter[] self = JFormalParameter.EMPTY]
{
  JFormalParameter		elem = null;
  ArrayList			vect = new ArrayList();
}
:
  (
    elem = jParameterDeclaration[desc]
      { vect.add(elem); }
    (
      COMMA elem = jParameterDeclaration[desc]
        { vect.add(elem); }
    )*
    { self = (JFormalParameter[])vect.toArray(new JFormalParameter[vect.size()]);}
    )?
;

// A formal parameter.
jParameterDeclaration [int desc]
  returns [JFormalParameter self = null]
{
  boolean	isFinal = false;
  int		bounds = 0;
  CType		type;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ( "final" { isFinal = true; } )?
  type = jTypeSpec[]
  ident:IDENT
  ( LBRACK RBRACK { bounds += 1; } )*
    {
      if (bounds > 0) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
	type = new CArrayType(type, bounds);
      }
      self = new JFormalParameter(sourceRef, desc, type, ident.getText(), isFinal);
    }
;

// Compound statement.  This is used in many contexts:
//   Inside a class definition prefixed with "static":
//      it is a class initializer
//   Inside a class definition without "static":
//      it is an instance initializer
//   As the body of a method
//   As a completely indepdent braced block of code inside a method
//      it starts a new scope for variable definitions

public jCompoundStatement[]
  returns [JStatement[] self = null]
{
  ArrayList		body = new ArrayList();
  JStatement		stmt;
}
:
  LCURLY
  (
    stmt = jBlockStatement[]
      {
	if (stmt instanceof JEmptyStatement) {
	  reportTrouble(new CWarning(stmt.getTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
	}
	body.add(stmt);
      }
  )*
  RCURLY
  { self = (JStatement[]) body.toArray(new JStatement[body.size()]);}
;

jBlockStatement []
   returns [JStatement self = null]
{
  JTypeDeclaration	type;
  int			modifiers;
}
:
  ( jModifiers[] "class" ) =>
  modifiers = jModifiers[] type = jClassDefinition[modifiers]
    { self = new JTypeDeclarationStatement(type.getTokenReference(), type); }
|
  // declarations are ambiguous with "ID DOT" relative to expression
  // statements.  Must backtrack to be sure.  Could use a semantic
  // predicate to test symbol table to see what the type was coming
  // up, but that's pretty hard without a symbol table ;-)
  ( jModifiers[] jTypeSpec[] IDENT ) =>
  modifiers = jModifiers[] self = jLocalVariableDeclaration[modifiers] SEMI
|
  self = jStatement[]
;

jStatement []
   returns [JStatement self = null]
{
  JExpression		expr;
  JStatement[]		stmts;
  JavaStyleComment[]	comments = getStatementComment();
  TokenReference	sourceRef = buildTokenReference();
}
:
  // A list of statements in curly braces -- start a new scope!
  stmts = jCompoundStatement[]
    { self = new JBlock(sourceRef, stmts, comments); }
|
  // An expression statement.  This could be a method call,
  // assignment statement, or any other expression evaluated for
  // side-effects.
  expr = jExpression[] SEMI
    { self = new JExpressionStatement(sourceRef, expr, comments); }
|
  self = jLabeledStatement[]
|
  self = jIfStatement[]
|
  self = jForStatement[]
|
  self = jWhileStatement[]
|
  self = jDoStatement[]
|
  self = jBreakStatement[]
|
  self = jContinueStatement[]
|
  self = jReturnStatement[]
|
  self = jSwitchStatement[]
|
  // exception try-catch block
  self = jTryBlock[]
|
  self = jThrowStatement[]
|
  self = jSynchronizedStatement[]
|
  self = jDeployStatement[]  
|
  self = jDeploySimpleStatement[] 
|
  self = jUndeploySimpleStatement[] 
|
  // empty statement
  SEMI
    { self = new JEmptyStatement(sourceRef, comments); }
;



jLabeledStatement []
  returns [JLabeledStatement self = null]
{
  JStatement		stmt;
  TokenReference	sourceRef = buildTokenReference();
}
:
  // Attach a label to the front of a statement
  label:IDENT COLON stmt = jStatement[]
    {
      if (stmt instanceof JEmptyStatement) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.STRAY_SEMICOLON, null));
      }
      self = new JLabeledStatement(sourceRef, label.getText(), stmt, getStatementComment());
    }
;


jIfStatement []
  returns [JIfStatement self = null]
{
  JExpression		cond;
  JStatement		thenClause;
  JStatement		elseClause = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "if" LPAREN cond = jExpression[] RPAREN
  thenClause = jStatement[]
  (
    ("else") => "else" elseClause = jStatement[]
    |
  )
    {
      if (! (thenClause instanceof JBlock)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_IF_THEN_IN_BLOCK, null));
      }
      if (elseClause != null && !(elseClause instanceof JBlock || elseClause instanceof JIfStatement)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_IF_ELSE_IN_BLOCK, null));
      }
      self = new JIfStatement(sourceRef, cond, thenClause, elseClause, getStatementComment());
    }
;

jWhileStatement []
  returns [JWhileStatement self = null]
{
  JExpression		cond;
  JStatement		body;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "while" LPAREN cond = jExpression[] RPAREN body = jStatement[]
    {
      if (!(body instanceof JBlock || body instanceof JEmptyStatement)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_LOOP_BODY_IN_BLOCK, null));
      }
      self = new JWhileStatement(sourceRef, cond, body, getStatementComment());
    }
;

jDoStatement []
  returns [JDoStatement self = null]
{
  JExpression		cond;
  JStatement		body;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "do" body = jStatement[] "while" LPAREN cond = jExpression[] RPAREN SEMI
    {
      if (! (body instanceof JBlock)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_LOOP_BODY_IN_BLOCK, null));
      }
      self = new JDoStatement(sourceRef, cond, body, getStatementComment());
    }
;

jBreakStatement []
  returns [JBreakStatement self = null]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  "break" ( label:IDENT )? SEMI
    { self = new JBreakStatement(sourceRef, label == null ? null : label.getText(), getStatementComment()); }
;

jContinueStatement []
  returns [JContinueStatement self = null]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  "continue" ( label:IDENT )? SEMI
    { self = new JContinueStatement(sourceRef, label == null ? null : label.getText(), getStatementComment()); }
;

jReturnStatement []
  returns [JStatement self = null]
{
  JExpression		expr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "return" ( expr = jExpression[] )? SEMI
    {
        self = new JReturnStatement(sourceRef, expr, getStatementComment());
    }
;

jThrowStatement []
  returns [JThrowStatement self = null]
{
  JExpression		expr;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "throw" expr = jExpression[] SEMI
    { self = new JThrowStatement(sourceRef, expr, getStatementComment()); }
;

jDeploySimpleStatement []
  returns [JExpressionStatement self = null]
{
  JExpression		expr;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "deploy" expr = jExpression[] SEMI
    { self = new JExpressionStatement(
    				sourceRef,
    				new CjMethodCallExpression(sourceRef,
					        expr,
					        "simpleDeploy",
					        JExpression.EMPTY),
					getStatementComment()); }
;

jUndeploySimpleStatement []
  returns [JExpressionStatement self = null]
{
  JExpression		expr;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "undeploy" expr = jExpression[] SEMI
    { self = new JExpressionStatement(
    				sourceRef,
    				new CjMethodCallExpression(sourceRef,
					        expr,
					        "simpleUndeploy",
					        JExpression.EMPTY),
					getStatementComment()); }
;

jSynchronizedStatement []
  returns [JSynchronizedStatement self = null]
{
  JExpression		expr;
  JStatement[]		body;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "synchronized" LPAREN expr = jExpression[] RPAREN body = jCompoundStatement[]
    {
      self = new JSynchronizedStatement(sourceRef,
					expr,
					new JBlock(sourceRef, body, null),
					getStatementComment());
    }
;

jSwitchStatement []
  returns [JSwitchStatement self = null]
{
  JExpression		expr = null;
  ArrayList		body = null;
  JSwitchGroup		group;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "switch"
  LPAREN expr = jExpression[] RPAREN
  LCURLY
    { body = new ArrayList(); }
  (
    group = jCasesGroup[]
    { body.add(group); }
  )*
  RCURLY
    {
      self = new JSwitchStatement(sourceRef,
				  expr,
				  (JSwitchGroup[])body.toArray(new JSwitchGroup[body.size()]),
				  getStatementComment());
    }
;

jCasesGroup []
  returns [JSwitchGroup self = null]
{
  ArrayList		labels = new ArrayList();
  ArrayList		stmts = new ArrayList();

  JSwitchLabel		label;
  JStatement		stmt;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    // CONFLICT: to which case group do the statements bind?
    //           ANTLR generates proper code: it groups the
    //           many "case"/"default" labels together then
    //           follows them with the statements
    options { warnWhenFollowAmbig = false; } :
    label = jACase[] { labels.add(label); }
  )+
  (
    stmt = jBlockStatement[]
    {
      if (stmt instanceof JEmptyStatement) {
	reportTrouble(new CWarning(stmt.getTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
      }
      stmts.add(stmt);
    }
  )*
    {
      self = new JSwitchGroup(sourceRef,
			      (JSwitchLabel[])labels.toArray(new JSwitchLabel[labels.size()]),
			      (JStatement[])stmts.toArray(new JStatement[stmts.size()]));
    }
;

jACase []
  returns [JSwitchLabel self = null]
{
  JExpression		expr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    "case" expr = jExpression[]
      { self = new JSwitchLabel(sourceRef, expr); }
  |
    "default"
      { self = new JSwitchLabel(sourceRef, null); }
  )
  COLON
;


jForStatement []
  returns [JForStatement self = null]
{
  JStatement		init;
  JExpression		cond;
  JStatement		iter;
  JStatement		body;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "for"
  LPAREN
  init = jForInit[] SEMI
  cond = jForCond[] SEMI
  iter = jForIter[]
  RPAREN
  body = jStatement[]
    {
      if (!(body instanceof JBlock || body instanceof JEmptyStatement)) {
	reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_LOOP_BODY_IN_BLOCK, null));
      }
      self = new JForStatement(sourceRef, init, cond, iter, body, getStatementComment());
    }
;

// The initializer for a for loop
jForInit []
  returns [JStatement self = null]
{
  int			modifiers;
  JExpression[]		list;
}
:
(
  // if it looks like a declaration, it is
  ( jModifiers[] jTypeSpec[] IDENT ) =>
  modifiers = jModifiers[] self = jLocalVariableDeclaration[modifiers]
|
  // otherwise it could be an expression list...
  list = jExpressionList[]
    { self = new JExpressionListStatement(buildTokenReference(), list, getStatementComment()); }
)?
;

jForCond []
  returns [JExpression expr = null]
:
  ( expr = jExpression[] )?
;

jForIter []
  returns [JExpressionListStatement self = null]
{
  JExpression[] list;
}
:
  (
   list = jExpressionList[]
     { self = new JExpressionListStatement(buildTokenReference(), list, null); }
  )?
;

// an exception handler try/catch block
jTryBlock []
  returns [JStatement self = null]
{
  JBlock		tryClause = null;
  JStatement[]		compound;
  ArrayList		catchClauses = new ArrayList();
  JBlock		finallyClause = null;
  JCatchClause		catcher = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "try"
  compound = jCompoundStatement[]
    { tryClause = new JBlock(sourceRef, compound, null); }
  (
    catcher = jHandler[]
      { catchClauses.add(catcher); }
  )*
  (
    "finally" compound = jCompoundStatement[]
      { finallyClause = new JBlock(sourceRef, compound, null); }
  )?
    {
      if (catchClauses.size() > 0) {
	self = new JTryCatchStatement(sourceRef,
				      tryClause,
				      (JCatchClause[])catchClauses.toArray(new JCatchClause[catchClauses.size()]),
				      finallyClause == null ? getStatementComment() : null);
      }
      if (finallyClause != null) {
	// If both catch and finally clauses are present,
	// the try-catch is embedded as try clause into a
	// try-finally statement.
	if (self != null) {
	  tryClause = new JBlock(sourceRef, new JStatement[] {self}, null);
	}
	self = new JTryFinallyStatement(sourceRef, tryClause, finallyClause, getStatementComment());
      }

      if (self == null) {
	// try without catch or finally: error
	reportTrouble(new PositionedError(sourceRef, KjcMessages.TRY_NOCATCH, null));
	self = tryClause;
      }
    }
;


// an exception handler
jHandler []
  returns [JCatchClause self = null]
{
  JFormalParameter	param;
  JStatement[]		body;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "catch" LPAREN param = jParameterDeclaration[JLocalVariable.DES_CATCH_PARAMETER] RPAREN body = jCompoundStatement[]
    {
      self = new JCatchClause(sourceRef,
			      param,
			      new JBlock(sourceRef, body, null));
    }
;


// expressions
// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//       nextHigherPrecedenceExpression
//           (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
// The operators in java have the following precedences:
//    lowest  (13)  = *= /= %= += -= <<= >>= >>>= &= ^= |=
//            (12)  ?:
//            (11)  ||
//            (10)  &&
//            ( 9)  |
//            ( 8)  ^
//            ( 7)  &
//            ( 6)  == !=
//            ( 5)  < <= > >=
//            ( 4)  << >>
//            ( 3)  +(binary) -(binary)
//            ( 2)  * / %
//            ( 1)  ++ -- +(unary) -(unary)  ~  !  (type)
//                  []   () (method call)  . (dot -- identifier qualification)
//                  new   ()  (explicit parenthesis)
//
// the last two are not usually on a precedence chart; I put them in
// to point out that new has a higher precedence than '.', so you
// can validy use
//     new Frame().show()
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
//   is usually very straightfoward



// the mother of all expressions
jExpression []
  returns [JExpression self = null]
:
  self = jAssignmentExpression[]
;


// This is a list of expressions.
jExpressionList []
  returns [JExpression[] self = null]
{
  JExpression		expr;
  ArrayList		vect = new ArrayList();
}
:
  expr = jExpression[]
    { vect.add(expr); }
  (
    COMMA expr = jExpression[]
      { vect.add(expr); }
  )*
  { self = (JExpression[])vect.toArray(new JExpression[vect.size()]); } //org.caesarj.util.Utils.toArray(vect, JExpression.class); }
;

// assignment expression (level 13)
jAssignmentExpression []
  returns [JExpression self = null]
{
  int			oper = -1;
  JExpression		right;
}
:
  self = jConditionalExpression[]
  (
    ASSIGN right = jAssignmentExpression[]
      { self = new JAssignmentExpression(self.getTokenReference(), self, right); }
  |
    oper = jCompoundAssignmentOperator[] right = jAssignmentExpression[]
      { self = new JCompoundAssignmentExpression(self.getTokenReference(), oper, self, right); }
  )?
;

jCompoundAssignmentOperator []
  returns [int self = -1]
:
  PLUS_ASSIGN { self = Constants.OPE_PLUS; }
|
  MINUS_ASSIGN { self = Constants.OPE_MINUS; }
|
  STAR_ASSIGN { self = Constants.OPE_STAR; }
|
  SLASH_ASSIGN { self = Constants.OPE_SLASH; }
|
  PERCENT_ASSIGN { self = Constants.OPE_PERCENT; }
|
  SR_ASSIGN { self = Constants.OPE_SR; }
|
  BSR_ASSIGN { self = Constants.OPE_BSR; }
|
  SL_ASSIGN { self = Constants.OPE_SL; }
|
  BAND_ASSIGN { self = Constants.OPE_BAND; }
|
  BXOR_ASSIGN { self = Constants.OPE_BXOR; }
|
  BOR_ASSIGN { self = Constants.OPE_BOR; }
;


// conditional test (level 12)
jConditionalExpression []
  returns [JExpression self = null]
{
  JExpression		middle;
  JExpression		right;
}
:
  self = jLogicalOrExpression[]
  (
    QUESTION middle = jAssignmentExpression[] COLON right = jConditionalExpression[]
      { self = new JConditionalExpression(self.getTokenReference(), self, middle, right); }
  )?
;

// logical or (||)  (level 11)
jLogicalOrExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jLogicalAndExpression[]
  (
    LOR right = jLogicalAndExpression[]
      { self = new JConditionalOrExpression(self.getTokenReference(), self, right); }
  )*
;

// logical and (&&)  (level 10)
jLogicalAndExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jInclusiveOrExpression[]
  (
    LAND right = jInclusiveOrExpression[]
      { self = new JConditionalAndExpression(self.getTokenReference(), self, right); }
  )*
;

// bitwise or non-short-circuiting or (|)  (level 9)
jInclusiveOrExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jExclusiveOrExpression[]
  (
    BOR right = jExclusiveOrExpression[]
      { self = new JBitwiseExpression(self.getTokenReference(), Constants.OPE_BOR, self, right); }
  )*
;

// exclusive or (^)  (level 8)
jExclusiveOrExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jAndExpression[]
  (
    BXOR right = jAndExpression[]
      { self = new JBitwiseExpression(self.getTokenReference(), Constants.OPE_BXOR, self, right); }
  )*
;

// bitwise or non-short-circuiting and (&)  (level 7)
jAndExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jEqualityExpression[]
  (
    BAND right = jEqualityExpression[]
      { self = new JBitwiseExpression(self.getTokenReference(), Constants.OPE_BAND, self, right); }
  )*
;

// equality/inequality (==/!=) (level 6)
jEqualityExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jRelationalExpression[]
  (
    NOT_EQUAL right = jRelationalExpression[]
      { self = new JEqualityExpression(self.getTokenReference(), false, self, right); }
  |
    EQUAL right = jRelationalExpression[]
      { self = new JEqualityExpression(self.getTokenReference(), true, self, right); }
  )*
;


// boolean relational expressions (level 5)
jRelationalExpression []
  returns [JExpression self = null]
{
  int			operator = -1;
  JExpression		right;
  CType			type;
}
:
  self = jShiftExpression[]
  (
    (
      (
        LT { operator = Constants.OPE_LT; }
      |
	GT { operator = Constants.OPE_GT; }
      |
	LE { operator = Constants.OPE_LE; }
      |
	GE { operator = Constants.OPE_GE; }
      )
      right = jShiftExpression[]
        { self = new JRelationalExpression(self.getTokenReference(), operator, self, right); }
    )*
  |
    "instanceof" type = jTypeSpec[]
      { self = new JInstanceofExpression(self.getTokenReference(), self, type); }
  )
;


// bit shift expressions (level 4)
jShiftExpression []
  returns [JExpression self = null]
{
  int			operator = -1;
  JExpression		right;
}
:
  self = jAdditiveExpression[]
  (
    (
      SL { operator = Constants.OPE_SL; }
    |
      SR { operator = Constants.OPE_SR; }
    |
      BSR { operator = Constants.OPE_BSR; }
    )
    right = jAdditiveExpression[]
      { self = new JShiftExpression(self.getTokenReference(), operator, self, right); }
  )*
;


// binary addition/subtraction (level 3)
jAdditiveExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jMultiplicativeExpression[]
  (
    PLUS right = jMultiplicativeExpression[]
      { self = new JAddExpression(self.getTokenReference(), self, right); }
  |
    MINUS right = jMultiplicativeExpression[]
      { self = new JMinusExpression(self.getTokenReference(), self, right); }
  )*
;


// multiplication/division/modulo (level 2)
jMultiplicativeExpression []
  returns [JExpression self = null]
{
  JExpression		right;
}
:
  self = jUnaryExpression[]
  (
    STAR right = jUnaryExpression[]
      { self = new JMultExpression(self.getTokenReference(), self, right); }
  |
    SLASH right = jUnaryExpression[]
      { self = new JDivideExpression(self.getTokenReference(), self, right); }
  |
    PERCENT right = jUnaryExpression[]
      { self = new JModuloExpression(self.getTokenReference(), self, right); }
  )*
;

jUnaryExpression []
  returns [JExpression self = null]
{
  TokenReference	sourceRef = buildTokenReference();
}
:
  INC self = jUnaryExpression[]
    { self = new JPrefixExpression(sourceRef, Constants.OPE_PREINC, self); }
|
  DEC self = jUnaryExpression[]
    { self = new JPrefixExpression(sourceRef, Constants.OPE_PREDEC, self); }
|
  MINUS self = jUnaryExpression[]
    { self = new JUnaryMinusExpression(sourceRef, self); }
|
  PLUS self = jUnaryExpression[]
    { self = new JUnaryPlusExpression(sourceRef, self); }
|
  self = jUnaryExpressionNotPlusMinus[]
;

jUnaryExpressionNotPlusMinus []
  returns [JExpression self = null]
{
  JExpression		expr;
  CType			dest;
  TokenReference	sourceRef = buildTokenReference();
}
:
  BNOT self = jUnaryExpression[]
    { self = new JBitwiseComplementExpression(sourceRef, self); }
|
  LNOT self = jUnaryExpression[]
    { self = new JLogicalComplementExpression(sourceRef, self); }
|
  (
    // subrule allows option to shut off warnings
    options {
      // "(int" ambig with postfixExpr due to lack of sequence
      // info in linear approximate LL(k).  It's ok.  Shut up.
      generateAmbigWarnings=false;
    } :
    // If typecast is built in type, must be numeric operand
    // Also, no reason to backtrack if type keyword like int, float...
    LPAREN dest = jBuiltInTypeSpec[]
    ( 
      RPAREN expr = jUnaryExpression[]
      { self = new JCastExpression(sourceRef, expr, dest); }
    |
      // the second posibility is e.g. " Class clazz = (int.class);"
      DOT "class" RPAREN
      { 
        if (dest instanceof CArrayType) {
          self = new JClassExpression(buildTokenReference(), ((CArrayType) dest).getBaseType(),((CArrayType) dest).getArrayBound()); 
        } else {
          self = new JClassExpression(buildTokenReference(), dest, 0); 
        }
      }
    )
    // Have to backtrack to see if operator follows.  If no operator
    // follows, it's a typecast.  No semantic checking needed to parse.
    // if it _looks_ like a cast, it _is_ a cast; else it's a "(expr)"
  |
    (LPAREN jClassTypeSpec[] RPAREN jUnaryExpressionNotPlusMinus[])=>
    LPAREN dest = jClassTypeSpec[] RPAREN
    expr = jUnaryExpressionNotPlusMinus[]
      { self = new JCastExpression(sourceRef, expr, dest); }
  |
   self = jPostfixExpression[]
  )
;

// qualified names, array expressions, method invocation, post inc/dec
jPostfixExpression[]
  returns [JExpression self = null]
{
  int			bounds = 0;
  JExpression		expr;
  JExpression[]		args = null;
  CType			type;
  TokenReference	sourceRef = buildTokenReference();
}
:
  self = jPrimaryExpression[] // start with a primary
  (
    // qualified id (id.id.id.id...) -- build the name
    DOT
    (
      ident : IDENT
        { self = new JNameExpression(sourceRef, self, ident.getText()); }
    |
      "this"
        { self = new JThisExpression(sourceRef, self); }
    | // 31.08.01
      "super"
        { self = new JSuperExpression(sourceRef, self); }
    |
      "class"
        { self = new JClassExpression(sourceRef, self, 0); }
    | 
      "wrappee"
        { self = new CjWrappeeExpression(sourceRef, self); }
    |      
      self = jQualifiedNewExpression[self]
    )
  |
    // allow ClassName[].class
    ( LBRACK RBRACK { bounds++; } )+
    DOT "class"
      { self = new JClassExpression(sourceRef, self, bounds); }
  |
    LBRACK expr = jExpression[] RBRACK
      { self = new JArrayAccessExpression(sourceRef, self, expr); }
  |
    LPAREN args = jArgList[] RPAREN
      {
	if (! (self instanceof JNameExpression)) {
	  reportTrouble(new PositionedError(sourceRef, KjcMessages.INVALID_METHOD_NAME, null));
	} else if(((JNameExpression)self).getName().equals("proceed")) {
	  self = new CjProceedExpression(sourceRef, args);	
	} else {
	  self = new CjMethodCallExpression(sourceRef,
					   ((JNameExpression)self).getPrefix(),
					   ((JNameExpression)self).getName(),
					   args);
	}
      }
  )*
  (
    INC
      { self = new JPostfixExpression(sourceRef, Constants.OPE_POSTINC, self); }
  |
    DEC
      { self = new JPostfixExpression(sourceRef, Constants.OPE_POSTDEC, self); }
  |
    // nothing
  )
;

// the basic element of an expression
jPrimaryExpression []
  returns [JExpression self = null]
{
  int			bounds = 0;
  CType			type;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ident : IDENT
    { self = new JNameExpression(sourceRef, ident.getText()); }
|
  self = jUnqualifiedNewExpression[]
|
  self = jLiteral[]
|
  "super"
    { self = new JSuperExpression(sourceRef); }
|
  "true"
    { self = new JBooleanLiteral(sourceRef, true); }
|
  "false"
    { self = new JBooleanLiteral(sourceRef, false); }
|
  "this"
    { self = new JThisExpression(sourceRef); }
| 
  "null"
    { self = new JNullLiteral(sourceRef); }
| 
  "wrappee"
    { self = new CjWrappeeExpression(sourceRef); }    
|
  LPAREN self = jAssignmentExpression[] RPAREN
    { self = new JParenthesedExpression(sourceRef, self); }
|
  type = jBuiltInType[]
  ( LBRACK RBRACK { bounds++; } )*
  DOT "class"
    { self = new JClassExpression(buildTokenReference(), type, bounds); }
;


jUnqualifiedNewExpression []
  returns [JExpression self = null]
{
  CType				type;
  JExpression[]			args;
  JArrayInitializer		init = null;
  JClassDeclaration		decl = null;
  ParseClassContext		context = null;
  TokenReference		sourceRef = buildTokenReference();
}
:
  "new"
  (
    type = jBuiltInType[]
    args = jNewArrayDeclarator[] ( init = jArrayInitializer[] )?
      { self = new JNewArrayExpression(sourceRef, type, args, init); }
  |
    type = jTypeName[]
    (
      args = jNewArrayDeclarator[] ( init = jArrayInitializer[] )?
        { self = new JNewArrayExpression(sourceRef, type, args, init); }
    |
      LPAREN args = jArgList[] RPAREN
      (
        { context = ParseClassContext.getInstance(); }
        jClassBlock[context]
        {
          JMethodDeclaration[]      methods;

          methods = context.getMethods();

	      decl = new JClassDeclaration(sourceRef,
					   org.caesarj.classfile.ClassfileConstants2.ACC_FINAL, // JLS 15.9.5
					   "",
					   null,
					   CReferenceType.EMPTY,
					   context.getFields(),
					   methods,
					   context.getInnerClasses(),
					   context.getBody(),
					   getJavadocComment(),
					   getStatementComment());
	      context.release();
	  	}
        { self = new JUnqualifiedAnonymousCreation(sourceRef, (CReferenceType)type, args, decl); }
      |
	// epsilon
        { self = new CjUnqualifiedInstanceCreation(sourceRef, (CReferenceType)type, args); }
      )
    )
  )
;

jQualifiedNewExpression [JExpression prefix]
  returns [JExpression self = null]
{
  CType				type;
  JExpression[]			args;
  JArrayInitializer		init = null;
  CjClassDeclaration		decl = null;
  ParseClassContext		context = null;
  TokenReference		sourceRef = buildTokenReference();
}
:
  "new" ident : IDENT
  LPAREN args = jArgList[] RPAREN
  (
    { context = ParseClassContext.getInstance(); }
    jClassBlock[context]
      {
        JMethodDeclaration[]      methods;

          methods = context.getMethods();

	decl = new CjClassDeclaration(sourceRef,
				     org.caesarj.classfile.ClassfileConstants2.ACC_FINAL, // JLS 15.9.5
				     ident.getText(),
				     null,
				     null,				     
				     CReferenceType.EMPTY,
				     context.getFields(),
				     methods,
				     context.getInnerClasses(),
				     context.getBody(),
				     getJavadocComment(),
				     getStatementComment());
	context.release();
      }
      { self = new JQualifiedAnonymousCreation(sourceRef, prefix, ident.getText(), args, decl); }
  |
    { self = new CjQualifiedInstanceCreation(sourceRef, prefix, ident.getText(), args); }
  )
;


jArgList []
  returns [JExpression[] self = JExpression.EMPTY]
:
  ( self = jExpressionList[] )?
;


// TODO : do these checks in semantic analysis
jNewArrayDeclarator[]
  returns [JExpression[] self = null]
{
  ArrayList		container = new ArrayList();
  JExpression		exp = null;
}
:
  (
    // CONFLICT:
    // newExpression is a primaryExpression which can be
    // followed by an array index reference.  This is ok,
    // as the generated code will stay in this loop as
    // long as it sees an LBRACK (proper behavior)
    options { warnWhenFollowAmbig = false; } :
    LBRACK
    ( exp = jExpression[] )?
    RBRACK
      { container.add(exp); exp = null; }
  )+
  { self = (JExpression[]) container.toArray(new JExpression[container.size()]); } //org.caesarj.util.Utils.toArray(container, JExpression.class); }
;

jLiteral []
  returns [JLiteral self = null]
:
  self = jIntegerLiteral[]
|
  self = jCharLiteral[]
|
  self = jStringLiteral[]
|
  self = jRealLiteral[]
;

jIntegerLiteral []
  returns [JLiteral self = null]
:
  i : INTEGER_LITERAL
    {
      try {
	self = JLiteral.parseInteger(buildTokenReference(), i.getText());
      } catch (PositionedError e) {
	reportTrouble(e);
	// allow parsing to continue
	self = new JIntLiteral(TokenReference.NO_REF, 0);
      }
    }
;

jCharLiteral []
  returns [JLiteral self = null]
:
  c : CHARACTER_LITERAL
    {
      try {
	self = new JCharLiteral(buildTokenReference(), c.getText());
      } catch (PositionedError e) {
	reportTrouble(e);
	// allow parsing to continue
	self = new JCharLiteral(TokenReference.NO_REF, '\0');
      }
    }
;

jStringLiteral []
  returns [JLiteral self = null]
:
  s : STRING_LITERAL
    { self = new JStringLiteral(buildTokenReference(), s.getText()); }
;

jRealLiteral []
  returns [JLiteral self = null]
:
  r : REAL_LITERAL
    {
      try {
	self = JLiteral.parseReal(buildTokenReference(), r.getText());
      } catch (PositionedError e) {
	reportTrouble(e);
	// allow parsing to continue
	self = new JFloatLiteral(TokenReference.NO_REF, 0f);
      }
    }
;

jNameList []
  returns [CReferenceType[] self = null]
{
  CReferenceType	name;
  ArrayList	container = new ArrayList();
}
:
  name = jTypeName[] { container.add(name); }
  (
    COMMA
    name = jTypeName[] { container.add(name); }
  )*
  { self = (CReferenceType[])container.toArray(new CReferenceType[container.size()]); }
;


jTypeName []
  returns [CReferenceType self = null]
{
  ArrayList                container = new ArrayList();
  StringBuffer          buffer = null;
}
:
  i:IDENT 
  (
    DOT j:IDENT 
    {
      (buffer == null ? (buffer = new StringBuffer(i.getText())) : buffer).append('/').append(j.getText());
    }
  )*
  {
    String              name = buffer == null ? i.getText() : buffer.toString();

    self = environment.getTypeFactory().createType(name, false);
  }
;


jCjAdviceDeclaration  [CaesarAdviceKind kind, int modifiers, JFormalParameter[] parameters, CType type, CReferenceType[] throwsList, JFormalParameter extraParam]
	returns [CjAdviceDeclaration self = null]
{
  int			bounds = 0;
  JStatement[]		body = null;
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
  TypeFactory factory = environment.getTypeFactory();  
}
:	
	pattern : TYPE_PATTERN
	body = jCompoundStatement[]
	{
	

		try {
			CaesarPatternParser patternParser = new CaesarPatternParser(
													pattern.getText(),
													new CaesarSourceContext(sourceRef) );
			CaesarPointcut pointcut = patternParser.parsePointcut();

			
			//handle throwing parameter
			if (extraParam != null) {
				JFormalParameter[] newParameters = new JFormalParameter[parameters.length + 1];
				System.arraycopy(parameters,0,newParameters,0,parameters.length);
				newParameters[newParameters.length - 1] = extraParam;						
				parameters = newParameters;			
			}
		
			self = new CjAdviceDeclaration(sourceRef,
                                       modifiers,
                                       type,
                                       parameters,
                                       throwsList,
                                       body == null ? null : new JBlock(sourceRef, body, null),
                                       javadoc,
                                       comments,
                                       pointcut,
                                       kind,
                                       extraParam != null);
			
		} catch(CaesarPatternParser.CaesarParserException e) {
			reportTrouble(new PositionedError(sourceRef, CaesarMessages.POINTCUT_SYNTAX_ERROR, e.getMessage()));			
		} catch(RuntimeException e) {
			reportTrouble(new PositionedError(sourceRef, CaesarMessages.POINTCUT_SYNTAX_ERROR, e.getMessage()));							
		}  			

		
	}
;

jPointcutDefinition [int modifiers]
	returns [CjPointcutDeclaration self = null]
{
  JFormalParameter[]	parameters;
  int			bounds = 0;
  TokenReference	sourceRef = buildTokenReference();
  JavadocComment	javadoc = getJavadocComment();
  JavaStyleComment[]	comments = getStatementComment();
  TypeFactory factory = environment.getTypeFactory();  
  CaesarPointcut pointcut = null;
}
:
  name : IDENT
  LPAREN parameters = jParameterDeclarationList[JLocalVariable.DES_PARAMETER] RPAREN
  pointcut = jPointcut []
  SEMI
	{self = new CjPointcutDeclaration(sourceRef,
                                               modifiers,
                                               factory.getVoidType(),
                                               name.getText(),
                                               parameters,
                                               javadoc,
                                               pointcut);
    }
;

jPointcut []
  returns [CaesarPointcut self = null]
{
	TokenReference	sourceRef = buildTokenReference();
	int mod = 0;
}
:
	(pattern : TYPE_PATTERN)?
	{
		if (pattern != null) {
		
			try {
				CaesarPatternParser patternParser = new CaesarPatternParser(
														pattern.getText(),
														new CaesarSourceContext(sourceRef) );
				self = patternParser.parsePointcut();
			} catch(CaesarPatternParser.CaesarParserException e) {
				reportTrouble(new PositionedError(sourceRef, CaesarMessages.POINTCUT_SYNTAX_ERROR, e.getMessage()));			
			} catch(RuntimeException e) {
			  	reportTrouble(new PositionedError(sourceRef, CaesarMessages.POINTCUT_SYNTAX_ERROR, e.getMessage()));							
			}  			
			
			
		} else {
		
			self = CaesarPointcut.makeMatchesNothing();
			
		}
	}
;

jDeployStatement []
  returns [CjDeployStatement self = null]
{
  JExpression	aspectToDeploy;
  JStatement	body;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "deploy" LPAREN aspectToDeploy = jExpression[] RPAREN
  body = jStatement[]
    {
      self = new CjDeployStatement(sourceRef, aspectToDeploy, body, getStatementComment());
    }
;


