package org.caesarj.compiler.ast.phylum.declaration;


import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.statement.*;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CMember;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CciWeaveletReferenceType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

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
				new JFormalParameter(
					getTokenReference(),
					JFormalParameter.DES_PARAMETER,
					superType,
					FjConstants.PARENT_NAME,
					false);
			parameters = newParameters;
			superTypeParameterAdded = true;
		}
	}

	public FjConstructorDeclaration getStandardBaseClassConstructor(
		CReferenceType superType) 
	{

		// every (clean-class-)constructor is still available
		// through passing a base-class-instance as base-object
		// those constructors are available for virtual classes,
		// too, though they are not callable, because calls will
		// be converted to calling the matching factory method,
		// which will itself call the parameterized constructor

		FjConstructorCall superCall =
			((FjConstructorBlock) getBody()).getConstructorCall();
		JExpression[] superArguments = JExpression.EMPTY;
		if (superCall != null) 
			superArguments = superCall.getArguments();


		JFormalParameter[] newParameters =
			new JFormalParameter[parameters.length];
		for (int i = 0; i < parameters.length; i++) 
		{
			newParameters[i] =
				(JFormalParameter) ((JFormalParameter) parameters[i]).clone();
		}

		JExpression[] arguments = null;
		if (superCall != null && superCall.isThis()) 
		{
			arguments = new JExpression[superArguments.length];
			for (int i = 0; i < superArguments.length; i++) 
				arguments[i] = superArguments[i];

		}
		else 
		{
			arguments = new JExpression[parameters.length + 1];
			for (int i = 0; i < parameters.length; i++) 
			{
				arguments[i + 1] =
					new JNameExpression(
						getTokenReference(),
						parameters[i].getIdent());
			}
			arguments[0] =
				new JUnqualifiedInstanceCreation(
					getTokenReference(),
					superType,
					superArguments);
		}
		FjConstructorDeclaration ctor = new FjConstructorDeclaration(
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
			
		ctor.setGenerated();
		return ctor;
	}

	/**
	 * Creates a constructor which receives a parameter whose type is the
	 * Collaboration Interface that one of its ancestors binds or provides.
	 * This constructor must be created only for children of the binding
	 * or providing classes.
	 * 
	 * @param superParameterType
	 * @param superType
	 * @return
	 */
	public FjConstructorDeclaration getCollaborationInterfaceConstructor(
		CReferenceType superParameterType, CReferenceType superType) 
	{

		FjConstructorCall superCall =
			((FjConstructorBlock) getBody()).getConstructorCall();
		JExpression[] superArguments = JExpression.EMPTY;
		if (superCall != null) 
			superArguments = superCall.getArguments();


		JFormalParameter[] newParameters =
			new JFormalParameter[parameters.length + 1];
		for (int i = 1, j = 0; i < newParameters.length; i++, j++) 
		{
			newParameters[i] =
				(JFormalParameter) ((JFormalParameter) parameters[j]).clone();
		}
		
		newParameters[0] = 
			new JFormalParameter(
				getTokenReference(), 
				JVariableDefinition.DES_PARAMETER,
				superParameterType,
				FjConstants.PARENT_NAME,
				false);

		JExpression[] arguments = null;
		if (superCall != null && superCall.isThis()) 
		{
			arguments = new JExpression[superArguments.length + 1];
			System.arraycopy(superArguments, 0, arguments, 
				1, superArguments.length);
				
			arguments[0] = new JNameExpression(
				getTokenReference(), 
				null, 
				FjConstants.PARENT_NAME);
		}
		else 
		{
			arguments = new JExpression[parameters.length + 1];
			for (int i = 0, j = 1; i < parameters.length; i++, j++) 
			{
				arguments[j] =
					new JNameExpression(
						getTokenReference(),
						parameters[i].getIdent());
			}
			
			JExpression[] newSuperArguments = new JExpression[
				superArguments.length + 1];
			System.arraycopy(superArguments, 0, newSuperArguments, 1, 
				superArguments.length);
				
			newSuperArguments[0] =
				new JNameExpression(
					getTokenReference(), 
					null, 
					FjConstants.PARENT_NAME);
					
			arguments[0] =
				new JUnqualifiedInstanceCreation(
					getTokenReference(),
					superType,
					newSuperArguments);
		}
		//Create the new constructor
		FjConstructorDeclaration ctor = new FjConstructorDeclaration(
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
		// now mark it as generated
		ctor.setGenerated();
		return ctor; 
	}
	
	/**
	 * Creates the constructor for the weavelet classes. This constructor
	 * will just call the super constructor with the right parameters:
	 * 
	 * super(new <BindingType>(new <ProvidingType>(new <CIType>), ..);
	 * 
	 * @param superType
	 * @return
	 */
	public FjConstructorDeclaration getStandardWeaveletClassConstructor(
		CciWeaveletReferenceType collaborationInterface) 
	{
		//Gets the arguments
		FjConstructorCall superCall =
			((FjConstructorBlock) getBody()).getConstructorCall();
		JExpression[] constructorCallArguments = JExpression.EMPTY;
		if (superCall != null) 
			constructorCallArguments = superCall.getArguments();

		//Clones the parameters.
		JFormalParameter[] newParameters =
			new JFormalParameter[parameters.length];
		for (int i = 0; i < parameters.length; i++) 
			newParameters[i] =
				(JFormalParameter) ((JFormalParameter) parameters[i]).clone();


		JExpression[] arguments = null;
		//If it calls this(..) do nothing
		if (superCall != null && superCall.isThis()) 
		{
			arguments = new JExpression[constructorCallArguments.length];
			System.arraycopy(constructorCallArguments, 0, arguments, 0, 
				constructorCallArguments.length);
		}
		//If it calls super (implicitly or explicitly) insert the new expression
		else 
		{
			JExpression[] newSuperArguments = new JExpression[
				constructorCallArguments.length + 1];
			System.arraycopy(constructorCallArguments, 0, newSuperArguments, 1, 
				constructorCallArguments.length);
			
			//Creates the expression: new <ProvidingType>(new <CiType>)
			newSuperArguments[0] =
				new JUnqualifiedInstanceCreation(
					getTokenReference(),
					new CClassNameType(
						collaborationInterface.getProvidingQualifiedName()),
					JExpression.EMPTY);
//					new JExpression[]
//					{
//						new CciInternalUnqualifiedInstanceCreation(
//							getTokenReference(),
//							new CClassNameType(
//								collaborationInterface.getQualifiedName()),
//							JExpression.EMPTY)
//					});
					
			//Creates the expression: new <BindingType>(newSuperArguments)
			arguments = new JExpression[]
			{
				new JUnqualifiedInstanceCreation(
					getTokenReference(),
					new CClassNameType(
						collaborationInterface.getBindingQualifiedName()),
					newSuperArguments)
			};
		}
		//Create the new constructor
		FjConstructorDeclaration ctor = new FjConstructorDeclaration(
			getTokenReference(),
			modifiers,
			ident,
			newParameters,
			exceptions,
			new FjConstructorBlock(
				getTokenReference(),
				new FjConstructorCall(
					getTokenReference(), 
					superCall != null && superCall.isThis(), 
					arguments),
				getBody().getBody()),
			null,
			null,
			cachedTypeFactory);
		// now mark it as generated
		ctor.setGenerated();
		return ctor; 
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

	public JMethodDeclaration getAbstractFactoryMethod() {
		return new JMethodDeclaration(
			getTokenReference(),
			ClassfileConstants2.ACC_PUBLIC,
			typeVariables,
			new CClassNameType("java/lang/Object"),
			FjConstants.factoryMethodName(FjConstants.toIfcName(ident)),
			parameters,
			exceptions,
			null,
			null,
			null);
	}
	

	public JFormalParameter[] getParameters() {
		return this.parameters;
	}

	
	/**
	 * @param context
	 * @return
	 * @throws PositionedError
	 * @author Walter Augusto Werner
	 */
	public CSourceMethod initFamilies(CClassContext context)
		throws PositionedError
	{
		/* FJRM
		((FjAdditionalContext) context).pushContextInfo(this);
		((FjAdditionalContext) context).pushContextInfo(parameters);		
		// after checking the parameters we rename overridden ones
		// and introduce downcasted variables with the old name		
		for (int i = 0; i < parameters.length; i++) 
		{
			JFormalParameter parameter = (JFormalParameter) parameters[ i ];
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
		*/
			
		CSourceMethod method = checkInterface(context);
			
		/* FJRM
		// pop parameters and method name from the stack again
		((FjAdditionalContext) context).popContextInfo();
		((FjAdditionalContext) context).popContextInfo();
		*/
		return method;
	}
	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkInterface(CClassContext)
	 */
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {

		CSourceMethod method = super.checkInterface(context);

		int classModifiers = context.getCClass().getModifiers();

		// generated constructors must not be checked (Karl Klose)
		if (isGenerated())	return method;

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
	
	/**
	 * Creates the abstract method which creates the instances of the wrappers.
	 * it is a method with only name and signature.
	 * Signature:
	 * public Object _getWrapper<WrapperTypeName>(...)
	 * 
	 * @return
	 */
	public JMethodDeclaration createAbstractWrapperInitializationMethod() 
	{
		return new JMethodDeclaration(
			getTokenReference(),
		ClassfileConstants2.ACC_PUBLIC,
			typeVariables,
			new CClassNameType(JAV_OBJECT),
			CciConstants.toWrapperMethodCreationName(
				FjConstants.toIfcName(ident)),
			parameters,
			exceptions,
			null,
			null,
			null);
	}
	/**
	 * Creates the argument to the key used by creator and destructor.
	 * 
	 * @param parameter
	 * @return
	 */
	protected JExpression createKeyArgument(JFormalParameter parameter)
	{
		TokenReference ref = getTokenReference();
		if (parameter.getType().isReference())
			return new JNameExpression(ref, parameter.getIdent());
		else
			return new JMethodCallExpression(
				ref, 
				new JTypeNameExpression(
					ref, 
					CciConstants.WRAPPER_KEY_TYPE),
				CciConstants.KEY_TRANSFORMER_METHOD_NAME,
				new JExpression[]
				{
					new JNameExpression(ref, parameter.getIdent())
				});
		
	}
	/**
	 * Creates the key expression for access the wrapper maps.
	 * 
	 * @return
	 */
	public JExpression createKeyExpression()
	{
		TokenReference ref = getTokenReference();
		
		if (parameters.length == 1)
			return createKeyArgument(parameters[0]);
		else
		{
			JExpression[] arrayInitializerElements = 
				new JExpression[parameters.length];
			
			for (int i = 0; i < parameters.length; i++)
				arrayInitializerElements[i] = createKeyArgument(parameters[i]);
					
			return 
				new JUnqualifiedInstanceCreation(
					ref,
					CciConstants.WRAPPER_KEY_TYPE,
					new JExpression[]
					{
						new JNewArrayExpression(
							ref, 
							new CClassNameType(JAV_OBJECT),
							new JExpression[1],
							new JArrayInitializer(
								ref,
								arrayInitializerElements))					
					}
				);
		}
	}

	/**
	 * Creates a constructor which receives the wrappee reference as parameter
	 * and sets it to the field wrappee in the class.
	 * 
	 * Signature:
	 * <TypeName>(<WrappeeType> _internalWrappee)
	 * Body:
	 * this.wrappee = _internalWrappee;
	 * 
	 * @param typeFactory
	 * @return
	 */
	public void addWrappeeParameter(
		CReferenceType wrappee)
		throws PositionedError
	{
		if (parameters.length > 0)
		{
			throw
				new PositionedError(
					getTokenReference(), 
					CaesarMessages.WRAPPER_DEFINES_CONSTRUCTOR,
					ident);			
		}
			
		TokenReference ref = getTokenReference();
		FjConstructorBlock oldBlock = (FjConstructorBlock) getBody();
		JStatement[] oldBody = oldBlock .getBody();
		JStatement[] constructorBody = new JStatement[oldBody.length + 1];
		System.arraycopy(oldBody, 0, constructorBody, 1, oldBody.length);
		constructorBody[0] =
			new JExpressionStatement(
				ref, 
				new JAssignmentExpression(
					ref,
					new JFieldAccessExpression(
						ref,
						new JThisExpression(ref),
						CciConstants.WRAPPEE_FIELD_NAME),
					new JNameExpression(
							ref,
							CciConstants.WRAPPEE_PARAMETER_NAME)),
				null);
			
		body = 
			new FjConstructorBlock(
				ref, 
				null,
				constructorBody);
				
		parameters = 
			new JFormalParameter[]
			{
				new JFormalParameter(
					ref,
					JLocalVariable.DES_PARAMETER,
					new CClassNameType(wrappee.getQualifiedName()),
					CciConstants.WRAPPEE_PARAMETER_NAME,
					false)
			};
	}

	/**
	 * Creates the method used to get references with the 
	 * wrapper recycling operartor.
	 * 
	 * Signature:
	 * public Object _getWrapper<WrapperTypeName>(...)
	 * 
	 * Body:
	 * Object key = <keyExpression>;
	 * Object _localWrapper = _<wrapperType>.get(key);
	 * if (_localWrapper == null)
	 * {
	 * 		_localWrapper = new <WrapperType>(...);
	 * 		_<wrapperType>.put(key, _localWrapper);
	 * }
	 * return _localWrapper;
	 * 
	 */	
	public JMethodDeclaration createWrapperCreatorMethod(
		String mapName)
	{
		
		TokenReference ref = getTokenReference();
		JExpression keyExpression = createKeyExpression();
		String methodName = 
			CciConstants.toWrapperMethodCreationName(
				FjConstants.toIfcName(ident));

		JFormalParameter[] newParameters = 
			new JFormalParameter[parameters.length];
		JExpression[] constructorArgs = new JExpression[parameters.length];
		
		for (int i = 0; i < newParameters.length; i++)
		{
			newParameters[i] = (JFormalParameter)
				 ((JFormalParameter) parameters[i]).clone();
			constructorArgs[i] = 
				new JNameExpression(ref, newParameters[i].getIdent());
		}


		JStatement[] statements = new JStatement[]
		{
			//Object key = <keyExpression>
			new JVariableDeclarationStatement(
				ref,
				new JVariableDefinition(
					ref, 
					0, 
					new CClassNameType(JAV_OBJECT), 
					CciConstants.WRAPPER_LOCAL_KEY,
					keyExpression),
				null),

			//Object wrapper = map.get(key)
			new JVariableDeclarationStatement(
				ref,
				new JVariableDefinition(
					ref, 
					0, 
					new CClassNameType(JAV_OBJECT), 
					CciConstants.WRAPPER_LOCAL_VAR,
					new FjMethodCallExpression(
						ref, 
						new JFieldAccessExpression(
							ref, 
							new JThisExpression(ref), 
							mapName),
						CciConstants.WRAPPER_MAP_ACCESS,
						new JExpression[]
						{
							new JNameExpression(
								ref, 
								CciConstants.WRAPPER_LOCAL_KEY)
						})),
				null),

			//if (wrapper == null)
			//{
			//	wrapper = new Binding(wrappee)
			//	map.put(key, wrapper)
			//}
			new JIfStatement(
				ref, 
				new JEqualityExpression(
					ref, 
					true, 
					new JNameExpression(
						ref,
						CciConstants.WRAPPER_LOCAL_VAR), 
						new JNullLiteral(ref)),
				new JBlock(
					ref, 
					new JStatement[]
					{
						//wrapper = new Wapper(wrappee)
						new JExpressionStatement(
							ref,
							new FjAssignmentExpression(
								ref, 
								new JNameExpression(
									ref, 
									CciConstants.WRAPPER_LOCAL_VAR), 
								new JUnqualifiedInstanceCreation(
									ref, 
									new CClassNameType(
										FjConstants.toIfcName(ident)), 
									constructorArgs)),
							null),
						//map.put(key, wrapper)
						new JExpressionStatement(
							ref,
							new FjMethodCallExpression(
								ref,
								new JFieldAccessExpression(
									ref, 
									null, 
									mapName),
								CciConstants.WRAPPER_MAP_PUT,
								new JExpression[]
								{
									new JNameExpression(
										ref, 
										CciConstants
											.WRAPPER_LOCAL_KEY),
									new JNameExpression(
										ref, 
										CciConstants.WRAPPER_LOCAL_VAR)						
								}),
							null),
					},
					null),
				null,
				null),//endIf
		
			//return wrapper
			new JReturnStatement(
				ref,
				new JNameExpression(ref, CciConstants.WRAPPER_LOCAL_VAR),
				null)
		};
	
		JBlock methodBody = new JBlock(
			ref,
			statements,
			null);
		
	
		return 
			new JMethodDeclaration(
				ref, 
				ACC_PUBLIC, 
				typeVariables, 
				new CClassNameType(JAV_OBJECT),
				methodName,
				newParameters,
				CReferenceType.EMPTY,
				methodBody,
				CciConstants.WRAPPER_CREATOR_JAVADOC,
				new JavaStyleComment[0]);
	}

	/**
	 * Creates the method used to destruct wrappers. It is the method used
	 * to realize the semantics of wrapper destructor.
	 * 
	 * Signature:
	 * public Object _destructWrapper<WrapperTypeName>(...)
	 * 
	 * Body:
	 * Object key = <keyExpression>;
	 * _<wrapperType>.remove(key);
	 */	
	public JMethodDeclaration createWrapperDestructorMethod(
		String mapName)
	{

		TokenReference ref = getTokenReference();
		JExpression keyExpression = createKeyExpression();
		String methodName = 
			CciConstants.toWrapperMethodDestructionName(
				FjConstants.toIfcName(ident));

		JFormalParameter[] newParameters = 
			new JFormalParameter[parameters.length];
		
		for (int i = 0; i < newParameters.length; i++)
		{
			newParameters[i] = (JFormalParameter)
				 ((JFormalParameter) parameters[i]).clone();
		}


		JStatement[] statements = new JStatement[]
		{
			//Object key = <keyExpression>
			new JVariableDeclarationStatement(
				ref,
				new JVariableDefinition(
					ref, 
					0, 
					new CClassNameType(JAV_OBJECT), 
					CciConstants.WRAPPER_LOCAL_KEY,
					keyExpression),
				null),

			//map.remove(key)
			new JExpressionStatement(
				ref,
				new FjMethodCallExpression(
					ref, 
					new JFieldAccessExpression(
						ref, 
						new JThisExpression(ref), 
						mapName),
					CciConstants.WRAPPER_MAP_REMOVE,
					new JExpression[]
					{
						new JNameExpression(
							ref, 
							CciConstants.WRAPPER_LOCAL_KEY)
					}),
				null),
		};
	
		JBlock methodBody = new JBlock(
			ref,
			statements,
			null);
		
	
		return 
			new JMethodDeclaration(
				ref, 
				ACC_PUBLIC, 
				typeVariables, 
				returnType,
				methodName,
				newParameters,
				CReferenceType.EMPTY,
				methodBody,
				CciConstants.WRAPPER_DESTRUCTOR_JAVADOC,
				new JavaStyleComment[0]);
	}



	/**
	 * DEBUG - WALTER
	 * @author Walter Augusto Werner
	 */
	/* FJRM		
	public void print()
	{
		super.print();
		System.out.print(" ------> Families: ");
		for (int i = 0; i < parameters.length; i++)
		{
			if (i > 0) System.out.print(", ");
			System.out.print(parameters[i].getIdent());
			System.out.print(" - ");
			System.out.print(((JFormalParameter)parameters[i]).getFamily());
		}
		System.out.println();

	}
	*/
}