package org.caesarj.compiler.cclass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.ldap.HasControls;

import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.mixer.Linearizator;
import org.caesarj.mixer.MixerException;
import org.caesarj.mixer.MixinList;
import org.caesarj.util.InconsistencyException;

/**
 * representation of all types defined/used in source code of current session
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeNode {
    
    private TypeGraph typeGraph;
    
    private JavaQualifiedName qualifiedName;
    private HashMap inners = new HashMap();
    private HashMap subTypes = new HashMap();
   
    private boolean furtherBindingChecked = false;
    private boolean furtherbinding = false;
    
    private boolean mixinListCreated = false;
    private List mixinList = new LinkedList(); // of CaesarTypeNode    
   
    private CaesarTypeNode outer = null;
    private List parents = new LinkedList(); // of CaesarType
    
    private JTypeDeclaration declaration = null; // TODO    
       
    private boolean binary = false;
    
    
    public CaesarTypeNode(TypeGraph typeGraph, String qualifiedName) {
        this(typeGraph, new JavaQualifiedName(qualifiedName));
    }  
    
    public CaesarTypeNode(TypeGraph typeGraph, JavaQualifiedName qualifiedName) {
        this.typeGraph = typeGraph;
        this.qualifiedName = qualifiedName;
    }  
    
    public void addSubType(CaesarTypeNode subType) {
        if(!subTypes.containsKey(subType.getQualifiedName().toString())) {
            subTypes.put(subType.getQualifiedName().toString(), subType);
            subType.parents.add(this);
        }
    }
    
    public void removeSubType(CaesarTypeNode subType) {
        if(subTypes.containsKey(subType.getQualifiedName().toString())) {
            subTypes.remove(subType.getQualifiedName().toString());
            subType.parents.remove(this);
        }
    }
    
    public void addInner(CaesarTypeNode inner) {
        inners.put(inner.getQualifiedName().toString(), inner);
        inner.outer = this;
    }
    
    public void removeInner(CaesarTypeNode inner) {
        if(inners.containsKey(inner.getQualifiedName().toString())) {
            inners.remove(inner.getQualifiedName().toString());
            inner.outer = null;
        }
    }   
    
    public void createMixinList(TypeGraph explicitGraph) {
        if(mixinListCreated) return;
        
        List outerMixinList = null;
        
        if(outer != null) {
            outer.createMixinList(explicitGraph);
            outerMixinList = outer.getMixinList();
        }
        
        createMixinList(explicitGraph, mixinList, outerMixinList, 0, qualifiedName);
        
        mixinListCreated = true;
    }
        
    private void createMixinList(
        TypeGraph explicitGraph,
        List mixinListToAppend,
        List outerMixinList, 
        int m,         
        JavaQualifiedName qualifiedName        
    ) {
        CaesarTypeNode currentMixin;
        CaesarTypeNode t;

        if(outerMixinList != null) {
            currentMixin = (CaesarTypeNode)outerMixinList.get(m);
            t = currentMixin.lookupInner(qualifiedName.getIdent());
        }
        else {
            t = explicitGraph.getType(qualifiedName.toString());
        }
        
        if(t != null) {
            
            mixinListToAppend.add(t);
            
            if(t.isFurtherbinding()) {                
                createMixinList(explicitGraph, mixinListToAppend, outerMixinList, m+1, qualifiedName);
            }
            else if(t.getParents().size() == 1) {
                CaesarTypeNode superType = (CaesarTypeNode)t.getParents().get(0);
                
                createMixinList(
                    explicitGraph, 
                    mixinListToAppend, 
                    outerMixinList, 
                    0, 
                    superType.getQualifiedName()
                );
            }
            else if(t.getParents().size() > 1) {                
                List[] superClasses = new List[t.getParents().size()];
                
                int i = 0;
                for (Iterator it = t.getParents().iterator(); it.hasNext();) {
                    CaesarTypeNode superType = (CaesarTypeNode) it.next();
                    
                    superType.createMixinList(explicitGraph);
                    
                    superClasses[i++] = superType.getMixinList();
                }
                                                
                try {
                    // linearize
                    List mergedList = Linearizator.instance().mix(superClasses);
                    
                    // append
                    for(Iterator it=mergedList.iterator(); it.hasNext(); ) {
                        mixinListToAppend.add(it.next());
                    }
                }
                catch (MixerException e) {
                    e.printStackTrace();
                }
                
                
            }
        }
        else if (m < outerMixinList.size()-1) {
            createMixinList(explicitGraph, mixinListToAppend, outerMixinList, m+1, qualifiedName);
        }
    }
    
    
    
    public void addImplicitTypesAndRelations(Set visited, Set added) {
        if(visited.contains(this)) return;
        
        for(Iterator it = parents.iterator(); it.hasNext();) {
            CaesarTypeNode parent = (CaesarTypeNode) it.next();
            parent.addImplicitTypesAndRelations(visited, added);
        }

                    
        // add implicit relations
        for(Iterator it = parents.iterator(); it.hasNext(); ) {
            CaesarTypeNode superClass = (CaesarTypeNode)it.next();
            
            // add implicit inner types and inheritance relation between virtual and further-binding
            for(Iterator it2 = superClass.getInners().keySet().iterator(); it2.hasNext(); ) {
                CaesarTypeNode virtualType = 
                    (CaesarTypeNode)superClass.getInners().get(it2.next());
                
                virtualType.addImplicitTypesAndRelations(visited, added);
                
                CaesarTypeNode furtherbindingType = 
                    lookupInner(virtualType.getQualifiedName().getIdent());
                
                if(furtherbindingType == null) {
                    // type doesn't exist -> create
                    furtherbindingType = new CaesarTypeNode(
                        typeGraph,
                        qualifiedName.toString()+JavaQualifiedName.innerSep+(virtualType.getQualifiedName().getIdent())
                    );                        
                    this.addInner(furtherbindingType);
                    added.add(furtherbindingType);
                }
            
                // type exists already in the graph -> just create implicit relation
                virtualType.addSubType(furtherbindingType);
            }
            
            // now establish inherited inheritance relations among created inners
            for(Iterator it2 = superClass.getInners().keySet().iterator(); it2.hasNext(); ) {
                CaesarTypeNode virtualType = 
                    (CaesarTypeNode)superClass.getInners().get(it2.next());
                
                for (Iterator it3 = virtualType.getParents().iterator(); it3.hasNext();) {
                    CaesarTypeNode virtualTypeSuper = (CaesarTypeNode) it3.next();
                    // we need only inheritance relations inside the enclosing class
                    if(virtualTypeSuper.getOuter() == virtualType.getOuter()) {
                        CaesarTypeNode furtherbindingType = 
                            lookupInner(virtualType.getQualifiedName().getIdent());
                        
                        CaesarTypeNode virtualTypeSuperFurtherbinding = 
                            lookupInner(virtualTypeSuper.getQualifiedName().getIdent());
                        
                        // connect
                        virtualTypeSuperFurtherbinding.addSubType(furtherbindingType);
                    }
                }
            }
        }
        
        visited.add(this);
        
        // recurse into inners
        for (Iterator it = inners.keySet().iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode)inners.get(it.next());
            item.addImplicitTypesAndRelations(visited, added);
        }
        
        // recurse into subTypes
        for (Iterator it = subTypes.keySet().iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode)subTypes.get(it.next());
            item.addImplicitTypesAndRelations(visited, added);
        }
                
    }
          
    public List getMixinList() {
        if(!mixinListCreated) throw new InconsistencyException("mixin list has to be created first");
        return mixinList;
    }
    
    public HashMap getInners() {
        return inners;
    }

    public CaesarTypeNode lookupInner(String ident) {
        String className = qualifiedName.toString() + JavaQualifiedName.innerSep + ident;
        return (CaesarTypeNode)inners.get(className);
    }
    
    public List getParents() {
        return parents;
    }

    public CaesarTypeNode getOuter() {
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
    
    
    /**
     * NOTE: this will only work in a complete type graph
     */
    public boolean isFurtherbinding() {
        if(!furtherBindingChecked) throw new InconsistencyException("furtherbinding not checked yet");
        return furtherbinding;
    }
    
    public void setFurtherbinding(boolean furtherbinding) {
        this.furtherBindingChecked = true;
        this.furtherbinding = furtherbinding;
    }
        
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(qualifiedName);
        if(parents.size() > 0) {
            res.append(" extends ");
            for(Iterator it = parents.iterator(); it.hasNext();) {
                res.append(((CaesarTypeNode)it.next()).getQualifiedName());
                if(it.hasNext())
                    res.append(", ");
            }            
        }

        res.append("; outer = ");
        if(outer==null)
            res.append("null");
        else
            res.append(outer.getQualifiedName());
        
        res.append('\n');
        
        res.append("\t[");
        for (Iterator it = mixinList.iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            res.append(item.getQualifiedName().getOuterPrefix()+item.getQualifiedName().getIdent());
            if(it.hasNext())
                res.append(", ");
        }
        res.append(']');

        return res.toString();
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public TypeGraph getTypeGraph() {
        return typeGraph;
    }

    public boolean isImplicit() {
        return 
            !((CaesarTypeNode)mixinList.get(0)).getQualifiedName().equals(qualifiedName);
    }
    
    
    public boolean equals(Object other) {
        return getQualifiedName().equals(((CaesarTypeNode)other).getQualifiedName());
    }
    
    
    public int hashCode() {
        return getQualifiedName().hashCode();
    }
}
