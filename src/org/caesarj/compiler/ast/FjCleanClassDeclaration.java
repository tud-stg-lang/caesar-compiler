package org.caesarj.compiler.ast;

import java.util.Arrays;
import java.util.Vector;

import org.caesarj.classfile.Constants;
import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.util.Utils;

public class FjCleanClassDeclaration extends FjClassDeclaration
{

	public FjCleanClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType superClass,
		CReferenceType[] interfaces,
		CReferenceType[] bindings,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment)
	{
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			bindings,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
		//Walter: the "bindings" was inserted as parameter.
	}

	public void checkInterface(CContext context) throws PositionedError
	{

		// clean classes may not ...

		// ... be abstract
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0,
			CaesarMessages.CLEAN_ABSTRACT_CLASS);

		// ... be inners
		check(
			context,
			(modifiers & FJC_CLEAN) == 0 || getOwner() == null,
			CaesarMessages.CLEAN_CLASS_NO_INNER);

		super.checkInterface(context);

		// ... define package access or protected methods
		for (int i = 0; i < methods.length; i++)
		{
			check(
				context,
				CModifier.contains(
					methods[i].getMethod().getModifiers(),
					ACC_PUBLIC | ACC_PRIVATE),
				CaesarMessages.METHOD_VISIBILITY_IN_CLEAN_CLASS,
				getMethods()[i].getMethod().getIdent());
		}

		// ... define fields with visibility other than private
		for (int i = 0; i < fields.length; i++)
		{
			check(
				context,
				(fields[i].getField().getModifiers() & ACC_PRIVATE) != 0,
				CaesarMessages.CLEAN_JUST_PRIVATE_FIELDS,
				fields[i].getField().getIdent());
		}

		// ... define non-clean inners
		for (int i = 0; i < inners.length; i++)
		{
			check(
				context,
				CModifier.contains(
					inners[i].getModifiers(),
					FJC_CLEAN | FJC_VIRTUAL | FJC_OVERRIDE),
				CaesarMessages.CLEAN_NO_NON_CLEAN_MEMBER,
				inners[i].getCClass().getIdent());
		}
	}

	public void join(CContext context) throws PositionedError
	{
		checkCleanSuperclass(context);
		super.join(context);
	}

	protected void checkCleanSuperclass(CContext context)
		throws PositionedError
	{
		try
		{
			getSuperClass().checkType(context);
		}
		catch (UnpositionedError e1)
		{
			try
			{
				String unModifiedName =
					FjConstants.toIfcName(getSuperClass().toString());
				new CClassNameType(unModifiedName).checkType(context);
				// if the unmodified typename is ok, then
				// we are inheriting a non clean class
				throw new UnpositionedError(
					CaesarMessages.CLEAN_INHERITS_NON_CLEAN,
					unModifiedName).addPosition(
					getTokenReference());
			}
			catch (UnpositionedError e2)
			{
				// if the unmodified typename isn't ok, too ...
				throw e2.addPosition(getTokenReference());
			}
		}
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

	protected void createAccessorsForPrivateMethods()
	{
		Vector privateMethodAccessors = new Vector();
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjPrivateMethodDeclaration)
			{
				FjPrivateMethodDeclaration method =
					(FjPrivateMethodDeclaration) methods[i];
				method = method.getAccessorMethod(this);
				if (method != null)
					privateMethodAccessors.add(method);
			}
		}
		for (int i = 0; i < privateMethodAccessors.size(); i++)
		{
			append((JMethodDeclaration) privateMethodAccessors.elementAt(i));
		}
	}

	private FjCleanClassInterfaceDeclaration cleanInterface;
	public FjCleanClassInterfaceDeclaration getCleanInterface()
	{
		return cleanInterface;
	}
	public FjCleanClassInterfaceDeclaration createCleanInterface(Object owner)
	{

		//createAccessorsForPrivateMethods();

		FjClassDeclaration ownerDecl =
			(owner instanceof FjClassDeclaration)
				? (FjClassDeclaration) owner
				: null;

		if (cleanInterface == null)
		{
			//Walter start
			/*cleanInterface = newInterfaceDeclaration(
				getTokenReference(),
				FjConstants.cleanInterfaceName( ident ),
				interfaces,
				getCleanMethods()
			);*/
			cleanInterface =
				newInterfaceDeclaration(
					getTokenReference(),
					FjConstants.cleanInterfaceName(ident),
					getAllInterfaces(),
					getCleanMethods());
			//Walter end			

			if (getSuperClass() != null)
			{
				CClassNameType superIfcType =
					new CClassNameType(
						FjConstants.cleanInterfaceName(
							getSuperClass().getQualifiedName()));
				cleanInterface.addInterface(superIfcType);
			}
		}
		return cleanInterface;
	}

	private FjCleanClassIfcImplDeclaration cleanInterfaceImplementation;
	public FjCleanClassIfcImplDeclaration getCleanInterfaceImplementation()
	{
		return cleanInterfaceImplementation;
	}
	public FjCleanClassIfcImplDeclaration createCleanInterfaceImplementation(Object owner)
	{

		FjClassDeclaration ownerDecl =
			(owner instanceof FjClassDeclaration)
				? (FjClassDeclaration) owner
				: null;

		if (cleanInterfaceImplementation == null)
		{
			cleanInterfaceImplementation =
				newIfcImplDeclaration(
					getTokenReference(),
					FjConstants.cleanInterfaceImplementationName(ident),
					new CReferenceType[] {
						new CClassNameType(
							FjConstants.cleanInterfaceName(ident))},
					getCleanMethods());

			if (getSuperClass() == null)
			{
				CClassNameType superImplType =
					new CClassNameType(FjConstants.CHILD_IMPL_TYPE_NAME);
				cleanInterfaceImplementation.setSuperClass(superImplType);
			}
			else
			{
				CClassNameType superIfcType =
					new CClassNameType(
						FjConstants.cleanInterfaceName(
							getSuperClass().getQualifiedName()));
				cleanInterfaceImplementation.setSuperIfc(superIfcType);
				CClassNameType superImplType =
					new CClassNameType(
						FjConstants.cleanInterfaceImplementationName(
							getSuperClass().getQualifiedName()));
				cleanInterfaceImplementation.setSuperClass(superImplType);
			}

			cleanInterfaceImplementation.addChildsConstructor(typeFactory);
		}
		return cleanInterfaceImplementation;
	}
	protected FjCleanClassInterfaceDeclaration newInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods)
	{

		return new FjCleanClassInterfaceDeclaration(
			getTokenReference(),
			ident,
			interfaces,
			methods,
			this);
	}
	protected FjCleanClassIfcImplDeclaration newIfcImplDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods)
	{

		return new FjCleanClassIfcImplDeclaration(
			getTokenReference(),
			ident,
			interfaces,
			methods,
			this);
	}

	private FjCleanMethodDeclaration[] cleanMethods;
	public FjCleanMethodDeclaration[] getCleanMethods()
	{
		if (cleanMethods == null)
		{
			int numberOfcleanMethods = 0;
			// count the number of clean methods
			for (int i = 0; i < methods.length; i++)
			{
				if (methods[i] instanceof FjCleanMethodDeclaration)
					numberOfcleanMethods++;
			}
			cleanMethods = new FjCleanMethodDeclaration[numberOfcleanMethods];
			// select the clean methods
			for (int i = 0, j = 0; i < methods.length; i++)
			{
				if (methods[i] instanceof FjCleanMethodDeclaration)
				{
					cleanMethods[j] = (FjCleanMethodDeclaration) methods[i];
					j++;
				}
			}
			// include factory-methods of inner virtual types
			for (int i = 0; i < inners.length; i++)
			{
				if (inners[i] instanceof FjVirtualClassDeclaration)
				{
					((FjVirtualClassDeclaration) inners[i]).setTypeFactory(
						typeFactory);
					cleanMethods =
						append(
							cleanMethods,
							((FjVirtualClassDeclaration) inners[i])
								.getFactoryMethods());
				}
			}
		}
		return cleanMethods;
	}
	private FjCleanMethodDeclaration[] append(
		FjCleanMethodDeclaration[] left,
		FjCleanMethodDeclaration[] right)
	{

		FjCleanMethodDeclaration[] result =
			new FjCleanMethodDeclaration[left.length + right.length];
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

	public FjCleanClassDeclaration getBaseClass()
	{
		return this;
	}

	public void append(JTypeDeclaration type)
	{
		getCleanInterface().append(type);
	}

	public void addInterface(CReferenceType ifc)
	{
		Vector interfaces = new Vector(Arrays.asList(this.interfaces));
		interfaces.add(ifc);
		this.interfaces =
			(CReferenceType[]) Utils.toArray(interfaces, CReferenceType.class);
	}

	public void addSelfContextToCleanMethods(CReferenceType selfType)
	{
		Vector methodVector = new Vector(this.methods.length * 3);

		for (int i = 0; i < this.methods.length; i++)
		{
			if (this.methods[i] instanceof FjMethodDeclaration)
			{
				FjMethodDeclaration[] transformedMethods =
					((FjMethodDeclaration) this.methods[i])
							.getSelfContextMethods(selfType);
				for (int j = 0; j < transformedMethods.length; j++)
				{
					methodVector.add(transformedMethods[j]);
				}
			}
			else
			{
				methodVector.add(this.methods[i]);
			}
		}

		this.methods =
			(JMethodDeclaration[]) toArray(methodVector,
				JMethodDeclaration.class);
	}

	public void addSuperTypeParameterToConstructors()
	{

		String superTypeName = getSuperClass().getQualifiedName();
		CReferenceType superType = null;
		JExpression superArg = null;

		if (superTypeName.equals(FjConstants.CHILD_IMPL_TYPE_NAME))
		{
			superType = null;
			superArg = new JNullLiteral(FjConstants.STD_TOKEN_REFERENCE);
		}
		else
		{
			superType =
				new CClassNameType(FjConstants.toIfcName(superTypeName));
			superArg =
				new FjNameExpression(
					FjConstants.STD_TOKEN_REFERENCE,
					FjConstants.PARENT_NAME);
		}

		// introduce additional parent-parameterized constructor
		FjConstructorDeclaration[] constructors = getConstructors();
		Vector oldConstructors = new Vector(constructors.length);
		for (int i = 0; i < constructors.length; i++)
		{
			if (superType != null)
			{
				// only if the class has a supertype
				if (getOwner() == null)
				{
					// only for standalone clean classes *)
					oldConstructors.add(
						constructors[i].getStandardBaseClassConstructor(
							superType));
				}
				constructors[i].addSuperTypeParameter(superType);
			}
			// always set a parent; no supertype => null
			constructors[i].setSuperArg(superArg);
		}

		// now provide the old constructors for *)
		// by passing a standard baseclass parent
		for (int i = 0; i < oldConstructors.size(); i++)
		{
			append((FjConstructorDeclaration) oldConstructors.elementAt(i));
		}
	}

	protected JTypeDeclaration getCleanInterfaceOwner()
	{
		return getCleanInterface();
	}

	public void setSuperClass()
	{

		if (getSuperClass() == null)
			setSuperClass(new CClassNameType(FjConstants.CHILD_IMPL_TYPE_NAME));
		else
		{
			setSuperClass(
				new CClassNameType(
					FjConstants.cleanInterfaceImplementationName(
						getSuperClass().getQualifiedName())));
		}
	}

	public void setIdent(String ident)
	{
		super.setIdent(ident);

		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjConstructorDeclaration)
				 ((FjConstructorDeclaration) methods[i]).setIdent(ident);
		}
	}

	public FjConstructorDeclaration[] getConstructors()
	{
		FjConstructorDeclaration[] constructors = super.getConstructors();

		// assure one constructor is there
		if (constructors.length != 0)
		{
			return constructors;
		}
		else
		{
			FjConstructorDeclaration noArgsConstructor =
				new FjConstructorDeclaration(
					getTokenReference(),
					Constants.ACC_PUBLIC,
					ident,
					JFormalParameter.EMPTY,
					CReferenceType.EMPTY,
					new FjConstructorBlock(
						getTokenReference(),
						new FjConstructorCall(
							getTokenReference(),
							false,
							JExpression.EMPTY),
						JStatement.EMPTY),
					null,
					null,
					typeFactory);
			append(noArgsConstructor);
			return new FjConstructorDeclaration[] { noArgsConstructor };
		}

	}
}
