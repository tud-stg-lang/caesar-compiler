package org.caesarj.compiler.typesys;

import org.apache.log4j.Logger;
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
    
    private static Logger log = Logger.getLogger(CaesarTypeSystem.class); 
    
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
                    
        log.debug(classQn+" in context of "+contextClassQn);
        log.debug("\t->"+res);
        
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

    public boolean isIncrementOf(String qnFurtherbinding, String qnFurtherbound) {
        CaesarTypeNode furtherbinding = 
            getCaesarTypeGraph().getType(new JavaQualifiedName(qnFurtherbinding));
        
        CaesarTypeNode furtherbound =
            getCaesarTypeGraph().getType(new JavaQualifiedName(qnFurtherbound));
        
        if(furtherbinding!=null && furtherbound!=null) {
            return furtherbinding.isIncrementFor(furtherbound);
        }            
        
        return false;
    }
}
