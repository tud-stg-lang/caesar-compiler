package org.caesarj.compiler.cclass;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeSystem {
    private CaesarTypeGraph explicitGraph = new CaesarTypeGraph();
    private CaesarTypeGraph completeGraph = new CaesarTypeGraph();
    private JavaTypeGraph javaGraph = new JavaTypeGraph(); 
    
    public CaesarTypeGraph getCompleteGraph() {
        return completeGraph;
    }

    public CaesarTypeGraph getExplicitGraph() {
        return explicitGraph;
    }
    
    public JavaTypeGraph getJavaGraph() {
        return javaGraph;
    }
    
    public void generate() {
        explicitGraph.debug();
        System.out.println("----------------------------------");
        completeGraph.addImplicitTypesAndRelations();
        explicitGraph.checkFurtherbindings(completeGraph);
        completeGraph.generateMixinLists(explicitGraph);        
        completeGraph.debug();
        System.out.println("----------------------------------");
        javaGraph.generateFrom(completeGraph);
        javaGraph.debug();
    }
}
