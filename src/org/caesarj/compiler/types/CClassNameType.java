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
 * $Id: CClassNameType.java,v 1.23 2006-05-05 14:00:42 gasiunas Exp $
 */

package org.caesarj.compiler.types;


import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents (generic) class type or type variable in the type structure
 */
public class CClassNameType extends CReferenceType
{

	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Construct a class type
	 * @param	qualifiedName	the class qualified name of the class
	 */
	public CClassNameType(String qualifiedName)
	{
		this(qualifiedName, false);
	}

	/**
	 * Construct a class type
	 * @param	qualifiedName	the class qualified name of the class
	 */
	public CClassNameType(String qualifiedName, boolean binary)
	{
	    super();

		if (qualifiedName.indexOf('.') >= 0)
		{
			throw new InconsistencyException(
				"Incorrect qualified name: " + qualifiedName);
		}

		this.qualifiedName = qualifiedName.intern();
		this.binary = binary;
	}

	// ----------------------------------------------------------------------
	// ACCESSORS
	// ----------------------------------------------------------------------

	/**
	 * Transforms this type to a string
	 */
	public String toString()
	{
		return qualifiedName != null
			? qualifiedName.replace('/', '.')
			: super.toString();
	}	

	/**
	 *
	 */
	public String getQualifiedName()
	{
		return qualifiedName == null ? super.getQualifiedName() : qualifiedName;
	}
    
	/**
	 * Returns the class object associated with this type
	 *
	 * If this type was never checked (read from class files)
	 * check it!
	 *
	 * @return the class object associated with this type
	 */
	public CClass getCClass()
	{
		if (!isChecked())
		{
			throw new InconsistencyException("type not checked");
		}

		return super.getCClass();
	}

	// ----------------------------------------------------------------------
	// INTERFACE CHECKING
	// ----------------------------------------------------------------------

	/**
	 * check that type is valid
	 * necessary to resolve String into java/lang/String
	 * @param	context		the context (may be be null)
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	
	public CType checkType(CTypeContext context) throws UnpositionedError {
	    CType res = _checkType(context);
	    
	    if (!context.allowsDependentTypes()) {
	    	return res;
	    }
	    
	    // store the context in which this reference type has been resolved
	    if(res instanceof CReferenceType && context instanceof CContext) {
	        ((CReferenceType)res).setDeclContext((CContext)context);
	    }
	    
	    // if we have a caesar reference which has been resolved as an CClassOrInterfaceType
	    // then map this type to a CDependentType with a implicit this expression
	    if(res.isChecked() && res.isCaesarReference()) {	        
	        CReferenceType refType = (CReferenceType)res;
	        
	        CClass ctxClass = context.getClassContext().getCClass();
	        
	        if(
	            refType.getCClass().isNested()
	            && (ctxClass.isMixin() || ctxClass.isMixinInterface())
	            && qualifiedName.indexOf('/') < 0
            ) {
	            // in this case we have an dependent type with a implicit family path
	            // convert it to dependent type
	            CDependentType depType = new CDependentType((CContext)context, (CContext)context, null, res);
	            return depType.checkType(context);
	        }
	        /*
	        else if( refType.getCClass().isNested() ) {
	            // we have a nested cclass which has been defined without a family path
	            // CTODO: not allowed for now
	            throw new UnpositionedError(...);
	        }
	        */
	    }
	    
	    return res;
	}
	
	private CType _checkType(CTypeContext context) throws UnpositionedError 
	{
		if (binary && qualifiedName.indexOf('/') >= 0)
		{
			return new CBinaryType(
				qualifiedName,
				context.getClassReader(),
				context.getTypeFactory());
		}
		if (qualifiedName.indexOf('/') >= 0)
		{
			if (context.getClassReader().hasClassFile(qualifiedName))
			{
				CType type =
					new CClassOrInterfaceType(
						context.getClassReader().loadClass(
							context.getTypeFactory(),
							qualifiedName));
				return type.checkType(context);
			}
			else
			{
				// maybe inner class
				int index = qualifiedName.lastIndexOf("/");

				CReferenceType outer;

				try
				{
					outer =
						new CClassNameType(
							qualifiedName.substring(0, index),
							binary);
					outer = (CReferenceType) outer.checkType(context);
				}
				catch (UnpositionedError ce)
				{
					throw new UnpositionedError(
						KjcMessages.TYPE_UNKNOWN,
						qualifiedName);
				}
				CClass caller;

				if (context == null)
				{
					caller =
						context
							.getTypeFactory()
							.createReferenceType(TypeFactory.RFT_OBJECT)
							.getCClass();
				}
				else
				{
					CClassContext classContext = context.getClassContext();

					if (classContext == null)
					{
						caller =
							context
								.getTypeFactory()
								.createReferenceType(TypeFactory.RFT_OBJECT)
								.getCClass();
					}
					else
					{
						caller = classContext.getCClass();
					}
				}

				CClass innerClass =
					outer.getCClass().lookupClass(
						caller,
						qualifiedName.substring(index + 1).intern());

				if (innerClass != null)
				{
					CType type =
						new CClassOrInterfaceType(innerClass);

					return type.checkType(context);
				}
				else
				{
					throw new UnpositionedError(
						KjcMessages.TYPE_UNKNOWN,
						qualifiedName);
				}
			}
		}
		else
		{
			// It is a class or interface
			CClassContext classContext = context.getClassContext();
			CClass caller;

			if (classContext == null)
			{
				caller =
					context
						.getTypeFactory()
						.createReferenceType(TypeFactory.RFT_OBJECT)
						.getCClass();
			}
			else
			{
				caller = classContext.getCClass();
			}
			CClass theClazz = context.lookupClass(caller, qualifiedName);

			if (theClazz != null)
			{
				CType type = new CClassOrInterfaceType(theClazz);

				return type.checkType(context);
			}
			else
			{
				if (context.getClassReader().hasClassFile(qualifiedName))
				{
					// unnamed Package
					return new CClassOrInterfaceType(
						context.getClassReader().loadClass(
							context.getTypeFactory(),
							qualifiedName)).checkType(
						context);
				}
				else
				{
					throw new UnpositionedError(
						KjcMessages.TYPE_UNKNOWN,
						qualifiedName);
				}
			}
		}
	}

    public boolean isAssignableTo(
		CTypeContext context,
		CType dest,
		CReferenceType[] substitution)
	{
		throw new InconsistencyException("check it before");
	}
	public boolean isAssignableTo(
		CTypeContext context,
		CType dest,
		boolean instantiation)
	{
		throw new InconsistencyException("check it before");
	}

	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	protected String qualifiedName; // null => checked
	private boolean binary;
}
