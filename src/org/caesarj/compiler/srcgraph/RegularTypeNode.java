package org.caesarj.compiler.srcgraph;

import java.util.Iterator;

import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.export.CClass;

/**
 * ... 
 * 
 * @author Ivica Aracic 
 */
public class RegularTypeNode extends TypeNode {

    private byte[] byteCode;
    private JTypeDeclaration typeDeclaration;   
    private CClass clazz;   

    RegularTypeNode(CClass clazz) {
        super();
        this.clazz = clazz;
    }

    public byte[] getByteCode() {
        return byteCode;
    }

    public void setByteCode(byte[] bs) {
        byteCode = bs;
    }

    public CClass getCClass() {
        return clazz;
    }

    public String getName() {        
        return clazz.getQualifiedName();
    }

    public JTypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    public void setTypeDeclaration(JTypeDeclaration declaration) {
        typeDeclaration = declaration;
    }

    public void setEnabled(boolean enabled) {
        if(typeDeclaration != null)
            typeDeclaration.setEnabled(enabled);
    }

    public boolean isEnabled() {
        if(typeDeclaration != null)
            return typeDeclaration.isEnabled();
        else
            return true;
    }

    public void calculateLevel(int i) {
        this.level = i;
        for(Iterator it=getSubTypes().iterator(); it.hasNext(); ) {
            TypeNode subNode = (TypeNode)it.next();
            if(subNode.getLevel() < level)
                subNode.calculateLevel(level);
        }
    }
}
