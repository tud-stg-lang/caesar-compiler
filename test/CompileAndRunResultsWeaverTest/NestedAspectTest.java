package generated;

import junit.framework.TestCase;

public cclass NestedAspectTest extends TestCase {

	public NestedAspectTest() {
		super("test");
	}

	public void test() {
		NestedAspect aspect = new NestedAspect();
		deploy(aspect) {
			xyz();
		}
	}

	public void xyz() {
		System.out.println("NestedAspectTest: m");
	}

	public cclass NestedAspect {
		pointcut callMethod() : call(* xyz(..));
		
		before() : callMethod() {
			System.out.println("NestedAspect: Before xyz");
		}
	}

}