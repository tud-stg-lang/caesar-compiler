package org.caesarj.compiler.ast;


import org.caesarj.classfile.Constants;
import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CMember;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JConstructorDeclaration;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JLocalVariableExpression;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;
import org.caesarj.kjc.JVariableDeclarationStatement;
import org.caesarj.kjc.JVariableDefinition;
import org.caesarj.kjc.TypeFactory;

public class FjConstructorDeclaration extends JConstructorDeclaration {

	protected TypeFactory cachedTypeFactory;
	protected boolean superTypeParameterAdded;

	public FjConstructorDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JConstructorBlock body,
		JavadocComment javadoc,
		JavaStyleComment[] comments,
		TypeFactory factory) {
		super(
			where,
			modifiers,
			ident,
			parameters,
			exceptions,
			body,
			javadoc,
			comments,
			factory);
		cachedTypeFactory = factory;
		superTypeParameterAdded = false;
	}

	protected void setInterface(CMember export) {
		super.setInterface(export);
	}

	public JConstructorBlock getBody() {
		return (JConstructorBlock) body;
	}

	public void setBody(JBlock body) {
		this.body = body;
	}

	public void addSuperTypeParameter(CReferenceType superType) {
		if (superType != null) {
			JFormalParameter[] newParameters =
				new JFormalParameter[parameters.length + 1];
			for (int i = 0; i < parameters.length; i++) {
				newParameters[i + 1] = parameters[i];
			}
			newParameters[0] =
				new FjFormalParameter(
					getTokenReference(),
					JFormalParameter.DES_PARAMETER,
					superType,
					FjConstants.PARENT_NAME,
					false);
			parameters = newParameters;
			superTypeParameterAdded = true;
		}
	}

	public FjConstructorDeclaration getStandardBaseClassConstructor(CReferenceType superType) {

		// every (clean-class-)constructor is still available
		// through passing a base-class-instance as base-object
		// those constructors are available for virtual classes,
		// too, though they are not callable, because calls will
		// be converted to calling the matching factory method,
		// which will itself call the parameterized constructor

		FjConstructorCall superCall =
			((FjConstructorBlock) getBody()).getConstructorCall();
		JExpression[] superArguments = JExpression.EMPTY;
		if (superCall != null) {
			superArguments = superCall.getArguments();
		}

		FjFormalParameter[] newParameters =
			new FjFormalParameter[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			newParameters[i] =
				(FjFormalParameter) ((FjFormalParameter) parameters[i]).clone();
		}

		JExpression[] arguments = null;
		if (superCall != null && superCall.isThis()) {
			arguments = new JExpression[superArguments.length];
			for (int i = 0; i < superArguments.length; i++) {
				/*
				CloneExpressionsFjVisitor cloner = new CloneExpressionsFjVisitor();
				arguments[ i ] = cloner.getClone( superArguments[ i ] );
				*/
				arguments[i] = superArguments[i];
			}
		} else {
			arguments = new JExpression[parameters.length + 1];
			for (int i = 0; i < parameters.length; i++) {
				arguments[i + 1] =
					new FjNameExpression(
						getTokenReference(),
						parameters[i].getIdent());
			}
			arguments[0] =
				new FjUnqualifiedInstanceCreation(
					getTokenReference(),
					superType,
					superArguments);
		}
		return new FjConstructorDeclaration(
			getTokenReference(),
			modifiers,
			ident,
			newParameters,
			exceptions,
			new FjConstructorBlock(
				getTokenReference(),
				new FjConstructorCall(getTokenReference(), true, arguments),
				JStatement.EMPTY),
			null,
			null,
			cachedTypeFactory);
	}

	public void setSuperArg(JExpression superArg) {

		// replace super( a, b, ... ) by super( parent )
		// and this( a, b, ... ) by this( parent, a, b, ... )
		FjConstructorCall oldCall =
			((FjConstructorBlock) getBody()).getConstructorCall();

		JExpression[] newArgs = null;
		if (oldCall != null && oldCall.isThis()) {
			if (superTypeParameterAdded) {
				newArgs = new JExpression[oldCall.getArguments().length + 1];
				for (int i = 0; i < oldCall.getArguments().length; i++) {
					newArgs[i + 1] = oldCall.getArguments()[i];
				}
				newArgs[0] = superArg;
			} else {
				newArgs = oldCall.getArguments();
			}
		} else {
			newArgs = new JExpression[1];
			newArgs[0] = superArg;
		}

		FjConstructorCall constructorCalled =
			new FjConstructorCall(
				getTokenReference(),
				oldCall != null && oldCall.isThis(),
				newArgs);

		JConstructorBlock newBody =
			new FjConstructorBlock(
				body.getTokenReference(),
				constructorCalled,
				body.getBody());
		body = newBody;
	}

	public void setIdent(String newIdent) {
		ident = newIdent;
	}

	public FjCleanMethodDeclaration getAbstractFactoryMethod() {
		return new FjCleanMethodDeclaration(
			getTokenReference(),
			Constants.ACC_PUBLIC,
			typeVariables,
			new CClassNameType("java/lang/Object"),
			FjConstants.factoryMethodName(FjConstants.toIfcName(ident)),
			parameters,
			exceptions,
			null,
			null,
			null);
	}
	
	public FjCleanMethodDeclaration getFactoryMethod(
		FjVirtualClassDeclaration factoredClass,
		String superTypeName) {

		// if the factored class extends a fields inner type
		// the field has to be prepended to the super-factory-call
		JExpression superFactoryPrefix = null;
		if (factoredClass.getSuperClassField() != null)
			superFactoryPrefix =
				new FjFieldAccessExpression(
					getTokenReference(),
					factoredClass
						.getSuperClassField()
						.getVariable()
						.getIdent());

		// eventually set the super-prefix for the superFactoryMethod-call
		CReferenceType returnType = new CClassNameType("java/lang/Object");
		if (superTypeName != null
			&& ident.equals(FjConstants.toImplName(isolateIdent(superTypeName)))
			&& superFactoryPrefix == null)
			superFactoryPrefix = new FjSuperExpression(getTokenReference());

		JFormalParameter[] newParameters =
			new JFormalParameter[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			newParameters[i] =
				(FjFormalParameter) ((FjFormalParameter) parameters[i]).clone();
		}

		// calculate the arguments to
		// pass to the called constructor
		JExpression[] constructorArgs = null;
		int argIndex = 0;
		if (!superTypeName.equals(FjConstants.CHILD_IMPL_TYPE_NAME)) {
			// calculate the parameters for the "supercall"
			FjConstructorCall superCall =
				((FjConstructorBlock) getBody()).getConstructorCall();
			JExpression[] superFactoryMethodArgs = null;
			if (superCall == null)
				superFactoryMethodArgs = JExpression.EMPTY;
			else
				superFactoryMethodArgs = superCall.getArguments();
			constructorArgs = new JExpression[newParameters.length + 1];
			// add "supercall" as first parameter
			if (superFactoryPrefix == null)
				constructorArgs[0] =
					new FjUnqualifiedInstanceCreation(
						getTokenReference(),
						new CClassNameType(
							FjConstants.toIfcName(superTypeName)),
						superFactoryMethodArgs);
			else
				constructorArgs[0] =
					new FjQualifiedInstanceCreation(
						getTokenReference(),
						superFactoryPrefix,
						isolateIdent(FjConstants.toIfcName(superTypeName)),
						superFactoryMethodArgs);
			argIndex = 1;
		} else {
			constructorArgs = new JExpression[newParameters.length];
			argIndex = 0;
		}
		for (int i = 0; i < newParameters.length; i++) {
			constructorArgs[i + argIndex] =
				new JLocalVariableExpression(
					getTokenReference(),
					newParameters[i]);
		}

		JBlock factoryBody =
			new JBlock(
				getTokenReference(),
				new JStatement[] {
					new JVariableDeclarationStatement(
						getTokenReference(),
						new JVariableDefinition(
							getTokenReference(),
							0,
							FjConstants.CHILD_TYPE,
							"child",
							new JUnqualifiedInstanceCreation(
								getTokenReference(),
								new CClassNameType(
									FjConstants.toImplName(ident)),
								constructorArgs)),
						null),
					new JExpressionStatement(
						getTokenReference(),
						new FjMethodCallExpression(
							getTokenReference(),
							new FjNameExpression(getTokenReference(), "child"),
							FjConstants.SET_FAMILY_METHOD_NAME,
							new JExpression[] {
								 new JThisExpression(getTokenReference())}),
						null),
					new JReturnStatement(
						getTokenReference(),
						new FjNameExpression(getTokenReference(), "child"),
						null)
		},
				null);

		FjCleanMethodDeclaration m =
			new FjCleanMethodDeclaration(
				getTokenReference(),
				Constants.ACC_PUBLIC,
				typeVariables,
				returnType,
				FjConstants.factoryMethodName(FjConstants.toIfcName(ident)),
				newParameters,
				exceptions,
				factoryBody,
				null,
				null);
		return m;
	}

	public JFormalParameter[] getParameters() {
		return this.parameters;
	}

	private String isolateIdent(String fullName) {
		fullName = fullName.replace('.', '/');
		fullName = fullName.replace('$', '/');
		fullName = fullName.replace('#', '/');
		int lastSeperator = fullName.lastIndexOf('/');
		return fullName.substring(lastSeperator + 1);
	}
	
	/**
	 * By now I'm coping the method from FjMethodDeclaration.
	 * @param context
	 * @return
	 * @throws PositionedError
	 * @author Walter Augusto Werner
	 */
	public CSourceMethod initFamilies(CClassContext context)
		throws PositionedError
	{
		((FjAdditionalContext) context).pushContextInfo(this);
		((FjAdditionalContext) context).pushContextInfo(parameters);		
		// after checking the parameters we rename overridden ones
		// and introduce downcasted variables with the old name		
		for (int i = 0; i < parameters.length; i++) 
		{
			FjFormalParameter parameter = (FjFormalParameter) parameters[ i ];
			try 
			{
				parameter.addFamily(context);
				parameter.upcastOverriddenType(context);
			} 
			catch (UnpositionedError e) 
			{
				context.reportTrouble(e.addPosition(parameter.getTokenReference()));
			}
		}
			
		CSourceMethod method = checkInterface(context);
			
		// pop parameters and method name from the stack again
		((FjAdditionalContext) context).popContextInfo();
		((FjAdditionalContext) context).popContextInfo();
		return method;
	}
	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkInterface(CClassContext)
	 */
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {

		CSourceMethod method = super.checkInterface(context);

		int classModifiers = context.getCClass().getModifiers();

		//Constructors in static deployed classes must be private
		if ((classModifiers & ACC_DEPLOYED) != 0
			&& ((modifiers & ACC_PRIVATE) == 0 || parameters.length != 0)) {
			context.reportTrouble(
				new PositionedError(
					getTokenReference(),
					CaesarMessages
						.DEPLOYED_CLASS_CONSTRUCTOR_NON_PRIVATE_OR_WITH_PARAMETER));
		}

		return method;
	}
	
	//Walter NEW
	public void updateWeaveletConstructor(CciWeaveletClassDeclaration owner)
	{
		TokenReference ref = getTokenReference();
		JExpression[] bindingCallArgs = new JExpression[]
		{
			new FjFieldAccessExpression(ref, 
				new FjThisExpression(ref), 
				CciConstants.IMPLEMENTATION_FIELD_NAME)
		};
		JExpression[] implementationCallArgs = new JExpression[]
		{
			new FjFieldAccessExpression(ref, 
				new FjThisExpression(ref), 
				CciConstants.BINDING_FIELD_NAME)
		};
		
		JStatement[] statements = new JStatement[]
		{
			new JExpressionStatement(ref,
				new FjAssignmentExpression(ref, 
					new FjFieldAccessExpression(ref, 
						CciConstants.IMPLEMENTATION_FIELD_NAME),
					new FjUnqualifiedInstanceCreation(ref,
						new CClassNameType(owner.getImplementationTypeName()), 
						new JExpression[0])),
				null),
			new JExpressionStatement(ref,
				new FjAssignmentExpression(ref, 
					new FjFieldAccessExpression(ref, 
						CciConstants.BINDING_FIELD_NAME),
					new FjUnqualifiedInstanceCreation(ref,
						new CClassNameType(owner.getBindingTypeName()), 
						new JExpression[0])),
				null),
			new JExpressionStatement(ref,
				new FjMethodCallExpression(ref, 
					new FjThisExpression(ref), 
					CciConstants.toSettingMethodName(
						CciConstants.BINDING_FIELD_NAME),
					implementationCallArgs),
				null),
			new JExpressionStatement(ref,
				new FjMethodCallExpression(ref, 
					new FjThisExpression(ref), 
					CciConstants.toSettingMethodName(
						CciConstants.IMPLEMENTATION_FIELD_NAME),
					bindingCallArgs),
				null)
		};
		JStatement[] currentStatements = body.getBody();
		JStatement[] finalStatements;
		if (currentStatements != null
			&& currentStatements.length > 0)
		{
			finalStatements = new JStatement[currentStatements.length + 
				statements.length];
			System.arraycopy(statements, 0, finalStatements, 0, 
				statements.length);
			System.arraycopy(currentStatements, 0, finalStatements, 
				statements.length, currentStatements.length);
		}
		else
			finalStatements = statements;
			
		
		body = new FjConstructorBlock(
			ref, 
			((FjConstructorBlock)body).getConstructorCall(), 
			finalStatements);
	}
	
//	//Walter
//	public void updateConstructor(String ciType, String fieldName)
//	{
//		JFormalParameter[] newParameters = 
//			new JFormalParameter[parameters.length + 1];
//			
//		System.arraycopy(parameters, 0, newParameters, 1, parameters.length);
//		newParameters[0] = 
//			new FjFormalParameter(
//				getTokenReference(), 
//				JFormalParameter.DES_PARAMETER, 
//				new CClassNameType(ciType), 
//				fieldName, 
//				false);
//
//		FjConstructorCall constructorCall = 
//			((FjConstructorBlock)body).getConstructorCall();
//
//		JExpression[] arguments = constructorCall.getArguments();
//
//		JExpression[] newArguments = new JExpression[arguments.length + 1];
//		System.arraycopy(arguments, 0, newArguments, 1, arguments.length);
//		newArguments[0] =
//			new FjNameExpression(
//				getTokenReference(),
//				fieldName);
//
//		constructorCall.setArguments(newArguments);
//		
//		JStatement[] statements = body.getBody();
//		JStatement[] newStatements = new JStatement[statements.length + 1];
//		
//		System.arraycopy(statements, 0, newStatements, 0, statements.length);
//		
//		newStatements[statements.length] = 
//			new JExpressionStatement(
//				getTokenReference(),
//				new FjAssignmentExpression(
//					getTokenReference(), 
//					new FjFieldAccessExpression(
//						getTokenReference(), 
//						new FjThisExpression(getTokenReference()), 
//						fieldName), 
//					new FjNameExpression(
//						getTokenReference(), 
//						fieldName)),
//				null);
//		
//		body = new FjConstructorBlock(getTokenReference(), 
//			constructorCall, newStatements);
//	}

	/**
	 * DEBUG - WALTER
	 * @author Walter Augusto Werner
	 */	
	public void print()
	{
		super.print();
		System.out.print(" ------> Families: ");
		for (int i = 0; i < parameters.length; i++)
		{
			if (i > 0) System.out.print(", ");
			System.out.print(parameters[i].getIdent());
			System.out.print(" - ");
			System.out.print(((FjFormalParameter)parameters[i]).getFamily());
		}
		System.out.println();

	}
}