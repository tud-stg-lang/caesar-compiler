package org.caesarj.compiler.ast.phylum.expression;

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
public class CjMethodCallExpression2 extends JExpression {
    
    protected JExpression prefix;
	protected String ident;
	protected JExpression[] args;
    
    public CjMethodCallExpression2(
		TokenReference where,
		JExpression prefix,
		String ident,
		JExpression[] args
	) {
		super(where);

		this.prefix = prefix;
		this.ident = ident.intern(); // $$$ graf 010530 : why intern ?
		this.args = args;
	}
    
    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        TypeFactory factory = context.getTypeFactory();       

        JExpression expr = new JMethodCallExpression(
            getTokenReference(),
            prefix, ident, args
        );
        
        /* TODO inserting automatic casts
        if(prefix != null) {
	        prefix = prefix.analyse(context);
	                
	        CType prefixType = prefix.getType(factory);
	        CClass prefixClass = prefixType.getCClass();
	        
	        CClass contextClass = context.getClassContext().getCClass();
	        
	        if(
	            (prefixClass.isMixinInterface() || prefixClass.isMixin())
	            && contextClass.isMixin()
	        ) {
	            
	            if(prefixClass.isMixin()) {
	                prefixType = prefixClass.getInterfaces()[0];
	                prefixClass = prefixType.getCClass();
	            }
	            
	            String newPrefixClassQn = 
	                context.getEnvironment().getCaesarTypeSystem().
	                	findInContextOf(
		                    prefixClass.getQualifiedName(),
		                    contextClass.convertToIfcQn()
		                );
	            
	            CClass newPrefixClass = 
	                context.getClassReader().loadClass(
	                    factory,
	                    newPrefixClassQn
	                );
	            
	            CType newPrefixType = newPrefixClass.getAbstractType();          
	            
	            expr = new JMethodCallExpression(
	                getTokenReference(),
	                new JCastExpression(
	                    getTokenReference(),
	                    prefix,
	                    newPrefixType
	                ),
	                ident, args
	            );
	        }
        }
        */
        
        
        JExpression res = expr.analyse(context);
        
        /*
        // insert cast for the method result (depending on the context again)
        CClass resClass = res.getType(factory).getCClass();
        if(resClass.isMixinInterface()) {
            String newResClassQn = 
                context.getEnvironment().getCaesarTypeSystem().
                	findInContextOf(
	                    resClass.getQualifiedName(),
	                    contextClass.convertToIfcQn()
	                );  
            
            CClass newResClass = 
                context.getClassReader().loadClass(
                    factory,
                    newResClassQn
                );
            
            res = new JCastExpression(
                getTokenReference(),
                res,
                newResClass.getAbstractType()
            );
        }
        */
                
        return res;
    }
    
    public boolean isStatementExpression() {
        return true;
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        throw new InconsistencyException();
    }
}
