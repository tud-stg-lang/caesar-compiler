package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.expression.literal.JBooleanLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JIntLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.*;
import org.caesarj.util.TokenReference;

public class FjCleanMethodDeclaration 
	extends FjPrivateMethodDeclaration 
{

	public FjCleanMethodDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body,
		JavadocComment javadoc,
		JavaStyleComment[] comments) {
		super(
			where,
			modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			exceptions,
			body,
			javadoc,
			comments);
	}
	
	public FjMethodDeclaration getAbstractMethodDeclaration() {
		// Walter: Now the parameters are cloned here. Without it, all changes
		// Walter: in the parameters of the other method was being reflected 
		// Walter: at the parameters of this new method. 
		JFormalParameter[] clonedParameters = 
			parameters == null 
				? null 
				: new JFormalParameter[parameters.length];
		for (int i = 0; i < clonedParameters.length; i++)
			clonedParameters[i] = (JFormalParameter)getParameters()[i].clone();
			
		FjMethodDeclaration md =
			new FjMethodDeclaration(
				getTokenReference(),
				modifiers
					& ~(
						CModifier.ACC_NATIVE
							| CModifier.ACC_SYNCHRONIZED
							| CModifier.ACC_FINAL
							| CModifier.ACC_STRICT)
					| CModifier.ACC_ABSTRACT,
				CTypeVariable.EMPTY,
				returnType,
				ident,
				clonedParameters,
				exceptions,
				null,
				null,
				null
			);
		return md;
	}
	
	public FjCleanMethodDeclaration getForwardSelfToImplementationMethod( CType selfType ) {
		JFormalParameter[] contextParameters =
			new JFormalParameter[parameters == null ? 1 : parameters.length + 1];
		for (int i = 1; i < contextParameters.length; i++) {
			contextParameters[i] = (JFormalParameter) getParameters()[i - 1].clone();
		}
		contextParameters[0] =
			new JFormalParameter(
				getTokenReference(),
				JFormalParameter.DES_PARAMETER,
				CStdType.Object,
				FjConstants.SELF_NAME,
				true
			);
	
		JExpression[] contextImplCallArgs =
			new JExpression[ contextParameters.length ];
		for (int args = 0; args < contextImplCallArgs.length; args++) {
			contextImplCallArgs[args] =
				new FjNameExpression(
					getTokenReference(),
					contextParameters[args].getIdent());
			if (args == 0) {
				contextImplCallArgs[args] =
					new JCastExpression(
						getTokenReference(),
						contextImplCallArgs[args],
						selfType );
			}
		}
		JExpression contextImplCall =
			new JMethodCallExpression(
				getTokenReference(),
				null,
				FjConstants.implementationMethodName( ident ),
				contextImplCallArgs);
	
		JBlock contextBody =
				new JBlock(
					getTokenReference(),
					new JStatement[] {
						returnType instanceof CVoidType
							? (JStatement) new JExpressionStatement(
								getTokenReference(),
								contextImplCall,
								null)
							: (JStatement) new JReturnStatement(getTokenReference(),
								contextImplCall,
								null)},
					null);

		return	new FjCleanMethodDeclaration(
			getTokenReference(),
		    removeAbstract( modifiers ),
		    typeVariables,
			returnType,
			FjConstants.selfContextMethodName( ident ),
			contextParameters,
			exceptions,
			contextBody,
			null,
			null );
	}
	
	public FjCleanMethodDeclaration getForwardSelfToParentMethod( CReferenceType parentType ) {
		JFormalParameter[] contextParameters =
			new JFormalParameter[parameters == null ? 1 : parameters.length + 1];
		for (int i = 1; i < contextParameters.length; i++) {
			contextParameters[i] = (JFormalParameter) getParameters()[i - 1].clone();
		}
		contextParameters[0] =
			new JFormalParameter(
				getTokenReference(),
				JFormalParameter.DES_PARAMETER,
				CStdType.Object,
				FjConstants.SELF_NAME,
				true
			);
	
		JExpression[] contextImplCallArgs =
			new JExpression[ contextParameters.length ];
		for (int args = 0; args < contextImplCallArgs.length; args++) {
			contextImplCallArgs[args] =
				new FjNameExpression(
					getTokenReference(),
					contextParameters[args].getIdent());
			if (args == 0) {
				contextImplCallArgs[args] = 
					getDispatcher();
			}
		}
		FjMethodCallExpression contextImplCall =
			new FjMethodCallExpression(
				getTokenReference(),
				null,
				FjConstants.selfContextMethodName( ident ),
				contextImplCallArgs );
		contextImplCall.setPrefix(
			new JCastExpression( 
				getTokenReference(),
				new FjMethodCallExpression(
					getTokenReference(),					
					new JFieldAccessExpression(
						getTokenReference(),
						FjConstants.PARENT_NAME ),
					FjConstants.GET_TARGET_METHOD_NAME,
					JExpression.EMPTY ),
				parentType ) );
								
		JBlock contextBody =
				new JBlock(
					getTokenReference(),
					new JStatement[] {
						returnType instanceof CVoidType
							? (JStatement) new JExpressionStatement(
								getTokenReference(),
								contextImplCall,
								null)
							: (JStatement) new JReturnStatement(getTokenReference(),
								contextImplCall,
								null)},
					null);

		return	new FjCleanMethodDeclaration(
			getTokenReference(),
		    removeAbstract( modifiers ),
		    typeVariables,
			returnType,
			FjConstants.selfContextMethodName( ident ),
			contextParameters,
			exceptions,
			contextBody,
			null,
			null );
	}
	
	public FjMethodDeclaration getForwardThisToSelfMethod() {
		JFormalParameter[] callParameters =
			new JFormalParameter[parameters == null ? 1 : parameters.length + 1];
		JFormalParameter[] ownParameters =
			new JFormalParameter[parameters == null ? 0 : parameters.length];
		for (int i = 1; i < callParameters.length; i++) {
			callParameters[i] = (JFormalParameter) getParameters()[i - 1].clone();
			ownParameters[i - 1] = (JFormalParameter) getParameters()[i - 1].clone();
		}
		callParameters[0] =
			new JFormalParameter(
				getTokenReference(),
				JFormalParameter.DES_PARAMETER,
				CStdType.Object,
				FjConstants.SELF_NAME,
				true
			);
	
		JExpression[] contextImplCallArgs =
			new JExpression[ callParameters.length ];
		for (int args = 0; args < contextImplCallArgs.length; args++) {
			contextImplCallArgs[args] =
				new FjNameExpression(
					getTokenReference(),
					callParameters[args].getIdent());
			if (args == 0) {
				contextImplCallArgs[args] =
					new JThisExpression(getTokenReference());
			}
		}
		JExpression contextImplCall =
			new JMethodCallExpression(
				getTokenReference(),
				null,
				FjConstants.selfContextMethodName( ident ),
				contextImplCallArgs);
	
		JBlock contextBody =
				new JBlock(
					getTokenReference(),
					new JStatement[] {
						returnType instanceof CVoidType
							? (JStatement) new JExpressionStatement(
								getTokenReference(),
								contextImplCall,
								null)
							: (JStatement) new JReturnStatement(getTokenReference(),
								contextImplCall,
								null)},
					null);

		return	new FjCleanMethodDeclaration(
			getTokenReference(),
		    removeAbstract( modifiers ),
		    typeVariables,
			returnType,
			ident,
			ownParameters,
			exceptions,
			contextBody,
			null,
			null );
	}

	protected JExpression getDispatcher() {
		return	new JMethodCallExpression(
			FjConstants.STD_TOKEN_REFERENCE,
			null,
			FjConstants.GET_DISPATCHER_METHOD_NAME,
			new JExpression[] {
				new FjNameExpression(
					getTokenReference(),
					FjConstants.SELF_NAME )
			} );			
	}
	
	public FjMethodDeclaration[] getSelfContextMethods( CType selfType ) {
		return new FjMethodDeclaration[] {
			getImplementationMethod( selfType ),
			getForwardSelfToImplementationMethod( selfType ),
			getForwardThisToImplementationMethod( selfType ) };
	}
	
	public FjCleanMethodDeclaration getAccessorMethod(JTypeDeclaration owner) {
		// only private methods need accessors
		return null;
	}
	
	private int removeAbstract( int modifiers ) {
		if( (modifiers & ACC_ABSTRACT) != 0 )
			modifiers ^= ACC_ABSTRACT;
		return modifiers;
	}
	/**
	 * Creates methods with the same signatures, but with empty bodies.
	 * @return JMethodDeclaration
	 */
	public FjCleanMethodDeclaration createEmptyMethod()
	{
		TokenReference ref = getTokenReference();
		JStatement[] statements;
		if (returnType instanceof CVoidType)
			statements = new JStatement[0];
		else
		{
			JExpression expression;
			if (returnType.isNumeric())
				expression = new JIntLiteral(ref, 
					CciConstants.DEFAULT_NUMERIC_RETURN);
			else if (returnType.isPrimitive())
				expression = new JBooleanLiteral(ref, 
					CciConstants.DEFAULT_BOOLEAN_RETURN);
			else
				expression = new JNullLiteral(ref);
			
			statements = new JStatement[]
			{
				new JReturnStatement(ref, expression, null)
			};
		}
		
		JBlock newBody =  new JBlock(ref, statements, null);
			
		return new FjCleanMethodDeclaration(
				getTokenReference(),
				~ ACC_ABSTRACT & modifiers,
				typeVariables,
				returnType,
				ident,
				parameters,
				exceptions,
				newBody, 
				null,
				null);
	}	

}
