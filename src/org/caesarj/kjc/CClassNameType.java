/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: CClassNameType.java,v 1.1 2003-07-05 18:29:38 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.util.InconsistencyException;
import org.caesarj.compiler.UnpositionedError;

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
		this(qualifiedName, EMPTY_ARG, false);
	}

	/**
	 * Construct a class type
	 * @param	qualifiedName	the class qualified name of the class
	 */
	public CClassNameType(String qualifiedName, boolean binary)
	{
		this(qualifiedName, EMPTY_ARG, binary);
	}

	/**
	 * Construct a class type
	 * @param	qualifiedName	the class qualified name of the class
	 */
	public CClassNameType(
		String qualifiedName,
		CReferenceType[][] arguments,
		boolean binary)
	{
		super();

		if (qualifiedName.indexOf('.') >= 0)
		{
			throw new InconsistencyException(
				"Incorrect qualified name: " + qualifiedName);
		}

		this.qualifiedName = qualifiedName.intern();
		this.arguments = arguments;
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
			? qualifiedName.replace('/', '.') + printArgs()
			: super.toString();
	}
	private String printArgs()
	{
		if (arguments == null
			|| arguments.length == 0
			|| arguments[arguments.length - 1] == null
			|| arguments[arguments.length - 1].length == 0)
		{
			return "";
		}
		StringBuffer buffer = new StringBuffer();

		buffer.append('<');
		for (int i = 0; i < arguments[arguments.length - 1].length; i++)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}
			buffer.append(arguments[arguments.length - 1][i]);
		}
		buffer.append('>');

		return buffer.toString();
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
	public CType checkType(CTypeContext context) throws UnpositionedError
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
							qualifiedName),
						arguments);
				return type.checkType(context);
			}
			else
			{
				// maybe inner class
				int index = qualifiedName.lastIndexOf("/");

				CReferenceType outer;

				try
				{
					CReferenceType[][] args;

					if (arguments.length < 2)
					{
						args = CReferenceType.EMPTY_ARG;
					}
					else
					{
						args = new CReferenceType[arguments.length - 1][];
						System.arraycopy(arguments, 0, args, 0, args.length);
					}

					outer =
						new CClassNameType(
							qualifiedName.substring(0, index),
							args,
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
						new CClassOrInterfaceType(innerClass, arguments);

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
			// is it a type variable
			CTypeVariable typeVariable =
				context.lookupTypeVariable(qualifiedName);

			if (typeVariable != null)
			{
				return typeVariable.checkType(context); // it is a typevariable
			}

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
				CType type = new CClassOrInterfaceType(theClazz, arguments);

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
							qualifiedName),
						arguments).checkType(
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
