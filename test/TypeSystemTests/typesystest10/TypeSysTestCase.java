package generated.typesystest09;

/**
 * result type depends on parameter 
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	public g.N foo(final G g) {
	    return g.new N();
	}
	
	public void bar(){
	    final G g = new G();
	    g.N n = foo(g);
	}
}

public cclass G {
    public cclass N {}
}
