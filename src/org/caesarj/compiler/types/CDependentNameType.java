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
 * $Id: CDependentNameType.java,v 1.4 2005-01-18 13:14:40 klose Exp $
 */

package org.caesarj.compiler.types;


import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassBodyContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.family.Path;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents (generic) class type or type variable in the type structure
 */
public class CDependentNameType extends CClassNameType 
{

	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Construct a class type
	 * @param	qualifiedName	the class qualified name of the class
	 */
	public CDependentNameType(String qualifiedName)
	{
		super(qualifiedName, false);
	}



	private JExpression convertToExpression() {
	    String pathSegs[] = qualifiedName.split("/");
	    JExpression expr = null;
	    for (int i = 0; i < pathSegs.length-1; i++) {
	        if(pathSegs[i].equals("this")) 
	            expr = new JThisExpression(TokenReference.NO_REF, expr); 
	        else 
	            expr = new JNameExpression(TokenReference.NO_REF, expr, pathSegs[i]);
        }
	    
	    return expr;
	}
	
	public CType checkType(CTypeContext context) throws UnpositionedError
	{	 	   
	    // IVICA: try to lookup the path first	   
	    JExpression expr = convertToExpression();
	    
	    if (expr != null){
            try {
                CContext ctx = (CContext)context;
                CExpressionContext ectx = null;
                //CClassContext classContext;
                KjcEnvironment env;

                // create expression context
                if (context instanceof CClassContext){
                    CClassContext classContext = (CClassContext)context;
                    env = classContext.getEnvironment();
	                ectx =
	                    new CExpressionContext(
		                    new CBlockContext(
		                        new CClassBodyContext(classContext, env), env, 0 
	                        ),
		                    env
	                    );
                } else if (context instanceof CBlockContext){
                    env = ((CBlockContext)context).getEnvironment();
                    ectx = new CExpressionContext( (CBlockContext)context, env );
                } else {
                    throw new Exception();
                }
                
                // try to anylse the fieldaccess
                try{
                    expr = expr.analyse(ectx);
                } catch (Exception e){
                    throw e;
                }
                
                if(expr instanceof JFieldAccessExpression) {                    
	                String pathSegs[] = qualifiedName.split("/");
	                
	                CClass clazz = context.getClassReader().loadClass(
	                    context.getTypeFactory(),
	                    expr.getType(context.getTypeFactory()).getCClass().getQualifiedName()+"$"+pathSegs[pathSegs.length-1]
	                );
	                
	                //
	                // find the first field access in the chain... x.y.z -> x
	                //
	                /*
	                JFieldAccessExpression fieldAccessExpr = (JFieldAccessExpression)expr;
	                while(fieldAccessExpr.getPrefix() instanceof JFieldAccessExpression) {
	                    fieldAccessExpr = (JFieldAccessExpression)fieldAccessExpr.getPrefix();
	                } 
	                
	                CClass owner = fieldAccessExpr.getField().getOwner();
	                CClass current = context.getClassContext().getCClass();
	                while(current != owner) {
	                    if(current == null) throw new InconsistencyException();
	                    current = current.getOwner();
	                    k++;
	                }
	                */
	                int k = Path.calcK((CContext)context, expr);
	                
	                //
	                // create and return new CDependentType
	                //
	                CType t = clazz.getAbstractType().checkType(context);
	                CDependentType dt = new CDependentType((CContext)context, k, expr, t);
	                //System.out.println("Resolved dp "+dt);
	                return dt;
                }
            }
            catch (Exception e) {
                // ...
            }            
	    }
	    
		throw new UnpositionedError(KjcMessages.TYPE_UNKNOWN, qualifiedName);
	}  
		
}
