package org.caesarj.compiler.cclass;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CompilationGraph {
    
    private CompilationNode root = new CompilationNode(this, new CaesarTypeNode(null, "java/lang/Object"));
    
    public CompilationGraph() {
    }
    
    public void generateFrom(TypeGraph completeGraph) {
        Map typeMap = completeGraph.getTypeMap();
        
        for (Iterator it = typeMap.entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode t = (CaesarTypeNode) ((Map.Entry)it.next()).getValue();
            
            List mixinList = t.getMixinList();
            
            // sort list into compilation graph
            CaesarTypeNode[] mixins = new CaesarTypeNode[mixinList.size()];
            mixins = (CaesarTypeNode[])mixinList.toArray(mixins);
            
            CompilationNode current = root;
            
            for (int i=mixins.length-1; i>=0; i--) {
                CaesarTypeNode mixin = mixins[i];
                CompilationNode next = current.getSubNode(mixin.getQualifiedName().toString());
                
                if(next == null) {
                    next = new CompilationNode(this, mixin);
                    current.addSubNode(next);
                }
                                
                current = next;
            }
            
            if(t.isImplicit()) {
                // append as leaf
                CompilationNode next = new CompilationNode(this, t);
                next.setType(t);
                current.addSubNode(next);
            }
            else {
                current.setType(t);
            }
        }
    }
    
    public void debug() {
        root.debug(0);
    }
    
    public void calculateCompilationLevels() {
        
    }
}
