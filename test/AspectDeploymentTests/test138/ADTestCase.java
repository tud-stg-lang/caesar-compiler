package generated.test138;

import junit.framework.TestCase;

/**
 * Test linearization of crosscuts
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{

	public ADTestCase()
	{
		super("test");
	}

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		":before testA:before testB:before testC:test";

	public void test()
	{
		System.out.println("-------> ADTest 38: Linearization of crosscuts: start");
		
		new Test().test();
		
		System.out.println(result.toString());
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 38: end");
	}
}

public cclass Test
{
    public void test()
    {
    	ADTestCase.result.append(":test");    	
    }
}

abstract public cclass AspectA
{
	before() : call(* Test.test())
	{
		ADTestCase.result.append(":before testA");
	}
}

abstract public cclass AspectB
{
	before() : call(* Test.test())
	{
		ADTestCase.result.append(":before testB");
	}
}

abstract public cclass AspectC
{
	before() : call(* Test.test())
	{
		ADTestCase.result.append(":before testC");
	}
}

abstract public cclass AspectAB extends AspectA & AspectB {
}

abstract public cclass AspectAC extends AspectA & AspectC {
}

abstract public cclass AspectBC extends AspectB & AspectC {
}

abstract public cclass AspectABC extends AspectA & AspectB & AspectC {
}

deployed public cclass AspectAll 
	extends AspectA & AspectB & AspectC & 
			AspectAB & AspectAC & AspectBC & AspectABC {	
}



