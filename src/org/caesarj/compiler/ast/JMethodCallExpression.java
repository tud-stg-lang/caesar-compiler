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
 * $Id: JMethodCallExpression.java,v 1.1 2004-02-08 16:47:41 ostermann Exp $
 */

package org.caesarj.compiler.ast;

import org.caesarj.compiler.CWarning;
import org.caesarj.compiler.Constants;
import org.caesarj.compiler.KjcMessages;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.CConstructorContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CArrayType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CThrowableInfo;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * JLS 15.12 Method Invocation Expressions
 */
public class JMethodCallExpression extends JExpression
{

	/**
	 * Construct a node in the parsing tree
	 * This method is directly called by the parser
	 * @param	where		the line of this node in the source code
	 * @param	prefix		an expression that is a field of a class representing a method
	 * @param	ident		the method identifier
	 * @param	args		the argument of the call
	 */
	public JMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args)
	{
		super(where);

		this.prefix = prefix;
		this.ident = ident.intern(); // $$$ graf 010530 : why intern ?
		this.args = args;
		this.analysed = false;
	}

	/**
	 * Constructs a node. This node need NOT be analysed
	 * For example these objects are used in sythetic accessors used for inner classes.
	 * The prefix and the arguments must be analysed before the creation of these object, 
	 * or they must be expressions which not need to be analysed.
	 *
	 * @param	where		the line of this node in the source code
	 * @param	prefix		an expression that is a field of a class representing a method
	 * @param	ident		the method identifier
	 * @param	args		the argument of the call
	 */
	public JMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		CMethod method,
		JExpression[] args)
	{
		super(where);

		this.prefix = prefix;
		this.method = method;
		this.ident = method.getIdent();
		this.args = args;
		this.type = method.getReturnType();
		this.analysed = true;
	}

	// ----------------------------------------------------------------------
	// ACCESSORS
	// ----------------------------------------------------------------------

	/**
	 * @return the type of this expression
	 */
	public CType getType(TypeFactory factory)
	{
		return type == null ? method.getReturnType() : type;
	}

	/**
	 * Returns true iff this expression can be used as a statement (JLS 14.8)
	 */
	public boolean isStatementExpression()
	{
		return true;
	}

	// ----------------------------------------------------------------------
	// SEMANTIC ANALYSIS
	// ----------------------------------------------------------------------
	/**
	 * Analyses the expression (semantically).
	 * @param	context		the analysis context
	 * @return	an equivalent, analysed expression
	 * @exception	PositionedError	the analysis detected an error
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError
	{
		TypeFactory factory = context.getTypeFactory();

		if (analysed)
		{
			return this;
		}
		
		//Walter: this method is called now 
		//rather than analise the args directly
		CType[] argTypes = getArgumentTypes(context, args, factory);
		CClass local = context.getClassContext().getCClass();

		findMethod(context, local, argTypes);

		CReferenceType[] exceptions = method.getThrowables();

		for (int i = 0; i < exceptions.length; i++)
		{
			if (exceptions[i].isCheckedException(context))
			{
				if (prefix == null
					|| // special case of clone
				!prefix
						.getType(factory)
						.isArrayType()
					|| ident != Constants.JAV_CLONE
					|| !exceptions[i].getCClass().getQualifiedName().equals(
						"java/lang/CloneNotSupportedException"))
				{
					context.getBodyContext().addThrowable(
						new CThrowableInfo(exceptions[i], this));
				}
			}
		}

		CClass access = method.getOwner();

		if (prefix == null && !method.isStatic())
		{
			if (access == local)
			{
				prefix = new JThisExpression(getTokenReference());
			}
			else
			{
				prefix = new JOwnerExpression(getTokenReference(), access);
			}
			prefix = prefix.analyse(context);
		}

		if ((prefixType == null) && (prefix != null))
		{
			prefixType = prefix.getType(factory);
		}

		// JLS 8.8.5.1
		// An explicit constructor invocation statement in a constructor body may 
		// not refer to any instance variables or instance methods declared in 
		// this class or any superclass, or use this or super in any expression; 
		// otherwise, a compile-time error occurs.     
		if ((context.getMethodContext() instanceof CConstructorContext)
			&& (prefix instanceof JThisExpression)
			&& !method.isStatic())
		{
			check(
				context,
				((CConstructorContext) context.getMethodContext())
					.isSuperConstructorCalled(),
				KjcMessages.INSTANCE_METHOD_IN_EXP_CONSTRUCTOR_CALL,
				method);
		}

		check(
			context,
			method.isStatic() || !(prefix instanceof JTypeNameExpression),
			KjcMessages.INSTANCE_METHOD_CALL_IN_STATIC_CONTEXT,
			method);

		if (method.isStatic()
			&& prefix != null
			&& !(prefix instanceof JTypeNameExpression))
		{
			context.reportTrouble(
				new CWarning(
					getTokenReference(),
					KjcMessages.INSTANCE_PREFIXES_STATIC_METHOD,
					method.getIdent(),
					prefix.getType(factory)));
		}

		argTypes = method.getParameters();
		for (int i = 0; i < argTypes.length; i++)
		{
			if (args[i] instanceof JTypeNameExpression)
			{
				check(
					context,
					false,
					KjcMessages.VAR_UNKNOWN,
					((JTypeNameExpression) args[i]).getQualifiedName());
			}
			args[i] = args[i].convertType(context, argTypes[i]);
		}

		// Mark method as used if it is a source method
		if (method instanceof CSourceMethod)
		{
			((CSourceMethod) method).setUsed();
		}

		if (method.getReturnType().getTypeID() != TID_VOID
			&& context.discardValue())
		{
			context.reportTrouble(
				new CWarning(
					getTokenReference(),
					KjcMessages.UNUSED_RETURN_VALUE_FROM_FUNCTION_CALL,
					method.getIdent()));

		}
		boolean isSuper = prefix instanceof JSuperExpression;

		if (method.requiresAccessor(local, isSuper))
		{
			if (!method.isStatic())
			{
				JExpression[] argsTmp = new JExpression[args.length + 1];

				if (isSuper)
				{
					prefix =
						new JFieldAccessExpression(
							getTokenReference(),
							new JThisExpression(getTokenReference()),
							local.getField(JAV_OUTER_THIS));
				}
				argsTmp[0] = prefix;
				System.arraycopy(args, 0, argsTmp, 1, args.length);
				prefix = null;
				args = argsTmp;
			}
			CSourceClass target = method.getAccessorOwner((CSourceClass) local);

			method =
				method.getAccessor(context.getTypeFactory(), target, isSuper);
		}

		// if the returntype is generic (and the owner of the method not 'this class')
		// or it is a typevariable and need a cast
		type = method.getReturnType();

		if (!context.discardValue() && prefixType != null)
		{
			CType returnType = method.getReturnType();

			if (returnType.isTypeVariable())
			{
				CReferenceType subType;
				if (!((CTypeVariable) returnType).isMethodTypeVariable())
				{
					subType =
						prefixType.getCClass().getSubstitution(
							(CTypeVariable) returnType,
							prefixType.getAllArguments());
				}
				else
				{
					subType = (CReferenceType) returnType;
					// FIX with methodType sub
				}
				try
				{
					if (method.getReturnType().getErasure(context)
						!= subType.getErasure(context))
					{
						analysed = true;
						return new JCastExpression(
							getTokenReference(),
							new JCheckedExpression(getTokenReference(), this),
							subType).analyse(
							context);
					}
					else
					{
						type = subType;
					}
				}
				catch (UnpositionedError e)
				{
					throw e.addPosition(getTokenReference());
				}
			}
			else if (
				returnType.isArrayType()
					&& ((CArrayType) returnType).getBaseType().isTypeVariable())
			{
				CReferenceType subType =
					prefixType.getCClass().getSubstitution(
						(CTypeVariable) ((CArrayType) returnType).getBaseType(),
						prefixType.getAllArguments());
				subType =
					new CArrayType(
						subType,
						((CArrayType) returnType).getArrayBound());
				try
				{
					if (method.getReturnType().getErasure(context)
						!= subType.getErasure(context))
					{
						analysed = true;
						return new JCastExpression(
							getTokenReference(),
							new JCheckedExpression(getTokenReference(), this),
							subType).analyse(
							context);
					}
					else
					{
						type = subType;
					}
				}
				catch (UnpositionedError e)
				{
					throw e.addPosition(getTokenReference());
				}
			}
			else if (returnType.isGenericType())
			{
				type =
					((CReferenceType) prefixType).createSubstitutedType(
						local,
						(CReferenceType) prefixType,
						local.getAbstractType().getAllArguments());
			}
			else if (
				returnType.isArrayType()
					&& ((CArrayType) returnType).getBaseType().isGenericType())
			{
				type =
					new CArrayType(
						((CReferenceType) prefixType).createSubstitutedType(
							local,
							(CReferenceType) prefixType,
							local.getAbstractType().getAllArguments()),
						((CArrayType) returnType).getArrayBound());
			}
		}
		// fixed lackner 18.03.2002 commment out because sometimes it is necessary to evaluate it twice.
		//    analysed = true;
		return this;
	}
	
	/**
	 * This method has been created to allow subclasses to override it.
	 * @param context
	 * @param args
	 * @param factory
	 * @return
	 * @throws PositionedError
	 * @author Walter Augusto Werner
	 */
	protected CType[] getArgumentTypes(
		CExpressionContext context,
		JExpression[] args,
		TypeFactory factory)
		throws PositionedError
	{
		CType[] argTypes = new CType[args.length];

		for (int i = 0; i < argTypes.length; i++)
		{
		// evaluate the arguments in rhs mode, result will be used
			args[i] =
				args[i].analyse(
					new CExpressionContext(context, context.getEnvironment()));
			argTypes[i] = args[i].getType(factory);
			try
			{
				argTypes[i] = argTypes[i].checkType(context);
			}
			catch (UnpositionedError e)
			{
				throw e.addPosition(getTokenReference());
			}
		}
		return argTypes;
	}

	protected void findMethod(
		CExpressionContext context,
		CClass local,
		CType[] argTypes)
		throws PositionedError
	{
		TypeFactory factory = context.getTypeFactory();

		if (prefix != null)
		{
			// evaluate the prefix in rhs mode, result will be used
			prefix =
				prefix.analyse(
					new CExpressionContext(context, context.getEnvironment()));
			if (prefix instanceof JNameExpression)
			{
				// condition as if-statement because of arguments to method check
				check(
					context,
					false,
					KjcMessages.BAD_METHOD_NAME,
					((JNameExpression) prefix).getName());
			}
			check(
				context,
				prefix.getType(factory).isReference(),
				KjcMessages.METHOD_BADPREFIX,
				ident,
				prefix.getType(factory));

			if (prefix.getType(factory).isArrayType())
			{
				// JLS 6.6.1
				// An array type is accessible if and only if its element type is accessible. 
				check(
					context,
					((CArrayType) prefix.getType(factory))
						.getBaseType()
						.isPrimitive()
						|| ((CArrayType) prefix.getType(factory))
							.getBaseType()
							.getCClass()
							.isAccessible(
							local),
					KjcMessages.CLASS_NOACCESS,
					((CArrayType) prefix.getType(factory)).getBaseType());
			}
			check(
				context,
				prefix.getType(factory).getCClass().isAccessible(local),
				KjcMessages.CLASS_NOACCESS,
				prefix.getType(factory).getCClass());

			// if method is defined in more than one bound??
			if (method != null)
			{
				return;
			}

			try
			{
				if (!prefix.getType(factory).isTypeVariable())
				{
					// FIX it lackner 19.11.01      used for internalInitLoadDefinition of PPage : prefix instanceof JSuperExpression
					method =
						prefix.getType(factory).getCClass().lookupMethod(
							context,
							local,
							(prefix instanceof JThisExpression
								|| prefix instanceof JSuperExpression)
								? null
								: prefix.getType(factory),
							ident,
							argTypes,
							prefix.getType(factory).getArguments());
					prefixType = prefix.getType(factory);
				}
				else
				{
					// find method in a type of the bound;
					CReferenceType[] bound =
						((CTypeVariable) prefix.getType(factory)).getBounds();

					if (bound.length == 0)
					{
						// bound is java.lang.Object
						method =
							context
								.getTypeFactory()
								.createReferenceType(TypeFactory.RFT_OBJECT)
								.getCClass()
								.lookupMethod(
									context,
									local,
									context
										.getTypeFactory()
										.createReferenceType(
										TypeFactory.RFT_OBJECT),
									ident,
									argTypes,
									CReferenceType.EMPTY);
					}
					else
					{
						for (int i = 0; i < bound.length; i++)
						{
							method =
								bound[i].getCClass().lookupMethod(
									context,
									local,
									bound[i],
									ident,
									argTypes,
									bound[i].getArguments());
							if (method != null)
							{
								prefixType = bound[i];
								break;
							}
						}
						// FIX !!! 
						// if method is defined in more than one bound??
					}
				}
			}
			catch (UnpositionedError e)
			{
				throw e.addPosition(getTokenReference());
			}
		}
		else
		{
			// FIX lackner 04.04.2002 workaround if double analysed with accessor method
			if (method != null)
			{
				return;
			}
			try
			{
				method =
					context.lookupMethod(context, local, null
				/*prefix == null*/
				, ident, argTypes);
			}
			catch (UnpositionedError e)
			{
				throw e.addPosition(getTokenReference());
			}
		}
		if (method == null)
		{
			String prefixName;

			if (prefix instanceof JNameExpression)
			{
				prefixName =
					((JNameExpression) prefix).getQualifiedName() + ".";
			}
			else if (prefix instanceof JTypeNameExpression)
			{
				prefixName =
					((JTypeNameExpression) prefix).getQualifiedName() + ".";
			}
			else
			{
				prefixName =
					(prefix == null)
						? ""
						: prefix.getType(factory).toString() + ".";
			}
			throw new CMethodNotFoundError(
				getTokenReference(),
				this,
				prefixName + ident,
				argTypes);
		}
	}
	
	// ----------------------------------------------------------------------
	// CODE GENERATION
	// ----------------------------------------------------------------------

	/**
	 * Accepts the specified visitor
	 * @param	p		the visitor
	 */
	public void accept(KjcVisitor p)
	{
		p.visitMethodCallExpression(this, prefix, ident, args);
	}

	/**
	 * Generates JVM bytecode to evaluate this expression.
	 *
	 * @param	code		the bytecode sequence
	 * @param	discardValue	discard the result of the evaluation ?
	 */
	public void genCode(GenerationContext context, boolean discardValue)
	{
		CodeSequence code = context.getCodeSequence();
		TypeFactory factory = context.getTypeFactory();

		setLineNumber(code);

		boolean forceNonVirtual = false;

		if (!method.isStatic())
		{
			prefix.genCode(context, false);
			if (prefix instanceof JSuperExpression)
			{
				forceNonVirtual = true;
			}
		}
		else if (prefix != null)
		{
			prefix.genCode(context, true);
		}

		for (int i = 0; i < args.length; i++)
		{
			args[i].genCode(context, false);
		}
		method.genCode(context, forceNonVirtual);

		if (discardValue)
		{
			code.plantPopInstruction(getType(factory));
		}
	}

	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	protected JExpression prefix;
	protected String ident;
	protected JExpression[] args;
	private boolean analysed;
	protected CMethod method;
	protected CType type;
	protected CType prefixType;
}
