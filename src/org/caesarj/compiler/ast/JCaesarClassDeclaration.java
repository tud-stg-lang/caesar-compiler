package org.caesarj.compiler.ast;

import java.util.Arrays;
import java.util.Vector;

import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.aspectj.CaesarScope;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.joinpoint.DeploymentPreparation;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

public class JCaesarClassDeclaration 
	extends JClassDeclaration
{
	/*
	 * Integration of FjClassDeclaration (Karl Klose)
	 */
	
	/** The declared advices */
	protected JAdviceDeclaration[] advices;
	/** e.g. declare precedence */
	protected CaesarDeclare[] declares;

	/** e.g. perSingleton, perCflow,..*/
	protected CaesarPointcut perClause;

	/** The declared pointcuts */
	protected JPointcutDeclaration[] pointcuts;

	
	public JCaesarClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType wrappee,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		JPointcutDeclaration[] pointcuts,
		JAdviceDeclaration[] advices,
		CaesarDeclare[] declares) {
			super(
				where,
				//modifiers & ACC_CAESARCLASS, // TODO 3 days of debugging this one
				modifiers | ACC_CAESARCLASS,
				ident,
				typeVariables,
				superClass,
				interfaces,
				fields,
				methods,
				inners,
				initializers,
				javadoc,
				comment);

			this.wrappee = wrappee;
			this.advices = advices;
			this.declares = declares;
			this.pointcuts = pointcuts;
			
				
			// structural detection of crosscutting property
			if ((advices.length > 0) || (pointcuts.length > 0))
				 this.modifiers |= ACC_CROSSCUTTING;
				
			
		}


	public void checkInterface(CContext context) throws PositionedError
	{
		//statically deployed classes are considered as aspects
		if (isStaticallyDeployed())
		{
			DeploymentPreparation.prepareForStaticDeployment(context, this);

			modifiers |= ACC_FINAL;
		}



		super.checkInterface(context);

		
		getFjSourceClass().setPerClause(perClause);


		if (isPrivileged() || isStaticallyDeployed())
		{
			getFjSourceClass().setPerClause(
				CaesarPointcut.createPerSingleton()
				);
		}

		//ckeckInterface of the pointcuts
		for (int j = 0; j < pointcuts.length; j++)
		{
			pointcuts[j].checkInterface(self);
		}
		
		//ckeckInterface of the advices
		for (int j = 0; j < advices.length; j++)
		{
			advices[j].checkInterface(self);
			//during the following compiler passes
			//the advices should be treated like methods
			CMethod m = advices[j].getMethod();
			getFjSourceClass().addMethod((CSourceAdviceMethod) advices[j].getMethod());
		}

		//consider declares
		if (declares != null)
		{
			for (int j = 0; j < declares.length; j++)
			{
				declares[j].resolve(
					new CaesarScope(
						constructContext(context),
						getFjSourceClass()));
			}

			getFjSourceClass().setDeclares(declares);
		}			
	}

	public void join(CContext context) throws PositionedError
	{
		super.join(context);
	}


	private TypeFactory typeFactory;
	public void setTypeFactory(TypeFactory typeFactory)
	{
		this.typeFactory = typeFactory;
	}
	public TypeFactory getTypeFactory()
	{
		return typeFactory;
	}

	protected JMethodDeclaration[] getInterfaceMethods() {
		// TODO
		return this.methods;
	}

	private JMethodDeclaration[] append(
			JMethodDeclaration[] left,
			JMethodDeclaration[] right)
	{

		JMethodDeclaration[] result =
			new JMethodDeclaration[left.length + right.length];
		int i = 0;
		for (int j = 0; j < left.length; j++)
		{
			result[i] = left[j];
			i++;
		}
		for (int j = 0; j < right.length; j++)
		{
			result[i] = right[j];
			i++;
		}
		return result;
	}

	public JCaesarClassDeclaration getBaseClass()
	{
		return this;
	}

	public void addInterface(CReferenceType ifc)
	{
		addInterface(ifc, interfaces.length);
	}

	public void addInterface(CReferenceType ifc, int index)
	{
		Vector interfaces = new Vector(Arrays.asList(this.interfaces));
		interfaces.add(index,ifc);
		this.interfaces =
			(CReferenceType[]) Utils.toArray(interfaces, CReferenceType.class);
	}

	protected CReferenceType getSuperConstructorType()
	{
		String superTypeName = getSuperClass().getQualifiedName();
		if (superTypeName.equals(FjConstants.CHILD_IMPL_TYPE_NAME))
			return null;
		return 
			new CClassNameType(FjConstants.toIfcName(superTypeName));
	}
	protected JExpression getSuperConstructorArgumentExpression()
	{
		String superTypeName = getSuperClass().getQualifiedName();
		if (superTypeName.equals(FjConstants.CHILD_IMPL_TYPE_NAME))
			return new JNullLiteral(FjConstants.STD_TOKEN_REFERENCE);
		
		return
			new JNameExpression(
				FjConstants.STD_TOKEN_REFERENCE,
				FjConstants.PARENT_NAME);
	}


	public void setIdent(String ident)
	{
		super.setIdent(ident);

		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof JConstructorDeclaration)
				 ((JConstructorDeclaration) methods[i]).setIdent(ident);
		}
	}

	public JConstructorDeclaration[] getConstructors()
	{
		JConstructorDeclaration[] constructors = super.getConstructors();

		// assure one constructor is there
		if (constructors.length != 0)
		{
			return constructors;
		}
		else
		{
			JConstructorDeclaration noArgsConstructor =
				new JConstructorDeclaration(
					getTokenReference(),
					ClassfileConstants2.ACC_PUBLIC,
					ident,
					JFormalParameter.EMPTY,
					CReferenceType.EMPTY,
					new JConstructorBlock(
						getTokenReference(),
						new JConstructorCall(
							getTokenReference(),
							false,
							JExpression.EMPTY),
						JStatement.EMPTY),
					null,
					null,
					typeFactory);
			append(noArgsConstructor);
			return new JConstructorDeclaration[] { noArgsConstructor };
		}

	}
	/**
	 * Checks if the class implements or binds all nested 
	 * interfaces from the CI and if the methods are in the right place. The 
	 * expected methods cannot be defined into a implementation class as 
	 * provided in binding classes.
	 * 
	 */
	public void checkTypeBody(CContext context) 
		throws PositionedError
	{
		checkWrapper(context);
		if (advices != null)
		{
			for (int i = 0; i < advices.length; i++)
			{
				advices[i].checkBody1(self);
			}
		}
	
		super.checkTypeBody(context);
	}

	/**
	 * Only virtual or override classes can wrap!
	 */
	protected void checkWrapper(CContext context)
		throws PositionedError
	{
		check(context, 
			wrappee == null,
			CaesarMessages.NON_BINDING_WRAPPER,
			getCClass().getQualifiedName());
		
	}


	/**
	 * This method is used to check if the method is defined in the class.
	 * 
	 * @param method the method which one wants to know if there is an
	 * implementation.
	 * @return true if the method is already defined, false otherwise.
	 */
	protected boolean isDefined(CMethod method)
	{
		for (int i = 0; i < methods.length; i++)
		{
			CMethod definedMethod = methods[i].getMethod();
			if (method.getIdent().equals(definedMethod.getIdent())
				&& method.hasSameSignature(definedMethod, null))
				return true;
		}
		return false;
	}
	

	
	/**
	 * Insert the wrapper mappings in the body reference of the class.
	 * It is for the compiler insert the right initialization.
	 */
	public void insertWrapperMappingsInitialization(JFieldDeclaration map)
	{
		JPhylum[] newBody = new JPhylum[body.length + 1];
		System.arraycopy(body, 0, newBody, 1, body.length);
		
		newBody[0] = map;

		body = newBody;
	}


	/**
	 * Creates a field declaration which will contain all 
	 * instances of the wrappers of the type passed.
	 * 
	 * @param binding inner type that will be contained in the map.
	 */
	public JFieldDeclaration createWrapperMap(
		String mapName)
	{
		TokenReference ref = getTokenReference();

		return
			new JFieldDeclaration(
				ref, 
				new JVariableDefinition(
					ref, 
					ACC_PRIVATE | ACC_FINAL,
					CciConstants.WRAPPER_MAP_TYPE,
					mapName,
					new JUnqualifiedInstanceCreation(
						ref, 
						CciConstants.WRAPPER_MAP_TYPE, 
						JExpression.EMPTY)),
				CciConstants.WRAPPER_MAP_JAVADOC,
				new JavaStyleComment[0]);
	}
	
		
	public void accept(KjcVisitor p) {
		super.accept(p);	
	}
    
	public JPointcutDeclaration[] getPointcuts()
	{
		return pointcuts;
	}

	public JAdviceDeclaration[] getAdvices()
	{
		return advices;
	}

	public void setPointcuts(JPointcutDeclaration[] pointcuts)
	{
		this.pointcuts = pointcuts;
	}

	public void setAdvices(JAdviceDeclaration[] advices)
	{
		this.advices = advices;
	}

	/**
	 * Returns the precedenceDeclaration.
	 * @return Declare
	 */
	public CaesarDeclare[] getDeclares()
	{
		return declares;
	}

	/**
	 * Sets the precedenceDeclaration.
	 * @param precedenceDeclaration The precedenceDeclaration to set
	 */
	public void setDeclares(CaesarDeclare[] declares)
	{
		this.declares = declares;
	}

	/**
	 * Sets the perClause.
	 * @param perClause The perClause to set
	 */
	public void setPerClause(CaesarPointcut perClause)
	{
		this.perClause = perClause;
	}

	public boolean isCrosscutting() {
		return CModifier.contains(modifiers, ACC_CROSSCUTTING);
	}

   public boolean isStaticallyDeployed()
   {
	   return (modifiers & ACC_DEPLOYED) != 0;
   }

}
