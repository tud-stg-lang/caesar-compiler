package generated.typesystest02;

/**
 * Simple Family Test
 *
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	final G g = new G();
	g.N n1 = g.new N();
	g.N n2 = g.new N();
		
	public void test() {
	    n1 = n2;
	    n2 = n1;
	}	
}

public cclass G {
    public cclass N {}
}
