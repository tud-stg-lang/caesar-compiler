package org.caesarj.compiler.typesys;

import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeGraph;
import org.caesarj.compiler.typesys.visitor.AddImplicitTypesAndRelationsVisitor;
import org.caesarj.compiler.typesys.visitor.DumpTypesVisitor;
import org.caesarj.compiler.typesys.visitor.FurtherbindingVisitor;
import org.caesarj.compiler.typesys.visitor.MixinListVisitor;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeSystem {
	private CaesarTypeGraph caesarTypeGraph = new CaesarTypeGraph();
	private JavaTypeGraph javaTypeGraph = new JavaTypeGraph();

	public CaesarTypeGraph getCaesarTypeGraph() {
		return caesarTypeGraph;
	}
	
	public JavaTypeGraph getJavaTypeGraph() {
		return javaTypeGraph;
	}
	
	public String findInContextOf(String classQn, String contextClassQn) {
	    String res = null;
	    
	    CaesarTypeGraph g = getCaesarTypeGraph();
        CaesarTypeNode prefixN = caesarTypeGraph.getType(new JavaQualifiedName(classQn));
        CaesarTypeNode contextN = caesarTypeGraph.getType(new JavaQualifiedName(contextClassQn));
        
        CaesarTypeNode n = prefixN.getTypeInContextOf(contextN);
        
        if(n != null)
            res = n.getQualifiedName().toString();
            
        System.out.println(classQn+" in context of "+contextClassQn+"\n\t-> "+res);
        
        return res;
    }
	
	public void generate() {
		DumpTypesVisitor dumpTypesVisitor = new DumpTypesVisitor(caesarTypeGraph);
		
		//dumpTypesVisitor.run();
		
		new AddImplicitTypesAndRelationsVisitor(caesarTypeGraph).run();
		new FurtherbindingVisitor(caesarTypeGraph).run();
		new MixinListVisitor(caesarTypeGraph).run();
		
		dumpTypesVisitor.run();
		
		javaTypeGraph.generateFrom(caesarTypeGraph);
		javaTypeGraph.debug();
    }
}
