package org.caesarj.compiler.ast;

import java.util.Hashtable;
import java.util.Vector;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CSourceField;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JVariableDefinition;
import org.caesarj.kjc.TypeFactory;

public class FjVirtualClassDeclaration extends FjCleanClassDeclaration
	implements FjResolveable {

	/** if inheriting a fields' inner type */
	protected JFieldDeclaration superClassField;

	public FjVirtualClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType binding,
		CReferenceType providing,
		CReferenceType wrappee,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment) {
		super(
			where,
			modifiers | FJC_VIRTUAL,
			ident,
			typeVariables,
			superClass,
			binding,
			providing,
			wrappee,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}

	protected FjCleanClassInterfaceDeclaration newInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods) {
		return new FjVirtualCleanClassInterfaceDeclaration(
			tokenReference,
			ident,
			modifiers & (CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING 
				| CCI_WEAVELET),			
			interfaces,
			methods,
			getOwnerDeclaration(),
			this );
	}

	protected FjCleanClassIfcImplDeclaration newIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods ) {
		return new FjVirtualCleanClassIfcImplDeclaration(
			tokenReference,
			ident,
			(modifiers & (CCI_COLLABORATION | CCI_BINDING | CCI_PROVIDING 
				| CCI_WEAVELET)) | ACC_PUBLIC,			
			interfaces,
			methods,
			getOwnerDeclaration(),
			this );
	}

	public void join(CContext context) throws PositionedError {
		if( superClassField == null )
			superClassField = resolveQualifiedSuperClass( context, this );
		super.join(context);
	}
	

	public void checkTypeBody(CContext context) throws PositionedError {		
		try {
			if( getSuperClassField() != null ) {
				FjFamily family = new FjFieldFamily( context, getSuperClassField().getField() );
				FjFamilyContext.getInstance().setFamilyOf( getCClass(),	family );
			}
			super.checkTypeBody(context);
		} catch( UnpositionedError e ) {
			throw e.addPosition( getTokenReference() );
		}
	}
	
	public JFieldDeclaration getSuperClassField() {
		return superClassField;
	}

	public static JFieldDeclaration resolveQualifiedSuperClass( CContext context, FjResolveable resolveClass )
		throws PositionedError {

		JTypeDeclaration ownerDecl = resolveClass.getOwnerDeclaration();
		
		// if a class has no owner it cannot inherit
		// an owner's field's inner types
		if( ownerDecl == null )
			return null;

		String superName = resolveClass.getBaseClass().getSuperClass().toString();
		FjTypeSystem fjts = new FjTypeSystem();
		String[] splitName = fjts.splitQualifier( superName );
		if( splitName != null ) {
			String qualifier = splitName[ 0 ];
			String remainder = splitName[ 1 ];
			
			CType familyType = null;
			JFieldDeclaration familyField =  null;
			int i = 0;
			for( ; i < ownerDecl.getFields().length; i++ ) {
				familyField = ownerDecl.getFields()[ i ];
				if( familyField.getVariable().getIdent().equals( qualifier ) ) {
					// the field has to be private
					resolveClass.check(
						context,
						(familyField.getVariable().getModifiers() & Constants.ACC_PRIVATE) != 0,
						CaesarMessages.PRIVATE_REFERENCE_NEEDED,
						familyField.getVariable().getIdent() );
					// the field has to be final
					resolveClass.check(
						context,
						(familyField.getVariable().getModifiers() & Constants.ACC_FINAL) != 0,
						CaesarMessages.FINAL_REFERENCE_NEEDED,
						familyField.getVariable().getIdent() );
					familyType = familyField.getVariable().getType();					
					break;
				}
			}
			
			// if the typename is qualified by a variable
			// the qualifier has to be resolved to its type					
			if( familyType != null ) {
				try {
					familyType = familyType.checkType( context );
					if( !familyType.isReference() )
						return null;
				} catch( UnpositionedError e ) {
					throw e.addPosition( ownerDecl.getFields()[ i ].getTokenReference() );
				}
				resolveClass.setSuperClass(
					new CClassNameType(
						familyType.getCClass().getQualifiedName() + "$" + remainder ) );
				return familyField;
			}
		}		
		return null;
	}

	public FjCleanMethodDeclaration[] getFactoryMethods() {
		FjCleanMethodDeclaration[] methods = new FjCleanMethodDeclaration[ getConstructors().length ];
		for( int i = 0; i < getConstructors().length; i++ ) {
			methods[ i ] = getConstructors()[ i ].getAbstractFactoryMethod();
		}
		return methods;
	}

	
	
	public Vector inherritConstructorsFromBaseClass( Hashtable markedVirtualClasses )
		throws PositionedError {
		
		// only override classes must provide
		// constructors of their base classes
		return new Vector();
	}
	
	// the following classes represent the constructor-list
	// when inherriting the constructors of a base class
	protected static class Constructors {
		public Vector constructors;
		protected Constructors( Vector constructors ) {
			this.constructors = constructors;
		}
	}
	protected static class UncheckedConstructors extends Constructors {
		public UncheckedConstructors( FjCleanClassDeclaration classDecl ) {
			super( findConstructors( classDecl ) );
		}
		private static Vector findConstructors( FjCleanClassDeclaration classDecl ) {
			FjConstructorDeclaration[] methods = classDecl.getConstructors();
			Vector constructors = new Vector();
			for( int i = 0; i < methods.length; i++ ) {
				constructors.add( new UncheckedConstructor( methods[ i ] ) );
			}
			return constructors;
		}	
	}
	protected static class CheckedConstructors extends Constructors {
		public CheckedConstructors( CClass checkedClass ) {
			super( findConstructors( checkedClass ) );
		}
		private static Vector findConstructors( CClass checkedClass ) {
			CMethod[] methods = checkedClass.getMethods();
			Vector constructors = new Vector();
			for( int i = 0; i < methods.length; i++ ) {
				if( methods[ i ].getCClass().getIdent().equals( Constants.JAV_CONSTRUCTOR ) )
					constructors.add( new CheckedConstructor( methods[ i ] ) );
			}
			return constructors;
		}
	}

	/**
	 */
	public void addOuterThis()
	{
		if (outerThis == null)
		{
			sourceClass.setHasOuterThis(true);

			CReferenceType outerType = sourceClass.getOwnerType();
			outerThis =
				new JFieldDeclaration(
					getTokenReference(),
					new JVariableDefinition(
						getTokenReference(),
						ACC_PRIVATE | ACC_FINAL,
						outerType,
						JAV_OUTER_THIS,
						null),
					null,
					null);
			((CSourceClass) getCClass()).addField(
				new CSourceField(
					getCClass(),
					ACC_PRIVATE | ACC_FINAL,
					JAV_OUTER_THIS,
					outerType,
					false,
					true));
			// synthetic
		}
	}
	
	// the following classes represent a single constructor
	// when inherriting the constructors of a base class
	protected static class Constructor {
		public Vector params;
		protected Constructor( Vector params ) {
			this.params = params;
		}
		public JFormalParameter[] toParameterArray() {
			JFormalParameter[] result = new JFormalParameter[ params.size() ];
			for( int i = 0; i < result.length; i++ ) {
				result[ i ] = (JFormalParameter) params.elementAt( i );
			}
			return result;
		}
		public String toString() {
			StringBuffer signature = new StringBuffer( " " );			
			for( int i = 0; i < params.size(); i++ ) {
				JFormalParameter p =
					(JFormalParameter) params.elementAt( i );
				signature.append( p.getType().toString() );
				signature.append( " " );
			}
			return signature.toString();
		}
	}	
	protected static class UncheckedConstructor extends Constructor {
		public UncheckedConstructor( FjConstructorDeclaration decl ) {
			super( parametersOf( decl ) );
		}
		private static Vector parametersOf( FjConstructorDeclaration constructor ) {
			JFormalParameter[] params = constructor.getParameters();
			Vector paramV = new Vector();
			for( int i = 0; i < params.length; i++ ) {
				paramV.add( params[ i ] );
			}
			return paramV;
		}
	}	
	protected static class CheckedConstructor extends Constructor {
		public CheckedConstructor( CMethod method ) {
			super( parametersOf( method ) );
		}
		private static Vector parametersOf( CMethod constructor ) {
			CType[] paramTypes = constructor.getParameters();			
			Vector paramV = new Vector();
			for( int i = 0; i < paramTypes.length; i++ ) {
				paramV.add( new FjFormalParameter(
					FjConstants.STD_TOKEN_REFERENCE,
					JFormalParameter.DES_PARAMETER,
					paramTypes[ i ],
					"v" + i,
					false
				) );
			}
			return paramV;
		}
	}


	protected Vector assertConstructorsAreAvailable( Constructors holder ) {
		Vector constructorsAppended = new Vector();
		for( int i = 0; i < holder.constructors.size(); i++ ) {			
			Constructor constructor =
				(Constructor) holder.constructors.elementAt( i );
			if( !hasMatchingConstructor( constructor ) ) {				
				
				JExpression[] args = new JExpression[ constructor.params.size() ];
				
				for( int j = 0; j < args.length; j++ ) {
					args[ j ] = new FjNameExpression(
						FjConstants.STD_TOKEN_REFERENCE,
						( (JFormalParameter) constructor.params.elementAt( j )).getIdent() );
				}
				
				FjConstructorDeclaration c = new FjConstructorDeclaration(
					FjConstants.STD_TOKEN_REFERENCE,
					Constants.ACC_PUBLIC,
					getIdent(),
					constructor.toParameterArray(),
					CReferenceType.EMPTY,
					new FjConstructorBlock(
						FjConstants.STD_TOKEN_REFERENCE,
						new FjConstructorCall(
							FjConstants.STD_TOKEN_REFERENCE,
							false,
							args ),
						JStatement.EMPTY ),
					null,
					null,
					getTypeFactory() );
				
				append( c );
				constructorsAppended.add( constructor );
			}
		}
		return constructorsAppended;
	}

	protected boolean hasMatchingConstructor( Constructor constructorToFind ) {
		FjConstructorDeclaration[] constructors = getConstructors();
		for( int i = 0; i < constructors.length; i++ ) {
			if( constructorToFind.params.size() == constructors[ i ].getParameters().length ) {
				boolean matching = true;
				for( int j = 0; j < constructorToFind.params.size(); j++ ) {					
					CType paramType = ((JFormalParameter) constructorToFind.params.elementAt( j ) ).getType();
					if( !paramType.getSignature().equals(
							constructors[ i ].getParameters()[ j ].getType().getSignature() ) ) {
						matching = false;
						break;
					} 
				}
				if( matching ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Only binding classes can wrap!
	 */
	protected void checkWrapper(CContext context)
		throws PositionedError
	{
		check(
			context,
			wrappee == null || binding != null 
			|| getSuperCollaborationInterface(getCClass(), CCI_BINDING) != null,
			CaesarMessages.NON_BINDING_WRAPPER,
			getCClass().getQualifiedName());
	}


	/**
	 * Adds the field wrappee and inserts the parameter and initialization
	 * in the constructors.
	 */
	public void addInternalWrapperRecyclingStructure(TypeFactory typeFactory)
		throws PositionedError
	{
		verify(wrappee != null);
		addField(createWrappeeField());
		FjConstructorDeclaration[] constructors = getConstructors();
		for (int i = 0; i < constructors.length; i++)
			constructors[i].addWrappeeParameter(wrappee);
	}
	
	/**
	 * Creates a field declaration which will contain all 
	 * instances of the wrappers of the type passed.
	 * 
	 * @param binding inner type that will be contained in the map.
	 */
	protected JFieldDeclaration createWrappeeField()
	{
		TokenReference ref = getTokenReference();

		return
			new FjFieldDeclaration(
				ref, 
				new FjVariableDefinition(
					ref, 
					ACC_PRIVATE,
					new CClassNameType(wrappee.getQualifiedName()),
					CciConstants.WRAPPEE_FIELD_NAME,
					null),
				false,
				CciConstants.WRAPPEE_FIELD_JAVADOC,
				new JavaStyleComment[0]);
	}
	
	public void addAdaptMethod()
	{
		TokenReference ref = getTokenReference();
		JBlock body = 
			new JBlock(
				ref, 
				new JStatement[]
				{
					new JReturnStatement(
						ref, 
						new JThisExpression(ref), 
						null)
				},
				null);
				
		JFormalParameter[] parameters = 
			new JFormalParameter[]
			{
				new JFormalParameter(
					ref, 
					JVariableDefinition.DES_PARAMETER, 
					new CClassNameType(JAV_OBJECT), 
					CciConstants.ADAPT_METHOD_PARAM_NAME, 
					false)
			};

		String methodName = CciConstants.toAdaptMethodName(ident);
		JMethodDeclaration adaptMethod = 
			new JMethodDeclaration(
				ref,
				ACC_PUBLIC,
				typeVariables,
				new CClassNameType(JAV_OBJECT),
				methodName,
				parameters,
				CReferenceType.EMPTY,
				body,
				CciConstants.ADAPT_METHOD_JAVADOC,
				new JavaStyleComment[0]);

		JMethodDeclaration adaptAbstractMethod = 
			new JMethodDeclaration(
				ref,
				ACC_PUBLIC,
				typeVariables,
				new CClassNameType(JAV_OBJECT),
				methodName,
				parameters,
				CReferenceType.EMPTY,
				null,
				CciConstants.ADAPT_METHOD_JAVADOC,
				new JavaStyleComment[0]);

				
		append(adaptMethod);
		//getCleanInterface().append(adaptAbstractMethod);
	}

}
