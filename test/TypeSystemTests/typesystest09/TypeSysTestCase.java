package generated.typesystest09;

/**
 * dependent types in method signatures
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

    final G g1 = null;
    final G g2 = null;
    
    final g1.X x1 = null;
    final g2.X x2 = null;
    
    x1.N n1 = null;
    x2.N n2 = null;
    

	public void foo() {
	    //g2.X x2 = bar(); // should not work
	    g1.X x = bar();
	    x = g1.new X();
	    x = bar();
	    bar(x1);
	    bar(null, null);
	    bar(g1, x1);
	    
	    x = bar(g1);
	    //x = bar(g2); // should not work
	    
	    //x = g1.new X();	    	   
	}
	
	public g1.X bar() {
	    return null;
	}		

	public void bar(g1.X x) {	    
	}		

	public void bar(final G g, g.X x) {	    
	}		

	public g.X bar(final G g) {
	    return null;
	}
}

public cclass B {
}

public cclass G {
    public cclass X {
        public N x() { return null; }
        public cclass N {}
    }    
}
