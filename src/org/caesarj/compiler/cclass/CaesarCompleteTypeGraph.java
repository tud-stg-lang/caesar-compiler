package org.caesarj.compiler.cclass;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarCompleteTypeGraph extends CaesarTypeGraph {
    /**
     * checks furtherbindings and initiializes furtherboundLists
     */
    public void checkFurtherbindings() {
        for (Iterator it = getTypeMap().entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode t = (CaesarTypeNode)((Map.Entry)it.next()).getValue();
                        
            CaesarTypeNode tOuter = t.getOuter();
            
            if(tOuter != null) {
                for (Iterator it2 = tOuter.getParents().iterator(); it2.hasNext();) {
                    CaesarTypeNode outerSuper = (CaesarTypeNode) it2.next();
                    
                    CaesarTypeNode furtherbound = outerSuper.lookupInner(t.getQualifiedName().getIdent());
                    if(furtherbound != null) {
                        t.addFurtherbinding(furtherbound);
                    }
                }
            }
        }
    }

    /**
     * uses Mixins1 funtion to create mixinlists for caesar types
     */
    public void generateMixinLists(CaesarExplicitTypeGraph explicitTypeGraph) {
        for (Iterator it = getTypeMap().entrySet().iterator(); it.hasNext();) {
            CaesarTypeNode type = (CaesarTypeNode)((Map.Entry)it.next()).getValue();
            
            type.createMixinList(explicitTypeGraph);
        }
    }
    
    /**
     * adds all implicit relations and types
     */
    public void addImplicitTypesAndRelations() {
        Set visited = new HashSet();
        Set added = new HashSet();
        
        for (Iterator it = getTypeMap().keySet().iterator(); it.hasNext();) {
            ((CaesarTypeNode)getTypeMap().get(it.next())).addImplicitTypesAndRelations(visited, added);
        }
        
        visited.clear();
        
        for (Iterator it = added.iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            getTypeMap().put(item.getQualifiedName(), item);
        }
    }
}
