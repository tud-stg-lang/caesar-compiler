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
 * $Id: CClassNameType.java,v 1.9 2004-11-19 15:59:38 klose Exp $
 */

package org.caesarj.compiler.types;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CClassBodyContext;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.TokenReference;
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

	private JExpression convertToExpression() {
	    String pathSegs[] = qualifiedName.split("/");
	    JExpression expr = null;
	    for (int i = 0; i < pathSegs.length-1; i++) {
            expr = new JNameExpression(TokenReference.NO_REF, expr, pathSegs[i]);
        }
	    
	    return expr;
	}
	
	private JPhylum[] makePos(CTypeContext context) {
        List list = new LinkedList();
        
        
        
	    if(context instanceof CContext) {
	        CMethodContext methodCtx = ((CContext)context).getMethodContext();
	        if(methodCtx != null) {
	            list.add(0, methodCtx.getMethodDeclaration());
	        }
        }
	    
	    CClassContext clsCtx = context.getClassContext();
	    
	    while(clsCtx != null) {
	        list.add(0, clsCtx.getTypeDeclaration());
	        if(clsCtx.getParentContext() instanceof CClassContext) {
	            clsCtx = (CClassContext)clsCtx.getParentContext();
	        }
	        else {
	            clsCtx = null;
	        }
	    }
	    
	    return (JPhylum[])list.toArray(new JPhylum[list.size()]);
    }	
	
	/**
	 * check that type is valid
	 * necessary to resolve String into java/lang/String
	 * @param	context		the context (may be be null)
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CType checkType(CTypeContext context) throws UnpositionedError
	{
	    /*************************************/	    
	    // IVICA: try to lookup the path first	    
	    
	    if(qualifiedName.equals("g/N")) {
	        boolean stop = true;
	    }
	    
	    JExpression expr = convertToExpression();
	    
	    
        if(expr != null && (context instanceof CBlockContext || context instanceof CExpressionContext)) {
            try {
                
                CExpressionContext ctx = null;
                                
                if(context instanceof CExpressionContext) {
                    ctx = (CExpressionContext)ctx;
                }
                else if(context instanceof CBodyContext) {
                    ctx = new CExpressionContext(
                        (CBodyContext)context, 
                        context.getClassContext().getEnvironment()
                    );
                }
                
                expr = expr.analyse(ctx);
                
                if(expr instanceof JFieldAccessExpression) {
	                String pathSegs[] = qualifiedName.split("/");
	                
	                CClass clazz = context.getClassReader().loadClass(
	                    context.getTypeFactory(),
	                    expr.getType(context.getTypeFactory()).getCClass().getQualifiedName()+"$"+pathSegs[pathSegs.length-1]
	                );
	                
	                // strip occurences of CClassBodyContext from 
	                JPhylum[] pos = makePos(context);
	                Vector v = new Vector();
	                
	                for (int i = 0; i < pos.length; i++) {
                        if (pos[i] instanceof JMethodDeclaration){
                            JMethodDeclaration md = (JMethodDeclaration)pos[i];
                            if (md.getIdent().equals(CClassBodyContext.METHOD_NAME)){
                                continue;
                            }
                        }
                        v.add(pos[i]);
                    }
	                
	                pos = (JPhylum[]) v.toArray(new JPhylum[0]);
	                
	                return 
	                	new CDependentType(pos, expr, clazz.getAbstractType());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("not dependent type");
            }            
	    }
	    
	    /*************************************/
	    
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
