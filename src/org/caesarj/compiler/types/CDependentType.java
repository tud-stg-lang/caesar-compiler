package org.caesarj.compiler.types;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;


/**
 * Dependent Types, e.g.: 
 * final ctx(1).G g; ctx(0).g.N n; 
 * 
 * @author Ivica Aracic
 */
public class CDependentType extends CReferenceType {
    
    private JPhylum[] pos; 	   	/** determines the position in the ast 
                                    (starting with the class) */
    
    private CType plainType;    /** static type of the */
    
    private JExpression family; /** family expression */
    
    private int k = 0;          /** ctx(k); determines how many steps to go out 
                                    of the current context */
    
    public CDependentType(JExpression family, CType staticType) {
        this.family = family;
        this.plainType = staticType;
    }
    
    public CClass getCClass() {
        return plainType.getCClass();
    }
    
    public JPhylum[] getPos() {
        return pos;
    }
    
    public void setPos(JPhylum[] path) {
        this.pos = path;
    }
    
    public boolean isAssignableTo(CTypeContext context, CType dest) {
        // only a dependent type is assignable to another dependet type
        if(dest instanceof CDependentType) {
            CDependentType other = (CDependentType)dest;
            
            if( plainType.isAssignableTo(context, other.getPlainType()) ) {
                return true;
            }
        }
        
        return false;
    }
    

    public boolean isChecked() {
        return plainType.isChecked() && pos != null;
    }

    // not castable for now
    public boolean isCastableTo(CType dest) {
        return false;
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
