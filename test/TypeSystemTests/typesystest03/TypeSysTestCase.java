package generated.typesystest03;

/**
 * Test local variables
 *
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	final G g = new G();
	g.N n = g.new N();	
		
	public void test() {
	    g.N n = g.new N();
	    this.n = n;
	}	
}

public cclass G {
    public cclass N {}
}
