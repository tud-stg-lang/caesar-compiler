package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;


/**
 * handles the outer().outer().outer()... calls
 * A.B.this.a() -> outer().a() in A.B.C
 * 
 * @author Ivica Aracic
 */
public class CjOuterExpression extends JExpression {
    
    private CReferenceType prefixType;
    private JExpression transformation = null;
    private int steps = 0; /** number of outer calls */
    
    CjOuterExpression(TokenReference where, CReferenceType prefixType) {
        super(where);
        this.prefixType = prefixType;
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {
        
        CClass caller =
            context.getClassReader().loadClass(
                context.getTypeFactory(),
                context.getClassContext().getCClass().convertToIfcQn()
            );
        
        transformation = 
            generateOuterAccessExpression(caller, prefixType.getCClass());
        
        check(
            context,
            transformation != null,
            KjcMessages.THIS_BADACCESS);
        
        transformation.analyse(context);
        
        return this;
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        transformation.genCode(context, discardValue);
    }
    
    public CType getType(TypeFactory factory) {
        if(transformation == null) throw new InconsistencyException();
        return transformation.getType(factory);
    }
    
    
    protected JExpression generateOuterAccessExpression(final CClass caller, final CClass target) throws PositionedError {
	    JExpression expr = new JThisExpression(getTokenReference());
	    	
	    CClass clazz = caller; 
	    steps = 0;
	    
	    while(!clazz.descendsFrom(target)) {
	        clazz = clazz.getOwner();
	        
		    if(clazz == null)
		        throw new PositionedError(getTokenReference(), KjcMessages.THIS_BADACCESS); 
	        
		    expr = 
		        new JCastExpression(
		            getTokenReference(),		            
			        new JMethodCallExpression(
			            getTokenReference(),
			            expr,
			            CaesarConstants.OUTER_ACCESS,
			            JExpression.EMPTY),
			        clazz.getAbstractType()
	            );	
		    
		    steps++;
	    }
	    
	    return expr;
	}
    
    public int getOuterSteps() {
        return steps;
    }
}
