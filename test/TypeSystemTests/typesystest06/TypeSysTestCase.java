package generated.typesystest06;

/**
 * Test static families
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	public void test() {
	    Singleton.g.N n1 = Singleton.g.new N();	        
	    Singleton.g.N n2 = Singleton.g.new N();
        n1 = n2;
	}	
}

public class Singleton {
    public static final G g = new G();
}

public cclass G {
    public cclass N {}
}
