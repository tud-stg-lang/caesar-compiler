package org.caesarj.compiler.types;

import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.family.Path;


/**
 * Dependent Types, e.g.: 
 * final ctx(1).G g; ctx(0).g.N n; 
 * 
 * @author Ivica Aracic
 */
public class CDependentType extends CReferenceType {   
    
    private CType plainType;    /** static type of the */
    
    private JExpression family; /** family expression */
    
    private int k = 0;          /** ctx(k); determines how many steps to go out 
                                    of the current context */
              
    
///    public CDependentType(CClassContext pos, int k, JFieldAccessExpression family, CType staticType) {
    public CDependentType(CContext ctx, int k, JExpression family, CType staticType) {
        setDefCtx(ctx);
        this.k = k;
        this.family = family;
        this.plainType = staticType;
        System.out.println("new CDependentType: k="+k);
    }
    
    /**
     *
     */

    public Path getPath() {
        return Path.createFrom(defCtx, family);
    }

    public CClass getCClass() {
        return plainType.getCClass();
    }
    
    /*
    public String makePath() {
        StringBuffer res = new StringBuffer();
        res.append("ctx("+k+")");
        if(family != null) {
            JFieldAccessExpression e = family;
            LinkedList l = new LinkedList();
            while(e != null) {
                l.add(0, e.getIdent());
                if(e.getPrefix() instanceof JFieldAccessExpression) {
                    e = (JFieldAccessExpression)e.getPrefix();
                }
                else {
                    e = null;
                }
            }
            
            for (Iterator it = l.iterator(); it.hasNext();) {
                res.append("."+it.next());
            }
        }
        return res.toString();
    }     
    */
    
    /**
     * simply check the plain type here
     * family checks are done in a separate step after analyse has been executed
     */
    public boolean isAssignableTo(CTypeContext context, CType dest) {
        // only a dependent type is assignable to another dependet type
        if(dest.isDependentType()) {
            CDependentType other = (CDependentType)dest;
            
            // check if plain types are subtypes
            if( plainType.isAssignableTo(context, other.plainType) ) {
                
                CContext ctx = (CContext) context;
                
                // TODO create static objects and compare
                CDependentType rightType = (CDependentType) dest;
                
                /*
                Path 	rightPath  = Path.createFrom(ctx, rightType.getFamily() ),
                		leftPath   = Path.createFrom(ctx, this.getFamily() );
                */
               
                /*
                StaticObject 	rightSO = rightPath.type(ctx),
                				leftSO = leftPath.type(ctx);
                                
                return leftSO.hasSameFamiliy(rightSO);
                */
                
                return true;
            }
        }
        
        return false;
    }
    

    public boolean isDependentType() {
        return true;
    }
    
    public boolean isChecked() {
        return plainType.isChecked();
    }

    // not castable for now
    public boolean isCastableTo(CType dest) {
        return false;
    }
    
    public int getK() {
        return k;
    }
    
    public JExpression getFamily() {
        return family;
    }
    
    public CType getPlainType() {
        return plainType;
    }
    
    public String toString() {
        StringBuffer res = new StringBuffer();
                
        res.append("ctx("+k+""+").");
        res.append(family);
        res.append(".");
        res.append(plainType);
        
        return res.toString();        
    }
}
