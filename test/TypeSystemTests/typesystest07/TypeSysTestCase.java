package generated.typesystest07;

/**
 * Test class nesting
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {
	public final G g = new G();	
	g.N n = g.new N();
	
	public class X {	    
	    public void test() {
	        g.N nn; // <- CP: $this0
	        nn = n; // <- CP: -- " --
	    }

	    
	    public class Y {	    
		    public void test() {
		        g.N nnn; // <- CP: accessor method 
		        nnn = n; // <- CP: -- " --
		    }
	    }
    }	
			
	public void test() {}	
}

public cclass G {
    public cclass N {}
}
