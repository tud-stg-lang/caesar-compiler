package generated.typesystest09;

/**
 * dependent types in method signatures
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

    final G g1 = new G();
    final G g2 = new G();
    
    final g1.X x1 = g1.new X();
    final g1.X x2 = g2.new X();
    
    x1.N n1 = x1.new N();
    x2.N n2 = x2.new N();
    
    
    
	public void foo() {	    
	    bar(g2, x1, n1);	    
	}
	
	public void bar(final G graph, graph.X x, x.N n) {
	    // ...
	}

}

public cclass G {
    public cclass X {
        public cclass N {
        }
    }    
}
