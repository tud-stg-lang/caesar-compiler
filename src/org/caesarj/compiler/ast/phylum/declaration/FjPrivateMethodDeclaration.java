package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Arrays;
import java.util.Vector;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.expression.FjMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.FjNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

public class FjPrivateMethodDeclaration extends FjMethodDeclaration {

	public FjPrivateMethodDeclaration(
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

	public FjMethodDeclaration getImplementationMethod(CType selfType) {
		JFormalParameter[] contextParameters =
			new JFormalParameter[parameters == null
				? 1
				: parameters.length + 1];
		for (int i = 1; i < contextParameters.length; i++) {
			contextParameters[i] = (JFormalParameter) getParameters()[i - 1].clone();
		}
		contextParameters[0] =
			new JFormalParameter(
				getTokenReference(),
				JFormalParameter.DES_PARAMETER,
				selfType,
				FjConstants.SELF_NAME,
				true
		);

		return new FjMethodDeclaration(
				getTokenReference(),
				ACC_PRIVATE | ClassfileConstants2.ACC_FINAL,
				typeVariables,
				returnType,
				FjConstants.implementationMethodName( ident ),
				contextParameters,
				exceptions,
				body,
				null,
				null );
	}

	public JFormalParameter[] getParameters() {
		Vector parameterVector = new Vector(
			Arrays.asList( parameters )
		);		
		return (JFormalParameter[])
			Utils.toArray( parameterVector, JFormalParameter.class );
	}
	
	public FjMethodDeclaration[] getSelfContextMethods( CType selfType ) {
		return new FjMethodDeclaration[] {
			getImplementationMethod( selfType ),
			getForwardThisToImplementationMethod( selfType ) };
	}

	public FjMethodDeclaration getForwardThisToImplementationMethod( CType selfType ) {
		JExpression[] implCallArgs = new JExpression[parameters == null ? 1 : parameters.length + 1];
		for (int args = 0; args < implCallArgs.length; args++) {
			if (args == 0) {
				implCallArgs[args] = new JThisExpression(getTokenReference());
			} else {
				implCallArgs[args] =
					new FjNameExpression(
						getTokenReference(),
						parameters[args - 1].getIdent());
			}
		}
		JExpression implCall =
			new FjMethodCallExpression(
				getTokenReference(),
				null,
				FjConstants.implementationMethodName( ident ),
				implCallArgs );
	
		JBlock contextBody = 
			new JBlock(
				getTokenReference(),
				new JStatement[] {
					returnType instanceof CVoidType
						? (JStatement) new JExpressionStatement(getTokenReference(),
							implCall,
							null)
						: (JStatement) new JReturnStatement(getTokenReference(),
							implCall,
							null)},
				null);
				
		return new FjMethodDeclaration(
				getTokenReference(),
				modifiers,
				typeVariables,
				returnType,
				ident,
				parameters,
				exceptions,
				contextBody,
				null,
				null );
	}

	public FjCleanMethodDeclaration getAccessorMethod( JTypeDeclaration owner ) {
		JExpression[] args = new JExpression[ parameters.length ];
		JFormalParameter[] newParameters = new JFormalParameter[ parameters.length ];
		for( int i = 0; i < args.length; i++ ) {
			args[ i ] =	new FjNameExpression( getTokenReference(), parameters[ i ].getIdent() );
			newParameters[ i ] = (JFormalParameter) ((JFormalParameter) parameters[ i ]).clone();
		}
		JExpression implCall =
			new FjMethodCallExpression(
				getTokenReference(),
				null,
				ident,
				args );	
		JBlock contextBody = 
			new JBlock(
				getTokenReference(),
				new JStatement[] {
					returnType instanceof CVoidType
						? (JStatement) new JExpressionStatement(getTokenReference(),
							implCall,
							null)
						: (JStatement) new JReturnStatement(getTokenReference(),
							implCall,
							null)},
				null);
		
		return new FjCleanMethodDeclaration(
				getTokenReference(),
				ACC_PUBLIC,
				typeVariables,
				returnType,
				FjConstants.privateAccessorId(
					ident,
					owner.getCClass().getQualifiedName(),
					FjConstants.uniqueMethodId( ident, parameters ) ),
				newParameters,
				exceptions,
				contextBody,
				null,
				null );
	}
}
