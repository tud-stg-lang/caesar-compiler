/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CClass.java,v 1.33 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.export;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CClassOrInterfaceType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.runtime.AdditionalCaesarTypeInformation;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.SimpleStringBuffer;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents the exported members of a class (inner classes, 
 * methods and fields)
 */
public abstract class CClass extends CMember
{

	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Constructs a class export from file
	 */
	public CClass(
		CClass owner,
		String sourceFile,
		int modifiers,
		String ident,
		String qualifiedName,
		CReferenceType superClass,
		boolean deprecated,
		boolean synthetic
		)
	{
		super(owner, modifiers, ident, deprecated, synthetic);

		this.sourceFile = sourceFile;
		this.qualifiedName = qualifiedName.intern();
		this.superClassType = superClass;
		this.qualifiedAndAnonymous = false;

		int cursor = qualifiedName.lastIndexOf('/');

		this.packageName =
			cursor > 0 ? qualifiedName.substring(0, cursor).intern() : "";
			
		if(owner != null)
		    depth = owner.getDepth() + 1;
	}

	/**
	 * Ends the definition of this class
	 */
	public void close(
		CReferenceType[] interfaces,
		Hashtable fields,
		CMethod[] methods)
	{
		this.interfaces = interfaces;
		this.fields = fields;
		this.methods = methods;
		verify(interfaces != null);
	}

	// ----------------------------------------------------------------------
	// ACCESSORS
	// ----------------------------------------------------------------------

	/**
	 * @return	the interface
	 */
	public CClass getCClass()
	{
		return this;
	}

    public boolean isObjectClass() {
        return getQualifiedName().equals(JAV_OBJECT);
    }
    
    public boolean isWrapper() {
        try {
            return 
            	lookupField(this, this, CaesarConstants.WRAPPER_WRAPPEE_FIELD) != null;
        }
        catch(UnpositionedError e) {
            return false;
        }
    }

    public boolean isMixin() {
        return CModifier.contains(getModifiers(), ACC_MIXIN);
    }
    
    public CClass getMixinInterface() {
        if(!isMixin() || interfaces.length != 1) {
            throw new InconsistencyException();
        }
        return interfaces[0].getCClass();
    }

	/**
	 * Checks whether this type is nested.
	 *
	 * JLS 8 (introduction), JLS 9 (introduction) :
	 * A nested type (class or interface) is any type whose declaration
	 * occurs within the body of another class or interface. A top level
	 * type is a type that is not a nested class.
	 *
	 * @return	true iff this type is nested
	 */
	public boolean isNested()
	{
		return getOwner() != null;
	}

	/**
	 * @return	true if this member is abstract
	 */
	public boolean isAbstract()
	{
		return CModifier.contains(getModifiers(), ACC_ABSTRACT);
	}

	/**
	 * @return true if this class is anonymous
	 */
	public boolean isAnonymous()
	{
		return getIdent().length() == 0 && isNested();
	}
	/**
	 * @return true if this class is local (JLS 14.3)
	 */
	public boolean isLocal()
	{
		return localClass;
	}
	public void setLocal(boolean localClass)
	{
		this.localClass = localClass;
	}
	public boolean isCrosscutting() {
		return (!CModifier.contains(getModifiers(),ACC_DEPLOYED)) &&
		  CModifier.contains(getModifiers(),ACC_CROSSCUTTING);
	}
	

	/**
	 * @return true if this class is defines the condition methods of an interface
	 */
	public boolean isAssertionClass()
	{
		throw new InconsistencyException("No Assertion class");
	}

	public CClass getAssertionStatusClass(CClassContext context)
		throws PositionedError
	{
		if (assertionStatusClass == null)
		{
			CjClassDeclaration decl =
				new CjClassDeclaration(
					TokenReference.NO_REF,
					0,
					getQualifiedName() + "$" + context.getNextSyntheticIndex(),
					context.getTypeFactory().createReferenceType(
						TypeFactory.RFT_OBJECT),
					CReferenceType.EMPTY,
					JFieldDeclaration.EMPTY,
					new JMethodDeclaration[0],
					new JTypeDeclaration[0],
					new JPhylum[0],
					null,
					null);

			decl.generateInterface(
				context.getEnvironment().getClassReader(),
				null,
				"");
			decl.join(context.getCompilationUnitContext());
			decl.checkInterface(context.getCompilationUnitContext());
			decl.checkInitializers(context);
			decl.checkTypeBody(context);

			assertionStatusClass = decl.getCClass();
		}

		return assertionStatusClass;
	}

	/**
	 * Returns true if the classs can declare static members, 
	 * which are not compile-time constatn fields.
	 */
	public boolean canDeclareStatic()
	{
		return !isAnonymous() && !isLocal() && (!isNested() || isStatic());
	}

	/**
	 * @return true if this class is has an outer this
	 */
	public boolean hasOuterThis()
	{
		return hasOuterThis;
	}

	/**
	 * Sets hasOuterThis
	 */
	public void setHasOuterThis(boolean hasOuterThis)
	{
		this.hasOuterThis = hasOuterThis;
	}

    /**
     * @return true if this class is an interface
     */
    public boolean isInterface()
    {
        return CModifier.contains(getModifiers(), ACC_INTERFACE);
    }

    /**
     * @return true if this class is an interface
     */
    public boolean isMixinInterface()
    {
        return CModifier.contains(getModifiers(), ACC_MIXIN_INTERFACE);
    }

	/**
	 * @return true if this class is a class
	 */
	public boolean isClass()
	{
		return !CModifier.contains(getModifiers(), ACC_INTERFACE);
	}

	/**
	 * @return the full name of this class
	 */
	public String getQualifiedName()
	{
		return qualifiedName;
	}

	/**
	 * @return the name of the package of the package this class belongs to
	 */
	public String getPackage()
	{
		return packageName;
	}

	/**
	 * Returns the super class of this class
	 */
	public CClass getSuperClass()
	{
		return superClassType == null ? null : superClassType.getCClass();
	}
	/**
	 * Returns the super class of this class
	 */
	public CReferenceType getSuperType()
	{
		return superClassType;
	}

	/**
	 * Sets the super class of this class
	 */
	public void setSuperClass(CReferenceType superClass)
	{
		this.superClassType = superClass;
	}

	/**
	 * Returns the type of this class.
	 */
	public CReferenceType getAbstractType()
	{
		if (type == null)
		{
			type =
				new CClassOrInterfaceType(
					this);
		}
		return type;
	}

	/**
	 * Returns the source file of this class.
	 */
	public String getSourceFile()
	{
		return sourceFile;
	}

	/**
	 * Returns the field with the specified name.
	 *
	 * @param	ident		the name of the field
	 */
	public CField getField(String ident)
	{
		return (CField) fields.get(ident);
	}

	/**
	 * Returns the number of fields.
	 */
	public int getFieldCount()
	{
		return fields.size();
	}

	/**
	 * Returns an array containing the fields defined by this class.
	 */
	public CField[] getFields()
	{
		CField[] result;

		result = new CField[fields.size()];
		for (Enumeration enum = fields.elements(); enum.hasMoreElements();)
		{
			CSourceField field = (CSourceField) enum.nextElement();

			result[field.getPosition()] = field;
		}
		return result;
	}

	/**
	 * Returns the interfaces defined by this class.
	 */
	public CReferenceType[] getInterfaces()
	{
		return interfaces;
	}
	/**
	 * Returns the interfaces defined by this class.
	 */
	public void setInterfaces(CReferenceType[] interfaces)
	{
		this.interfaces = interfaces;
	}

	/**
	 * Returns the methods defined by this class.
	 */
	public CMethod[] getMethods()
	{
		return methods;
	}

	/**
	 * @return the InnerClasses
	 */
	public CReferenceType[] getInnerClasses()
	{
		return innerClasses;
	}

	/**
	 * End of first pass, we need innerclassesinterfaces
	 */
	public void setInnerClasses(CReferenceType[] inners)
	{
		this.innerClasses = inners;
	}

	/**
	 * descendsFrom
	 * @param	from	an other CClass
	 * @return	true if this class inherit from "from" or equals "from"
		 * 
		 * Walter: I've changed the method to prevent stack overflows. Now all 
		 * classes (the paramenter "from") that pass by the method is inserted into 
		 * the the array history. So, this array is used for check if the class
		 * has already been tested by the method. If it occurs, it returns false. 
	 */
	public boolean descendsFrom(CClass from)
	{
		return descendsFrom(from, new ArrayList());
	}
	/**
	 * This method is part of the method decendsFrom(CClass)
	 * 
	 * @param from the class that it is looking for.
	 * @param history classes that have already been checked
	 * 
	 * @author Walter Augusto Werner
	 */
	protected boolean descendsFrom(CClass from, ArrayList history)
	{
		//If I'm from then true
		if (from == this)
			return true;

		//If it has already been tried then false
		if (history.contains(this))
			return false;

		history.add(this);
		//Try for interfaces
		CReferenceType[] interfaces = getInterfaces();
		if (interfaces != null)
			for (int i = 0; i < interfaces.length; i++)
				if (interfaces[i].getCClass().descendsFrom(from, history))
					return true;

		//I arrived here, and I don't have parent
		if (superClassType == null)
			return false;

		//Does my parent descend from "from"?
		else if (getSuperClass().descendsFrom(from, history))
			return true;

		//No I'm not descendent
		return false;
	}

	

	/**
	 * Returns true iff this class is defined inside the specified class
	 * @param	outer		another class
	 */
	public boolean isDefinedInside(CClass outer)
	{
		if (this == outer)
		{
			return true;
		}
		else if (getOwner() == null)
		{
			return false;
		}
		else
		{
			return getOwner().isDefinedInside(outer);
		}
	}

	public boolean dependsOn(CClass from)
	{
		// JLS 8.1.3
		// A class C directly depends on a type T if T is mentioned 
		// in the extends or implements clause of C 
		if (this == from)
		{
			return true;
		}

		for (int i = 0; i < interfaces.length; i++)
		{
			if (interfaces[i].getCClass() == from)
			{
				return true;
			}
			// C directly depends on a class D that depends 
			// on T (using this definition recursively). 
			if (interfaces[i].getCClass().dependsOn(from))
			{
				return true;
			}
		}

		if (isDefinedInside(from))
		{
			return true;
		}

		if (isNested() && getOwner().dependsOn(from))
		{
			return true;
		}
		if (!isInterface())
		{
			if (superClassType == null)
			{
				return false; //object depends on nothing
			}

			// C directly depends on T. 
			if (superClassType.getCClass() == from)
			{
				return true;
			}
			// C directly depends on an interface I that depends (?9.1.2) on T. 
			if (superClassType.getCClass().dependsOn(from))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a string representation of this class.
	 */
	public String toString()
	{
		return qualifiedName;
	}

	public void setQualifiedAndAnonymous(boolean qualifiedAndAnonymous)
	{
		this.qualifiedAndAnonymous = qualifiedAndAnonymous;
	}
	public boolean isQualifiedAndAnonymous()
	{
		return qualifiedAndAnonymous;
	}
	// ----------------------------------------------------------------------
	// LOOKUP
	// ----------------------------------------------------------------------

	/**
	 * This can be used to see if a given class name is visible
	 * inside of this file.  This includes globally-qualified class names that
	 * include their package and simple names that are visible thanks to some
	 * previous import statement or visible because they are in this file.
	 * If one is found, that entry is returned, otherwise null is returned.
	 * @param	caller		the class of the caller
	 * @param	name		a TypeName (6.5.2)
	 */
	public CClass lookupClass(CClass caller, String name)
		throws UnpositionedError
	{	    
	    
	    // IVICA: changed lookup for mixin classes
	    // if we are in a mixin class and the mixin interface has been set
	    // (this is the case after adjustSuperTypes pass is finished)
	    // then looking types is only done in the direct mixin interface CClass,
	    // since all (explicit and implicit) types are contained
	    if(this.isMixin() && this.getInterfaces().length > 0) {	        
	        CReferenceType[] inners =
	            this.getInterfaces()[0].getCClass().getInnerClasses();
	            
	        for (int i = 0; i < inners.length; i++) {
                if(inners[i].getCClass().getIdent().equals(name))
                    return inners[i].getCClass();
            }
	    }
	    
	    //System.out.println("~~~ this="+this.getQualifiedName()+", caller="+caller.getQualifiedName()+", name="+name);
	    
		CClass[] candidates =
			new CClass[(interfaces == null) ? 1 : interfaces.length + 1];
		int length = 0;

		if (innerClasses != null && (name.indexOf('/') == -1))
		{
			for (int i = 0; i < innerClasses.length; i++)
			{
				CClass innerClass = innerClasses[i].getCClass();

				if (innerClass.getIdent().equals(name))
				{
					if (innerClass.isAccessible(caller))
					{
						candidates[length++] = innerClass;
					}
					break;
				}
			}
		}

		if (length != 0)
		{
			/* JLS 8.5 If the class declares a member type with a certain name, 
			   then the declaration of that type is said to hide any and all 
			   accessible declarations of member types with the same name in 
			   superclasses and superinterfaces of the class. */
			return candidates[0];
		}
		else
		{
			if (interfaces != null)
			{
				for (int i = 0; i < interfaces.length; i++)
				{
					// check if the interface does not descend from this
					// it is for check circularity in the hierarchy, 
					// and prevent stack overflows
					// inserted by Walter
					if (!interfaces[i].getCClass().descendsFrom(this))
					{
						CClass superFound =
							interfaces[i].getCClass().lookupClass(caller, name);

						if (superFound != null)
						{
							candidates[length++] = superFound;
						}
					}
				}
			}
			if (superClassType != null)
			{
				// check if the superClass does not descend from this
				// it is for check circularity in the hierarchy, 
				// and prevent stack overflows
				// inserted by Walter
				if (!superClassType.getCClass().descendsFrom(this))
				{
					CClass superFound =
						superClassType.getCClass().lookupClass(caller, name);

					if (superFound != null)
					{
						candidates[length++] = superFound;
					}
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
				{
				    // IVICA: added && !(candidates[0].isMixinInterface() && candidates[length - 1].isMixinInterface())
				    // if we have found more than one mixin ifc, then it is ok to choose the first one, 
				    // which is the most specific one 
					if(
					    candidates[0] != candidates[length - 1] 
                        && !(candidates[0].isMixinInterface() && candidates[length - 1].isMixinInterface())
                    ) {
						break;
					}
				}
				
				if (length == 1)
				{
					return candidates[0];
				}
				else
				{
					/* JLS 8.5 A class may inherit two or more type declarations with 
					   the same name, either from two interfaces or from its superclass 
					   and an interface. A compile-time error occurs on any attempt to 
					   refer to any ambiguously inherited class or interface by its simple 
					   name. */
					throw new UnpositionedError(
						KjcMessages.CLASS_AMBIGUOUS,
						//Walter start
						//name);
						name, candidates);
						//Walter end
				}
			}
		}
	}

	/**
	 * Searches the class or interface to locate declarations of fields that are
	 * accessible.
	 * 
	 * @param	caller		the class of the caller
	 * @param     primary         the class of the primary expression (can be null)
	 * @param	ident		the simple name of the field
	 * @return	the field definition
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CField lookupField(CClass caller, CClass primary, String ident)
		throws UnpositionedError
	{
		CField field = getField(ident);

		if (field != null && field.isAccessible(primary, caller))
		{
			return field;
		}
		else
		{
			return lookupSuperField(caller, primary, ident);
		}
	}

	/**
	 * Searches the class or interface to locate declarations of fields that are
	 * accessible.
	 * 
	 * @param	caller		the class of the caller
	 * @param	ident		the simple name of the field
	 * @return	the field definition
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CField lookupSuperField(CClass caller, CClass primary, String ident)
		throws UnpositionedError
	{
		CField field;

		if (superClassType == null)
		{
			// java.lang.Object
			field = null;
		}
		else
		{
			field = getSuperClass().lookupField(caller, primary, ident);
		}

		for (int i = 0; i < interfaces.length; i++)
		{
			CField newField =
				interfaces[i].getCClass().lookupField(caller, primary, ident);

			if ((field != null)
				&& (newField != null)
				&& (field.getOwner() != newField.getOwner()))
			{
				throw new UnpositionedError(KjcMessages.FIELD_AMBIGUOUS, ident);
			}

			if (newField != null)
			{
				field = newField;
			}
		}

		return field;
	}

	/**
	 * JLS 15.12.2 :
	 * Searches the class or interface to locate method declarations that are
	 * both applicable and accessible, that is, declarations that can be correctly
	 * invoked on the given arguments. There may be more than one such method
	 * declaration, in which case the most specific one is chosen.
	 *
	 * @param	caller		the class of the caller
	 * @param	ident		method invocation name
	 * @param	actuals		method invocation arguments
	 * @return	the method to apply
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CMethod lookupMethod(
		CTypeContext context,
		CClass caller,
		CType primary,
		String ident,
		CType[] actuals)
		throws UnpositionedError
	{
		CMethod[] applicable =
			getApplicableMethods(context, ident, actuals);
		CMethod[] candidates = new CMethod[applicable.length];
		int length = 0;

		// find the maximally specific methods
		_all_methods_ : for (int i = 0; i < applicable.length; i++)
		{
			int current = 0;

			if (!applicable[i].isAccessible(primary, caller))
			{
				continue _all_methods_;
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
						applicable[i]))
				{
					// other method is more specific: skip it
					continue _all_methods_;
				}
				else if (
					applicable[i].isMoreSpecificThan(
						context,
						candidates[current]))
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
					.hasSameSignature(candidates[0]))
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

	/*
	 * Returns a string representation of the call.
	 */
	protected static String buildCallSignature(String ident, CType[] actuals)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append(ident);
		buffer.append("(");
		for (int i = 0; i < actuals.length; i++)
		{
			if (i != 0)
			{
				buffer.append(", ");
			}
			buffer.append(actuals[i]);
		}
		buffer.append(")");

		return buffer.toString();
	}

	/**
	 * Searches the superclass and superinterfaces to locate method declarations
	 * that are applicable. No check for accessibility is done here.
	 *
	 * @param	ident		method invocation name
	 * @param	actuals		method invocation arguments
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	//   public CMethod[] lookupSuperMethod(String ident, CType[] actuals)
	//     throws UnpositionedError
	//   {
	//     ArrayList	container = new ArrayList();

	//     if (superClassType != null) {
	//       // java.lang.object
	//       getSuperClass().collectApplicableMethods(container, ident, actuals);
	//     }

	//     for (int i = 0; i < interfaces.length; i++) {
	//       interfaces[i].getCClass().collectApplicableMethods(container, ident, actuals);
	//     }

	//     return (CMethod[])Utils.toArray(container, CMethod.class);
	//   }

	public void checkOverriding(
		CClassContext context,
		CMethod method,
		ArrayList bridges)
		throws UnpositionedError
	{
		String ident = method.getIdent();

		if (ident == JAV_CONSTRUCTOR
			|| ident == JAV_INIT
			|| ident == JAV_STATIC_INIT)
		{
			return; // these methods are not overridden
		}

		if (superClassType != null)
		{
			getSuperClass().checkOverriding(
				context,
				method,
				bridges);
		}

		for (int i = 0; i < interfaces.length; i++)
		{
			// interface method require bridges, they are abstract and must be overridden
			// the correct overwritting should be checked.
			interfaces[i].getCClass().checkOverridingInThis(
				context,
				method,
				bridges);
		}
	}

	protected void checkOverridingInThis(
		CClassContext context,
		CMethod ovMethod,
		ArrayList bridges)
		throws UnpositionedError
	{
		for (int i = 0; i < methods.length; i++)
		{
			CMethod method = methods[i];
			boolean direct; //direct overriding?
			boolean indirect; //indirct overriding?

			if (method.isPrivate())
			{
				continue;
			}
			if (ovMethod.getIdent() != method.getIdent())
			{
				continue;
			}
			direct = ovMethod.hasSameSignature(context, method);
			indirect = ovMethod.hasSameSignature(method);
			if (!direct && !indirect)
			{
				continue;
			}

			if (direct && !indirect)
			{
				throw new UnpositionedError(
					KjcMessages.GENERIC_OVER_RULE2,
					ovMethod,
					method);
			}
			else
			{
				ovMethod.checkOverriding(context, method);

				if (direct)
				{
					CType rt1 = ovMethod.getReturnType().getErasure(context);
					CType rt2 = method.getReturnType().getErasure(context);

					if (rt2.isClassType()
						&& rt1.getCClass() != rt2.getCClass()
						&& !method.isPrivate())
					{
						bridges.add(method); //construct bridge
					}
				}
				else if (indirect)
				{
					if (!method.isPrivate())
					{
						bridges.add(method); //construct bridge
					}
				}
			}
		}
	}

	/**
	 * Returns the abstract methods of this class or interface.
	 *
	 * JLS 8.1.1.1 :
	 * A class C has abstract methods if any of the following is true :
	 * -	C explicitly contains a declaration of an abstract method.
	 * -	Any of C's superclasses declares an abstract method that has
	 *	not been implemented in C or any of its superclasses.
	 * -	A direct superinterface of C declares or inherits a method
	 *	(which is therefore necessarily abstract) and C neither
	 *	declares nor inherits a method that implements it.
	 */
	public CMethod[] getAbstractMethods(CTypeContext context, boolean test)
		throws UnpositionedError
	{
		ArrayList container = new ArrayList();

		// C explicitly contains a declaration of an abstract method.
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].isAbstract())
			{
				container.add(methods[i]);
			}
		}

		// Any of C's superclasses declares an abstract method that has
		// not been implemented in C or any of its superclasses.
		if (superClassType != null)
		{
			CMethod[] inherited;

			inherited = getSuperClass().getAbstractMethods(context, false);
			for (int i = 0; i < inherited.length; i++)
			{
				if (test)
				{
					if (!inherited[i].isAccessible(this) && (!isAbstract()))
					{
						throw new UnpositionedError(
							KjcMessages.CLASS_CAN_NOT_IMPLEMENT,
							new Object[] {
								this,
								inherited[i],
								inherited[i].getOwner()});
					}
				}

				if (getImplementingMethod(context, inherited[i], this, true)
					== null)
				{
					container.add(inherited[i]);
				}
			}
		}

		// A direct superinterface of C declares or inherits a method
		// (which is therefore necessarily abstract) and C neither
		// declares nor inherits a method that implements it.
		ArrayList interfaceMethods = new ArrayList();

		for (int i = 0; i < interfaces.length; i++)
		{
			CMethod[] inherited;

			inherited =
				interfaces[i].getCClass().getAbstractMethods(context, false);
			for (int j = 0; j < inherited.length; j++)
			{
				CMethod implMethod =
					getImplementingMethod(context, inherited[j], this, false);

				if (implMethod == null)
				{
					container.add(inherited[j]);
				}

				if (test)
				{
					if (implMethod != null)
					{
						// FIX 020206 lackner 
						// parameter null is substitution in the generic case!!
						implMethod.checkOverriding(context, inherited[j]);
					}
					else
					{
						// check it against other interface methods
						Iterator en = interfaceMethods.iterator();

						while (en.hasNext())
						{
							CMethod iMethod = (CMethod) en.next();

							if (iMethod.getIdent() == inherited[j].getIdent()
								&& iMethod.hasSameSignature(
									context,
									inherited[j]))
							{
								iMethod.checkInheritence(
									context,
									inherited[j],
									null);
							}
						}

					}
					interfaceMethods.add(inherited[j]);
				}
			}
		}

		return (CMethod[]) container.toArray(new CMethod[container.size()]);
	}

	public void testInheritMethods(CTypeContext context)
		throws UnpositionedError
	{
		ArrayList interfaceMethods = new ArrayList();

		for (int i = 0; i < interfaces.length; i++)
		{
			CMethod[] inherited;

			inherited =
				interfaces[i].getCClass().getAbstractMethods(context, false);
			for (int j = 0; j < inherited.length; j++)
			{
				// check it against other interface methods
				Iterator en = interfaceMethods.iterator();

				while (en.hasNext())
				{
					CMethod iMethod = (CMethod) en.next();

					if (iMethod.getIdent() == inherited[j].getIdent()
						&& iMethod.hasSameSignature(context, inherited[j]))
					{
						iMethod.checkInheritence(context, inherited[j], null);
					}
				}
				interfaceMethods.add(inherited[j]);
			}
		}
	}

	/**
	 * Returns the non-abstract method that implements the
	 * specified method, or null if the method is not implemented.
	 *
	 * @param	pattern		the method specification
	 * @param	localClassOnly	do not search superclasses ?
	 */
	protected CMethod getImplementingMethod(
		CTypeContext context,
		CMethod pattern,
		CClass caller,
		boolean localClassOnly)
		throws UnpositionedError
	{
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].getIdent() == pattern.getIdent()
				&& methods[i].hasSameSignature(context, pattern))
			{
				return (
					methods[i].isAbstract()
						|| !methods[i].isAccessible(caller))
					? null
					: methods[i];
			}
		}

		if (localClassOnly || superClassType == null)
		{
			return null;
		}
		else
		{
			return getSuperClass().getImplementingMethod(
				context,
				pattern,
				caller,
				false);
		}
	}

	/**
	 * Returns all applicable methods (JLS 15.12.2.1).
	 * @param	ident		method invocation name
	 * @param	actuals		method invocation arguments
	 */
	public CMethod[] getApplicableMethods(
		CTypeContext context,
		String ident,
		CType[] actuals)
	{
		ArrayList container = new ArrayList();

		collectApplicableMethods(
			context,
			container,
			ident,
			actuals);
		return (CMethod[]) container.toArray(new CMethod[container.size()]);
		//Utils.toArray(container, CMethod.class);
	}

	/**
	 * Adds all applicable methods (JLS 15.12.2.1) to the specified container.
	 * @param	container	the container for the methods
	 * @param	ident		method invocation name
	 * @param	actuals		method invocation arguments
	 */
	public void collectApplicableMethods(
		CTypeContext context,
		ArrayList container,
		String ident,
		CType[] actuals)
	{
		// look in current class
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i]
				.isApplicableTo(context, ident, actuals))
			{
				container.add(methods[i]);
			}
		}

		// look in super classes and interfaces
		if (ident != JAV_CONSTRUCTOR
			&& ident != JAV_INIT
			&& ident != JAV_STATIC_INIT)
		{
			if (superClassType != null)
			{
				getSuperClass().collectApplicableMethods(
					context,
					container,
					ident,
					actuals);
			}

			for (int i = 0; i < interfaces.length; i++)
			{
				interfaces[i].getCClass().collectApplicableMethods(
					context,
					container,
					ident,
					actuals);
			}
		}
	}

	/**
	 * @return the short name of this class
	 */
	protected static String getIdent(String qualifiedName)
	{
		verify(qualifiedName != null);
		String syntheticName;

		int cursor = qualifiedName.lastIndexOf('/');
		if (cursor > 0)
		{
			syntheticName = qualifiedName.substring(cursor + 1);
		}
		else
		{
			syntheticName = qualifiedName;
		}

		cursor = syntheticName.lastIndexOf('$');
		if (cursor > 0)
		{
			return syntheticName.substring(cursor + 1);
		}
		else
		{
			return syntheticName;
		}
	}

	/**
	 * Checks whether this type is accessible from the specified class (JLS 6.6).
	 *
	 * @return	true iff this member is accessible
	 */
	public boolean isAccessible(CClass from)
	{
		if (!isNested())
		{
			// JLS 6.6.1
			// If a TOP LEVEL class or interface type is declared public, then it
			// may be accessed by any code, provided that the compilation unit in
			// which it is declared is observable. If a top level class or
			// interface type is not declared public, then it may be accessed only
			// from within the package in which it is declared.
			// Note (graf 010602) :
			// The words TOP LEVEL near the beginning of the first sentence have
			// been added
			return isPublic() || getPackage() == from.getPackage();
		}
		else
		{
			// Rules for members apply
			return super.isAccessible(from);
		}
	}

	/**
	 * Adds a field.
	 */
	public void addField(CSourceField field)
	{
		field.setPosition(fields.size());
		fields.put(field.getIdent(), field);
	}

	/**
	 * Adds a method.
	 */
    public void addMethod(CSourceMethod method)
    {
        addMethod(new CSourceMethod[]{method});
    }

    public void addMethod(CSourceMethod m[])
    {
        CMethod[] tmp = new CMethod[methods.length + m.length];
        System.arraycopy(methods, 0, tmp, 0, methods.length);
        System.arraycopy(m, 0, tmp, methods.length, m.length);
        methods = tmp;
    }

	/**
	 * Adds a inner class.
	 */
    public void addInnerClass(CReferenceType[] newInners)
    {
        CReferenceType[] tmp = new CReferenceType[innerClasses.length + newInners.length];
        System.arraycopy(innerClasses, 0, tmp, 0, innerClasses.length);
        System.arraycopy(newInners, 0, tmp, innerClasses.length, newInners.length);
        innerClasses = tmp;
    }

    public void addInnerClass(CReferenceType innerClass)
    {
        addInnerClass(new CReferenceType[]{innerClass});
    }
    
	/**
	 * Returns the invariant method for this class or null if no invariant exists
	 *
	 */
	public CMethod getInvariant()
	{
		return null;
	}	
	
	// CTODO this here is not nice
	public String convertToIfcQn() {
		return new JavaQualifiedName(getQualifiedName()).convertToIfcName().toString();
	}
	
	public String convertToImplQn() {
		return new JavaQualifiedName(getQualifiedName()).convertToImplName().toString();
	}

	/**
	 * generates generic signature
	 */
	public String getSignature()
	{
		SimpleStringBuffer buffer;
		String result;

		buffer = SimpleStringBuffer.request();


		superClassType.appendSignature(buffer);
		for (int i = 0; i < interfaces.length; i++)
		{
			interfaces[i].appendSignature(buffer);
		}
		result = buffer.toString();
		SimpleStringBuffer.release(buffer);
		return result;
	}
	public void analyseConditions() throws PositionedError
	{
		// do something in the sourceclass but not here
	}
	// ----------------------------------------------------------------------
	// INNER CLASS SUPPORT
	// ----------------------------------------------------------------------

	/**
	 * add synthetic parameters to method def
	 */
	public int getNextSyntheticIndex()
	{
		return syntheticIndex++;
	}
	/**
	 * add synthetic parameters to method def
	 */
	public CType[] genConstructorArray(CType[] params)
	{
		throw new InconsistencyException();
	}

	/**
	 * add synthetic parameters to method call
	 */
	public void genOuterSyntheticParams(GenerationContext context)
	{
		// do nothing
	}

	public void genOuterSyntheticParams(GenerationContext context, int length)
	{
		// do nothing
	}

	// ----------------------------------------------------------------------
	// PRIVATE INITIALIZATIONS
	// ----------------------------------------------------------------------

	public static CClass CLS_UNDEFINED =
		new CSourceClass(
			null,
			TokenReference.NO_REF,
			0,
			"<gen>",
			"<gen>",
			false,
			true,
			null);

    
    // IVICA maps from qualified name to impl qualified name
    // for example generated/G$Edge -> generated/G_Impl$Edge_Impl
    public String getImplQualifiedName() {
        StringBuffer strBuf = new StringBuffer();
        String qualifiedName = getQualifiedName();
        String packagePrefix = "";
        String type = qualifiedName;
        
        int i = qualifiedName.lastIndexOf('/');
        
        if(i >= 0) {
            packagePrefix = qualifiedName.substring(0, i+1);
            type = qualifiedName.substring(i+1, qualifiedName.length());
        }
        
        strBuf.append(packagePrefix);
        
        StringTokenizer tok = new StringTokenizer(type, "$");
        while(tok.hasMoreTokens()) {
            strBuf.append(tok.nextToken()+"_Impl");
            
            if(tok.hasMoreTokens())
                strBuf.append('$');
        }
        
        String res = strBuf.toString();
        
        return res;
    }

    public int getDepth() {
        return depth;
    }
    
	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	private final String sourceFile;
	private final String qualifiedName;
	private final String packageName;

	private CClass assertionStatusClass; // JSR 41

	private CReferenceType[] interfaces;
	private CReferenceType type;
	private CReferenceType[] innerClasses;
	protected CReferenceType superClassType;

	private boolean localClass;
	private boolean hasOuterThis;

	protected Hashtable fields;
	protected CMethod[] methods;
	private boolean qualifiedAndAnonymous;
	private int syntheticIndex = 0;

	private boolean implicit = false;
	
	private int depth = 0;
	
	private AdditionalCaesarTypeInformation additionalTypeInfo = null;
	
	public void setAdditionalTypeInformation(AdditionalCaesarTypeInformation additionalTypeInfo) {
	    this.additionalTypeInfo = additionalTypeInfo;
	}
	
	public AdditionalCaesarTypeInformation getAdditionalTypeInformation() {
	    return additionalTypeInfo;
	}
	

    public void setOwner(CClass owner) {
        this.owner = owner;
    }
    
    public boolean isImplicit() {
        return implicit;
    }
    
    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }
}
