package org.caesarj.compiler.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caesarj.classfile.Attribute;
import org.caesarj.classfile.AttributedClassInfo;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.ClassInfo;
import org.caesarj.classfile.ConstantPoolOverflowException;
import org.caesarj.classfile.InstructionOverflowException;
import org.caesarj.classfile.LocalVariableOverflowException;
import org.caesarj.compiler.aspectj.AttributeAdapter;
import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.joinpoint.PrivilegedAccessHandler;
import org.caesarj.compiler.optimize.BytecodeOptimizer;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;
import org.caesarj.util.Utils;

// FJKEEP
public class CCjSourceClass extends CSourceClass
{

	protected CaesarPointcut perClause;
	protected CaesarDeclare[] declares = new CaesarDeclare[0];

	/**handles the access to non visible members if this class is privileged*/
	protected PrivilegedAccessHandler privilegedAccessHandler;

	protected List resolvedPointcuts = new ArrayList();
	
	public CCjSourceClass(
		CClass owner,
		TokenReference where,
		int modifiers,
		String ident,
		String qualifiedName,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JTypeDeclaration decl)
	{
		this(
			owner,
			where,
			modifiers,
			ident,
			qualifiedName,
			typeVariables,
			deprecated,
			synthetic,
			decl,
			null);
	}

	public CCjSourceClass(
		CClass owner,
		TokenReference where,
		int modifiers,
		String ident,
		String qualifiedName,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JTypeDeclaration decl,
		CaesarPointcut perClause)
	{
		super(
			owner,
			where,
			modifiers,
			ident,
			qualifiedName,
			typeVariables,
			deprecated,
			synthetic,
			decl);

		this.perClause = perClause;

		//privileged classes need an priviledge handler
		if (isPrivileged())
		{
			privilegedAccessHandler = new PrivilegedAccessHandler();
			privilegedAccessHandler.setAspect(this);
		}
	}

	public CClass lookupClass(CClass caller, String name)
		throws UnpositionedError
	{
		CReferenceType[] interfaces = getInterfaces();
		CReferenceType[] innerClasses = getInnerClasses();

		// andreas start copy from CClass.lookupClass( ... )

		CClass[] candidates =
			new CClass[(interfaces == null) ? 1 : interfaces.length + 1];
		int length = 0;

		if (innerClasses != null && (name.indexOf('/') == -1))
		{
			for (int i = 0; i < innerClasses.length; i++)
			{
				CClass innerClass = innerClasses[i].getCClass();

				if (innerClass.getIdent().equals(name))
					if (innerClass.isAccessible(caller))
						return innerClass;
			}
		}

		if (interfaces != null)
		{
			for (int i = 0; i < interfaces.length; i++)
			{
				// Walter: check if the interface does not descend from this
				// Walter: it is for check circularity in the hierarchy, 
				// Walter: and prevent stack overflows
				if (!interfaces[i].getCClass().descendsFrom(this))
				{
					CClass superFound =
						interfaces[i].getCClass().lookupClass(caller, name);

					if (superFound != null)
						candidates[length++] = superFound;
				}
			}
		}

		if (superClassType != null)
		{
			// Walter: check if the superClass does not descend from this
			// Walter: it is for check circularity in the hierarchy, 
			// Walter: and prevent stack overflows
			if (!superClassType.getCClass().descendsFrom(this))
			{
				CClass superFound =
					superClassType.getCClass().lookupClass(caller, name);

				if (superFound != null)
					candidates[length++] = superFound;
			}
		}
		if (length == 0)
		{
			return null;
		}
		else if (length == 1)
		{
			return candidates[0];
		}
		else
		{
			// found the same class?

			for (; length > 1; length--)
				if (candidates[0] != candidates[length - 1])
					break;

			if (length == 1)
				return candidates[0];
			else
				/* JLS 8.5 A class may inherit two or more type declarations with 
				   the same name, either from two interfaces or from its superclass 
				   and an interface. A compile-time error occurs on any attempt to 
				   refer to any ambiguously inherited class or interface by its simple 
				   name. */
				throw new UnpositionedError(KjcMessages.CLASS_AMBIGUOUS,
				// andreas start: throw candidates up, too
				//name);
				name, candidates);
			// andreas end

		}

		// andreas end
	}


	/**
	 * Returns the resolvedPointcuts.
	 * @return List (of ResolvedPointcut)
	 */
	public List getResolvedPointcuts()
	{
		List ret = new ArrayList();
		for (Iterator it = resolvedPointcuts.iterator(); it.hasNext();)
		{
			CaesarMember	resolvedPointcutDef = ((CaesarMember)it.next()); 		
			ret.add( resolvedPointcutDef.wrappee() );
		}

		if (getSuperClass() != null
			&& CModifier.contains(
				getSuperClass().getModifiers(),
				ACC_CROSSCUTTING))
		{

			ret.addAll(
				((CCjSourceClass) getSuperClass()).getResolvedPointcuts());
		}

		return ret;
	}

	public boolean isPrivileged()
	{
		return CModifier.contains(getModifiers(), ACC_PRIVILEGED);
	}

	public void setDeclares(CaesarDeclare[] declares)
	{
		this.declares = declares;
	}

	public CaesarDeclare[] getDeclares()
	{
		return declares;
	}

	/**
	 * Sets the perClause.
	 * @param perClause The perClause to set
	 */
	public void setPerClause(CaesarPointcut perClause)
	{
		this.perClause = perClause;
	}

	/**
	 * Returns the privilegedAccessHandler.
	 * 
	 * @return PrivilegedAccessHandler
	 */
	public PrivilegedAccessHandler getPrivilegedAccessHandler()
	{
		return privilegedAccessHandler;
	}

	/**
	 * Lookup of the specified field.
	 * Consider privileged access.
	 */
	public CField lookupField(CClass caller, CClass primary, String ident)
		throws UnpositionedError
	{

		boolean privilegedAccess =
			caller instanceof CCjSourceClass
				&& ((CCjSourceClass) caller).isPrivileged();

		CField field = getField(ident);
		if (field != null)
		{

			if (field.isAccessible(primary, caller))
			{

				return field;

			}
			else if (privilegedAccess)
			{

				CField unaccessibleField =
					((CCjSourceClass) caller)
						.getPrivilegedAccessHandler()
						.getPrivilegedAccessField(
						field);
				return unaccessibleField;

			}
			else
			{

				return lookupSuperField(caller, primary, ident);
			}

		}
		else
		{

			return lookupSuperField(caller, primary, ident);

		}

	}

	/**
	 * Overriden to consider privileged access.
	 */
	public CMethod lookupMethod(
		CTypeContext context,
		CClass caller,
		CType primary,
		String ident,
		CType[] actuals,
		CReferenceType[] substitution)
		throws UnpositionedError
	{

		CMethod[] applicable =
			getApplicableMethods(context, ident, actuals, substitution);
		CMethod[] candidates = new CMethod[applicable.length];
		int length = 0;

		// find the maximally specific methods
		_all_methods_ : for (int i = 0; i < applicable.length; i++)
		{
			int current = 0;

			if (!applicable[i].isAccessible(primary, caller))
			{

				//consider privileged access
				CCjSourceClass sourceClass = (CCjSourceClass) caller;
				if (sourceClass.isPrivileged())
				{
					return sourceClass
						.getPrivilegedAccessHandler()
						.getPrivilegedAccessMethod(
						applicable[i]);
				}
				else
				{
					continue _all_methods_;
				}
			}

			for (;;)
			{
				if (current == length)
				{
					// no other method is more specific: add it
					candidates[length++] = applicable[i];
					continue _all_methods_;
				}
				else if (
					candidates[current].isMoreSpecificThan(
						context,
						applicable[i],
						substitution))
				{
					// other method is more specific: skip it
					continue _all_methods_;
				}
				else if (
					applicable[i].isMoreSpecificThan(
						context,
						candidates[current],
						substitution))
				{
					// this method is more specific: remove the other
					if (current < length - 1)
					{
						candidates[current] = candidates[length - 1];
					}
					length -= 1;
				}
				else
				{
					// none is more specific than the other: check next candidate
					current += 1;
				}
			}
		}

		if (length == 0)
		{
			// no applicable method
			return null;
		}
		else if (length == 1)
		{
			// exactly one most specific method
			return candidates[0];
		}
		else
		{
			// two or more methods are maximally specific

			// do all the maximally specific methods have the same signature ?
			for (int i = 1; i < length; i++)
			{
				if (!candidates[i]
					.hasSameSignature(candidates[0], substitution))
				{
					// more than one most specific method with different signatures: ambiguous
					throw new UnpositionedError(
						KjcMessages.METHOD_INVOCATION_AMBIGUOUS,
						new Object[] {
							buildCallSignature(ident, actuals),
							candidates[0],
							candidates[i] });
				}
			}

			// now, all maximally specific methods have the same signature
			// is there a non-abstract method (there is at most one) ?
			for (int i = 0; i < length; i++)
			{
				if (!candidates[i].isAbstract())
				{
					// the non-abstract method is the most specific one
					return candidates[i];
				}
			}

			// now, all maximally specific methods have the same signature and are abstract
			// !!! FIXME graf 010128
			// "Otherwise, all the maximally specific methods are necessarily declared abstract.
			// The most specific method is chosen arbitrarily among the maximally specific methods.
			// However, the most specific method is considered to throw a checked exception
			// if and only if that exception OR A SUPERCLASS OF THAT EXCEPTION [Java Spec Report]
			// is declared in the throws clause of each of the maximally specific methods."
			// !!! FIXME graf 010128
			return candidates[0];
		}

	}

    /**
     * Generates the AspectJ specific class attributes.
     */
    protected Attribute[] genExtraAttributes()
    {
        List attributeList = new ArrayList();

        if (perClause != null)
        {
            attributeList.add( AttributeAdapter.createAspect(perClause) );
        }
        else if (declares.length > 0)
        {
            attributeList.add(
                AttributeAdapter.createAspect(CaesarPointcut.createPerSingleton())
            );
        }

        for (int i = 0; i < declares.length; i++)
        {
            attributeList.add(
                AttributeAdapter.createDeclareAttribute( declares[i] ) );
        }

        if (isPrivileged())
        {
            attributeList.add(
                AttributeAdapter.createPrivilegedAttribute(
                        privilegedAccessHandler.getAccessedMembers() )
                    );
        }

        Iterator iterator = resolvedPointcuts.iterator();
        while (iterator.hasNext())
        {
            CaesarMember rpd =  (CaesarMember)iterator.next();

            attributeList.add(
                AttributeAdapter.createPointcutDeclarationAttribute(rpd)
                );
        }

        return (Attribute[]) attributeList.toArray(new Attribute[0]);
    }

    
    /**
     * Overriden to create addition attribute in ClassInfo.
     */
    public ClassInfo genClassInfo(
        BytecodeOptimizer optimizer,
        String destination,
        TypeFactory factory)
        throws IOException, ClassFileFormatException, PositionedError
    {
        decl = null; // garbage
        String[] classPath =
            Utils.splitQualifiedName(getSourceFile(), File.separatorChar);

        try
        {
            AttributedClassInfo classInfo =
                new AttributedClassInfo(
                    (short) (getModifiers() & (~ACC_STATIC)),
                    getQualifiedName(),
                    getSuperClass() == null
                        ? null
                        : getSuperClass().getQualifiedName(),
                    genInterfaces(),
                    genFields(factory),
                    genMethods(optimizer, factory),
                    genInnerClasses(),
                    classPath[1],
                    getSuperClass() == null ? null : getGenericSignature(),
                    isDeprecated(),
                    isSynthetic(),
                    genExtraAttributes());
            
            return classInfo;
        }
        catch (ConstantPoolOverflowException e)
        {
            throw new PositionedError(
                new TokenReference(classPath[1], 0),
                KjcMessages.CONSTANTPOOL_OVERFLOW);
        }
        catch (InstructionOverflowException e)
        {
            throw new PositionedError(
                new TokenReference(classPath[1], 0),
                KjcMessages.INSTRUCTION_OVERFLOW,
                e.getMethod());
        }
        catch (LocalVariableOverflowException e)
        {
            throw new PositionedError(
                new TokenReference(classPath[1], 0),
                KjcMessages.LOCAL_VARIABLE_OVERFLOW,
                e.getMethod());
        }
        catch (ClassFileFormatException e)
        {
            System.err.println(
                "GenCode failure in source class: " + getQualifiedName());
            throw e;
        }
    }

	public void addResolvedPointcut(CaesarMember rpd)
	{
		resolvedPointcuts.add(rpd);
	}

}
