package generated.typesystest08;

/**
 * family as formal parameter 
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	public final G g = new G();
	
	public void foo(final G g2) {
	    g2.N n1 = g2.new N();
	    g2.N n2 = g2.new N();
	    n1 = n2;
	}
	
//	public void foo(final G g2, g2.N n) {
//	}
}

public cclass G {
    public cclass N {}
}
