package org.caesarj.compiler.cclass;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeSystem {
    private TypeGraph explicitGraph = new TypeGraph();
    private TypeGraph completeGraph = new TypeGraph();
    private CompilationGraph compilationGraph = new CompilationGraph(); 
    
    public TypeGraph getCompleteGraph() {
        return completeGraph;
    }

    public TypeGraph getExplicitGraph() {
        return explicitGraph;
    }
    
    public void generate() {
        explicitGraph.debug();
        System.out.println("----------------------------------");
        completeGraph.addImplicitTypesAndRelations();
        explicitGraph.checkFurtherbindings(completeGraph);
        completeGraph.generateMixinLists(explicitGraph);        
        completeGraph.debug();
        System.out.println("----------------------------------");
        compilationGraph.generateFrom(completeGraph);
        compilationGraph.calculateCompilationLevels();
        compilationGraph.debug();
    }
}
