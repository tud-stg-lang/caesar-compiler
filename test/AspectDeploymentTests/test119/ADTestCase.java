package generated.test119;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test deploy block robustness
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
    	":before cutAA-a:before cutAA-b:A.A" +
    	":before cutAA-a:before cutAA-b:A.A" +
    	":before cutAA-a:A.A" +
    	":before cutAA-a:A.A" +
    	":A.A";

    public void test()
    {
		System.out.println("-------> ADTest 19: Deploy robustness: start");

        new DeployA().test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 19: end");
    }
}

public cclass DeployA
{
    public void test()
    {
		OuterA oa = new OuterA();

		AspectA a = new AspectA().init("a");
		AspectA b = null;
		Object  c = new AspectA().init("b");
		Object  d  = new ClassA();

		deploy(a) // normal Caesar aspect
        {
			deploy(b) // null object
			{
				deploy(c) // non-Caesar static type
				{
					deploy(d) // non-Caesar dynamic type
					{
						oa.doA();
					}

					oa.doA(); // test if correctly undeployed
				}

				oa.doA(); // test if correctly undeployed
			}

			oa.doA(); // test if correctly undeployed
        }

        oa.doA(); // test if correctly undeployed
    }
}

cclass AspectA
{
	private String _id;

	public AspectA init(String id)
	{
		_id = id;
		return this;
	}

	pointcut cutAA() : call(* OuterA+.doA(..));

	before() : cutAA()
	{
		ADTestCase.result.append(":before cutAA-" + _id);
	}
}


public cclass OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":A.A");
	}
}

public class ClassA
{

}
