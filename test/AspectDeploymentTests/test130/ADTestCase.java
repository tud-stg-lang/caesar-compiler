package generated.test130;

import junit.framework.TestCase;

/**
 * Tests joinpoint throwing exception
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("test");
	}

	public void test()
	{
		System.out.println("-------> ADTest 30");
		
		Test test = new Test();

		deploy(new Aspect()) {
			test.foo();
		}

		System.out.println("-------> ADTest 30: end");
	}
}

public class Test {
    public void foo() throws RuntimeException {}
}

public cclass Aspect {
	
	pointcut callFoo() : call(* Test.foo(..));
	
	after() throwing(RuntimeException e) : callFoo() {
		System.out.println("Success");
		thisJoinPointStaticPart.toString();		
	}	
}