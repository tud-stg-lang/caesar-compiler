package org.caesarj.compiler.srcgraph;

import java.util.Iterator;

import org.caesarj.compiler.types.CCompositeType;

/**
 * ... 
 * 
 * @author Ivica Aracic 
 */
public class CompositeTypeNode extends TypeNode {

    public CompositeTypeNode(CCompositeType compositeType) {
        super();
        this.compositeType = compositeType;
    }

    public String getName() {
        return compositeType.getQualifiedName();
    }

    public void calculateLevel(int i) {        
        this.level = i+1;
        for(Iterator it=getSubTypes().iterator(); it.hasNext(); ) {
            TypeNode subNode = (TypeNode)it.next();
            if(subNode.level < level)
                subNode.calculateLevel(level);
        }
    }

    public CCompositeType getCompositeType() {
        return compositeType;
    }

    private CCompositeType compositeType;
}
