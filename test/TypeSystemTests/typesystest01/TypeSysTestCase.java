package generated.typesystest01;

import junit.framework.TestCase;

/**
 * Test Typesystem
 *
 * @author Ivica Aracic
 */
public class TypeSysTestCase extends TestCase {

	public TypeSysTestCase() {
		super("test");
	}
	
	public static StringBuffer res = new StringBuffer();

	public X x = new X();	
	
	public final G g = new G();
	public g.N n;	// DT: [pos=TypeSysTestCase, k=0, fam=x1.g, PT=G.N]		
		
	class X {
	    Y y = new Y();
	    class Y {
		    public g.N n;
	    }
	}	
	
	public void test() {	
		x.y.n = n; // this(1).x1.n = this(1).n1
		//x2.n = n1;		
	}
	
	/*
	public void foo(g.N _n) {
	    g.N n;
	    n = _n;
	}
	*/
}

public cclass G {
    public cclass E {}
    public cclass UE extends E {}
    public cclass N {}
    public cclass N2 extends N {}
}
