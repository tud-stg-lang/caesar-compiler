package org.caesarj.compiler.ast;

import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class FjCleanClassIfcImplDeclaration
	extends JClassDeclaration 
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
			modifiers | FJC_CLEAN | ACC_PUBLIC,
			ident,
			CTypeVariable.EMPTY,
			new CClassNameType( FjConstants.CHILD_IMPL_TYPE_NAME ),
			baseDecl.getBinding(),
			baseDecl.getProviding(),
			null,
			interfaces,
			new JFieldDeclaration[0], // clean classes - no fields
			FjCleanClassIfcImplDeclaration.importMethods(methods, interfaces),
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
		CReferenceType[] interfaces) {
		
		FjMethodDeclaration[] implMethods = new FjMethodDeclaration[ cleanMethods.length * 2 ];
		for( int i = 0; i < cleanMethods.length; i++ ) {
			implMethods[ 2*i ] =
				cleanMethods[ i ].getForwardThisToSelfMethod();
			implMethods[ 2*i + 1 ] =
				cleanMethods[ i ].getForwardSelfToParentMethod(interfaces[0]);
		}
		return implMethods;
	}
	
	/**
	 * Adds a clean method, importing it to this context.
	 * 
	 * @param methodToAdd
	 */
	public void addMethod(FjCleanMethodDeclaration methodToAdd)
	{
		addMethods(new FjCleanMethodDeclaration[]{methodToAdd});
	}
	/**
	 * Adds methods imported to this context
	 * @param methodsToAdd
	 */
	public void addMethods(FjCleanMethodDeclaration[] methodsToAdd)
	{
		FjMethodDeclaration[] importedMethods = 
			importMethods(methodsToAdd, interfaces);

		JMethodDeclaration[] newMethods =
			new JMethodDeclaration[methods.length + importedMethods.length];

		System.arraycopy(methods, 0, newMethods, 0, methods.length);
		System.arraycopy(
			importedMethods,
			0,
			newMethods,
			methods.length,
			importedMethods.length);

		methods = newMethods;		
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

		constructor.setGenerated();
				
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
