package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.util.MessageDescription;

/**
 * AST element for a class declaration.
 * Created by the parser when it finds a class that 
 * implements or binds an interface.
 * 
 * 
 * @author Walter Augusto Werner
 */
public class CciClassDeclaration 
	extends JClassDeclaration
{
	/** 
	 * The CIs that the class binds.
	 */
	protected CReferenceType[] bindings;
	/** 
	 * The CIs that the class implements.
	 */
	protected CReferenceType[] implementations;

	/**
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param typeVariables
	 * @param superClass
	 * @param interfaces
	 * @param bindings
	 * @param fields
	 * @param methods
	 * @param inners
	 * @param initializers
	 * @param javadoc
	 * @param comment
	 */
	public CciClassDeclaration(
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
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
		
		//binding will never be null!
		this.bindings = (bindings == null) ? new CReferenceType[0] : bindings;
	}

	/**
	 * Initializes the array <code>implementations</coed> with all the CI 
	 * implemented by the class, removing this interfaces from the 
	 * <code>interfaces</code> array.
	 */
	protected void initImplemetations()
	{
		ArrayList tempInterfaces = new ArrayList(
			Arrays.asList(interfaces));
		ArrayList tempImplementations = new ArrayList();
		for (int i = 0, j = 0; i < interfaces.length; i++)
		{
			if (CModifier.contains(interfaces[i].getCClass().getModifiers(),
					CCI_COLLABORATION))
			{
				tempImplementations.add(interfaces[i]);
				tempInterfaces.remove(interfaces[i]);				
			}
		}
					
		implementations = (CReferenceType[]) tempImplementations.toArray(
			new CReferenceType[tempImplementations.size()]);
			
		interfaces = (CReferenceType[]) tempInterfaces.toArray(
			new CReferenceType[tempInterfaces.size()]);

	}


	/**
	 * This method resolves the interface that is passed as parameter 
	 * and performs the checks. 
	 * It performs all checks that are done for standard java interfaces
	 * implementations plus the check if it is a CI, if the last parameter is 
	 * true.
	 *  
	 * @param context
	 * @param interfaceType the interface type to resolve.
	 * @param checkCollaboration if it is true it checks if the interface is 
	 * a collaboration interface, otherwise it does not.
	 * @throws PositionedError
	 */
	protected CReferenceType resolveInterface(CContext context, String ciName, 
		CType interfaceType, boolean checkCollaboration) 
		throws PositionedError
	{
		CReferenceType type;
		try
		{
			type = (CReferenceType) interfaceType.checkType(self);
		}
		catch (UnpositionedError e)
		{
			if (ciName != null)
			{
				try
				{
					type = (CReferenceType) new CClassNameType(
						ciName + "$" + interfaceType).checkType(context);
				}
				catch (UnpositionedError e1)
				{
					throw e.addPosition(getTokenReference());
				}
			}
			else
				throw e.addPosition(getTokenReference());
		}

		CClass clazz = type.getCClass();

		check(
			context,
			clazz.isInterface(),
			KjcMessages.SUPERINTERFACE_WRONG_TYPE,
			type.getQualifiedName());

		check(
			context,
			clazz.isAccessible(sourceClass),
			KjcMessages.SUPERINTERFACE_NOT_ACCESSIBLE,
			type.getQualifiedName());
		check(
			context,
			!(sourceClass.getQualifiedName() == JAV_OBJECT),
			KjcMessages.CIRCULAR_INTERFACE,
			type.getQualifiedName());

		if (checkCollaboration)
		{
			check(
				context,
				CModifier.contains(clazz.getModifiers(), CCI_COLLABORATION),
				CaesarMessages.NON_CI,
				type.getQualifiedName());
		}
		return type;
	}
	
	/**
	 * Overriden for resolve the bindings and insert the name of 
	 * the CI that are defined at the owner. Beyond that, the implementations
	 * of the inner classes must be added. The implementation of the inner 
	 * CIs is not defined by the programmer with the implements clause. It 
	 * is done using the same name as the name used for the inner interface 
	 * of the CI. So we have to add these implementations before resolve them.
	 */
	protected void resolveInterfaces(CContext context)
		throws PositionedError	
	{
		//First try to find the name of the owner CI, if there is one.
		String ciName = null;
		CClass owner = getOwner();
		
		if (owner != null && owner instanceof CciClass)
		{
			// By now, there is always at most one binding or implementation
			CReferenceType[] allInterfaces = owner.getInterfaces();
			CciClass ownerClass = ((CciClass)owner);
		
			//First try the bindings			
			CReferenceType[] ownerCis = ownerClass.getBindings();
			if (ownerCis != null && ownerCis.length > 0)
				ciName = ownerCis[0].getQualifiedName();
			else
			{
				//Now the implementations, adding the implementation interfaces
				//to the inners.
				ownerCis = ownerClass.getImplementations();
				if (ownerCis != null && ownerCis.length > 0)
				{
					ciName = ownerCis[0].getQualifiedName();
					addImplementation(ownerCis[0]);
				}
				else
				{
					//The owner can be an interface, so here we look for the
					// CI in the interfaces of the owner.
					ownerCis = owner.getInterfaces();
					if (ownerCis != null && ownerCis.length > 0)
					{
						CReferenceType ci = null;
						for (int i = 0; i < ownerCis.length; i++)
						{
							if (CModifier.contains(ownerCis[i].getCClass()
								.getModifiers(), CCI_COLLABORATION))
							{
								ciName = ownerCis[i].getQualifiedName();
								ci = ownerCis[i];
								break;
							}
						}
						
						//Here just add the interface if there is one.
						if (ci != null)
							addImplementation(ci);
					}
				}
			}
		}
		
		//Resolve the interfaces
		for (int i = 0; i < interfaces.length; i++)
		{
			interfaces[i] = resolveInterface(
				context, ciName, interfaces[i], false);
			
		}
		
		//After resolving the interfaces initializes the implementations
		initImplemetations();
		
		//Resolves the bindings
		for (int i = 0; i < bindings.length; i++)
		{
			//The bindings must be CIs, so the last parameter is true.
			//And it is added the name of the owner CI on it.
			bindings[i] = resolveInterface(
				context,
				ciName, 
				ciName != null 
					? new CClassNameType(ciName + "$" + bindings[i])
					: bindings[i], 
				true);
		}
	
		sourceClass.setInterfaces(getAllInterfaces());

	}
	
	/**
	 * This method is used to insert the collaboration interaface when the 
	 * class is nested and shall implement the collaboration interface that 
	 * is nested on the super collaboration interface. The class shall 
	 * implement it if it has the same name of a nested interface of the 
	 * collaboration interface of its owner. 
	 * 
	 * @param ownerCi
	 */
	protected void addImplementation(CReferenceType ownerCi)
	{
		FjTypeSystem typeSystem = new FjTypeSystem();
		CReferenceType[] ownerInners = ownerCi.getCClass().getInnerClasses();					
		for (int i = 0; i < interfaces.length; i++)
		{
			String interfaceIdent = interfaces[i].getQualifiedName();
			interfaceIdent = interfaceIdent.substring(
				interfaceIdent.lastIndexOf("$") + 1);
			
			CReferenceType ownerInnerType = typeSystem.declaresInner(
				ownerCi.getCClass(), interfaceIdent);
			if (ownerInnerType != null)
			{
				CReferenceType[] newInterfaces = 
					new CReferenceType[interfaces.length + 1];
					
				System.arraycopy(interfaces, 0, newInterfaces, 
					0, interfaces.length);
					
				newInterfaces[interfaces.length] = 
					new CClassNameType(ownerInnerType.getQualifiedName());
					
				interfaces = newInterfaces;
				
				//It will override the super virtual type.
				modifiers = modifiers | FJC_OVERRIDE;
				sourceClass.setModifiers(modifiers);
				return;
			}
		}
	}
	
	/**
	 * @return the Collaboration Interfaces which it binds.
	 */
	public CReferenceType[] getBindings()
	{
		return bindings;
	}

	/**
	 * @return the Collaboration Interfaces which it implements.
	 */
	public CReferenceType[] getImplementations()
	{
		return implementations == null 
			? new CReferenceType[0]
			: implementations;
	}
	/**
	 * Sets the implementations of the class
	 * @param implementations
	 */
	public void setImplementations(CReferenceType[] implementations)
	{
		this.implementations = implementations;
	}
	
	/**
	 * @return all interfaces including the CIs.
	 */
	public CReferenceType[] getAllInterfaces()
	{
		CReferenceType[] bindings = getBindings();
		CReferenceType[] implementations = getImplementations();
		
		CReferenceType[] allInterfaces = 
			new CReferenceType[
				interfaces.length 
				+ bindings.length 
				+ (implementations == null 
					? 0 
					: implementations.length)];
		
		System.arraycopy(interfaces, 0, allInterfaces, 0, interfaces.length);	
		System.arraycopy(bindings, 0, allInterfaces, interfaces.length, 
			bindings.length);
			
		if (implementations != null)
			System.arraycopy(implementations, 0, allInterfaces, 
				interfaces.length + bindings.length, 
				implementations.length);
			
		return allInterfaces;
	
	}
	
	/**
	 * @return An int with all modifiers allowed for classes.
	 */	
	protected int getAllowedModifiers()
	{
		return ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE | 
			ACC_ABSTRACT | ACC_STATIC | ACC_FINAL | ACC_STRICT;
	}

	/**
	 * Construct the source class.
	 * @param owner
	 * @param prefix
	 * @return
	 */
	protected CSourceClass constructSourceClass(CClass owner, String prefix)
	{
		return new CciSourceClass(
			owner, 
			getTokenReference(), 
			modifiers, 
			ident, 
			prefix + ident, 
			typeVariables, 
			isDeprecated(), 
			false, 
			this);
	}
	
	/**
	 * @return the super class of the class.
	 */
	public CReferenceType getSuperClass()
	{
		return superClass;
	}	
	
	/**
	 * Constructs a context.
	 */
	protected CClassContext constructContext(CContext context)
	{
		
		return new CciClassContext(context, context.getEnvironment(), 
			sourceClass, this);
	}

	/**
	 * This method was pulled up from FjClassDeclaration.
	 * @param type
	 */
	public void append(JTypeDeclaration type)
	{
		JTypeDeclaration[] newInners = new JTypeDeclaration[inners.length + 1];
		
		System.arraycopy(inners, 0, newInners, 0, inners.length);

		newInners[inners.length] = type;
		setInners(newInners);
	}

	/**
	 * This method was pulled up from FjClassDeclaration.
	 * @param type
	 * @author Walter Augusto Werner
	 */
	protected void setInners(JTypeDeclaration[] d)
	{
		inners = d;
	}

	/**
	 * Overriden for check if the class implements or binds all nested 
	 * interfaces from the CI.
	 * 
	 * @see at.dms.kjc.JTypeDeclaration#checkTypeBody(at.dms.kjc.CContext)
	 */
	public void checkTypeBody(CContext context) throws PositionedError
	{
		if (bindings.length > 0)
		{
			checkInnerTypeImplementation(context, 
				bindings[0].getCClass().getInnerClasses(), 
				bindings[0], 
				CaesarMessages.NESTED_TYPE_NOT_BOUND);
		}
		else if (implementations != null && implementations.length > 0)
		{
			checkInnerTypeImplementation(context, 
				implementations[0].getCClass().getInnerClasses(), 
				implementations[0], 
				CaesarMessages.NESTED_TYPE_NOT_IMPLEMENTED);
		}
		
		super.checkTypeBody(context);
	}

	/**
	 * This method checks if the class implements or binds all nested 
	 * types from the CI.
	 * 
	 * @param context The valid context.
	 * @param ciInnerClasses The nested classes from the CI.
	 * @param ci The CI that the class implements or binds.
	 * @param errorMessage The error message that must be shown if any of the 
	 * 	nested classes are not implemented or bound.
	 * @throws PositionedError
	 */
	protected void checkInnerTypeImplementation(
		CContext context,
		CReferenceType[] ciInnerClasses,
		CReferenceType ci,
		MessageDescription errorMessage)
		throws PositionedError
	{
		if (ciInnerClasses != null && ! FjConstants.isIfcImplName(ident))
		{
			for (int i = 0; i < ciInnerClasses.length; i++)
			{
				boolean found = false;
				for (int k = 0; k < inners.length; k++)
				{
					if (inners[k].getCClass().descendsFrom(ciInnerClasses[i].getCClass()))
					{
						found = true;
						break;
					}
				}
				check(context, found, errorMessage, 
					ciInnerClasses[i].getIdent(), ci.getIdent());
			}
		}
	}

	/* (non-Javadoc)
	 * @see at.dms.kjc.JTypeDeclaration#print()
	 */
	public void print()
	{
		System.out.print(CModifier.toString(modifiers));
		System.out.print("class ");
		super.print();
		System.out.print(" extends " + superClass );
		System.out.print(" implements ");
		for (int i = 0; i < interfaces.length; i++)
		{
			if (i > 0)
				System.out.print(", ");
				
			System.out.print(interfaces[i]);
		}
		System.out.print(" implementsCI ");
		for (int i = 0; implementations != null && i < implementations.length; i++)
		{
			if (i > 0)
				System.out.print(", ");
			
			System.out.print(implementations[i]);
		}
			
		System.out.print(" binds ");
		for (int i = 0; i < bindings.length; i++)
		{
			if (i > 0)
				System.out.print(", ");
	
			System.out.print(bindings[i]);
		}
		
		System.out.println();
	}


}
