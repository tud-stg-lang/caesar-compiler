package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMember;


/**
 * Dependent Types, e.g.: 
 * final G g; g.N n; 
 * 
 * @author Ivica Aracic
 */
public class CDependentType extends CReferenceType {
    
    private CMember[] pos;
    private CType staticType;
    private String[] path;
    private int k = 0;
    
    public CDependentType(CMember[] pos, String[] path, CType staticType) {
        this.pos = pos;
        this.path = path;
        this.staticType = staticType;
    }
    
    public CClass getCClass() {
        return staticType.getCClass();
    }
    
    protected boolean isPosEqual(CMember[] otherPos) {
        if(pos.length != otherPos.length)
            return false;
        
        for (int i = 0; i < otherPos.length; i++) {
            if( pos[i] != otherPos[i] ) 
                return false;
        }
        
        return true;
    }
    
    protected boolean isPathEqual(String[] otherPath) {
        if(path.length != otherPath.length)
            return false;
        
        for (int i = 0; i < otherPath.length; i++) {
            if( !path[i].equals(otherPath[i]) ) 
                return false;
        }
        
        return true;
    }
    
    public boolean isAssignableTo(CTypeContext context, CType dest) {
        // only a dependent type is assignable to another dependet type
        if(dest instanceof CDependentType) {
            CDependentType other = (CDependentType)dest;
            
            if( staticType.isAssignableTo(context, other.getStaticType()) ) {
                if(isPosEqual(other.pos)) {
                    if(isPathEqual(other.path)) {
                        return true;
                    }                
                }
            }
        }
        
        return false;
    }
    

    public boolean isChecked() {
        return staticType.isChecked();
    }

    // not castable for now
    public boolean isCastableTo(CType dest) {
        return false;
    }
    
    public CMember[] getPos() {
        return pos;
    }
    
    public String[] getPath() {
        return path;
    }
    
    public CType getStaticType() {
        return staticType;
    }
    
    public String toString() {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < path.length; i++) {            
            res.append(path[i]);
            res.append('.');        
        }
        
        res.append(staticType);
        
        return res.toString();        
    }
}
