package generated.typesystest05;

/**
 * Test nesting in methods
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {
	final G g = new G();
	
	public void test() {	    	    
	    {
	        g.N n1 = g.new N();
	        {
	            g.N n2 = g.new N();
	            {
	                n1 = n2;
	            }
	        }
	    }	    
	}	
}

public cclass G {
    public cclass N {}
}
