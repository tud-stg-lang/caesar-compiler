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
    final g2.X x2 = g2.new X();
    
    x1.N n1 = x1.new N();
    x2.N n2 = x2.new N();
    

    B b = new B();
    b.g.X x = null;
    
	public void foo() {	    
	    bar(g1, x1, n1);
	    bar(x1); 
	    
	    b.bar(x);
	    //b.x.bar(x);
	    
	    g1.X x;
	    x = bar();
	}

	public g1.X bar() {
	    return null;
	}	
	
	/*
	public g.X bar(final G g) {
	    return null;
	}
	*/
	
	
	public void bar(final G graph, graph.X x, x.N n) {}

	public void bar(g1.X x) {}

}

public cclass B {
    public G g = new G();
    
    public void bar(g.X x) {
    }
    
    public X x = new X();
    public cclass X {
        public void bar(g.X x) { }
    }
}

public cclass G {
    public cclass X {
        public cclass N {}
    }    
}
