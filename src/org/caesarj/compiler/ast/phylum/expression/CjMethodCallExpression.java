package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Inserts casts for method calls 
 * 
 * @author Ivica Aracic 
 */
public class CjMethodCallExpression extends JExpression implements CaesarConstants {
    
    protected JExpression prefix;
	protected String ident;
	protected JExpression[] args;
    
    public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args
	) {
		super(where);

		this.prefix = prefix;
		this.ident = ident.intern();
		this.args = args;
	}
    
    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        TypeFactory factory = context.getTypeFactory();       

        JExpression expr;
                
        /*
        if(prefix != null) {
	        prefix = prefix.analyse(context);
	                
	        CType prefixType = prefix.getType(factory);
	        
	        CClass contextClass = context.getClassContext().getCClass();
	        
	        expr = new JMethodCallExpression(getTokenReference(), prefix, ident, args);
	        
	        CType castType = CastUtils.instance().castFrom(context, prefixType, contextClass);
	        
	        if(castType != null) {
	            expr = new JMethodCallExpression(
	                getTokenReference(),
	                new JCastExpression(
	                    getTokenReference(),
	                    prefix,
	                    castType
	                ),
	                ident, args
	            );
	        }
        }  
        else {
            expr = new JMethodCallExpression(getTokenReference(), prefix, ident, args);
        }
        */
        
        if(prefix != null) {
            prefix = prefix.analyse(context);
        }
        expr = new JMethodCallExpression(getTokenReference(), prefix, ident, args);

        // analyse expression
        expr = expr.analyse(context);
        
        
        // cast the return type
        CType returnType = expr.getType(factory);        
        if(returnType.isClassType()) {
            CClass returnClass = returnType.getCClass();
            if(returnClass.isMixinInterface()) {
                
                String contextClassName = null;
                
                if(prefix instanceof JSuperExpression) {
                    contextClassName = context.getClassContext().getCClass().convertToIfcQn(); 		        
                }
                else if(prefix != null) {
                    contextClassName = prefix.getType(factory).getCClass().convertToIfcQn();    		        
                }
                else if(context.getClassContext().getCClass().isMixin()) {
        	        contextClassName = context.getClassContext().getCClass().convertToIfcQn();                    
                }
                
                String newReturnClassQn = 
	                context.getEnvironment().getCaesarTypeSystem().
	                	findInContextOf(
                	        returnClass.getQualifiedName(),
                	        contextClassName
		                );
                
                if(newReturnClassQn != null) {
		            CClass newReturnClass = 
		                context.getClassReader().loadClass(
		                    factory,
		                    newReturnClassQn
		                );
		            
		            expr = new JCastExpression(
	                    getTokenReference(),
	                    expr,
	                    newReturnClass.getAbstractType()
		            );
	            }
            }
        }
        
        return expr;
    }
    
    
    public boolean isStatementExpression() {
        return true;
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        throw new InconsistencyException();
    }
    
    public void recurse(IVisitor p) {
        if(prefix != null)
            prefix.accept(p);
        for (int i = 0; i < args.length; i++) {
            args[i].accept(p);
        }
    }    
}
