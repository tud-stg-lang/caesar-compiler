package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CCompositeType;
import org.caesarj.mixer.MixinList;

/**
 * This is bidirectional inheritance graph 
 * for JTypeDeclaration 
 * 
 * @author Ivica Aracic 
 */
public class TypeGraph {

    /** root of inner and inheritance hierarchy */
    private Set topClassRoot    = new HashSet();
    private Set inheritanceRoot = new HashSet();
    private HashMap typeMap = new HashMap();

    public TypeGraph() {
    }
    
    public Iterator iterator() {
        return typeMap.values().iterator();
    }
    
    public boolean hasType(String qualifiedName) {
        return typeMap.containsKey(qualifiedName);
    }
    
    public CaesarType getType(String qualifiedName) {
        CaesarType res = (CaesarType)typeMap.get(qualifiedName);
        if(res == null) {
            res = new CaesarType(qualifiedName);
            typeMap.put(qualifiedName, res);
        }
        
        return res;
    }
    
    public void generateMixinLists(TypeGraph explicitTypeGraph) {
        for (Iterator it = typeMap.entrySet().iterator(); it.hasNext();) {
            CaesarType type = (CaesarType)((Map.Entry)it.next()).getValue();
            
            type.createMixinList(explicitTypeGraph);
        }
    }
    
    /**
     * adds all implicit relations and types
     */
    public void addImplicitTypesAndRelations() {
        Set visited = new HashSet();
        Set added = new HashSet();
        
        for (Iterator it = typeMap.keySet().iterator(); it.hasNext();) {
            ((CaesarType)typeMap.get(it.next())).addImplicitTypesAndRelations(visited, added);
        }
        
        visited.clear();
        
        for (Iterator it = added.iterator(); it.hasNext();) {
            CaesarType item = (CaesarType) it.next();
            typeMap.put(item.getQualifiedName(), item);
        }
    }
    
    public void debug() {
        for (Iterator it = typeMap.keySet().iterator(); it.hasNext();) {
            System.out.println(typeMap.get(it.next()));
        }
    }

    public Set getInheritanceRoot() {
        return inheritanceRoot;
    }

    public Set getTopClassRoot() {
        return topClassRoot;
    }
}
