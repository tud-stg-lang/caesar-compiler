package generated.test118;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Advices on inherited pointcuts
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
    	":before cutAA:before cutBA:before cutCA:before cutDA:A.A";

    public void test()
    {
		System.out.println("-------> ADTest 18: Advices on inherited poincuts: start");

        new DeployA().test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 18: end");
    }
}

public cclass DeployA
{
    public void test()
    {
		OuterA oa = new OuterA();

		deploy(new AspectD())
        {
        	oa.doA();
        }
    }
}

public cclass AspectA
{
	pointcut cutAA() : call(* OuterA+.doA(..));
}

public cclass AspectB extends AspectA
{
	pointcut cutBA() : call(* OuterA+.doA(..));
}

public cclass AspectC extends AspectA
{
	pointcut cutCA() : call(* OuterA+.doA(..));
}

public cclass AspectD extends AspectB & AspectC
{
	pointcut cutDA() : call(* OuterA+.doA(..));

	before() : cutAA()
	{
		ADTestCase.result.append(":before cutAA");
	}

	before() : cutBA()
	{
		ADTestCase.result.append(":before cutBA");
	}

	before() : cutCA()
	{
		ADTestCase.result.append(":before cutCA");
	}

	before() : cutDA()
	{
		ADTestCase.result.append(":before cutDA");
	}
}

public cclass OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":A.A");
	}
}

