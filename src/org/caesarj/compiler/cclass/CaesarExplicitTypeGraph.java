package org.caesarj.compiler.cclass;

import java.util.Iterator;
import java.util.Map;

import org.caesarj.util.InconsistencyException;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarExplicitTypeGraph extends CaesarTypeGraph {
    /**
     * copys furtherbinding information from a complete graph to this graph
     * generateMixinLists depends on furtherbinding information
     */
    public void setFurtherbindings(CaesarCompleteTypeGraph completeGraph) {
        for (Iterator it = getTypeMap().entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode t = (CaesarTypeNode)((Map.Entry)it.next()).getValue();
            
            CaesarTypeNode tInCompleteGraph =
                completeGraph.getType(t.getQualifiedName());
            
            if(tInCompleteGraph == null)
                throw new InconsistencyException("explicit graph should be subgraph of complete graph");
            
            for (Iterator it2 = tInCompleteGraph.getFurtherboundList().iterator(); it2.hasNext();) {
                CaesarTypeNode furtherboundInCompleteGraph = (CaesarTypeNode) it2.next();
                CaesarTypeNode furtherbound = getType(furtherboundInCompleteGraph.getQualifiedName());
                
                t.addFurtherbinding(furtherbound);        
            }
        }
    }

}
