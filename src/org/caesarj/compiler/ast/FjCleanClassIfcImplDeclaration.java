package org.caesarj.compiler.ast;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JConstructorDeclaration;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.TypeFactory;

public class FjCleanClassIfcImplDeclaration
	extends FjClassDeclaration 
{

	protected FjCleanClassDeclaration baseDecl;

	protected FjCleanClassIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifiers,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjCleanClassDeclaration baseDecl ) {
		super(
			tokenReference,
			modifiers,
			ident,
			CTypeVariable.EMPTY,
			new CClassNameType( FjConstants.CHILD_IMPL_TYPE_NAME ),
			interfaces,
			baseDecl.getBinding(),
			new JFieldDeclaration[0], // clean classes - no fields
			FjCleanClassIfcImplDeclaration.importMethods( methods, interfaces ),
			new JTypeDeclaration[0], // inners are possible
			new JPhylum[0],
			new JavadocComment( "Automatically generated interface implementation.", false, false ),
			new JavaStyleComment[0]);
		// Walter: baseDecl.getBinding() is now passed as parameter.			
		this.baseDecl = baseDecl;
	}

	public FjCleanClassIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjCleanClassDeclaration baseDecl ) {
		this(
			tokenReference,
			ident,
			ACC_PUBLIC,
			interfaces,
			methods,
			baseDecl );
	}

	protected static FjMethodDeclaration[] importMethods(
		FjCleanMethodDeclaration[] cleanMethods,
		CReferenceType[] interfaces ) {
			
		FjMethodDeclaration[] implMethods = new FjMethodDeclaration[ cleanMethods.length * 2 ];
		for( int i = 0; i < cleanMethods.length; i++ ) {
			implMethods[ 2*i ] =
				cleanMethods[ i ].getForwardThisToSelfMethod();
			implMethods[ 2*i + 1 ] =
				cleanMethods[ i ].getForwardSelfToParentMethod( interfaces[ 0 ] );
		}
		return implMethods;
	}

	public FjCleanClassDeclaration getBaseClass() {
		return baseDecl;
	}

	public void addChildsConstructor( TypeFactory typeFactory ) {
		
		JFormalParameter parent =
			new FjFormalParameter(
				getTokenReference(),
				JFormalParameter.DES_PARAMETER,
				new CClassNameType( FjConstants.CHILD_TYPE_NAME ),
				"parent",
				false );

		FjConstructorCall superConstructorCall =
			new FjConstructorCall(
				getTokenReference(),
				false,
				new JExpression[] {
					new FjNameExpression(
						getTokenReference(),
						"parent" )
				} );

		JConstructorDeclaration constructor =
			new FjConstructorDeclaration(
				getTokenReference(),
				CModifier.ACC_PROTECTED,
				ident,
				new JFormalParameter[] {
					parent
				},
				CReferenceType.EMPTY,
				new FjConstructorBlock(
					getTokenReference(),
					superConstructorCall,
					JStatement.EMPTY ),
				null,
				null,
				typeFactory );
				
		JMethodDeclaration[] newMethods =
			new JMethodDeclaration[ methods.length + 1 ];
		newMethods[ 0 ] = constructor;
		System.arraycopy(methods, 0, newMethods, 1, methods.length);
		methods = newMethods;
	}
	
	private CReferenceType superIfc;
	public void setSuperIfc( CReferenceType superIfc ) {
		this.superIfc = superIfc;
	}
	public CReferenceType getSuperIfc() {
		return this.superIfc;
	}
	
	public void join(CContext context) throws PositionedError {
		try {
			super.join(context);
		} catch( PositionedError e ) {
			// an error occuring here will occur in the
			// base class this class is derived from, too
		}
	}
}
