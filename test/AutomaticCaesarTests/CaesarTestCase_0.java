package generated;

import junit.framework.TestCase;

public class CaesarTestCase_0 extends TestCase {

    public StringBuffer result = new StringBuffer();

    public String expectedResult =
        "|before-foo|foo|subbefore-foo|before-foo|before-foo|foo";        

    public CaesarTestCase_0() {
        super("test");
    }
    
    public void test() {
        new TestCase_0(result).test();
        assertEquals(expectedResult, result.toString());
    }
}

cclass TestCase_0 {    
    StringBuffer result;
    
    public TestCase_0(StringBuffer result) {
        this.result = result;
    }

	public void test() {
		deploy(new TestCase_0_Impl.InnerAspect_Impl()) {
			foo();
            
            deploy(new TestCase_0_Impl.InnerAspect_Sub_Impl()) {
                foo();
            }
		}
	}

	public void foo() {
		result.append("|foo");
	}

	cclass InnerAspect {
	    pointcut fooCall() : call(* TestCase_0.foo());
	
	    before() : fooCall() {
		  result.append("|before-foo");	
	    }
	}
	
    cclass InnerAspect_Sub extends InnerAspect {
        before() : fooCall() {
            result.append("|subbefore-foo");	
		}
	}
}
