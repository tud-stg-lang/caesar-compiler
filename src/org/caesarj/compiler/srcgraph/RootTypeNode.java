package org.caesarj.compiler.srcgraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ... 
 * 
 * @author Ivica Aracic 
 */
public class RootTypeNode extends TypeNode {

    RootTypeNode() {
        super();
    }

    public String getName() {        
        return "<ROOT>";
    }

    public void debug() {
		debug("", new HashSet());
	}


    public void calculateLevel(int i) {
        this.level = i;
        for(Iterator it=getSubTypes().iterator(); it.hasNext(); ) {
            TypeNode subNode = (TypeNode)it.next();
            subNode.calculateLevel(level);
        }
    }
    
    
}
