package generated.smtest01;

import junit.framework.TestCase;

/**
 * Test Structure Model
 *
 * @author Ivica Aracic
 */
public class SMTestCase extends TestCase {

	public SMTestCase()	{
		super("test");
	}
	
	public static StringBuffer res = new StringBuffer();

	public void test() {
		System.out.println("-------> StructureModel test 01: start");

		deploy(new ClassA()) {
		    PlainClass pc = new PlainClass();
		    pc.m2();
		}

		assertEquals(res.toString(), "123");
		
		System.out.println("-------> StructureModel test 01: end");
	}
}

public interface I {}


public cclass ClassA {
	
    public int fa;
    public String fb;
    
	public ClassA() {}
	
	public void m1() {}

	pointcut p(): call( void PlainClass.m2() );
	
	before() : p() {
	    SMTestCase.res.append("1");
	}	

	after() : p() {
	    SMTestCase.res.append("3");
	}	
}


public class PlainClass {	
    
    public String fc;
    
	public void m2() {
	    SMTestCase.res.append("2");
	}
}