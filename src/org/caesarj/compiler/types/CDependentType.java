package org.caesarj.compiler.types;

import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;


/**
 * Dependent Types, e.g.: 
 * final ctx(1).G g; ctx(0).g.N n; 
 * 
 * @author Ivica Aracic
 */
public class CDependentType extends CReferenceType {
    
    private CClassContext pos;         /** position of this type */
    
    private CType plainType;    /** static type of the */
    
    private JFieldAccessExpression family; /** family expression */
    
    private int k = 0;          /** ctx(k); determines how many steps to go out 
                                    of the current context */
              
    
    public CDependentType(CClassContext pos, int k, JFieldAccessExpression family, CType staticType) {
        this.pos = pos;
        this.k = k;
        this.family = family;
        this.plainType = staticType;
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
                
                /*
                String p1 = makePath();
                String p2 = other.makePath();
                System.out.println("this: "+p1+"    other: "+p2);
                
                return p1.equals(p2);
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
    
    public JFieldAccessExpression getFamily() {
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
