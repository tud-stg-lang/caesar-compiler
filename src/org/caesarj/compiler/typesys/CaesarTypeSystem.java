package org.caesarj.compiler.typesys;

import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
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
	
	public void generate() {
		DumpTypesVisitor dumpTypesVisitor = new DumpTypesVisitor(caesarTypeGraph);
		
		//dumpTypesVisitor.run();
		
		new AddImplicitTypesAndRelationsVisitor(caesarTypeGraph).run();
		new FurtherbindingVisitor(caesarTypeGraph).run();
		new MixinListVisitor(caesarTypeGraph).run();
		
		dumpTypesVisitor.run();
		
		javaTypeGraph.generateFrom(caesarTypeGraph);
    }
}
