package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.mixer.MixinList;

/**
 * representation of all types defined in source code of current session
 * 
 * @author Ivica Aracic
 */
public class CaesarSourceType {
    
    private JavaQualifiedName qualifiedName;
    private HashMap inners = new HashMap();
    private HashMap subTypes = new HashMap();
    private MixinList mixinList = new MixinList();
    private CaesarSourceType outer = null;
    private CaesarSourceType parent = null;
    
    private JTypeDeclaration declaration = null;
    
    public CaesarSourceType(String qualifiedName) {
        this(new JavaQualifiedName(qualifiedName));
    }  
    
    public CaesarSourceType(JavaQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }  
    
    public MixinList getMixinList() {        
        return mixinList;
    }
    
    public void addSubType(CaesarSourceType subType) {
        subTypes.put(subType.getQualifiedName(), subType);
        subType.parent = this;
    }
    
    public void removeSubType(CaesarSourceType subType) {
        if(subTypes.containsKey(subType.getQualifiedName())) {
            subTypes.remove(subType.getQualifiedName());
            subType.parent = null;
        }
    }
    
    public void addInner(CaesarSourceType inner) {
        inners.put(inner.getQualifiedName(), inner);
        inner.outer = this;
    }
    
    public void removeInner(CaesarSourceType inner) {
        if(inners.containsKey(inner.getQualifiedName())) {
            inners.remove(inner.getQualifiedName());
            inner.outer = null;
        }
    }   

    public CaesarSourceType getParent() {
        return parent;
    }

    public CaesarSourceType getOuter() {
        return outer;
    }
    
    public JavaQualifiedName getQualifiedName() {       
        return qualifiedName;
    }

    public JTypeDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(JTypeDeclaration declaration) {        
        this.declaration = declaration;
    }
    
    public boolean isFurtherbinding() {
        boolean res = false;
        if(outer != null) {
            CaesarSourceType outerSuper = outer.getParent();
            if(outerSuper.lookupInner(qualifiedName.getIdent()) != null) {
                res = true;
            }
        }
        return res;
    }
       
    public CaesarSourceType lookupInner(String ident) {
        CaesarSourceType res = null;
        String className = qualifiedName.asString()+JavaQualifiedName.innerSep+ident;
        if(subTypes.containsKey(className)) {
            res = (CaesarSourceType)subTypes.get(className);
        }
        return res;
    }
    
}
