package generated;

import junit.framework.TestCase;

public class NestedAspectTest extends TestCase {

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

	public class NestedAspect {
		pointcut callMethod() : call(* xyz(..));
		
		before() : callMethod() {
			System.out.println("NestedAspect: Before xyz");
		}
	}

}