package generated;

import junit.framework.TestCase;

public class CaesarTestCase_1 extends TestCase {

	public CaesarTestCase_1() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public String expectedResult =
		":foo:before foo:foo:before foo:foo:after foo";

    public void test() {
        new TestCase_1(result).test();
        assertEquals(expectedResult, result.toString());
    }
}

cclass TestCase_1 {
    
    StringBuffer result;
    
    public TestCase_1(StringBuffer result) {
        this.result = result;
    }

	/**
	 * tests inheriting aspects
	 *
	 */
	public void test() {
		deploy(new TestCase_1_Impl.InnerAspect_Impl()) {
			foo();
		}

		deploy(new TestCase_1_Impl.InnerAspect_Sub_Impl()) {
			foo();
		}

		deploy(new TestCase_1_Impl.InnerAspect_Sub_Sub_Impl()) {
			foo();
		}	
	}

	public void foo() {
		result.append(":foo");
	}

	cclass InnerAspect {
		pointcut fooCall() : call(* TestCase_1.foo());
		before() : fooCall() {
			System.out.println("just for testing");
		}
	}

	cclass InnerAspect_Sub extends InnerAspect {
		before() : fooCall() {
			result.append(":before foo");	
		}

	}

	cclass InnerAspect_Sub_Sub extends InnerAspect_Sub {
		after() : fooCall() {
			result.append(":after foo");
		}
	}
	
	
	
}
