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

	public X x1 = new X();	
	public X x2 = new X();	
	
	public x1.g.N n1;
	public x2.g.N n2;
	
	class X {
		public final G g = new G();		
		public g.N n;
	}
	
	public void test() {	
		x1.n = n1;
		//x2.n = n1;
		
		x2.n = x2.g.new N2();
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
