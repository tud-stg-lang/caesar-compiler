package generated.typesystest09;

/**
 * dependent types in method signatures
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	public final G g = new G();	
	
	public void foo(final G g2, g2.N n) {
	    g2.N nn;
	    nn = n;
	}
}

public cclass G {
    public cclass N {}
}
