package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CjUnqualifiedInstanceCreation extends JExpression {
    private CReferenceType type;
    private JExpression[] params;

    public CjUnqualifiedInstanceCreation(
        TokenReference where,
        CReferenceType type,
        JExpression[] params
    ) {
        super(where);

        this.type = type;
        this.params = params;
    }

    public CType getType(TypeFactory factory) {
        throw new InconsistencyException();
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {        
        JExpression expr; 
        
        TypeFactory factory = context.getTypeFactory();

        try {
            type = (CReferenceType)type.checkType(context);            
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
        
        CClass typeClass = type.getCClass();
        
        // if we have a caesar class, we have to replace the interface with the impl class
        // and add null parameter to the constructor
        // new C() -> (C)(new C_Impl(null)) if not a nested class (owner == null)
        // new C() -> this.$newC() if a nested class (owner != null)
        if((typeClass.isMixinInterface() || typeClass.isMixin()) && params.length == 0) {
            CClass implClass = context.getClassReader().loadClass(
                factory,
                type.getCClass().convertToImplQn()
            );
            
            CReferenceType newType = implClass.getAbstractType();
            
            if(implClass.getOwner() != null) {
                expr = new JMethodCallExpression(
                    getTokenReference(),
                    new JThisExpression(getTokenReference()),
                    "$new"+type.getCClass().getIdent(),
                    params
                );
            }
            else {            
	            params = new JExpression[]{new JNullLiteral(getTokenReference())};            
	            expr = new JUnqualifiedInstanceCreation(getTokenReference(), newType, params);
	            expr = new JCastExpression(getTokenReference(), expr, type);
            }
        }
        else {
            expr = new JUnqualifiedInstanceCreation(getTokenReference(), type, params);
        }
        
        return expr.analyse(context);
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        throw new InconsistencyException();
    }
}