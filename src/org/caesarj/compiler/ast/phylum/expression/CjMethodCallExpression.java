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
    
    private JMethodCallExpression expr;
    
    public CjMethodCallExpression(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args
	) {
		super(where);
		
		expr = new JMethodCallExpression(getTokenReference(), prefix, ident, args);
	}
    
    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        TypeFactory factory = context.getTypeFactory();              

        // analyse expression
        expr = (JMethodCallExpression)expr.analyse(context);
        
        
        // cast the return type
        CType returnType = expr.getType(factory);        
        
        if(returnType.isClassType()) {
            
            CClass returnClass = returnType.getCClass();
            
            if(returnClass.isMixinInterface()) {
                
                String contextClassName = null;
                
                CClass prefixClass = expr.getPrefixType().getCClass();
                
                if(prefixClass.isMixin()) {
                    // in this case we have this or super;
                    // in both cases the context class is the local class
                    contextClassName = context.getClassContext().getCClass().convertToIfcQn();
                }
                else {
                    contextClassName = prefixClass.getQualifiedName();
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
		            
		            JExpression res = new JCastExpression(
	                    getTokenReference(),
	                    expr,
	                    newReturnClass.getAbstractType()
		            );
		            
		            return res.analyse(context);
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
    
    /**
     * note: this one is important for join point reflection visitor,
     * which searches for the usage of thisJoinPoint* variables
     */
    public void recurse(IVisitor p) {
        expr.accept(p);
    }
}
