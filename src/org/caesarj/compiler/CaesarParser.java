// $ANTLR 1.5A: "Caesar.g" -> "CaesarParser.java"$
 package org.caesarj.compiler; 
import java.util.ArrayList;

import org.caesarj.compiler.aspectj.CaesarAdviceKind;
import org.caesarj.compiler.aspectj.CaesarPatternParser;
import org.caesarj.compiler.aspectj.CaesarPatternParser.CaesarParserException;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.aspectj.CaesarSourceContext;

import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.compiler.ast.CciInterfaceDeclaration;
import org.caesarj.compiler.ast.CciWeaveletClassDeclaration;
import org.caesarj.compiler.ast.CciWeaveletReferenceType;
import org.caesarj.compiler.ast.CciWrappeeExpression;
import org.caesarj.compiler.ast.CciWrapperDestructorExpression;
import org.caesarj.compiler.ast.DeployStatement;
import org.caesarj.compiler.ast.FjAssignmentExpression;
import org.caesarj.compiler.ast.FjCastExpression;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.ast.FjCleanMethodDeclaration;
import org.caesarj.compiler.ast.FjCompilationUnit;
import org.caesarj.compiler.ast.FjConstructorBlock;
import org.caesarj.compiler.ast.FjConstructorCall;
import org.caesarj.compiler.ast.FjConstructorDeclaration;
import org.caesarj.compiler.ast.FjEqualityExpression;
import org.caesarj.compiler.ast.FjFieldDeclaration;
import org.caesarj.compiler.ast.FjFormalParameter;
import org.caesarj.compiler.ast.FjMethodCallExpression;
import org.caesarj.compiler.ast.FjMethodDeclaration;
import org.caesarj.compiler.ast.FjNameExpression;
import org.caesarj.compiler.ast.FjOverrideClassDeclaration;
import org.caesarj.compiler.ast.FjParenthesedExpression;
import org.caesarj.compiler.ast.FjPrivateMethodDeclaration;
import org.caesarj.compiler.ast.FjQualifiedInstanceCreation;
import org.caesarj.compiler.ast.FjReturnStatement;
import org.caesarj.compiler.ast.FjSuperExpression;
import org.caesarj.compiler.ast.FjThisExpression;
import org.caesarj.compiler.ast.FjUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.FjVariableDefinition;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.compiler.ast.PointcutDeclaration;
import org.caesarj.compiler.ast.ProceedExpression;
import org.caesarj.compiler.tools.antlr.extra.InputBuffer;
import org.caesarj.compiler.tools.antlr.runtime.BitSet;
import org.caesarj.compiler.tools.antlr.runtime.NoViableAltException;
import org.caesarj.compiler.tools.antlr.runtime.RecognitionException;
import org.caesarj.compiler.tools.antlr.runtime.Token;
import org.caesarj.compiler.tools.antlr.runtime.TokenStreamException;
import org.caesarj.kjc.CArrayType;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CParseCompilationUnitContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JAddExpression;
import org.caesarj.kjc.JArrayAccessExpression;
import org.caesarj.kjc.JArrayInitializer;
import org.caesarj.kjc.JBitwiseComplementExpression;
import org.caesarj.kjc.JBitwiseExpression;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JBooleanLiteral;
import org.caesarj.kjc.JBreakStatement;
import org.caesarj.kjc.JCatchClause;
import org.caesarj.kjc.JCharLiteral;
import org.caesarj.kjc.JClassBlock;
import org.caesarj.kjc.JClassExpression;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JCompoundAssignmentExpression;
import org.caesarj.kjc.JConditionalAndExpression;
import org.caesarj.kjc.JConditionalExpression;
import org.caesarj.kjc.JConditionalOrExpression;
import org.caesarj.kjc.JConstructorCall;
import org.caesarj.kjc.JConstructorDeclaration;
import org.caesarj.kjc.JContinueStatement;
import org.caesarj.kjc.JDivideExpression;
import org.caesarj.kjc.JDoStatement;
import org.caesarj.kjc.JEmptyStatement;
import org.caesarj.kjc.JEqualityExpression;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionListStatement;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFloatLiteral;
import org.caesarj.kjc.JForStatement;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JIfStatement;
import org.caesarj.kjc.JInstanceofExpression;
import org.caesarj.kjc.JIntLiteral;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JLabeledStatement;
import org.caesarj.kjc.JLiteral;
import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JLogicalComplementExpression;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JMinusExpression;
import org.caesarj.kjc.JModuloExpression;
import org.caesarj.kjc.JMultExpression;
import org.caesarj.kjc.JNameExpression;
import org.caesarj.kjc.JNewArrayExpression;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPostfixExpression;
import org.caesarj.kjc.JPrefixExpression;
import org.caesarj.kjc.JQualifiedAnonymousCreation;
import org.caesarj.kjc.JRelationalExpression;
import org.caesarj.kjc.JShiftExpression;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JStringLiteral;
import org.caesarj.kjc.JSwitchGroup;
import org.caesarj.kjc.JSwitchLabel;
import org.caesarj.kjc.JSwitchStatement;
import org.caesarj.kjc.JSynchronizedStatement;
import org.caesarj.kjc.JThrowStatement;
import org.caesarj.kjc.JTryCatchStatement;
import org.caesarj.kjc.JTryFinallyStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JTypeDeclarationStatement;
import org.caesarj.kjc.JUnaryMinusExpression;
import org.caesarj.kjc.JUnaryPlusExpression;
import org.caesarj.kjc.JUnqualifiedAnonymousCreation;
import org.caesarj.kjc.JVariableDeclarationStatement;
import org.caesarj.kjc.JVariableDefinition;
import org.caesarj.kjc.JWhileStatement;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.kjc.KopiAssertStatement;
import org.caesarj.kjc.KopiAssertionClassDeclaration;
import org.caesarj.kjc.KopiConstraintStatement;
import org.caesarj.kjc.KopiConstructorBlock;
import org.caesarj.kjc.KopiConstructorDeclaration;
import org.caesarj.kjc.KopiFailStatement;
import org.caesarj.kjc.KopiInvariantDeclaration;
import org.caesarj.kjc.KopiInvariantStatement;
import org.caesarj.kjc.KopiMethodDeclaration;
import org.caesarj.kjc.KopiMethodPostconditionDeclaration;
import org.caesarj.kjc.KopiMethodPreconditionDeclaration;
import org.caesarj.kjc.KopiOldValueExpression;
import org.caesarj.kjc.KopiPostconditionDeclaration;
import org.caesarj.kjc.KopiPostconditionStatement;
import org.caesarj.kjc.KopiPreconditionDeclaration;
import org.caesarj.kjc.KopiPreconditionStatement;
import org.caesarj.kjc.KopiReturnStatement;
import org.caesarj.kjc.KopiReturnValueExpression;
import org.caesarj.kjc.TypeFactory;

public class CaesarParser extends org.caesarj.compiler.tools.antlr.extra.Parser
	   implements CaesarTokenTypes
 {

  public CaesarParser(Compiler compiler, InputBuffer buffer, KjcEnvironment environment) {
	super(compiler, new CaesarScanner(compiler, buffer), MAX_LOOKAHEAD);
	this.environment = environment;
  }

  private final KjcEnvironment  environment;
// Generated by org.caesarj.at.dms.compiler.tools.antlr
private static final int MAX_LOOKAHEAD = 2;
{
  tokenNames = _tokenNames;
}
// Generated by org.caesarj.at.dms.compiler.tools.antlr

	public final JCompilationUnit  jCompilationUnit(
		
	) throws RecognitionException, TokenStreamException {
		JCompilationUnit self = null;
		
		
		JPackageName			pack;
		CParseCompilationUnitContext	context = CParseCompilationUnitContext.getInstance();
		TokenReference		sourceRef = buildTokenReference();
		
		
		pack=jPackageDeclaration();
		if ( inputState.guessing==0 ) {
			context.setPackage(pack);
		}
		jImportDeclarations(context);
		{
		_loop3:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				jTypeDefinition(context);
			}
			else {
				break _loop3;
			}
			
		} while (true);
		}
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			
			self = new FjCompilationUnit(sourceRef,
			environment,
							  pack,
							  context.getPackageImports(),
							  context.getClassImports(),
							  context.getTypeDeclarations());
			context.release();
			
		}
		return self;
	}
	
	private final JPackageName  jPackageDeclaration(
		
	) throws RecognitionException, TokenStreamException {
		JPackageName self = JPackageName.UNNAMED;
		
		
		String	name;
		
		
		{
		switch ( LA(1)) {
		case LITERAL_package:
		{
			match(LITERAL_package);
			name=jIdentifier();
			match(SEMI);
			if ( inputState.guessing==0 ) {
				self = new JPackageName(buildTokenReference(), name, getStatementComment());
			}
			break;
		}
		case EOF:
		case LITERAL_abstract:
		case LITERAL_class:
		case LITERAL_final:
		case LITERAL_import:
		case LITERAL_interface:
		case LITERAL_native:
		case LITERAL_private:
		case LITERAL_protected:
		case LITERAL_public:
		case LITERAL_static:
		case LITERAL_strictfp:
		case LITERAL_synchronized:
		case LITERAL_transient:
		case LITERAL_volatile:
		case LITERAL_virtual:
		case LITERAL_override:
		case LITERAL_clean:
		case LITERAL_collaboration:
		case LITERAL_provided:
		case LITERAL_expected:
		case LITERAL_crosscutting:
		case LITERAL_deployed:
		case LITERAL_privileged:
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final void jImportDeclarations(
		CParseCompilationUnitContext context
	) throws RecognitionException, TokenStreamException {
		
		
		jAutomaticImports(context);
		{
		_loop8:
		do {
			if ((LA(1)==LITERAL_import)) {
				jImportDeclaration(context);
			}
			else {
				break _loop8;
			}
			
		} while (true);
		}
	}
	
	private final void jTypeDefinition(
		CParseCompilationUnitContext context
	) throws RecognitionException, TokenStreamException {
		
		
		int			mods = 0;
		JTypeDeclaration	decl = null;
		TokenReference	sourceRef = buildTokenReference();
		
		
		switch ( LA(1)) {
		case LITERAL_abstract:
		case LITERAL_class:
		case LITERAL_final:
		case LITERAL_interface:
		case LITERAL_native:
		case LITERAL_private:
		case LITERAL_protected:
		case LITERAL_public:
		case LITERAL_static:
		case LITERAL_strictfp:
		case LITERAL_synchronized:
		case LITERAL_transient:
		case LITERAL_volatile:
		case LITERAL_virtual:
		case LITERAL_override:
		case LITERAL_clean:
		case LITERAL_collaboration:
		case LITERAL_provided:
		case LITERAL_expected:
		case LITERAL_crosscutting:
		case LITERAL_deployed:
		case LITERAL_privileged:
		{
			mods=jModifiers();
			{
			switch ( LA(1)) {
			case LITERAL_class:
			{
				decl=jClassDefinition(mods);
				break;
			}
			case LITERAL_interface:
			{
				decl=jInterfaceDefinition(mods);
				if ( inputState.guessing==0 ) {
					
					if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
					context.addTypeDeclaration(environment.getClassReader(), ((JInterfaceDeclaration)decl).getAssertionClass());
					}
					
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
				context.addTypeDeclaration(environment.getClassReader(), decl);
				
			}
			break;
		}
		case SEMI:
		{
			match(SEMI);
			if ( inputState.guessing==0 ) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.STRAY_SEMICOLON, null));
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	private final String  jIdentifier(
		
	) throws RecognitionException, TokenStreamException {
		String self = null;
		
		Token  i = null;
		Token  j = null;
		
		StringBuffer buffer = null;
		
		
		i = LT(1);
		match(IDENT);
		{
		_loop31:
		do {
			if ((LA(1)==DOT)) {
				match(DOT);
				j = LT(1);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					(buffer == null ? (buffer = new StringBuffer(i.getText())) : buffer).append('/').append(j.getText());
				}
			}
			else {
				break _loop31;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			self = buffer == null ? i.getText() : buffer.toString();
		}
		return self;
	}
	
	private final void jAutomaticImports(
		CParseCompilationUnitContext context
	) throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			
			context.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
			
		}
	}
	
	private final void jImportDeclaration(
		CParseCompilationUnitContext context
	) throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		Token  j = null;
		
		StringBuffer	buffer = null;
		boolean	star = false;
		String	name = null;
		
		
		match(LITERAL_import);
		i = LT(1);
		match(IDENT);
		{
		_loop12:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				match(DOT);
				j = LT(1);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					(buffer == null ? (buffer = new StringBuffer(i.getText())) : buffer).append('/').append(j.getText());
				}
			}
			else {
				break _loop12;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			name = buffer == null ? i.getText() : buffer.toString();
		}
		{
		switch ( LA(1)) {
		case DOT:
		{
			match(DOT);
			match(STAR);
			if ( inputState.guessing==0 ) {
				star = true;
			}
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMI);
		if ( inputState.guessing==0 ) {
			
			if (star) {
				context.addPackageImport(new JPackageImport(buildTokenReference(), name, getStatementComment()));
			} else {
				context.addClassImport(new JClassImport(buildTokenReference(), name, getStatementComment()));
			}
			
		}
	}
	
	private final int  jModifiers(
		
	) throws RecognitionException, TokenStreamException {
		int self = 0;
		
		
		int		mod;
		
		
		{
		_loop19:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				mod=jModifier();
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				break _loop19;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			//!!! 010428 move to JXxxDeclaration
			if (CModifier.getSubsetSize(self,
							  org.caesarj.kjc.Constants.ACC_PUBLIC
							  | org.caesarj.kjc.Constants.ACC_PROTECTED
							  | org.caesarj.kjc.Constants.ACC_PRIVATE) > 1) {
				reportTrouble(new PositionedError(buildTokenReference(),
								  KjcMessages.INCOMPATIBLE_MODIFIERS,
								  CModifier.toString(CModifier.getSubsetOf(self,
													   org.caesarj.kjc.Constants.ACC_PUBLIC
													   | org.caesarj.kjc.Constants.ACC_PROTECTED
													   | org.caesarj.kjc.Constants.ACC_PRIVATE))));
			}
			
		}
		return self;
	}
	
	private final FjClassDeclaration  jClassDefinition(
		int modifiers
	) throws RecognitionException, TokenStreamException {
		FjClassDeclaration self = null;
		
		Token  ident = null;
		
		CTypeVariable[]       	typeVariables = CTypeVariable.EMPTY;
		CReferenceType			superClass = null;
		CReferenceType[]			interfaces = CReferenceType.EMPTY;
		CReferenceType			binding = null;
		CReferenceType			providing = null;
		CReferenceType			wrappee = null;  
		ParseClassContext	context = ParseClassContext.getInstance();
		TokenReference	sourceRef = buildTokenReference();
		JavadocComment	javadoc = getJavadocComment();
		JavaStyleComment[]	comments = getStatementComment();
		
		
		match(LITERAL_class);
		ident = LT(1);
		match(IDENT);
		{
		switch ( LA(1)) {
		case LT:
		{
			typeVariables=kTypeVariableDeclarationList();
			break;
		}
		case LITERAL_extends:
		case LITERAL_implements:
		case LITERAL_binds:
		case LITERAL_provides:
		case LITERAL_wraps:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LITERAL_extends:
		{
			superClass=jSuperClassClause();
			break;
		}
		case LITERAL_binds:
		{
			binding=jBindsClause();
			break;
		}
		case LITERAL_provides:
		{
			providing=jProvidesClause();
			break;
		}
		case LITERAL_implements:
		case LITERAL_wraps:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LITERAL_implements:
		{
			interfaces=jImplementsClause();
			break;
		}
		case LITERAL_wraps:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LITERAL_wraps:
		{
			wrappee=jWrapsClause();
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		jClassBlock(context);
		if ( inputState.guessing==0 ) {
			
			JMethodDeclaration[]      methods;
			
			if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
			JMethodDeclaration[]    assertions = context.getAssertions();
			JMethodDeclaration[]    decMethods = context.getMethods();
			
			methods = new JMethodDeclaration[assertions.length+decMethods.length];
			// assertions first!
			System.arraycopy(assertions, 0, methods, 0, assertions.length);
			System.arraycopy(decMethods, 0, methods, assertions.length, decMethods.length);
			} else {
			methods = context.getMethods();
			}
			if (superClass instanceof CciWeaveletReferenceType)
				self = new CciWeaveletClassDeclaration(sourceRef,
							   modifiers,
							   ident.getText(),
							   typeVariables,
							   (CciWeaveletReferenceType)superClass,
							   wrappee,
							   interfaces,
							   context.getFields(),
							   methods,
							   context.getInnerClasses(),
							   context.getBody(),
							   javadoc,
							   comments);
				  else if (providing != null || binding != null)
				  {
					if (CModifier.contains(modifiers, org.caesarj.kjc.Constants.FJC_VIRTUAL))
			self = new FjVirtualClassDeclaration(sourceRef,
							   modifiers,
							   ident.getText(),
							   typeVariables,
							   superClass,
							   binding,
							   providing,
							   wrappee,				   
							   interfaces,
							   context.getFields(),
							   methods,
							   context.getInnerClasses(),
							   context.getBody(),
							   javadoc,
							   comments);
					 else
			self = new FjCleanClassDeclaration(sourceRef,
							   modifiers | org.caesarj.kjc.Constants.FJC_CLEAN,
							   ident.getText(),
							   typeVariables,
							   superClass,
							   binding,
							   providing,
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
							   context.getDeclares()				   
							   );
				 }
			else if( CModifier.contains( modifiers, org.caesarj.kjc.Constants.FJC_OVERRIDE ) ) {
			self = new FjOverrideClassDeclaration(sourceRef,
							   modifiers,
							   ident.getText(),
							   typeVariables,
							   superClass,
							   binding,
							   providing,
							   wrappee,
							   interfaces,
							   context.getFields(),
							   methods,
							   context.getInnerClasses(),
							   context.getBody(),
							   javadoc,
							   comments);
			} else if( CModifier.contains( modifiers, org.caesarj.kjc.Constants.FJC_VIRTUAL ) ) {
			self = new FjVirtualClassDeclaration(sourceRef,
							   modifiers,
							   ident.getText(),
							   typeVariables,
							   superClass,
							   binding,
							   providing,
							   wrappee,			   
							   interfaces,
							   context.getFields(),
							   methods,
							   context.getInnerClasses(),
							   context.getBody(),
							   javadoc,
							   comments);
			} else if( CModifier.contains( modifiers, org.caesarj.kjc.Constants.FJC_CLEAN ) ) {
			self = new FjCleanClassDeclaration(sourceRef,
							   modifiers,
							   ident.getText(),
							   typeVariables,
							   superClass,
							   binding,
							   providing,
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
							   context.getDeclares());
			} else {
			self = new FjClassDeclaration(sourceRef,
							   modifiers,
							   ident.getText(),
			typeVariables,
							   superClass,
							   binding,
							   providing,
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
							   context.getDeclares());
						}
			context.release();
			
		}
		return self;
	}
	
	private final JTypeDeclaration  jInterfaceDefinition(
		int modifiers
	) throws RecognitionException, TokenStreamException {
		JTypeDeclaration self = null;
		
		Token  ident = null;
		
		CTypeVariable[]       typeVariables = CTypeVariable.EMPTY;
		CReferenceType[]		interfaces =  CReferenceType.EMPTY;
		ParseClassContext	context = ParseClassContext.getInstance();
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_interface);
		ident = LT(1);
		match(IDENT);
		{
		switch ( LA(1)) {
		case LT:
		{
			typeVariables=kTypeVariableDeclarationList();
			break;
		}
		case LITERAL_extends:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		interfaces=jInterfaceExtends();
		jClassBlock(context);
		if ( inputState.guessing==0 ) {
			
			
			self = new CciInterfaceDeclaration(sourceRef,
			modifiers,
			ident.getText(),
			typeVariables,
			interfaces,
			context.getFields(),
			context.getMethods(),
			context.getInnerClasses(),
			context.getBody(),
			getJavadocComment(),
			getStatementComment());
			
			
			if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
			KopiAssertionClassDeclaration assertionClass =
			new KopiAssertionClassDeclaration(sourceRef,
			modifiers,
			ident.getText(),
			CTypeVariable.cloneArray(typeVariables),
			context.getAssertions(),
			null,
			null,
			environment.getTypeFactory());
					if (self instanceof JInterfaceDeclaration)
				((JInterfaceDeclaration)self).setAssertionClass(assertionClass);
			}
			
			context.release();
			
		}
		return self;
	}
	
	private final JVariableDeclarationStatement  jLocalVariableDeclaration(
		int modifiers
	) throws RecognitionException, TokenStreamException {
		JVariableDeclarationStatement self = null;
		
		
		CType			type;
		JVariableDefinition[] decl;
		TokenReference	sourceRef = buildTokenReference();
		
		
		type=jTypeSpec();
		decl=jVariableDefinitions(modifiers, type);
		if ( inputState.guessing==0 ) {
			
			self = new JVariableDeclarationStatement(sourceRef,
									  decl,
									  getStatementComment());
			
		}
		return self;
	}
	
	private final CType  jTypeSpec(
		
	) throws RecognitionException, TokenStreamException {
		CType self = null;
		
		
		switch ( LA(1)) {
		case IDENT:
		{
			self=jClassTypeSpec();
			break;
		}
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_short:
		case LITERAL_void:
		{
			self=jBuiltInTypeSpec();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JVariableDefinition[]  jVariableDefinitions(
		int modifiers, CType type
	) throws RecognitionException, TokenStreamException {
		JVariableDefinition[] self = null;
		
		
		ArrayList		vars = new ArrayList();
		JVariableDefinition	decl;
		
		
		decl=jVariableDeclarator(modifiers, type);
		if ( inputState.guessing==0 ) {
			vars.add(decl);
		}
		{
		_loop96:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				decl=jVariableDeclarator(modifiers, type);
				if ( inputState.guessing==0 ) {
					vars.add(decl);
				}
			}
			else {
				break _loop96;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			self = (JVariableDefinition[]) vars.toArray(new FjVariableDefinition[vars.size()]);
		}
		return self;
	}
	
	private final int  jModifier(
		
	) throws RecognitionException, TokenStreamException {
		int self = 0;
		
		
		switch ( LA(1)) {
		case LITERAL_public:
		{
			match(LITERAL_public);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_PUBLIC;
			}
			break;
		}
		case LITERAL_protected:
		{
			match(LITERAL_protected);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_PROTECTED;
			}
			break;
		}
		case LITERAL_private:
		{
			match(LITERAL_private);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_PRIVATE;
			}
			break;
		}
		case LITERAL_static:
		{
			match(LITERAL_static);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_STATIC;
			}
			break;
		}
		case LITERAL_abstract:
		{
			match(LITERAL_abstract);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_ABSTRACT;
			}
			break;
		}
		case LITERAL_final:
		{
			match(LITERAL_final);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_FINAL;
			}
			break;
		}
		case LITERAL_native:
		{
			match(LITERAL_native);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_NATIVE;
			}
			break;
		}
		case LITERAL_strictfp:
		{
			match(LITERAL_strictfp);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_STRICT;
			}
			break;
		}
		case LITERAL_synchronized:
		{
			match(LITERAL_synchronized);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_SYNCHRONIZED;
			}
			break;
		}
		case LITERAL_transient:
		{
			match(LITERAL_transient);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_TRANSIENT;
			}
			break;
		}
		case LITERAL_volatile:
		{
			match(LITERAL_volatile);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_VOLATILE;
			}
			break;
		}
		case LITERAL_virtual:
		{
			match(LITERAL_virtual);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.FJC_VIRTUAL;
			}
			break;
		}
		case LITERAL_override:
		{
			match(LITERAL_override);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.FJC_OVERRIDE;
			}
			break;
		}
		case LITERAL_clean:
		{
			match(LITERAL_clean);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.FJC_CLEAN;
			}
			break;
		}
		case LITERAL_privileged:
		{
			match(LITERAL_privileged);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_PRIVILEGED;
			}
			break;
		}
		case LITERAL_crosscutting:
		{
			match(LITERAL_crosscutting);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_CROSSCUTTING;
			}
			break;
		}
		case LITERAL_deployed:
		{
			match(LITERAL_deployed);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.ACC_DEPLOYED;
			}
			break;
		}
		case LITERAL_collaboration:
		{
			match(LITERAL_collaboration);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.CCI_COLLABORATION;
			}
			break;
		}
		case LITERAL_provided:
		{
			match(LITERAL_provided);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.CCI_PROVIDED;
			}
			break;
		}
		case LITERAL_expected:
		{
			match(LITERAL_expected);
			if ( inputState.guessing==0 ) {
				self = org.caesarj.kjc.Constants.CCI_EXPECTED;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final CReferenceType  jClassTypeSpec(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		
		String		name = null;
		int			bounds = 0;
		
		
		self=jTypeName();
		{
		_loop23:
		do {
			if ((LA(1)==LBRACK)) {
				match(LBRACK);
				match(RBRACK);
				if ( inputState.guessing==0 ) {
					bounds += 1;
				}
			}
			else {
				break _loop23;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			if (bounds > 0) {
				self = new CArrayType(self, bounds);
			}
			
		}
		return self;
	}
	
	private final CType  jBuiltInTypeSpec(
		
	) throws RecognitionException, TokenStreamException {
		CType self = null;
		
		
		int			bounds = 0;
		
		
		self=jBuiltInType();
		{
		_loop26:
		do {
			if ((LA(1)==LBRACK)) {
				match(LBRACK);
				match(RBRACK);
				if ( inputState.guessing==0 ) {
					bounds += 1;
				}
			}
			else {
				break _loop26;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			if (bounds > 0) {
				self = new CArrayType(self, bounds);
			}
			
		}
		return self;
	}
	
	private final CReferenceType  jTypeName(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		Token  i = null;
		Token  j = null;
		
		CReferenceType[]          typeParameters = null;
		CReferenceType[][]        allTypeParameters; // incl. outer
		ArrayList                container = new ArrayList();
		StringBuffer          buffer = null;
		
		
		i = LT(1);
		match(IDENT);
		{
		switch ( LA(1)) {
		case LT:
		{
			typeParameters=kReferenceTypeList();
			break;
		}
		case EOF:
		case LITERAL_implements:
		case FJEQUAL:
		case LITERAL_wraps:
		case LITERAL_around:
		case ASSIGN:
		case BAND:
		case BAND_ASSIGN:
		case BOR:
		case BOR_ASSIGN:
		case BSR_ASSIGN:
		case BXOR:
		case BXOR_ASSIGN:
		case COLON:
		case COMMA:
		case DOT:
		case EQUAL:
		case GT:
		case LAND:
		case LBRACK:
		case LCURLY:
		case LOR:
		case LPAREN:
		case MINUS_ASSIGN:
		case NOT_EQUAL:
		case PERCENT_ASSIGN:
		case PLUS_ASSIGN:
		case QUESTION:
		case RBRACK:
		case RCURLY:
		case RPAREN:
		case SEMI:
		case SLASH_ASSIGN:
		case SL_ASSIGN:
		case SR_ASSIGN:
		case STAR_ASSIGN:
		case IDENT:
		case TYPE_PATTERN:
		case ENSURE:
		case REQUIRE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
			container.add(typeParameters);
			typeParameters = null;
			
		}
		{
		_loop255:
		do {
			if ((LA(1)==DOT)) {
				match(DOT);
				j = LT(1);
				match(IDENT);
				{
				switch ( LA(1)) {
				case LT:
				{
					typeParameters=kReferenceTypeList();
					break;
				}
				case EOF:
				case LITERAL_implements:
				case FJEQUAL:
				case LITERAL_wraps:
				case LITERAL_around:
				case ASSIGN:
				case BAND:
				case BAND_ASSIGN:
				case BOR:
				case BOR_ASSIGN:
				case BSR_ASSIGN:
				case BXOR:
				case BXOR_ASSIGN:
				case COLON:
				case COMMA:
				case DOT:
				case EQUAL:
				case GT:
				case LAND:
				case LBRACK:
				case LCURLY:
				case LOR:
				case LPAREN:
				case MINUS_ASSIGN:
				case NOT_EQUAL:
				case PERCENT_ASSIGN:
				case PLUS_ASSIGN:
				case QUESTION:
				case RBRACK:
				case RCURLY:
				case RPAREN:
				case SEMI:
				case SLASH_ASSIGN:
				case SL_ASSIGN:
				case SR_ASSIGN:
				case STAR_ASSIGN:
				case IDENT:
				case TYPE_PATTERN:
				case ENSURE:
				case REQUIRE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					
					container.add(typeParameters);
					typeParameters = null;
					(buffer == null ? (buffer = new StringBuffer(i.getText())) : buffer).append('/').append(j.getText());
					
				}
			}
			else {
				break _loop255;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			String              name = buffer == null ? i.getText() : buffer.toString();
			
			allTypeParameters = (CReferenceType[][])container.toArray(new CReferenceType[container.size()][]);
			self = environment.getTypeFactory().createType(name, allTypeParameters, false);
			
		}
		return self;
	}
	
	private final CType  jBuiltInType(
		
	) throws RecognitionException, TokenStreamException {
		CType self = null;
		
		
		TypeFactory factory = environment.getTypeFactory();
		
		
		switch ( LA(1)) {
		case LITERAL_void:
		{
			match(LITERAL_void);
			if ( inputState.guessing==0 ) {
				self = factory.getVoidType();
			}
			break;
		}
		case LITERAL_boolean:
		{
			match(LITERAL_boolean);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);
			}
			break;
		}
		case LITERAL_byte:
		{
			match(LITERAL_byte);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_BYTE);
			}
			break;
		}
		case LITERAL_char:
		{
			match(LITERAL_char);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_CHAR);
			}
			break;
		}
		case LITERAL_short:
		{
			match(LITERAL_short);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_SHORT);
			}
			break;
		}
		case LITERAL_int:
		{
			match(LITERAL_int);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_INT);
			}
			break;
		}
		case LITERAL_long:
		{
			match(LITERAL_long);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_LONG);
			}
			break;
		}
		case LITERAL_float:
		{
			match(LITERAL_float);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_FLOAT);
			}
			break;
		}
		case LITERAL_double:
		{
			match(LITERAL_double);
			if ( inputState.guessing==0 ) {
				self = factory.getPrimitiveType(TypeFactory.PRM_DOUBLE);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final CType  jType(
		
	) throws RecognitionException, TokenStreamException {
		CType type = null;
		
		
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_short:
		case LITERAL_void:
		{
			type=jBuiltInType();
			break;
		}
		case IDENT:
		{
			type=jTypeName();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return type;
	}
	
	private final CTypeVariable[]  kTypeVariableDeclarationList(
		
	) throws RecognitionException, TokenStreamException {
		CTypeVariable[] self = CTypeVariable.EMPTY;
		
		
		CTypeVariable  tv = null;
		ArrayList	 container = new ArrayList();
		
		
		match(LT);
		tv=kTypeVariableDeclaration();
		if ( inputState.guessing==0 ) {
			container.add(tv);
		}
		{
		_loop261:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				tv=kTypeVariableDeclaration();
				if ( inputState.guessing==0 ) {
					container.add(tv);
				}
			}
			else {
				break _loop261;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			self = (CTypeVariable[])container.toArray(new CTypeVariable[container.size()]);
			for (int i =0; i< self.length; i++) {
			self[i].setIndex(i);
			}
			
		}
		match(GT);
		if ( inputState.guessing==0 ) {
			
			if (!environment.isGenericEnabled()) {
			reportTrouble(new PositionedError(buildTokenReference(), KjcMessages.UNSUPPORTED_GENERIC_TYPE, null));
			}
			
		}
		return self;
	}
	
	private final CReferenceType  jSuperClassClause(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		
		{
		match(LITERAL_extends);
		self=jSuperTypeName();
		}
		return self;
	}
	
	private final CReferenceType  jBindsClause(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		
		{
		match(LITERAL_binds);
		self=jTypeName();
		}
		return self;
	}
	
	private final CReferenceType  jProvidesClause(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		
		{
		match(LITERAL_provides);
		self=jTypeName();
		}
		return self;
	}
	
	private final CReferenceType[]  jImplementsClause(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType[] self = CReferenceType.EMPTY;
		
		
		{
		match(LITERAL_implements);
		self=jNameList();
		}
		return self;
	}
	
	private final CReferenceType  jWrapsClause(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		
		{
		match(LITERAL_wraps);
		self=jTypeName();
		}
		return self;
	}
	
	private final void jClassBlock(
		ParseClassContext context
	) throws RecognitionException, TokenStreamException {
		
		
		match(LCURLY);
		{
		_loop51:
		do {
			switch ( LA(1)) {
			case LITERAL_abstract:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_class:
			case LITERAL_double:
			case LITERAL_final:
			case LITERAL_float:
			case LITERAL_int:
			case LITERAL_interface:
			case LITERAL_long:
			case LITERAL_native:
			case LITERAL_private:
			case LITERAL_protected:
			case LITERAL_public:
			case LITERAL_short:
			case LITERAL_static:
			case LITERAL_strictfp:
			case LITERAL_synchronized:
			case LITERAL_transient:
			case LITERAL_void:
			case LITERAL_volatile:
			case LITERAL_virtual:
			case LITERAL_override:
			case LITERAL_clean:
			case LITERAL_collaboration:
			case LITERAL_provided:
			case LITERAL_expected:
			case LITERAL_after:
			case LITERAL_before:
			case LITERAL_crosscutting:
			case LITERAL_declare:
			case LITERAL_deployed:
			case LITERAL_pointcut:
			case LITERAL_privileged:
			case LCURLY:
			case LT:
			case IDENT:
			case INVARIANT:
			{
				jMember(context);
				break;
			}
			case SEMI:
			{
				match(SEMI);
				if ( inputState.guessing==0 ) {
					reportTrouble(new CWarning(buildTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
				}
				break;
			}
			default:
			{
				break _loop51;
			}
			}
		} while (true);
		}
		match(RCURLY);
	}
	
	private final CReferenceType  jSuperTypeName(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType self = null;
		
		
			CReferenceType collaborationInterface = null;
			CReferenceType implementation = null;
			CReferenceType binding = null;
		
		
		{
		switch ( LA(1)) {
		case IDENT:
		{
			collaborationInterface=jTypeName();
			break;
		}
		case LITERAL_implements:
		case LITERAL_wraps:
		case LCURLY:
		case LPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			match(LPAREN);
			implementation=jTypeName();
			match(COMMA);
			binding=jTypeName();
			match(RPAREN);
			break;
		}
		case LITERAL_implements:
		case LITERAL_wraps:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
				if (collaborationInterface != null && implementation != null && binding != null)
					self = new CciWeaveletReferenceType(collaborationInterface,
								implementation,
								binding);
				else
					self = collaborationInterface;
			
			
		}
		return self;
	}
	
	private final CReferenceType[]  jInterfaceExtends(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType[] self = CReferenceType.EMPTY;
		
		
		{
		switch ( LA(1)) {
		case LITERAL_extends:
		{
			match(LITERAL_extends);
			self=jNameList();
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final void jMember(
		ParseClassContext context
	) throws RecognitionException, TokenStreamException {
		
		Token  pattern = null;
		
		int				modifiers = 0;
		CType				type;
		JMethodDeclaration		method;
		CTypeVariable[]               typeVariables;
		JTypeDeclaration		decl;
		JVariableDefinition[]		vars;
		JStatement[]			body = null;
		TokenReference		sourceRef = buildTokenReference();
		KopiInvariantDeclaration   invariant = null;
		JFormalParameter[] parameters;
		JFormalParameter extraParam = null;  
		CaesarAdviceKind kind = null;
		TypeFactory factory = environment.getTypeFactory();
		CReferenceType[]		throwsList = CReferenceType.EMPTY;  
		PointcutDeclaration pointcutDecl;
		AdviceDeclaration adviceDecl;
		
		
		switch ( LA(1)) {
		case INVARIANT:
		{
			match(INVARIANT);
			body=jCompoundStatement();
			if ( inputState.guessing==0 ) {
				
					if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
						invariant = new KopiInvariantDeclaration(sourceRef,
					new KopiInvariantStatement(sourceRef,
											new JBlock(sourceRef,
											body,
										null)),
				getJavadocComment(),
				getStatementComment(),
				environment.getTypeFactory());
						context.addAssertionDeclaration(invariant);
					} else {
						reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_INVARIANT, null));
					}
					
			}
			break;
		}
		case LITERAL_abstract:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_class:
		case LITERAL_double:
		case LITERAL_final:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_interface:
		case LITERAL_long:
		case LITERAL_native:
		case LITERAL_private:
		case LITERAL_protected:
		case LITERAL_public:
		case LITERAL_short:
		case LITERAL_static:
		case LITERAL_strictfp:
		case LITERAL_synchronized:
		case LITERAL_transient:
		case LITERAL_void:
		case LITERAL_volatile:
		case LITERAL_virtual:
		case LITERAL_override:
		case LITERAL_clean:
		case LITERAL_collaboration:
		case LITERAL_provided:
		case LITERAL_expected:
		case LITERAL_after:
		case LITERAL_before:
		case LITERAL_crosscutting:
		case LITERAL_declare:
		case LITERAL_deployed:
		case LITERAL_pointcut:
		case LITERAL_privileged:
		case LCURLY:
		case LT:
		case IDENT:
		{
			{
			if ((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2)))) {
				modifiers=jModifiers();
				{
				switch ( LA(1)) {
				case LITERAL_class:
				{
					decl=jClassDefinition(modifiers);
					if ( inputState.guessing==0 ) {
						context.addInnerDeclaration(decl);
					}
					break;
				}
				case LITERAL_interface:
				{
					decl=jInterfaceDefinition(modifiers);
					if ( inputState.guessing==0 ) {
						
								context.addInnerDeclaration(decl);
								if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
									context.addInnerDeclaration(((JInterfaceDeclaration)decl).getAssertionClass());
								}
							
					}
					break;
				}
				case LT:
				{
					typeVariables=kTypeVariableDeclarationList();
					type=jTypeSpec();
					method=jMethodDefinition(context, modifiers, type, typeVariables);
					if ( inputState.guessing==0 ) {
						context.addMethodDeclaration(method);
					}
					break;
				}
				case LITERAL_pointcut:
				{
					match(LITERAL_pointcut);
					pointcutDecl=jPointcutDefinition(modifiers, CTypeVariable.EMPTY);
					if ( inputState.guessing==0 ) {
						
										context.addPointcutDeclaration(pointcutDecl);	
									
					}
					break;
				}
				case LITERAL_declare:
				{
					match(LITERAL_declare);
					pattern = LT(1);
					match(TYPE_PATTERN);
					if ( inputState.guessing==0 ) {
						try{
							CaesarPatternParser patternParser = new CaesarPatternParser(
																	"declare "+pattern.getText(),
																	new CaesarSourceContext(sourceRef) );
							context.addDeclare(patternParser.parseDeclare());			
						}
						catch(CaesarParserException e) {
							reportTrouble(new PositionedError(sourceRef, CaesarMessages.WEAVER_ERROR, e.getMessage()));			
						}
						catch(RuntimeException e) {
							reportTrouble(new PositionedError(sourceRef, CaesarMessages.WEAVER_ERROR, e.getMessage()));
						}							
					}
					break;
				}
				case LITERAL_before:
				{
					match(LITERAL_before);
					match(LPAREN);
					parameters=jParameterDeclarationList(JLocalVariable.DES_PARAMETER);
					match(RPAREN);
					adviceDecl=jAdviceDeclaration(CaesarAdviceKind.Before, modifiers, parameters, factory.getVoidType(), CReferenceType.EMPTY, null);
					if ( inputState.guessing==0 ) {
							
										context.addAdviceDeclaration(adviceDecl);
									
					}
					break;
				}
				case LITERAL_after:
				{
					match(LITERAL_after);
					if ( inputState.guessing==0 ) {
						kind = CaesarAdviceKind.After;
					}
					match(LPAREN);
					parameters=jParameterDeclarationList(JLocalVariable.DES_PARAMETER);
					match(RPAREN);
					{
					switch ( LA(1)) {
					case LITERAL_returning:
					{
						match(LITERAL_returning);
						{
						switch ( LA(1)) {
						case LPAREN:
						{
							match(LPAREN);
							extraParam=jParameterDeclaration(JLocalVariable.DES_GENERATED);
							match(RPAREN);
							break;
						}
						case TYPE_PATTERN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						if ( inputState.guessing==0 ) {
							kind = CaesarAdviceKind.AfterReturning;
						}
						break;
					}
					case LITERAL_throwing:
					{
						match(LITERAL_throwing);
						{
						switch ( LA(1)) {
						case LPAREN:
						{
							match(LPAREN);
							extraParam=jParameterDeclaration(JLocalVariable.DES_GENERATED);
							match(RPAREN);
							break;
						}
						case TYPE_PATTERN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						if ( inputState.guessing==0 ) {
							kind = CaesarAdviceKind.AfterThrowing;
						}
						break;
					}
					case TYPE_PATTERN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					adviceDecl=jAdviceDeclaration(kind, modifiers, parameters, factory.getVoidType(), CReferenceType.EMPTY, extraParam);
					if ( inputState.guessing==0 ) {
						
										context.addAdviceDeclaration(adviceDecl);			
									
					}
					break;
				}
				default:
					if ((LA(1)==IDENT) && (LA(2)==LPAREN)) {
						method=jConstructorDefinition(context, modifiers);
						if ( inputState.guessing==0 ) {
							context.addMethodDeclaration(method);
						}
					}
					else if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
						type=jTypeSpec();
						{
						if ((LA(1)==LITERAL_around)) {
							match(LITERAL_around);
							match(LPAREN);
							parameters=jParameterDeclarationList(JLocalVariable.DES_PARAMETER);
							match(RPAREN);
							{
							switch ( LA(1)) {
							case LITERAL_throws:
							{
								throwsList=jThrowsClause();
								break;
							}
							case TYPE_PATTERN:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
							}
							adviceDecl=jAdviceDeclaration(CaesarAdviceKind.Around, modifiers, parameters, type, throwsList, extraParam);
							if ( inputState.guessing==0 ) {
								
												context.addAdviceDeclaration(adviceDecl);	  			
									  		
							}
						}
						else if ((LA(1)==IDENT) && (LA(2)==LPAREN)) {
							method=jMethodDefinition(context, modifiers, type, CTypeVariable.EMPTY);
							if ( inputState.guessing==0 ) {
								context.addMethodDeclaration(method);
							}
						}
						else if ((LA(1)==IDENT) && (_tokenSet_6.member(LA(2)))) {
							vars=jVariableDefinitions(modifiers, type);
							match(SEMI);
							if ( inputState.guessing==0 ) {
								
												for (int i = 0; i < vars.length; i++) {
													context.addFieldDeclaration(new FjFieldDeclaration(sourceRef,
																										vars[i],
																										getJavadocComment(),
																										getStatementComment()));
												}
											
							}
						}
						else {
							throw new NoViableAltException(LT(1), getFilename());
						}
						
						}
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else if ((LA(1)==LITERAL_static) && (LA(2)==LCURLY)) {
				match(LITERAL_static);
				body=jCompoundStatement();
				if ( inputState.guessing==0 ) {
					context.addBlockInitializer(new JClassBlock(sourceRef, true, body));
				}
			}
			else if ((LA(1)==LCURLY)) {
				body=jCompoundStatement();
				if ( inputState.guessing==0 ) {
					context.addBlockInitializer(new JClassBlock(sourceRef, false, body));
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	private final CReferenceType[]  jNameList(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType[] self = null;
		
		
		CReferenceType	name;
		ArrayList	container = new ArrayList();
		
		
		name=jTypeName();
		if ( inputState.guessing==0 ) {
			container.add(name);
		}
		{
		_loop250:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				name=jTypeName();
				if ( inputState.guessing==0 ) {
					container.add(name);
				}
			}
			else {
				break _loop250;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			self = (CReferenceType[])container.toArray(new CReferenceType[container.size()]);
		}
		return self;
	}
	
	private final JStatement[]  jCompoundStatement(
		
	) throws RecognitionException, TokenStreamException {
		JStatement[] self = null;
		
		
		ArrayList		body = new ArrayList();
		JStatement		stmt;
		
		
		match(LCURLY);
		{
		_loop118:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				stmt=jBlockStatement();
				if ( inputState.guessing==0 ) {
					
						if (stmt instanceof JEmptyStatement) {
						  reportTrouble(new CWarning(stmt.getTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
						}
						body.add(stmt);
					
				}
			}
			else {
				break _loop118;
			}
			
		} while (true);
		}
		match(RCURLY);
		if ( inputState.guessing==0 ) {
			self = (JStatement[]) body.toArray(new JStatement[body.size()]);
		}
		return self;
	}
	
	private final JConstructorDeclaration  jConstructorDefinition(
		ParseClassContext context, int modifiers
	) throws RecognitionException, TokenStreamException {
		JConstructorDeclaration self = null;
		
		Token  name = null;
		
		JFormalParameter[]	parameters;
		CReferenceType[]		throwsList = CReferenceType.EMPTY;
		JConstructorCall	constructorCall = null;
		ArrayList		body = new ArrayList();
		JStatement		stmt;
		JStatement[]	    	ensure = null;
		JStatement[]          require = null;
		TokenReference	sourceRef = buildTokenReference();
		JavadocComment	javadoc = getJavadocComment();
		JavaStyleComment[]	comments = getStatementComment();
		
		
		name = LT(1);
		match(IDENT);
		match(LPAREN);
		parameters=jParameterDeclarationList(JLocalVariable.DES_PARAMETER);
		match(RPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_throws:
		{
			throwsList=jThrowsClause();
			break;
		}
		case LCURLY:
		case REQUIRE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case REQUIRE:
		{
			match(REQUIRE);
			require=jCompoundStatement();
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LCURLY);
		{
		boolean synPredMatched72 = false;
		if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
			int _m72 = mark();
			synPredMatched72 = true;
			inputState.guessing++;
			try {
				{
				{
				switch ( LA(1)) {
				case LITERAL_this:
				{
					match(LITERAL_this);
					break;
				}
				case LITERAL_super:
				{
					match(LITERAL_super);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(LPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched72 = false;
			}
			rewind(_m72);
			inputState.guessing--;
		}
		if ( synPredMatched72 ) {
			constructorCall=jExplicitConstructorInvocation();
		}
		else {
			boolean synPredMatched74 = false;
			if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
				int _m74 = mark();
				synPredMatched74 = true;
				inputState.guessing++;
				try {
					{
					jPrimaryExpression();
					match(DOT);
					match(LITERAL_super);
					match(LPAREN);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched74 = false;
				}
				rewind(_m74);
				inputState.guessing--;
			}
			if ( synPredMatched74 ) {
				constructorCall=jExplicitConstructorInvocation();
			}
			else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_11.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop76:
			do {
				if ((_tokenSet_7.member(LA(1)))) {
					stmt=jBlockStatement();
					if ( inputState.guessing==0 ) {
						
							if (stmt instanceof JEmptyStatement) {
							  reportTrouble(new CWarning(stmt.getTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
							}
							body.add(stmt);
						
					}
				}
				else {
					break _loop76;
				}
				
			} while (true);
			}
			match(RCURLY);
			{
			switch ( LA(1)) {
			case ENSURE:
			{
				match(ENSURE);
				ensure=jCompoundStatement();
				break;
			}
			case LITERAL_abstract:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_class:
			case LITERAL_double:
			case LITERAL_final:
			case LITERAL_float:
			case LITERAL_int:
			case LITERAL_interface:
			case LITERAL_long:
			case LITERAL_native:
			case LITERAL_private:
			case LITERAL_protected:
			case LITERAL_public:
			case LITERAL_short:
			case LITERAL_static:
			case LITERAL_strictfp:
			case LITERAL_synchronized:
			case LITERAL_transient:
			case LITERAL_void:
			case LITERAL_volatile:
			case LITERAL_virtual:
			case LITERAL_override:
			case LITERAL_clean:
			case LITERAL_collaboration:
			case LITERAL_provided:
			case LITERAL_expected:
			case LITERAL_after:
			case LITERAL_before:
			case LITERAL_crosscutting:
			case LITERAL_declare:
			case LITERAL_deployed:
			case LITERAL_pointcut:
			case LITERAL_privileged:
			case LCURLY:
			case LT:
			case RCURLY:
			case SEMI:
			case IDENT:
			case INVARIANT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
				if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
				KopiPostconditionDeclaration            post = null;
				KopiPreconditionDeclaration             pre = null;
				
				if (ensure != null) {
				context.addAssertionDeclaration(post = new KopiPostconditionDeclaration(sourceRef,
				modifiers,
				CTypeVariable.EMPTY,
				environment.getTypeFactory().getVoidType(),
				name.getText(),
				parameters,
				throwsList,
				new JBlock(sourceRef, ensure, null),
				getJavadocComment(),
				getStatementComment(),
				environment.getTypeFactory()));
				}
				if (require != null) {
				context.addAssertionDeclaration(pre = new  KopiPreconditionDeclaration(sourceRef,
				modifiers | Constants.ACC_STATIC,
				CTypeVariable.EMPTY,
				environment.getTypeFactory().getVoidType(),
				name.getText(),
				parameters,
				throwsList,
				new JBlock(sourceRef, require, null),
				getJavadocComment(),
				getStatementComment(),
				environment.getTypeFactory()));
				}
				self = new KopiConstructorDeclaration(sourceRef,
				modifiers,
				name.getText(),
				parameters,
				throwsList,
				new KopiConstructorBlock(sourceRef,
				constructorCall,
				(JStatement[]) body.toArray(new JStatement[body.size()])), //org.caesarj.util.Utils.toArray(body, JStatement.class)),
				javadoc,
				comments,
				pre,
				post,
				environment.getTypeFactory());
				} else {
				if (require != null) {
				reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_REQUIRE, null));
				}
				if (ensure != null) {
				reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_ENSURE, null));
				}
				self = new FjConstructorDeclaration(sourceRef,
				modifiers,
				name.getText(),
				parameters,
				throwsList,
				new FjConstructorBlock(sourceRef,
				constructorCall,
				(JStatement[]) body.toArray(new JStatement[body.size()])),
				javadoc,
				comments,
				environment.getTypeFactory());
				}
				
			}
			return self;
		}
		
	private final JMethodDeclaration  jMethodDefinition(
		ParseClassContext context, int modifiers, CType type, CTypeVariable[] typeVariables
	) throws RecognitionException, TokenStreamException {
		JMethodDeclaration self = null;
		
		Token  name = null;
		
		JFormalParameter[]	parameters;
		int			bounds = 0;
		CReferenceType[]		throwsList = CReferenceType.EMPTY;
		JStatement[]		body = null;
		JStatement[]	    	ensure = null;
		JStatement[]          require = null;
		TokenReference	sourceRef = buildTokenReference();
		JavadocComment	javadoc = getJavadocComment();
		JavaStyleComment[]	comments = getStatementComment();
		
		
		name = LT(1);
		match(IDENT);
		match(LPAREN);
		parameters=jParameterDeclarationList(JLocalVariable.DES_PARAMETER);
		match(RPAREN);
		{
		_loop87:
		do {
			if ((LA(1)==LBRACK)) {
				match(LBRACK);
				match(RBRACK);
				if ( inputState.guessing==0 ) {
					bounds += 1;
				}
			}
			else {
				break _loop87;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			if (bounds > 0) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
				type = new CArrayType(type, bounds);
			}
			
		}
		{
		switch ( LA(1)) {
		case LITERAL_throws:
		{
			throwsList=jThrowsClause();
			break;
		}
		case LCURLY:
		case SEMI:
		case ENSURE:
		case REQUIRE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		{
		switch ( LA(1)) {
		case REQUIRE:
		{
			match(REQUIRE);
			require=jCompoundStatement();
			break;
		}
		case LCURLY:
		case SEMI:
		case ENSURE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			body=jCompoundStatement();
			{
			switch ( LA(1)) {
			case ENSURE:
			{
				match(ENSURE);
				ensure=jCompoundStatement();
				break;
			}
			case LITERAL_abstract:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_class:
			case LITERAL_double:
			case LITERAL_final:
			case LITERAL_float:
			case LITERAL_int:
			case LITERAL_interface:
			case LITERAL_long:
			case LITERAL_native:
			case LITERAL_private:
			case LITERAL_protected:
			case LITERAL_public:
			case LITERAL_short:
			case LITERAL_static:
			case LITERAL_strictfp:
			case LITERAL_synchronized:
			case LITERAL_transient:
			case LITERAL_void:
			case LITERAL_volatile:
			case LITERAL_virtual:
			case LITERAL_override:
			case LITERAL_clean:
			case LITERAL_collaboration:
			case LITERAL_provided:
			case LITERAL_expected:
			case LITERAL_after:
			case LITERAL_before:
			case LITERAL_crosscutting:
			case LITERAL_declare:
			case LITERAL_deployed:
			case LITERAL_pointcut:
			case LITERAL_privileged:
			case LCURLY:
			case LT:
			case RCURLY:
			case SEMI:
			case IDENT:
			case INVARIANT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case SEMI:
		case ENSURE:
		{
			{
			switch ( LA(1)) {
			case ENSURE:
			{
				match(ENSURE);
				ensure=jCompoundStatement();
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(SEMI);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
			if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
			if (body != null) {
			body = new JStatement[] {new KopiConstraintStatement(sourceRef,
			new JBlock(sourceRef, body, null),
			parameters)};
			}
			KopiMethodPostconditionDeclaration      postMethod = null;
			KopiMethodPreconditionDeclaration       preMethod = null;
			
			if (((modifiers & (Constants.ACC_NATIVE | Constants.ACC_PRIVATE | Constants.ACC_STATIC)) == 0)
			|| ensure != null) {
			// native methods have no cond.
			// if ensure != null and the method is native, a failure is raised later
			KopiPostconditionStatement              postBody;
			
			postBody = new KopiPostconditionStatement(sourceRef,
			parameters,
			type,
			ensure  == null ? null : new JBlock(sourceRef,
			ensure,
			null));
			
			postMethod =
			new KopiMethodPostconditionDeclaration(sourceRef,
			modifiers,
			CTypeVariable.cloneArray(typeVariables),
			type,
			name.getText(),
			parameters,
			throwsList,
			postBody,
			getJavadocComment(),
			getStatementComment(),
			environment.getTypeFactory());
			// must be before constrained methed
			context.addAssertionDeclaration(postMethod);
			}
			if (((modifiers & (Constants.ACC_NATIVE | Constants.ACC_PRIVATE | Constants.ACC_STATIC)) == 0)
			|| require != null) {
			// native methods have no cond.
			// if require != null and the method is native, a failure is raised later
			KopiPreconditionStatement              preBody;
			
			preBody = new KopiPreconditionStatement(sourceRef,
			parameters,
			type,
			require == null ? null : new JBlock(sourceRef,
			require,
			null));
			
			preMethod =
			new  KopiMethodPreconditionDeclaration(sourceRef,
			modifiers,
			CTypeVariable.cloneArray(typeVariables),
			type,
			name.getText(),
			parameters,
			throwsList,
			preBody,
			getJavadocComment(),
			getStatementComment(),
			environment.getTypeFactory());
			// must be before constrained methed
			context.addAssertionDeclaration(preMethod);
			}
			
			self = new KopiMethodDeclaration(sourceRef,
			modifiers,
			typeVariables,
			type,
			name.getText(),
			parameters,
			throwsList,
			body == null ? null : new JBlock(sourceRef, body, null),
			javadoc,
			comments,
			preMethod,
			postMethod);
			} else {
			if (require != null) {
			reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_REQUIRE, null));
			}
			if (ensure != null) {
			reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_ENSURE, null));
			}
			
			// check if we have a clean method, i.e. a method that
			// could be taken into a clean class's interface
			if( (modifiers & Constants.ACC_PUBLIC) != 0
			&& (modifiers & Constants.ACC_STATIC) == 0 )
			self = new FjCleanMethodDeclaration(sourceRef,
			modifiers,
			typeVariables,
			type,
			name.getText(),
			parameters,
			throwsList,
			body == null ? null : new JBlock(sourceRef, body, null),
			javadoc,
			comments);
					 // check if we have private methods that might
					 // need to receive a self parameter later
			else if( (modifiers & Constants.ACC_PRIVATE) != 0 
			&& (modifiers & Constants.ACC_STATIC) == 0 )
			self = new FjPrivateMethodDeclaration(sourceRef,
			modifiers,
			typeVariables,
			type,
			name.getText(),
			parameters,
			throwsList,
			body == null ? null : new JBlock(sourceRef, body, null),
			javadoc,
			comments);
			// if this is not the case, instantiate
			// a regular method
			else
			self = new FjMethodDeclaration(sourceRef,
			modifiers, typeVariables, type,
			name.getText(), parameters, throwsList,
			body == null 
			? null 
			: new JBlock(sourceRef, body, null),
			javadoc, comments);
			
			}
			
		}
		return self;
	}
	
	private final PointcutDeclaration  jPointcutDefinition(
		int modifiers, CTypeVariable[] typeVariables
	) throws RecognitionException, TokenStreamException {
		PointcutDeclaration self = null;
		
		Token  name = null;
		
		JFormalParameter[]	parameters;
		int			bounds = 0;
		TokenReference	sourceRef = buildTokenReference();
		JavadocComment	javadoc = getJavadocComment();
		JavaStyleComment[]	comments = getStatementComment();
		TypeFactory factory = environment.getTypeFactory();  
		CaesarPointcut pointcut = null;
		
		
		name = LT(1);
		match(IDENT);
		match(LPAREN);
		parameters=jParameterDeclarationList(JLocalVariable.DES_PARAMETER);
		match(RPAREN);
		pointcut=jPointcut();
		match(SEMI);
		if ( inputState.guessing==0 ) {
			self = new PointcutDeclaration(sourceRef,
			modifiers,
			typeVariables,
			factory.getVoidType(),
			name.getText(),
			parameters,
			javadoc,
			pointcut);
			
		}
		return self;
	}
	
	private final JFormalParameter[]  jParameterDeclarationList(
		int desc
	) throws RecognitionException, TokenStreamException {
		JFormalParameter[] self = JFormalParameter.EMPTY;
		
		
		JFormalParameter		elem = null;
		ArrayList			vect = new ArrayList();
		
		
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_final:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_short:
		case LITERAL_void:
		case IDENT:
		{
			elem=jParameterDeclaration(desc);
			if ( inputState.guessing==0 ) {
				vect.add(elem);
			}
			{
			_loop111:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					elem=jParameterDeclaration(desc);
					if ( inputState.guessing==0 ) {
						vect.add(elem);
					}
				}
				else {
					break _loop111;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				self = (JFormalParameter[])vect.toArray(new JFormalParameter[vect.size()]);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final AdviceDeclaration  jAdviceDeclaration(
		CaesarAdviceKind kind, int modifiers, JFormalParameter[] parameters, CType type, CReferenceType[] throwsList, JFormalParameter extraParam
	) throws RecognitionException, TokenStreamException {
		AdviceDeclaration self = null;
		
		Token  pattern = null;
		
		int			bounds = 0;
		JStatement[]		body = null;
		JStatement[]	    	ensure = null;
		JStatement[]          require = null;
		TokenReference	sourceRef = buildTokenReference();
		JavadocComment	javadoc = getJavadocComment();
		JavaStyleComment[]	comments = getStatementComment();
		TypeFactory factory = environment.getTypeFactory();  
		
		
		pattern = LT(1);
		match(TYPE_PATTERN);
		body=jCompoundStatement();
		if ( inputState.guessing==0 ) {
			
				
			
					try {
						CaesarPointcut pointcut = null;
							CaesarPatternParser patternParser = new CaesarPatternParser(
																	pattern.getText(),
																	new CaesarSourceContext(sourceRef) );
						pointcut = patternParser.parsePointcut();
						//handle throwing parameter
						if (extraParam != null) {
							JFormalParameter[] newParameters = new JFormalParameter[parameters.length + 1];
							System.arraycopy(parameters,0,newParameters,0,parameters.length);
							newParameters[newParameters.length - 1] = extraParam;						
							parameters = newParameters;			
						}
					
						self = new AdviceDeclaration(sourceRef,
			modifiers,
			CTypeVariable.EMPTY,
			type,
			parameters,
			throwsList,
			body == null ? null : new JBlock(sourceRef, body, null),
			javadoc,
			comments,
			pointcut,
			kind,
			extraParam != null);
						
					}
					
					catch(CaesarParserException e) {
						reportTrouble(new PositionedError(sourceRef, CaesarMessages.WEAVER_ERROR, e.getMessage()));			
					}
					catch(RuntimeException e) {
						reportTrouble(new PositionedError(sourceRef, CaesarMessages.WEAVER_ERROR, e.getMessage()));							
					}  			
			
					
				
		}
		return self;
	}
	
	private final JFormalParameter  jParameterDeclaration(
		int desc
	) throws RecognitionException, TokenStreamException {
		JFormalParameter self = null;
		
		Token  ident = null;
		
		boolean	isFinal = false;
		int		bounds = 0;
		CType		type;
		TokenReference	sourceRef = buildTokenReference();
		
		
		{
		switch ( LA(1)) {
		case LITERAL_final:
		{
			match(LITERAL_final);
			if ( inputState.guessing==0 ) {
				isFinal = true;
			}
			break;
		}
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_short:
		case LITERAL_void:
		case IDENT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		type=jTypeSpec();
		ident = LT(1);
		match(IDENT);
		{
		_loop115:
		do {
			if ((LA(1)==LBRACK)) {
				match(LBRACK);
				match(RBRACK);
				if ( inputState.guessing==0 ) {
					bounds += 1;
				}
			}
			else {
				break _loop115;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			if (bounds > 0) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
				type = new CArrayType(type, bounds);
			}
			self = new FjFormalParameter(sourceRef, desc, type, ident.getText(), isFinal);
			
		}
		return self;
	}
	
	private final CReferenceType[]  jThrowsClause(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType[] self;
		
		
		match(LITERAL_throws);
		self=jNameList();
		return self;
	}
	
	private final JConstructorCall  jExplicitConstructorInvocation(
		
	) throws RecognitionException, TokenStreamException {
		JConstructorCall self = null;
		
		
		boolean		functorIsThis = false;
		JExpression[]		args = null;
		JExpression           expr = null;
		JavaStyleComment[]	comments = getStatementComment();
		TokenReference	sourceRef = buildTokenReference();
		
		
		{
		boolean synPredMatched81 = false;
		if (((LA(1)==LITERAL_this) && (LA(2)==LPAREN))) {
			int _m81 = mark();
			synPredMatched81 = true;
			inputState.guessing++;
			try {
				{
				match(LITERAL_this);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched81 = false;
			}
			rewind(_m81);
			inputState.guessing--;
		}
		if ( synPredMatched81 ) {
			match(LITERAL_this);
			if ( inputState.guessing==0 ) {
				functorIsThis = true;
			}
		}
		else {
			boolean synPredMatched83 = false;
			if (((LA(1)==LITERAL_super) && (LA(2)==LPAREN))) {
				int _m83 = mark();
				synPredMatched83 = true;
				inputState.guessing++;
				try {
					{
					match(LITERAL_super);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched83 = false;
				}
				rewind(_m83);
				inputState.guessing--;
			}
			if ( synPredMatched83 ) {
				match(LITERAL_super);
				if ( inputState.guessing==0 ) {
					functorIsThis = false;
				}
			}
			else if ((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
				{
				switch ( LA(1)) {
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_double:
				case LITERAL_false:
				case LITERAL_float:
				case LITERAL_int:
				case LITERAL_long:
				case LITERAL_new:
				case LITERAL_null:
				case LITERAL_short:
				case LITERAL_super:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_void:
				case LITERAL_wrappee:
				case WDESTRUCTOR:
				case LPAREN:
				case CHARACTER_LITERAL:
				case IDENT:
				case INTEGER_LITERAL:
				case REAL_LITERAL:
				case STRING_LITERAL:
				case ATAT:
				{
					expr=jPrimaryExpression();
					break;
				}
				case DOT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(DOT);
				match(LITERAL_super);
				if ( inputState.guessing==0 ) {
					functorIsThis = false;
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LPAREN);
			args=jArgList();
			match(RPAREN);
			match(SEMI);
			if ( inputState.guessing==0 ) {
				self = new FjConstructorCall(sourceRef, functorIsThis, expr, args);
			}
			return self;
		}
		
	private final JExpression  jPrimaryExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		Token  ident = null;
		
		int			bounds = 0;
		CType			type;
		TokenReference	sourceRef = buildTokenReference();
		
		
		switch ( LA(1)) {
		case IDENT:
		{
			ident = LT(1);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				self = new FjNameExpression(sourceRef, ident.getText());
			}
			break;
		}
		case LITERAL_new:
		{
			self=jUnqualifiedNewExpression();
			break;
		}
		case CHARACTER_LITERAL:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		{
			self=jLiteral();
			break;
		}
		case LITERAL_super:
		{
			match(LITERAL_super);
			if ( inputState.guessing==0 ) {
				self = new FjSuperExpression(sourceRef);
			}
			break;
		}
		case LITERAL_true:
		{
			match(LITERAL_true);
			if ( inputState.guessing==0 ) {
				self = new JBooleanLiteral(sourceRef, true);
			}
			break;
		}
		case LITERAL_false:
		{
			match(LITERAL_false);
			if ( inputState.guessing==0 ) {
				self = new JBooleanLiteral(sourceRef, false);
			}
			break;
		}
		case LITERAL_this:
		{
			match(LITERAL_this);
			if ( inputState.guessing==0 ) {
				self = new FjThisExpression(sourceRef);
			}
			break;
		}
		case LITERAL_wrappee:
		{
			match(LITERAL_wrappee);
			if ( inputState.guessing==0 ) {
				self = new CciWrappeeExpression(sourceRef);
			}
			break;
		}
		case LITERAL_null:
		{
			match(LITERAL_null);
			if ( inputState.guessing==0 ) {
				self = new JNullLiteral(sourceRef);
			}
			break;
		}
		case WDESTRUCTOR:
		{
			self=jWrapperDestructorExpression(null);
			break;
		}
		case ATAT:
		{
			match(ATAT);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_double:
			case LITERAL_false:
			case LITERAL_float:
			case LITERAL_int:
			case LITERAL_long:
			case LITERAL_new:
			case LITERAL_null:
			case LITERAL_short:
			case LITERAL_super:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_void:
			case LITERAL_wrappee:
			case WDESTRUCTOR:
			case BNOT:
			case DEC:
			case INC:
			case LNOT:
			case LPAREN:
			case MINUS:
			case PLUS:
			case CHARACTER_LITERAL:
			case IDENT:
			case INTEGER_LITERAL:
			case REAL_LITERAL:
			case STRING_LITERAL:
			case ATAT:
			{
				self=jExpression();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				
				if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
				if (self == null) {
				self = new KopiReturnValueExpression(sourceRef);
				} else {
				self = new KopiOldValueExpression(sourceRef,  self);
				}
				} else {
					  reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_ATAT, null));
				}
				
			}
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			self=jAssignmentExpression();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				self = new FjParenthesedExpression(sourceRef, self);
			}
			break;
		}
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_short:
		case LITERAL_void:
		{
			type=jBuiltInType();
			{
			_loop227:
			do {
				if ((LA(1)==LBRACK)) {
					match(LBRACK);
					match(RBRACK);
					if ( inputState.guessing==0 ) {
						bounds++;
					}
				}
				else {
					break _loop227;
				}
				
			} while (true);
			}
			match(DOT);
			match(LITERAL_class);
			if ( inputState.guessing==0 ) {
				self = new JClassExpression(buildTokenReference(), type, bounds);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JStatement  jBlockStatement(
		
	) throws RecognitionException, TokenStreamException {
		JStatement self = null;
		
		
		JTypeDeclaration	type;
		int			modifiers;
		
		
		boolean synPredMatched121 = false;
		if (((_tokenSet_12.member(LA(1))) && (_tokenSet_13.member(LA(2))))) {
			int _m121 = mark();
			synPredMatched121 = true;
			inputState.guessing++;
			try {
				{
				jModifiers();
				match(LITERAL_class);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched121 = false;
			}
			rewind(_m121);
			inputState.guessing--;
		}
		if ( synPredMatched121 ) {
			modifiers=jModifiers();
			type=jClassDefinition(modifiers);
			if ( inputState.guessing==0 ) {
				self = new JTypeDeclarationStatement(type.getTokenReference(), type);
			}
		}
		else {
			boolean synPredMatched123 = false;
			if (((_tokenSet_14.member(LA(1))) && (_tokenSet_15.member(LA(2))))) {
				int _m123 = mark();
				synPredMatched123 = true;
				inputState.guessing++;
				try {
					{
					jModifiers();
					jTypeSpec();
					match(IDENT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched123 = false;
				}
				rewind(_m123);
				inputState.guessing--;
			}
			if ( synPredMatched123 ) {
				modifiers=jModifiers();
				self=jLocalVariableDeclaration(modifiers);
				match(SEMI);
			}
			else if ((_tokenSet_16.member(LA(1))) && (_tokenSet_17.member(LA(2)))) {
				self=jStatement();
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			return self;
		}
		
	private final JExpression[]  jArgList(
		
	) throws RecognitionException, TokenStreamException {
		JExpression[] self = JExpression.EMPTY;
		
		
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case DEC:
		case INC:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			self=jExpressionList();
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JVariableDefinition  jVariableDeclarator(
		int modifiers, CType type
	) throws RecognitionException, TokenStreamException {
		JVariableDefinition self = null;
		
		Token  ident = null;
		
		int			bounds = 0;
		JExpression		expr = null;
		TokenReference	sourceRef = buildTokenReference();
		
		
		ident = LT(1);
		match(IDENT);
		{
		_loop99:
		do {
			if ((LA(1)==LBRACK)) {
				match(LBRACK);
				match(RBRACK);
				if ( inputState.guessing==0 ) {
					bounds += 1;
				}
			}
			else {
				break _loop99;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case ASSIGN:
		{
			match(ASSIGN);
			expr=jVariableInitializer();
			break;
		}
		case COMMA:
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
			if (bounds > 0) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.OLD_STYLE_ARRAY_BOUNDS, null));
				type = new CArrayType(type, bounds);
			}
			self = new FjVariableDefinition(sourceRef, modifiers, type, ident.getText(), expr);
			
		}
		return self;
	}
	
	private final JExpression  jVariableInitializer(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case DEC:
		case INC:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			self=jExpression();
			break;
		}
		case LCURLY:
		{
			self=jArrayInitializer();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JExpression  jExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		self=jAssignmentExpression();
		return self;
	}
	
	private final JArrayInitializer  jArrayInitializer(
		
	) throws RecognitionException, TokenStreamException {
		JArrayInitializer self = null;
		
		
		JExpression		expr = null;
		ArrayList		vect = new ArrayList();
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LCURLY);
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case DEC:
		case INC:
		case LCURLY:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			expr=jVariableInitializer();
			if ( inputState.guessing==0 ) {
				vect.add(expr);
			}
			{
			_loop105:
			do {
				if ((LA(1)==COMMA) && (_tokenSet_18.member(LA(2)))) {
					match(COMMA);
					expr=jVariableInitializer();
					if ( inputState.guessing==0 ) {
						vect.add(expr);
					}
				}
				else {
					break _loop105;
				}
				
			} while (true);
			}
			break;
		}
		case COMMA:
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case COMMA:
		{
			match(COMMA);
			if ( inputState.guessing==0 ) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.STRAY_COMMA, null));
			}
			break;
		}
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RCURLY);
		if ( inputState.guessing==0 ) {
			
			self = new JArrayInitializer(sourceRef,
							   (JExpression[]) vect.toArray(new JExpression[vect.size()]));
			
		}
		return self;
	}
	
	private final JStatement  jStatement(
		
	) throws RecognitionException, TokenStreamException {
		JStatement self = null;
		
		
		JExpression		expr;
		JStatement[]		stmts;
		JavaStyleComment[]	comments = getStatementComment();
		TokenReference	sourceRef = buildTokenReference();
		
		
		switch ( LA(1)) {
		case ATASSERT:
		case JAVAASSERT:
		case ATFAIL:
		{
			self=kAssertStatement();
			break;
		}
		case LCURLY:
		{
			stmts=jCompoundStatement();
			if ( inputState.guessing==0 ) {
				self = new JBlock(sourceRef, stmts, comments);
			}
			break;
		}
		case LITERAL_if:
		{
			self=jIfStatement();
			break;
		}
		case LITERAL_for:
		{
			self=jForStatement();
			break;
		}
		case LITERAL_while:
		{
			self=jWhileStatement();
			break;
		}
		case LITERAL_do:
		{
			self=jDoStatement();
			break;
		}
		case LITERAL_break:
		{
			self=jBreakStatement();
			break;
		}
		case LITERAL_continue:
		{
			self=jContinueStatement();
			break;
		}
		case LITERAL_return:
		{
			self=jReturnStatement();
			break;
		}
		case LITERAL_switch:
		{
			self=jSwitchStatement();
			break;
		}
		case LITERAL_try:
		{
			self=jTryBlock();
			break;
		}
		case LITERAL_throw:
		{
			self=jThrowStatement();
			break;
		}
		case LITERAL_synchronized:
		{
			self=jSynchronizedStatement();
			break;
		}
		case LITERAL_deploy:
		{
			self=jDeployStatement();
			break;
		}
		case SEMI:
		{
			match(SEMI);
			if ( inputState.guessing==0 ) {
				self = new JEmptyStatement(sourceRef, comments);
			}
			break;
		}
		default:
			if ((_tokenSet_19.member(LA(1))) && (_tokenSet_20.member(LA(2)))) {
				expr=jExpression();
				match(SEMI);
				if ( inputState.guessing==0 ) {
					self = new JExpressionStatement(sourceRef, expr, comments);
				}
			}
			else if ((LA(1)==IDENT) && (LA(2)==COLON)) {
				self=jLabeledStatement();
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JStatement  kAssertStatement(
		
	) throws RecognitionException, TokenStreamException {
		JStatement self = null;
		
		
		JExpression		cond;
		JExpression		expr = null;
		TokenReference	sourceRef = buildTokenReference();
		JavaStyleComment[]	comments = getStatementComment();
		
		
		{
		switch ( LA(1)) {
		case ATASSERT:
		{
			match(ATASSERT);
			cond=jExpression();
			{
			switch ( LA(1)) {
			case COLON:
			{
				match(COLON);
				expr=jExpression();
				match(SEMI);
				break;
			}
			case SEMI:
			{
				match(SEMI);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
				if (environment.getAssertExtension() == KjcEnvironment.AS_ALL
				|| environment.getAssertExtension() ==  KjcEnvironment.AS_SIMPLE) {
				self = new KopiAssertStatement(sourceRef, cond, expr, false, comments);
				} else {
				reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_ASSERT, null));
				}
				
			}
			break;
		}
		case JAVAASSERT:
		{
			match(JAVAASSERT);
			cond=jExpression();
			{
			switch ( LA(1)) {
			case COLON:
			{
				match(COLON);
				expr=jExpression();
				match(SEMI);
				break;
			}
			case SEMI:
			{
				match(SEMI);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
				self = new KopiAssertStatement(sourceRef, cond, expr, true, comments);
				
			}
			break;
		}
		case ATFAIL:
		{
			match(ATFAIL);
			{
			switch ( LA(1)) {
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_double:
			case LITERAL_false:
			case LITERAL_float:
			case LITERAL_int:
			case LITERAL_long:
			case LITERAL_new:
			case LITERAL_null:
			case LITERAL_short:
			case LITERAL_super:
			case LITERAL_this:
			case LITERAL_true:
			case LITERAL_void:
			case LITERAL_wrappee:
			case WDESTRUCTOR:
			case BNOT:
			case DEC:
			case INC:
			case LNOT:
			case LPAREN:
			case MINUS:
			case PLUS:
			case CHARACTER_LITERAL:
			case IDENT:
			case INTEGER_LITERAL:
			case REAL_LITERAL:
			case STRING_LITERAL:
			case ATAT:
			{
				expr=jExpression();
				match(SEMI);
				break;
			}
			case SEMI:
			{
				match(SEMI);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
				if (environment.getAssertExtension() == KjcEnvironment.AS_ALL
				|| environment.getAssertExtension() ==  KjcEnvironment.AS_SIMPLE) {
				self = new KopiFailStatement(sourceRef, expr, comments);
				} else {
				reportTrouble(new PositionedError(sourceRef, KjcMessages.UNSUPPORTED_FAIL, null));
				}
				
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JLabeledStatement  jLabeledStatement(
		
	) throws RecognitionException, TokenStreamException {
		JLabeledStatement self = null;
		
		Token  label = null;
		
		JStatement		stmt;
		TokenReference	sourceRef = buildTokenReference();
		
		
		label = LT(1);
		match(IDENT);
		match(COLON);
		stmt=jStatement();
		if ( inputState.guessing==0 ) {
			
			if (stmt instanceof JEmptyStatement) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.STRAY_SEMICOLON, null));
			}
			self = new JLabeledStatement(sourceRef, label.getText(), stmt, getStatementComment());
			
		}
		return self;
	}
	
	private final JIfStatement  jIfStatement(
		
	) throws RecognitionException, TokenStreamException {
		JIfStatement self = null;
		
		
		JExpression		cond;
		JStatement		thenClause;
		JStatement		elseClause = null;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_if);
		match(LPAREN);
		cond=jExpression();
		match(RPAREN);
		thenClause=jStatement();
		{
		boolean synPredMatched134 = false;
		if (((LA(1)==LITERAL_else) && (_tokenSet_16.member(LA(2))))) {
			int _m134 = mark();
			synPredMatched134 = true;
			inputState.guessing++;
			try {
				{
				match(LITERAL_else);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched134 = false;
			}
			rewind(_m134);
			inputState.guessing--;
		}
		if ( synPredMatched134 ) {
			match(LITERAL_else);
			elseClause=jStatement();
		}
		else if ((_tokenSet_21.member(LA(1))) && (_tokenSet_22.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			
			if (! (thenClause instanceof JBlock)) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_IF_THEN_IN_BLOCK, null));
			}
			if (elseClause != null && !(elseClause instanceof JBlock || elseClause instanceof JIfStatement)) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_IF_ELSE_IN_BLOCK, null));
			}
			self = new JIfStatement(sourceRef, cond, thenClause, elseClause, getStatementComment());
			
		}
		return self;
	}
	
	private final JForStatement  jForStatement(
		
	) throws RecognitionException, TokenStreamException {
		JForStatement self = null;
		
		
		JStatement		init;
		JExpression		cond;
		JStatement		iter;
		JStatement		body;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_for);
		match(LPAREN);
		init=jForInit();
		match(SEMI);
		cond=jForCond();
		match(SEMI);
		iter=jForIter();
		match(RPAREN);
		body=jStatement();
		if ( inputState.guessing==0 ) {
			
			if (!(body instanceof JBlock || body instanceof JEmptyStatement)) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_LOOP_BODY_IN_BLOCK, null));
			}
			self = new JForStatement(sourceRef, init, cond, iter, body, getStatementComment());
			
		}
		return self;
	}
	
	private final JWhileStatement  jWhileStatement(
		
	) throws RecognitionException, TokenStreamException {
		JWhileStatement self = null;
		
		
		JExpression		cond;
		JStatement		body;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_while);
		match(LPAREN);
		cond=jExpression();
		match(RPAREN);
		body=jStatement();
		if ( inputState.guessing==0 ) {
			
			if (!(body instanceof JBlock || body instanceof JEmptyStatement)) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_LOOP_BODY_IN_BLOCK, null));
			}
			self = new JWhileStatement(sourceRef, cond, body, getStatementComment());
			
		}
		return self;
	}
	
	private final JDoStatement  jDoStatement(
		
	) throws RecognitionException, TokenStreamException {
		JDoStatement self = null;
		
		
		JExpression		cond;
		JStatement		body;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_do);
		body=jStatement();
		match(LITERAL_while);
		match(LPAREN);
		cond=jExpression();
		match(RPAREN);
		match(SEMI);
		if ( inputState.guessing==0 ) {
			
			if (! (body instanceof JBlock)) {
				reportTrouble(new CWarning(sourceRef, KjcMessages.ENCLOSE_LOOP_BODY_IN_BLOCK, null));
			}
			self = new JDoStatement(sourceRef, cond, body, getStatementComment());
			
		}
		return self;
	}
	
	private final JBreakStatement  jBreakStatement(
		
	) throws RecognitionException, TokenStreamException {
		JBreakStatement self = null;
		
		Token  label = null;
		
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_break);
		{
		switch ( LA(1)) {
		case IDENT:
		{
			label = LT(1);
			match(IDENT);
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMI);
		if ( inputState.guessing==0 ) {
			self = new JBreakStatement(sourceRef, label == null ? null : label.getText(), getStatementComment());
		}
		return self;
	}
	
	private final JContinueStatement  jContinueStatement(
		
	) throws RecognitionException, TokenStreamException {
		JContinueStatement self = null;
		
		Token  label = null;
		
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_continue);
		{
		switch ( LA(1)) {
		case IDENT:
		{
			label = LT(1);
			match(IDENT);
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMI);
		if ( inputState.guessing==0 ) {
			self = new JContinueStatement(sourceRef, label == null ? null : label.getText(), getStatementComment());
		}
		return self;
	}
	
	private final JStatement  jReturnStatement(
		
	) throws RecognitionException, TokenStreamException {
		JStatement self = null;
		
		
		JExpression		expr = null;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_return);
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case DEC:
		case INC:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			expr=jExpression();
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMI);
		if ( inputState.guessing==0 ) {
			
			if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
			self = new KopiReturnStatement(sourceRef, expr, getStatementComment());
			} else {
			self = new FjReturnStatement(sourceRef, expr, getStatementComment());
			}
			
		}
		return self;
	}
	
	private final JSwitchStatement  jSwitchStatement(
		
	) throws RecognitionException, TokenStreamException {
		JSwitchStatement self = null;
		
		
		JExpression		expr = null;
		ArrayList		body = null;
		JSwitchGroup		group;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_switch);
		match(LPAREN);
		expr=jExpression();
		match(RPAREN);
		match(LCURLY);
		if ( inputState.guessing==0 ) {
			body = new ArrayList();
		}
		{
		_loop147:
		do {
			if ((LA(1)==LITERAL_case||LA(1)==LITERAL_default)) {
				group=jCasesGroup();
				if ( inputState.guessing==0 ) {
					body.add(group);
				}
			}
			else {
				break _loop147;
			}
			
		} while (true);
		}
		match(RCURLY);
		if ( inputState.guessing==0 ) {
			
			self = new JSwitchStatement(sourceRef,
							  expr,
							  (JSwitchGroup[])body.toArray(new JSwitchGroup[body.size()]),
							  getStatementComment());
			
		}
		return self;
	}
	
	private final JStatement  jTryBlock(
		
	) throws RecognitionException, TokenStreamException {
		JStatement self = null;
		
		
		JBlock		tryClause = null;
		JStatement[]		compound;
		ArrayList		catchClauses = new ArrayList();
		JBlock		finallyClause = null;
		JCatchClause		catcher = null;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_try);
		compound=jCompoundStatement();
		if ( inputState.guessing==0 ) {
			tryClause = new JBlock(sourceRef, compound, null);
		}
		{
		_loop166:
		do {
			if ((LA(1)==LITERAL_catch)) {
				catcher=jHandler();
				if ( inputState.guessing==0 ) {
					catchClauses.add(catcher);
				}
			}
			else {
				break _loop166;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case LITERAL_finally:
		{
			match(LITERAL_finally);
			compound=jCompoundStatement();
			if ( inputState.guessing==0 ) {
				finallyClause = new JBlock(sourceRef, compound, null);
			}
			break;
		}
		case LITERAL_abstract:
		case LITERAL_boolean:
		case LITERAL_break:
		case LITERAL_byte:
		case LITERAL_case:
		case LITERAL_char:
		case LITERAL_class:
		case LITERAL_continue:
		case LITERAL_default:
		case LITERAL_do:
		case LITERAL_double:
		case LITERAL_else:
		case LITERAL_false:
		case LITERAL_final:
		case LITERAL_float:
		case LITERAL_for:
		case LITERAL_if:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_native:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_private:
		case LITERAL_protected:
		case LITERAL_public:
		case LITERAL_return:
		case LITERAL_short:
		case LITERAL_static:
		case LITERAL_strictfp:
		case LITERAL_super:
		case LITERAL_switch:
		case LITERAL_synchronized:
		case LITERAL_this:
		case LITERAL_throw:
		case LITERAL_transient:
		case LITERAL_true:
		case LITERAL_try:
		case LITERAL_void:
		case LITERAL_volatile:
		case LITERAL_while:
		case LITERAL_virtual:
		case LITERAL_override:
		case LITERAL_clean:
		case LITERAL_collaboration:
		case LITERAL_provided:
		case LITERAL_expected:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case LITERAL_crosscutting:
		case LITERAL_deploy:
		case LITERAL_deployed:
		case LITERAL_privileged:
		case BNOT:
		case DEC:
		case INC:
		case LCURLY:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case RCURLY:
		case SEMI:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		case ATASSERT:
		case JAVAASSERT:
		case ATFAIL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
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
		return self;
	}
	
	private final JThrowStatement  jThrowStatement(
		
	) throws RecognitionException, TokenStreamException {
		JThrowStatement self = null;
		
		
		JExpression		expr;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_throw);
		expr=jExpression();
		match(SEMI);
		if ( inputState.guessing==0 ) {
			self = new JThrowStatement(sourceRef, expr, getStatementComment());
		}
		return self;
	}
	
	private final JSynchronizedStatement  jSynchronizedStatement(
		
	) throws RecognitionException, TokenStreamException {
		JSynchronizedStatement self = null;
		
		
		JExpression		expr;
		JStatement[]		body;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_synchronized);
		match(LPAREN);
		expr=jExpression();
		match(RPAREN);
		body=jCompoundStatement();
		if ( inputState.guessing==0 ) {
			
			self = new JSynchronizedStatement(sourceRef,
								expr,
								new JBlock(sourceRef, body, null),
								getStatementComment());
			
		}
		return self;
	}
	
	private final DeployStatement  jDeployStatement(
		
	) throws RecognitionException, TokenStreamException {
		DeployStatement self = null;
		
		
		JExpression	aspectToDeploy;
		JStatement	body;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_deploy);
		match(LPAREN);
		aspectToDeploy=jExpression();
		match(RPAREN);
		body=jStatement();
		if ( inputState.guessing==0 ) {
			
			self = new DeployStatement(sourceRef, aspectToDeploy, body, getStatementComment());
			
		}
		return self;
	}
	
	private final JSwitchGroup  jCasesGroup(
		
	) throws RecognitionException, TokenStreamException {
		JSwitchGroup self = null;
		
		
		ArrayList		labels = new ArrayList();
		ArrayList		stmts = new ArrayList();
		
		JSwitchLabel		label;
		JStatement		stmt;
		TokenReference	sourceRef = buildTokenReference();
		
		
		{
		int _cnt150=0;
		_loop150:
		do {
			if ((LA(1)==LITERAL_case||LA(1)==LITERAL_default) && (_tokenSet_23.member(LA(2)))) {
				label=jACase();
				if ( inputState.guessing==0 ) {
					labels.add(label);
				}
			}
			else {
				if ( _cnt150>=1 ) { break _loop150; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt150++;
		} while (true);
		}
		{
		_loop152:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				stmt=jBlockStatement();
				if ( inputState.guessing==0 ) {
					
					if (stmt instanceof JEmptyStatement) {
						reportTrouble(new CWarning(stmt.getTokenReference(), KjcMessages.STRAY_SEMICOLON, null));
					}
					stmts.add(stmt);
					
				}
			}
			else {
				break _loop152;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			self = new JSwitchGroup(sourceRef,
							  (JSwitchLabel[])labels.toArray(new JSwitchLabel[labels.size()]),
							  (JStatement[])stmts.toArray(new JStatement[stmts.size()]));
			
		}
		return self;
	}
	
	private final JSwitchLabel  jACase(
		
	) throws RecognitionException, TokenStreamException {
		JSwitchLabel self = null;
		
		
		JExpression		expr = null;
		TokenReference	sourceRef = buildTokenReference();
		
		
		{
		switch ( LA(1)) {
		case LITERAL_case:
		{
			match(LITERAL_case);
			expr=jExpression();
			if ( inputState.guessing==0 ) {
				self = new JSwitchLabel(sourceRef, expr);
			}
			break;
		}
		case LITERAL_default:
		{
			match(LITERAL_default);
			if ( inputState.guessing==0 ) {
				self = new JSwitchLabel(sourceRef, null);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(COLON);
		return self;
	}
	
	private final JStatement  jForInit(
		
	) throws RecognitionException, TokenStreamException {
		JStatement self = null;
		
		
		int			modifiers;
		JExpression[]		list;
		
		
		{
		boolean synPredMatched159 = false;
		if (((_tokenSet_14.member(LA(1))) && (_tokenSet_15.member(LA(2))))) {
			int _m159 = mark();
			synPredMatched159 = true;
			inputState.guessing++;
			try {
				{
				jModifiers();
				jTypeSpec();
				match(IDENT);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched159 = false;
			}
			rewind(_m159);
			inputState.guessing--;
		}
		if ( synPredMatched159 ) {
			modifiers=jModifiers();
			self=jLocalVariableDeclaration(modifiers);
		}
		else if ((_tokenSet_19.member(LA(1))) && (_tokenSet_24.member(LA(2)))) {
			list=jExpressionList();
			if ( inputState.guessing==0 ) {
				self = new JExpressionListStatement(buildTokenReference(), list, getStatementComment());
			}
		}
		else if ((LA(1)==SEMI)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		return self;
	}
	
	private final JExpression  jForCond(
		
	) throws RecognitionException, TokenStreamException {
		JExpression expr = null;
		
		
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case DEC:
		case INC:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			expr=jExpression();
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return expr;
	}
	
	private final JExpressionListStatement  jForIter(
		
	) throws RecognitionException, TokenStreamException {
		JExpressionListStatement self = null;
		
		
		JExpression[] list;
		
		
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case DEC:
		case INC:
		case LNOT:
		case LPAREN:
		case MINUS:
		case PLUS:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			list=jExpressionList();
			if ( inputState.guessing==0 ) {
				self = new JExpressionListStatement(buildTokenReference(), list, null);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JExpression[]  jExpressionList(
		
	) throws RecognitionException, TokenStreamException {
		JExpression[] self = null;
		
		
		JExpression		expr;
		ArrayList		vect = new ArrayList();
		
		
		expr=jExpression();
		if ( inputState.guessing==0 ) {
			vect.add(expr);
		}
		{
		_loop172:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				expr=jExpression();
				if ( inputState.guessing==0 ) {
					vect.add(expr);
				}
			}
			else {
				break _loop172;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			self = (JExpression[])vect.toArray(new JExpression[vect.size()]);
		}
		return self;
	}
	
	private final JCatchClause  jHandler(
		
	) throws RecognitionException, TokenStreamException {
		JCatchClause self = null;
		
		
		JFormalParameter	param;
		JStatement[]		body;
		TokenReference	sourceRef = buildTokenReference();
		
		
		match(LITERAL_catch);
		match(LPAREN);
		param=jParameterDeclaration(JLocalVariable.DES_CATCH_PARAMETER);
		match(RPAREN);
		body=jCompoundStatement();
		if ( inputState.guessing==0 ) {
			
			self = new JCatchClause(sourceRef,
							  param,
							  new JBlock(sourceRef, body, null));
			
		}
		return self;
	}
	
	private final JExpression  jAssignmentExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		int			oper = -1;
		JExpression		right;
		
		
		self=jConditionalExpression();
		{
		switch ( LA(1)) {
		case ASSIGN:
		{
			match(ASSIGN);
			right=jAssignmentExpression();
			if ( inputState.guessing==0 ) {
				self = new FjAssignmentExpression(self.getTokenReference(), self, right);
			}
			break;
		}
		case BAND_ASSIGN:
		case BOR_ASSIGN:
		case BSR_ASSIGN:
		case BXOR_ASSIGN:
		case MINUS_ASSIGN:
		case PERCENT_ASSIGN:
		case PLUS_ASSIGN:
		case SLASH_ASSIGN:
		case SL_ASSIGN:
		case SR_ASSIGN:
		case STAR_ASSIGN:
		{
			oper=jCompoundAssignmentOperator();
			right=jAssignmentExpression();
			if ( inputState.guessing==0 ) {
				self = new JCompoundAssignmentExpression(self.getTokenReference(), oper, self, right);
			}
			break;
		}
		case COLON:
		case COMMA:
		case RBRACK:
		case RCURLY:
		case RPAREN:
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JExpression  jConditionalExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		middle;
		JExpression		right;
		
		
		self=jLogicalOrExpression();
		{
		switch ( LA(1)) {
		case QUESTION:
		{
			match(QUESTION);
			middle=jAssignmentExpression();
			match(COLON);
			right=jConditionalExpression();
			if ( inputState.guessing==0 ) {
				self = new JConditionalExpression(self.getTokenReference(), self, middle, right);
			}
			break;
		}
		case ASSIGN:
		case BAND_ASSIGN:
		case BOR_ASSIGN:
		case BSR_ASSIGN:
		case BXOR_ASSIGN:
		case COLON:
		case COMMA:
		case MINUS_ASSIGN:
		case PERCENT_ASSIGN:
		case PLUS_ASSIGN:
		case RBRACK:
		case RCURLY:
		case RPAREN:
		case SEMI:
		case SLASH_ASSIGN:
		case SL_ASSIGN:
		case SR_ASSIGN:
		case STAR_ASSIGN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final int  jCompoundAssignmentOperator(
		
	) throws RecognitionException, TokenStreamException {
		int self = -1;
		
		
		switch ( LA(1)) {
		case PLUS_ASSIGN:
		{
			match(PLUS_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_PLUS;
			}
			break;
		}
		case MINUS_ASSIGN:
		{
			match(MINUS_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_MINUS;
			}
			break;
		}
		case STAR_ASSIGN:
		{
			match(STAR_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_STAR;
			}
			break;
		}
		case SLASH_ASSIGN:
		{
			match(SLASH_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_SLASH;
			}
			break;
		}
		case PERCENT_ASSIGN:
		{
			match(PERCENT_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_PERCENT;
			}
			break;
		}
		case SR_ASSIGN:
		{
			match(SR_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_SR;
			}
			break;
		}
		case BSR_ASSIGN:
		{
			match(BSR_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_BSR;
			}
			break;
		}
		case SL_ASSIGN:
		{
			match(SL_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_SL;
			}
			break;
		}
		case BAND_ASSIGN:
		{
			match(BAND_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_BAND;
			}
			break;
		}
		case BXOR_ASSIGN:
		{
			match(BXOR_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_BXOR;
			}
			break;
		}
		case BOR_ASSIGN:
		{
			match(BOR_ASSIGN);
			if ( inputState.guessing==0 ) {
				self = Constants.OPE_BOR;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JExpression  jLogicalOrExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jLogicalAndExpression();
		{
		_loop180:
		do {
			if ((LA(1)==LOR)) {
				match(LOR);
				right=jLogicalAndExpression();
				if ( inputState.guessing==0 ) {
					self = new JConditionalOrExpression(self.getTokenReference(), self, right);
				}
			}
			else {
				break _loop180;
			}
			
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jLogicalAndExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jInclusiveOrExpression();
		{
		_loop183:
		do {
			if ((LA(1)==LAND)) {
				match(LAND);
				right=jInclusiveOrExpression();
				if ( inputState.guessing==0 ) {
					self = new JConditionalAndExpression(self.getTokenReference(), self, right);
				}
			}
			else {
				break _loop183;
			}
			
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jInclusiveOrExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jExclusiveOrExpression();
		{
		_loop186:
		do {
			if ((LA(1)==BOR)) {
				match(BOR);
				right=jExclusiveOrExpression();
				if ( inputState.guessing==0 ) {
					self = new JBitwiseExpression(self.getTokenReference(), Constants.OPE_BOR, self, right);
				}
			}
			else {
				break _loop186;
			}
			
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jExclusiveOrExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jAndExpression();
		{
		_loop189:
		do {
			if ((LA(1)==BXOR)) {
				match(BXOR);
				right=jAndExpression();
				if ( inputState.guessing==0 ) {
					self = new JBitwiseExpression(self.getTokenReference(), Constants.OPE_BXOR, self, right);
				}
			}
			else {
				break _loop189;
			}
			
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jAndExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jEqualityExpression();
		{
		_loop192:
		do {
			if ((LA(1)==BAND)) {
				match(BAND);
				right=jEqualityExpression();
				if ( inputState.guessing==0 ) {
					self = new JBitwiseExpression(self.getTokenReference(), Constants.OPE_BAND, self, right);
				}
			}
			else {
				break _loop192;
			}
			
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jEqualityExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jRelationalExpression();
		{
		_loop195:
		do {
			switch ( LA(1)) {
			case NOT_EQUAL:
			{
				match(NOT_EQUAL);
				right=jRelationalExpression();
				if ( inputState.guessing==0 ) {
					self = new JEqualityExpression(self.getTokenReference(), false, self, right);
				}
				break;
			}
			case FJEQUAL:
			{
				match(FJEQUAL);
				right=jRelationalExpression();
				if ( inputState.guessing==0 ) {
					self = new FjEqualityExpression(self.getTokenReference(), true, self, right);
				}
				break;
			}
			case EQUAL:
			{
				match(EQUAL);
				right=jRelationalExpression();
				if ( inputState.guessing==0 ) {
					self = new JEqualityExpression(self.getTokenReference(), true, self, right);
				}
				break;
			}
			default:
			{
				break _loop195;
			}
			}
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jRelationalExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		int			operator = -1;
		JExpression		right;
		CType			type;
		
		
		self=jShiftExpression();
		{
		switch ( LA(1)) {
		case FJEQUAL:
		case ASSIGN:
		case BAND:
		case BAND_ASSIGN:
		case BOR:
		case BOR_ASSIGN:
		case BSR_ASSIGN:
		case BXOR:
		case BXOR_ASSIGN:
		case COLON:
		case COMMA:
		case EQUAL:
		case GE:
		case GT:
		case LAND:
		case LE:
		case LOR:
		case LT:
		case MINUS_ASSIGN:
		case NOT_EQUAL:
		case PERCENT_ASSIGN:
		case PLUS_ASSIGN:
		case QUESTION:
		case RBRACK:
		case RCURLY:
		case RPAREN:
		case SEMI:
		case SLASH_ASSIGN:
		case SL_ASSIGN:
		case SR_ASSIGN:
		case STAR_ASSIGN:
		{
			{
			_loop200:
			do {
				if ((_tokenSet_25.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case LT:
					{
						match(LT);
						if ( inputState.guessing==0 ) {
							operator = Constants.OPE_LT;
						}
						break;
					}
					case GT:
					{
						match(GT);
						if ( inputState.guessing==0 ) {
							operator = Constants.OPE_GT;
						}
						break;
					}
					case LE:
					{
						match(LE);
						if ( inputState.guessing==0 ) {
							operator = Constants.OPE_LE;
						}
						break;
					}
					case GE:
					{
						match(GE);
						if ( inputState.guessing==0 ) {
							operator = Constants.OPE_GE;
						}
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					right=jShiftExpression();
					if ( inputState.guessing==0 ) {
						self = new JRelationalExpression(self.getTokenReference(), operator, self, right);
					}
				}
				else {
					break _loop200;
				}
				
			} while (true);
			}
			break;
		}
		case LITERAL_instanceof:
		{
			match(LITERAL_instanceof);
			type=jTypeSpec();
			if ( inputState.guessing==0 ) {
				self = new JInstanceofExpression(self.getTokenReference(), self, type);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JExpression  jShiftExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		int			operator = -1;
		JExpression		right;
		
		
		self=jAdditiveExpression();
		{
		_loop204:
		do {
			if ((_tokenSet_26.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case SL:
				{
					match(SL);
					if ( inputState.guessing==0 ) {
						operator = Constants.OPE_SL;
					}
					break;
				}
				case SR:
				{
					match(SR);
					if ( inputState.guessing==0 ) {
						operator = Constants.OPE_SR;
					}
					break;
				}
				case BSR:
				{
					match(BSR);
					if ( inputState.guessing==0 ) {
						operator = Constants.OPE_BSR;
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				right=jAdditiveExpression();
				if ( inputState.guessing==0 ) {
					self = new JShiftExpression(self.getTokenReference(), operator, self, right);
				}
			}
			else {
				break _loop204;
			}
			
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jAdditiveExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jMultiplicativeExpression();
		{
		_loop207:
		do {
			switch ( LA(1)) {
			case PLUS:
			{
				match(PLUS);
				right=jMultiplicativeExpression();
				if ( inputState.guessing==0 ) {
					self = new JAddExpression(self.getTokenReference(), self, right);
				}
				break;
			}
			case MINUS:
			{
				match(MINUS);
				right=jMultiplicativeExpression();
				if ( inputState.guessing==0 ) {
					self = new JMinusExpression(self.getTokenReference(), self, right);
				}
				break;
			}
			default:
			{
				break _loop207;
			}
			}
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jMultiplicativeExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		right;
		
		
		self=jUnaryExpression();
		{
		_loop210:
		do {
			switch ( LA(1)) {
			case STAR:
			{
				match(STAR);
				right=jUnaryExpression();
				if ( inputState.guessing==0 ) {
					self = new JMultExpression(self.getTokenReference(), self, right);
				}
				break;
			}
			case SLASH:
			{
				match(SLASH);
				right=jUnaryExpression();
				if ( inputState.guessing==0 ) {
					self = new JDivideExpression(self.getTokenReference(), self, right);
				}
				break;
			}
			case PERCENT:
			{
				match(PERCENT);
				right=jUnaryExpression();
				if ( inputState.guessing==0 ) {
					self = new JModuloExpression(self.getTokenReference(), self, right);
				}
				break;
			}
			default:
			{
				break _loop210;
			}
			}
		} while (true);
		}
		return self;
	}
	
	private final JExpression  jUnaryExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		TokenReference	sourceRef = buildTokenReference();
		
		
		switch ( LA(1)) {
		case INC:
		{
			match(INC);
			self=jUnaryExpression();
			if ( inputState.guessing==0 ) {
				self = new JPrefixExpression(sourceRef, Constants.OPE_PREINC, self);
			}
			break;
		}
		case DEC:
		{
			match(DEC);
			self=jUnaryExpression();
			if ( inputState.guessing==0 ) {
				self = new JPrefixExpression(sourceRef, Constants.OPE_PREDEC, self);
			}
			break;
		}
		case MINUS:
		{
			match(MINUS);
			self=jUnaryExpression();
			if ( inputState.guessing==0 ) {
				self = new JUnaryMinusExpression(sourceRef, self);
			}
			break;
		}
		case PLUS:
		{
			match(PLUS);
			self=jUnaryExpression();
			if ( inputState.guessing==0 ) {
				self = new JUnaryPlusExpression(sourceRef, self);
			}
			break;
		}
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case BNOT:
		case LNOT:
		case LPAREN:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			self=jUnaryExpressionNotPlusMinus();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JExpression  jUnaryExpressionNotPlusMinus(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		JExpression		expr;
		CType			dest;
		TokenReference	sourceRef = buildTokenReference();
		
		
		switch ( LA(1)) {
		case BNOT:
		{
			match(BNOT);
			self=jUnaryExpression();
			if ( inputState.guessing==0 ) {
				self = new JBitwiseComplementExpression(sourceRef, self);
			}
			break;
		}
		case LNOT:
		{
			match(LNOT);
			self=jUnaryExpression();
			if ( inputState.guessing==0 ) {
				self = new JLogicalComplementExpression(sourceRef, self);
			}
			break;
		}
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_false:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_new:
		case LITERAL_null:
		case LITERAL_short:
		case LITERAL_super:
		case LITERAL_this:
		case LITERAL_true:
		case LITERAL_void:
		case LITERAL_wrappee:
		case WDESTRUCTOR:
		case LPAREN:
		case CHARACTER_LITERAL:
		case IDENT:
		case INTEGER_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
		case ATAT:
		{
			{
			if ((LA(1)==LPAREN) && (_tokenSet_27.member(LA(2)))) {
				match(LPAREN);
				dest=jBuiltInTypeSpec();
				{
				switch ( LA(1)) {
				case RPAREN:
				{
					match(RPAREN);
					expr=jUnaryExpression();
					if ( inputState.guessing==0 ) {
						self = new FjCastExpression(sourceRef, expr, dest, true, true);
					}
					break;
				}
				case DOT:
				{
					match(DOT);
					match(LITERAL_class);
					match(RPAREN);
					if ( inputState.guessing==0 ) {
						
						if (dest instanceof CArrayType) {
						self = new JClassExpression(buildTokenReference(), ((CArrayType) dest).getBaseType(),((CArrayType) dest).getArrayBound()); 
						} else {
						self = new JClassExpression(buildTokenReference(), dest, 0); 
						}
						
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				boolean synPredMatched216 = false;
				if (((LA(1)==LPAREN) && (LA(2)==IDENT))) {
					int _m216 = mark();
					synPredMatched216 = true;
					inputState.guessing++;
					try {
						{
						match(LPAREN);
						jClassTypeSpec();
						match(RPAREN);
						jUnaryExpressionNotPlusMinus();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched216 = false;
					}
					rewind(_m216);
					inputState.guessing--;
				}
				if ( synPredMatched216 ) {
					match(LPAREN);
					dest=jClassTypeSpec();
					match(RPAREN);
					expr=jUnaryExpressionNotPlusMinus();
					if ( inputState.guessing==0 ) {
						self = new FjCastExpression(sourceRef, expr, dest, true, true);
					}
				}
				else if ((_tokenSet_28.member(LA(1))) && (_tokenSet_29.member(LA(2)))) {
					self=jPostfixExpression();
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			return self;
		}
		
	private final JExpression  jPostfixExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		Token  ident = null;
		
		int			bounds = 0;
		JExpression		expr;
		JExpression[]		args = null;
		CType			type;
		TokenReference	sourceRef = buildTokenReference();
		
		
		self=jPrimaryExpression();
		{
		_loop222:
		do {
			switch ( LA(1)) {
			case DOT:
			{
				match(DOT);
				{
				switch ( LA(1)) {
				case IDENT:
				{
					ident = LT(1);
					match(IDENT);
					if ( inputState.guessing==0 ) {
						self = new FjNameExpression(sourceRef, self, ident.getText());
					}
					break;
				}
				case LITERAL_this:
				{
					match(LITERAL_this);
					if ( inputState.guessing==0 ) {
						self = new FjThisExpression(sourceRef, self);
					}
					break;
				}
				case LITERAL_super:
				{
					match(LITERAL_super);
					if ( inputState.guessing==0 ) {
						self = new FjSuperExpression(sourceRef, self);
					}
					break;
				}
				case LITERAL_class:
				{
					match(LITERAL_class);
					if ( inputState.guessing==0 ) {
						self = new JClassExpression(sourceRef, self, 0);
					}
					break;
				}
				case WDESTRUCTOR:
				{
					self=jWrapperDestructorExpression(self);
					break;
				}
				case LITERAL_new:
				{
					self=jQualifiedNewExpression(self);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case LPAREN:
			{
				match(LPAREN);
				args=jArgList();
				match(RPAREN);
				if ( inputState.guessing==0 ) {
					
						if (! (self instanceof JNameExpression)) {
						  reportTrouble(new PositionedError(sourceRef, KjcMessages.INVALID_METHOD_NAME, null));
						} else if(((JNameExpression)self).getName().equals("proceed")) {
						  self = new ProceedExpression(sourceRef, args);	
						} else {
						  self = new FjMethodCallExpression(sourceRef,
										   ((JNameExpression)self).getPrefix(),
										   ((JNameExpression)self).getName(),
										   args);
						}
					
				}
				break;
			}
			default:
				if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
					{
					int _cnt221=0;
					_loop221:
					do {
						if ((LA(1)==LBRACK)) {
							match(LBRACK);
							match(RBRACK);
							if ( inputState.guessing==0 ) {
								bounds++;
							}
						}
						else {
							if ( _cnt221>=1 ) { break _loop221; } else {throw new NoViableAltException(LT(1), getFilename());}
						}
						
						_cnt221++;
					} while (true);
					}
					match(DOT);
					match(LITERAL_class);
					if ( inputState.guessing==0 ) {
						self = new JClassExpression(sourceRef, self, bounds);
					}
				}
				else if ((LA(1)==LBRACK) && (_tokenSet_19.member(LA(2)))) {
					match(LBRACK);
					expr=jExpression();
					match(RBRACK);
					if ( inputState.guessing==0 ) {
						self = new JArrayAccessExpression(sourceRef, self, expr);
					}
				}
			else {
				break _loop222;
			}
			}
		} while (true);
		}
		{
		switch ( LA(1)) {
		case INC:
		{
			match(INC);
			if ( inputState.guessing==0 ) {
				self = new JPostfixExpression(sourceRef, Constants.OPE_POSTINC, self);
			}
			break;
		}
		case DEC:
		{
			match(DEC);
			if ( inputState.guessing==0 ) {
				self = new JPostfixExpression(sourceRef, Constants.OPE_POSTDEC, self);
			}
			break;
		}
		case LITERAL_instanceof:
		case FJEQUAL:
		case ASSIGN:
		case BAND:
		case BAND_ASSIGN:
		case BOR:
		case BOR_ASSIGN:
		case BSR:
		case BSR_ASSIGN:
		case BXOR:
		case BXOR_ASSIGN:
		case COLON:
		case COMMA:
		case EQUAL:
		case GE:
		case GT:
		case LAND:
		case LE:
		case LOR:
		case LT:
		case MINUS:
		case MINUS_ASSIGN:
		case NOT_EQUAL:
		case PERCENT:
		case PERCENT_ASSIGN:
		case PLUS:
		case PLUS_ASSIGN:
		case QUESTION:
		case RBRACK:
		case RCURLY:
		case RPAREN:
		case SEMI:
		case SL:
		case SLASH:
		case SLASH_ASSIGN:
		case SL_ASSIGN:
		case SR:
		case SR_ASSIGN:
		case STAR:
		case STAR_ASSIGN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JExpression  jWrapperDestructorExpression(
		JExpression prefix
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		CType				type;
		JExpression[]			args;
		TokenReference		sourceRef = buildTokenReference();
		
		
		match(WDESTRUCTOR);
		type=jTypeName();
		match(LPAREN);
		args=jArgList();
		match(RPAREN);
		if ( inputState.guessing==0 ) {
			self = new CciWrapperDestructorExpression(sourceRef, prefix, (CReferenceType) type, args);
		}
		return self;
	}
	
	private final JExpression  jQualifiedNewExpression(
		JExpression prefix
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		Token  ident = null;
		
		CType				type;
		JExpression[]			args;
		JArrayInitializer		init = null;
		FjClassDeclaration		decl = null;
		ParseClassContext		context = null;
		TokenReference		sourceRef = buildTokenReference();
		
		
		match(LITERAL_new);
		ident = LT(1);
		match(IDENT);
		match(LPAREN);
		args=jArgList();
		match(RPAREN);
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			if ( inputState.guessing==0 ) {
				context = ParseClassContext.getInstance();
			}
			jClassBlock(context);
			if ( inputState.guessing==0 ) {
				
				JMethodDeclaration[]      methods;
				
				if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
				JMethodDeclaration[]    assertions = context.getAssertions();
				JMethodDeclaration[]    decMethods = context.getMethods();
				
				methods = new JMethodDeclaration[assertions.length+decMethods.length];
				// assertions first!
				System.arraycopy(assertions, 0, methods, 0, assertions.length);
				System.arraycopy(decMethods, 0, methods, assertions.length, decMethods.length);
				} else {
				methods = context.getMethods();
				}
				
					decl = new FjClassDeclaration(sourceRef,
									 org.caesarj.kjc.Constants.ACC_FINAL, // JLS 15.9.5
									 ident.getText(),
				CTypeVariable.EMPTY,
									 null,
									 null,
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
			if ( inputState.guessing==0 ) {
				self = new JQualifiedAnonymousCreation(sourceRef, prefix, ident.getText(), args, decl);
			}
			break;
		}
		case LITERAL_instanceof:
		case FJEQUAL:
		case ASSIGN:
		case BAND:
		case BAND_ASSIGN:
		case BOR:
		case BOR_ASSIGN:
		case BSR:
		case BSR_ASSIGN:
		case BXOR:
		case BXOR_ASSIGN:
		case COLON:
		case COMMA:
		case DEC:
		case DOT:
		case EQUAL:
		case GE:
		case GT:
		case INC:
		case LAND:
		case LBRACK:
		case LE:
		case LOR:
		case LPAREN:
		case LT:
		case MINUS:
		case MINUS_ASSIGN:
		case NOT_EQUAL:
		case PERCENT:
		case PERCENT_ASSIGN:
		case PLUS:
		case PLUS_ASSIGN:
		case QUESTION:
		case RBRACK:
		case RCURLY:
		case RPAREN:
		case SEMI:
		case SL:
		case SLASH:
		case SLASH_ASSIGN:
		case SL_ASSIGN:
		case SR:
		case SR_ASSIGN:
		case STAR:
		case STAR_ASSIGN:
		{
			if ( inputState.guessing==0 ) {
				self = new FjQualifiedInstanceCreation(sourceRef, prefix, ident.getText(), args);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JExpression  jUnqualifiedNewExpression(
		
	) throws RecognitionException, TokenStreamException {
		JExpression self = null;
		
		
		CType				type;
		JExpression[]			args;
		JArrayInitializer		init = null;
		FjClassDeclaration		decl = null;
		ParseClassContext		context = null;
		TokenReference		sourceRef = buildTokenReference();
		
		
		match(LITERAL_new);
		{
		switch ( LA(1)) {
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_double:
		case LITERAL_float:
		case LITERAL_int:
		case LITERAL_long:
		case LITERAL_short:
		case LITERAL_void:
		{
			type=jBuiltInType();
			args=jNewArrayDeclarator();
			{
			switch ( LA(1)) {
			case LCURLY:
			{
				init=jArrayInitializer();
				break;
			}
			case LITERAL_instanceof:
			case FJEQUAL:
			case ASSIGN:
			case BAND:
			case BAND_ASSIGN:
			case BOR:
			case BOR_ASSIGN:
			case BSR:
			case BSR_ASSIGN:
			case BXOR:
			case BXOR_ASSIGN:
			case COLON:
			case COMMA:
			case DEC:
			case DOT:
			case EQUAL:
			case GE:
			case GT:
			case INC:
			case LAND:
			case LBRACK:
			case LE:
			case LOR:
			case LPAREN:
			case LT:
			case MINUS:
			case MINUS_ASSIGN:
			case NOT_EQUAL:
			case PERCENT:
			case PERCENT_ASSIGN:
			case PLUS:
			case PLUS_ASSIGN:
			case QUESTION:
			case RBRACK:
			case RCURLY:
			case RPAREN:
			case SEMI:
			case SL:
			case SLASH:
			case SLASH_ASSIGN:
			case SL_ASSIGN:
			case SR:
			case SR_ASSIGN:
			case STAR:
			case STAR_ASSIGN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				self = new JNewArrayExpression(sourceRef, type, args, init);
			}
			break;
		}
		case IDENT:
		{
			type=jTypeName();
			{
			switch ( LA(1)) {
			case LBRACK:
			{
				args=jNewArrayDeclarator();
				{
				switch ( LA(1)) {
				case LCURLY:
				{
					init=jArrayInitializer();
					break;
				}
				case LITERAL_instanceof:
				case FJEQUAL:
				case ASSIGN:
				case BAND:
				case BAND_ASSIGN:
				case BOR:
				case BOR_ASSIGN:
				case BSR:
				case BSR_ASSIGN:
				case BXOR:
				case BXOR_ASSIGN:
				case COLON:
				case COMMA:
				case DEC:
				case DOT:
				case EQUAL:
				case GE:
				case GT:
				case INC:
				case LAND:
				case LBRACK:
				case LE:
				case LOR:
				case LPAREN:
				case LT:
				case MINUS:
				case MINUS_ASSIGN:
				case NOT_EQUAL:
				case PERCENT:
				case PERCENT_ASSIGN:
				case PLUS:
				case PLUS_ASSIGN:
				case QUESTION:
				case RBRACK:
				case RCURLY:
				case RPAREN:
				case SEMI:
				case SL:
				case SLASH:
				case SLASH_ASSIGN:
				case SL_ASSIGN:
				case SR:
				case SR_ASSIGN:
				case STAR:
				case STAR_ASSIGN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					self = new JNewArrayExpression(sourceRef, type, args, init);
				}
				break;
			}
			case LPAREN:
			{
				match(LPAREN);
				args=jArgList();
				match(RPAREN);
				{
				switch ( LA(1)) {
				case LCURLY:
				{
					if ( inputState.guessing==0 ) {
						context = ParseClassContext.getInstance();
					}
					jClassBlock(context);
					if ( inputState.guessing==0 ) {
						
						JMethodDeclaration[]      methods;
						
						if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
						JMethodDeclaration[]    assertions = context.getAssertions();
						JMethodDeclaration[]    decMethods = context.getMethods();
						
						methods = new JMethodDeclaration[assertions.length+decMethods.length];
						// assertions first!
						System.arraycopy(assertions, 0, methods, 0, assertions.length);
						System.arraycopy(decMethods, 0, methods, assertions.length, decMethods.length);
						} else {
						methods = context.getMethods();
						}
						
								decl = new FjClassDeclaration(sourceRef,
											 org.caesarj.kjc.Constants.ACC_FINAL, // JLS 15.9.5
											 "", //((CReferenceType)type).getQualifiedName(),
						CTypeVariable.EMPTY,
											 null,
											 null,
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
					if ( inputState.guessing==0 ) {
						self = new JUnqualifiedAnonymousCreation(sourceRef, (CReferenceType)type, args, decl);
					}
					break;
				}
				case LITERAL_instanceof:
				case FJEQUAL:
				case ASSIGN:
				case BAND:
				case BAND_ASSIGN:
				case BOR:
				case BOR_ASSIGN:
				case BSR:
				case BSR_ASSIGN:
				case BXOR:
				case BXOR_ASSIGN:
				case COLON:
				case COMMA:
				case DEC:
				case DOT:
				case EQUAL:
				case GE:
				case GT:
				case INC:
				case LAND:
				case LBRACK:
				case LE:
				case LOR:
				case LPAREN:
				case LT:
				case MINUS:
				case MINUS_ASSIGN:
				case NOT_EQUAL:
				case PERCENT:
				case PERCENT_ASSIGN:
				case PLUS:
				case PLUS_ASSIGN:
				case QUESTION:
				case RBRACK:
				case RCURLY:
				case RPAREN:
				case SEMI:
				case SL:
				case SLASH:
				case SLASH_ASSIGN:
				case SL_ASSIGN:
				case SR:
				case SR_ASSIGN:
				case STAR:
				case STAR_ASSIGN:
				{
					if ( inputState.guessing==0 ) {
						self = new FjUnqualifiedInstanceCreation(sourceRef, (CReferenceType)type, args);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return self;
	}
	
	private final JLiteral  jLiteral(
		
	) throws RecognitionException, TokenStreamException {
		JLiteral self = null;
		
		
		switch ( LA(1)) {
		case INTEGER_LITERAL:
		{
			self=jIntegerLiteral();
			break;
		}
		case CHARACTER_LITERAL:
		{
			self=jCharLiteral();
			break;
		}
		case STRING_LITERAL:
		{
			self=jStringLiteral();
			break;
		}
		case REAL_LITERAL:
		{
			self=jRealLiteral();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return self;
	}
	
	private final JExpression[]  jNewArrayDeclarator(
		
	) throws RecognitionException, TokenStreamException {
		JExpression[] self = null;
		
		
		ArrayList		container = new ArrayList();
		JExpression		exp = null;
		
		
		{
		int _cnt242=0;
		_loop242:
		do {
			if ((LA(1)==LBRACK) && (_tokenSet_30.member(LA(2)))) {
				match(LBRACK);
				{
				switch ( LA(1)) {
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_double:
				case LITERAL_false:
				case LITERAL_float:
				case LITERAL_int:
				case LITERAL_long:
				case LITERAL_new:
				case LITERAL_null:
				case LITERAL_short:
				case LITERAL_super:
				case LITERAL_this:
				case LITERAL_true:
				case LITERAL_void:
				case LITERAL_wrappee:
				case WDESTRUCTOR:
				case BNOT:
				case DEC:
				case INC:
				case LNOT:
				case LPAREN:
				case MINUS:
				case PLUS:
				case CHARACTER_LITERAL:
				case IDENT:
				case INTEGER_LITERAL:
				case REAL_LITERAL:
				case STRING_LITERAL:
				case ATAT:
				{
					exp=jExpression();
					break;
				}
				case RBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RBRACK);
				if ( inputState.guessing==0 ) {
					container.add(exp); exp = null;
				}
			}
			else {
				if ( _cnt242>=1 ) { break _loop242; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt242++;
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			self = (JExpression[]) container.toArray(new JExpression[container.size()]);
		}
		return self;
	}
	
	private final JLiteral  jIntegerLiteral(
		
	) throws RecognitionException, TokenStreamException {
		JLiteral self = null;
		
		Token  i = null;
		
		i = LT(1);
		match(INTEGER_LITERAL);
		if ( inputState.guessing==0 ) {
			
			try {
				self = JLiteral.parseInteger(buildTokenReference(), i.getText());
			} catch (PositionedError e) {
				reportTrouble(e);
				// allow parsing to continue
				self = new JIntLiteral(TokenReference.NO_REF, 0);
			}
			
		}
		return self;
	}
	
	private final JLiteral  jCharLiteral(
		
	) throws RecognitionException, TokenStreamException {
		JLiteral self = null;
		
		Token  c = null;
		
		c = LT(1);
		match(CHARACTER_LITERAL);
		if ( inputState.guessing==0 ) {
			
			try {
				self = new JCharLiteral(buildTokenReference(), c.getText());
			} catch (PositionedError e) {
				reportTrouble(e);
				// allow parsing to continue
				self = new JCharLiteral(TokenReference.NO_REF, '\0');
			}
			
		}
		return self;
	}
	
	private final JLiteral  jStringLiteral(
		
	) throws RecognitionException, TokenStreamException {
		JLiteral self = null;
		
		Token  s = null;
		
		s = LT(1);
		match(STRING_LITERAL);
		if ( inputState.guessing==0 ) {
			self = new JStringLiteral(buildTokenReference(), s.getText());
		}
		return self;
	}
	
	private final JLiteral  jRealLiteral(
		
	) throws RecognitionException, TokenStreamException {
		JLiteral self = null;
		
		Token  r = null;
		
		r = LT(1);
		match(REAL_LITERAL);
		if ( inputState.guessing==0 ) {
			
			try {
				self = JLiteral.parseReal(buildTokenReference(), r.getText());
			} catch (PositionedError e) {
				reportTrouble(e);
				// allow parsing to continue
				self = new JFloatLiteral(TokenReference.NO_REF, 0f);
			}
			
		}
		return self;
	}
	
	private final CReferenceType[]  kReferenceTypeList(
		
	) throws RecognitionException, TokenStreamException {
		CReferenceType[] self = null;
		
		
		CReferenceType    typeParameter;
		ArrayList        container = new ArrayList();
		
		
		match(LT);
		typeParameter=jClassTypeSpec();
		if ( inputState.guessing==0 ) {
			container.add(typeParameter);
		}
		{
		_loop258:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				typeParameter=jClassTypeSpec();
				if ( inputState.guessing==0 ) {
					container.add(typeParameter);
				}
			}
			else {
				break _loop258;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			self = (CReferenceType[])container.toArray(new CReferenceType[container.size()]);
			
		}
		match(GT);
		if ( inputState.guessing==0 ) {
			
			if (!environment.isGenericEnabled()) {
			reportTrouble(new PositionedError(buildTokenReference(), KjcMessages.UNSUPPORTED_GENERIC_TYPE, null));
			}
			
		}
		return self;
	}
	
	private final CTypeVariable  kTypeVariableDeclaration(
		
	) throws RecognitionException, TokenStreamException {
		CTypeVariable self = null;
		
		Token  ident = null;
		
		CReferenceType     bound;
		ArrayList	 container = new ArrayList();
		
		
		ident = LT(1);
		match(IDENT);
		{
		switch ( LA(1)) {
		case LITERAL_extends:
		{
			match(LITERAL_extends);
			bound=jTypeName();
			if ( inputState.guessing==0 ) {
				container.add(bound);
			}
			{
			_loop267:
			do {
				if ((LA(1)==BAND)) {
					match(BAND);
					bound=jTypeName();
					if ( inputState.guessing==0 ) {
						container.add(bound);
					}
				}
				else {
					break _loop267;
				}
				
			} while (true);
			}
			break;
		}
		case COMMA:
		case GT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
			self = new CTypeVariable(ident.getText(), (CReferenceType[])container.toArray(new CReferenceType[container.size()]));
			
		}
		return self;
	}
	
	private final CaesarPointcut  jPointcut(
		
	) throws RecognitionException, TokenStreamException {
		CaesarPointcut self = null;
		
		Token  pattern = null;
		
			TokenReference	sourceRef = buildTokenReference();
			int mod = 0;
		
		
		{
		switch ( LA(1)) {
		case TYPE_PATTERN:
		{
			pattern = LT(1);
			match(TYPE_PATTERN);
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
					if (pattern != null) {
					
						try {
							CaesarPatternParser patternParser = new CaesarPatternParser(
																	pattern.getText(),
																	new CaesarSourceContext(sourceRef) );
							self = patternParser.parsePointcut();
						} 
						catch(CaesarParserException e) {
							reportTrouble(new PositionedError(sourceRef, CaesarMessages.WEAVER_ERROR, e.getMessage()));			
						}
						catch(RuntimeException e) {
							reportTrouble(new PositionedError(sourceRef, CaesarMessages.WEAVER_ERROR, e.getMessage()));							
						}  			
						
						
					} else {
					
						self = CaesarPointcut.makeMathesNothing();
						
					}
				
		}
		return self;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"abstract\"",
		"\"boolean\"",
		"\"break\"",
		"\"byte\"",
		"\"case\"",
		"\"catch\"",
		"\"char\"",
		"\"class\"",
		"\"const\"",
		"\"continue\"",
		"\"default\"",
		"\"do\"",
		"\"double\"",
		"\"else\"",
		"\"extends\"",
		"\"false\"",
		"\"final\"",
		"\"finally\"",
		"\"float\"",
		"\"for\"",
		"\"goto\"",
		"\"if\"",
		"\"implements\"",
		"\"import\"",
		"\"instanceof\"",
		"\"int\"",
		"\"interface\"",
		"\"long\"",
		"\"native\"",
		"\"new\"",
		"\"null\"",
		"\"package\"",
		"\"private\"",
		"\"protected\"",
		"\"public\"",
		"\"return\"",
		"\"short\"",
		"\"static\"",
		"\"strictfp\"",
		"\"super\"",
		"\"switch\"",
		"\"synchronized\"",
		"\"this\"",
		"\"throw\"",
		"\"throws\"",
		"\"transient\"",
		"\"true\"",
		"\"try\"",
		"\"void\"",
		"\"volatile\"",
		"\"while\"",
		"\"virtual\"",
		"\"override\"",
		"\"clean\"",
		"===",
		"\"collaboration\"",
		"\"provided\"",
		"\"expected\"",
		"\"binds\"",
		"\"provides\"",
		"\"wraps\"",
		"\"wrappee\"",
		"#",
		"\"after\"",
		"\"around\"",
		"\"before\"",
		"\"crosscutting\"",
		"\"declare\"",
		"\"deploy\"",
		"\"deployed\"",
		"\"pointcut\"",
		"\"precedence\"",
		"\"privileged\"",
		"\"returning\"",
		"\"throwing\"",
		"=",
		"&",
		"&=",
		"~",
		"|",
		"|=",
		">>>",
		">>>=",
		"^",
		"^=",
		":",
		",",
		"--",
		".",
		"==",
		">=",
		">",
		"++",
		"&&",
		"[",
		"{",
		"<=",
		"!",
		"||",
		"(",
		"<",
		"-",
		"-=",
		"!=",
		"%",
		"%=",
		"+",
		"+=",
		"?",
		"]",
		"}",
		")",
		";",
		"<<",
		"/",
		"/=",
		"<<=",
		">>",
		">>=",
		"*",
		"*=",
		"a character literal (inside simple quote)",
		"an identifier",
		"an integer literal",
		"a real literal",
		"a string literal (inside double quote)",
		"a type pattern for pointcut definition",
		"@@",
		"@invariant",
		"@ensure",
		"@require",
		"@assert",
		"@assert",
		"@fail"
	};
	
	private static final long[] _tokenSet_0_data_ = { 4297039262312826896L, 4503599627375168L, 0L, 0L };
	public static final BitSet _tokenSet_0 = new BitSet(_tokenSet_0_data_);
	private static final long[] _tokenSet_1_data_ = { 4297039261239083024L, 4672L, 0L, 0L };
	public static final BitSet _tokenSet_1 = new BitSet(_tokenSet_1_data_);
	private static final long[] _tokenSet_2_data_ = { 4301543964140440752L, 4611687117939021544L, 0L, 0L };
	public static final BitSet _tokenSet_2 = new BitSet(_tokenSet_2_data_);
	private static final long[] _tokenSet_3_data_ = { 4301543964140440752L, 4611687685143140088L, 4L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_3 = new BitSet(_tokenSet_3_data_);
	private static final long[] _tokenSet_4_data_ = { 4504701827613856L, 4611686018427387904L, 0L, 0L };
	public static final BitSet _tokenSet_4 = new BitSet(_tokenSet_4_data_);
	private static final long[] _tokenSet_5_data_ = { 0L, 4611687135387320336L, 0L, 0L };
	public static final BitSet _tokenSet_5 = new BitSet(_tokenSet_5_data_);
	private static final long[] _tokenSet_6_data_ = { 0L, 4503616874381312L, 0L, 0L };
	public static final BitSet _tokenSet_6 = new BitSet(_tokenSet_6_data_);
	private static final long[] _tokenSet_7_data_ = { 4323174131376434416L, -2301266115834932410L, 907L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_7 = new BitSet(_tokenSet_7_data_);
	private static final long[] _tokenSet_8_data_ = { 5709792341984416L, -2305842459189444602L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_8 = new BitSet(_tokenSet_8_data_);
	private static final long[] _tokenSet_9_data_ = { 5709792341984416L, -2305769732373741562L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_9 = new BitSet(_tokenSet_9_data_);
	private static final long[] _tokenSet_10_data_ = { 4323174131376434416L, -2300140215928089786L, 907L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_10 = new BitSet(_tokenSet_10_data_);
	private static final long[] _tokenSet_11_data_ = { 4611404508870323440L, -2814749834242066L, 955L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_11 = new BitSet(_tokenSet_11_data_);
	private static final long[] _tokenSet_12_data_ = { 4297039261239085072L, 4672L, 0L, 0L };
	public static final BitSet _tokenSet_12 = new BitSet(_tokenSet_12_data_);
	private static final long[] _tokenSet_13_data_ = { 4297039261239085072L, 4611686018427392576L, 0L, 0L };
	public static final BitSet _tokenSet_13 = new BitSet(_tokenSet_13_data_);
	private static final long[] _tokenSet_14_data_ = { 4301543963066696880L, 4611686018427392576L, 0L, 0L };
	public static final BitSet _tokenSet_14 = new BitSet(_tokenSet_14_data_);
	private static final long[] _tokenSet_15_data_ = { 4301543963066696880L, 4611687135387324992L, 0L, 0L };
	public static final BitSet _tokenSet_15 = new BitSet(_tokenSet_15_data_);
	private static final long[] _tokenSet_16_data_ = { 26170054509438176L, -2301266115834937082L, 907L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_16 = new BitSet(_tokenSet_16_data_);
	private static final long[] _tokenSet_17_data_ = { 4611404507796598256L, -2814749834243258L, 907L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_17 = new BitSet(_tokenSet_17_data_);
	private static final long[] _tokenSet_18_data_ = { 5709792341984416L, -2305769715462307834L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_18 = new BitSet(_tokenSet_18_data_);
	private static final long[] _tokenSet_19_data_ = { 5709792341984416L, -2305769749822046202L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_19 = new BitSet(_tokenSet_19_data_);
	private static final long[] _tokenSet_20_data_ = { 293940168762131616L, -3940684134383610L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_20 = new BitSet(_tokenSet_20_data_);
	private static final long[] _tokenSet_21_data_ = { 4323174131376582128L, -2300140215928089786L, 907L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_21 = new BitSet(_tokenSet_21_data_);
	private static final long[] _tokenSet_22_data_ = { 4611404508872568816L, -2814749834242066L, 955L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_22 = new BitSet(_tokenSet_22_data_);
	private static final long[] _tokenSet_23_data_ = { 5709792341984416L, -2305769749788491770L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_23 = new BitSet(_tokenSet_23_data_);
	private static final long[] _tokenSet_24_data_ = { 293940168762131616L, -3940684067274746L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_24 = new BitSet(_tokenSet_24_data_);
	private static final long[] _tokenSet_25_data_ = { 0L, 1171452329984L, 0L, 0L };
	public static final BitSet _tokenSet_25 = new BitSet(_tokenSet_25_data_);
	private static final long[] _tokenSet_26_data_ = { 0L, 153122387332694016L, 0L, 0L };
	public static final BitSet _tokenSet_26 = new BitSet(_tokenSet_26_data_);
	private static final long[] _tokenSet_27_data_ = { 4504701827613856L, 0L, 0L };
	public static final BitSet _tokenSet_27 = new BitSet(_tokenSet_27_data_);
	private static final long[] _tokenSet_28_data_ = { 5709792341984416L, -2305842459457880058L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_28 = new BitSet(_tokenSet_28_data_);
	private static final long[] _tokenSet_29_data_ = { 293940168762131616L, -34359771130L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_29 = new BitSet(_tokenSet_29_data_);
	private static final long[] _tokenSet_30_data_ = { 5709792341984416L, -2305206799868624890L, 11L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_30 = new BitSet(_tokenSet_30_data_);
	
	}
