package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CjQualifiedInstanceCreation extends JExpression {

    private JExpression expr = null; // 
    
    private JExpression prefix;
    private String ident;
    private JExpression[] params;

    public CjQualifiedInstanceCreation(
        TokenReference where,
        JExpression prefix,
        String ident,
        JExpression[] params
    ) {
        super(where);

        this.prefix = prefix;
        this.ident = ident;
        this.params = params;
    }

    public CType getType(TypeFactory factory) {
        assertExprChoosen();
        return expr.getType(factory);
    }
    
    public JExpression analyse(CExpressionContext context) throws PositionedError {
        if(expr != null) 
            return expr.analyse(context);
        
        TypeFactory factory = context.getTypeFactory();       

        prefix = prefix.analyse(context);
        
        check(context,
    	  prefix.getType(factory).isClassType(),
    	  KjcMessages.FIELD_BADACCESS, prefix.getType(factory));

        CClass	newClass;
        CType prefixType = prefix.getType(factory);
        CClass prefixClass = prefixType.getCClass();
        
        if((prefixClass.isMixinInterface() || prefixClass.isMixin()) && params.length == 0) {
            // convert to factory method
            // a.new C() -> a.$newC()
            expr = new JMethodCallExpression(getTokenReference(), prefix, "$new"+ident, params);
        }
        else {
            // create normal qualified instance creation
            expr = new JQualifiedInstanceCreation(getTokenReference(), prefix, ident, params);
        }       
        
        return expr.analyse(context);
    }
    
    public void genCode(GenerationContext context, boolean discardValue) {
        assertExprChoosen();
        expr.genCode(context, discardValue);
    }
    
    private void assertExprChoosen() {
        if(expr==null) throw new InconsistencyException("analyse not executed");
    }
}