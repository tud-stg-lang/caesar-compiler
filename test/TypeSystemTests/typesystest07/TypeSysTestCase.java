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
	        g.N nn;
	        nn = n;
	    }
    }
			
	public void test() {}	
}

public cclass G {
    public cclass N {}
}
