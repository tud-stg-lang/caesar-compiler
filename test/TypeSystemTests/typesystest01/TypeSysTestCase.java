package generated.typesystest01;

import junit.framework.TestCase;

/**
 * Test Typesystem
 *
 * @author Ivica Aracic
 */
public class TypeSysTestCase extends TestCase {

	public TypeSysTestCase()	{
		super("test");
	}
	
	public static StringBuffer res = new StringBuffer();
	
	final G g1 = new G();
	final G g2 = new G();	
	g1.N x = g1.new N();
	g1.N y = x;
	
	
	public void test() {	
	    g1.N n1 = null;
	    g1.N n2 = null;
	    
	    {
	    	g1.N n3 = null;
	    	
	    	// ctx(1).n1 = ctx(1).n2
			n1 = n2;	    
			
			for(int i=0; i<0; i++) {
			
				// ctx(3).g1.N n4 
				// @ generated.typesystest01 -> TypeSysTestCase -> test -> B -> B -> B
				g1.N n4 = null;
			}
		}
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
}
