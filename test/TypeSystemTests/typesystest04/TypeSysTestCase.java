package generated.typesystest04;

/**
 * Test local families
 *
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	public void test() {
	    final G g = new G();
	    g.N n1 = g.new N();
	    g.N n2 = g.new N();
	    n1 = n2;
	}	
}

public cclass G {
    public cclass N {}
}
