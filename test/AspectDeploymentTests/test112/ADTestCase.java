package generated.test112;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test deployment of multiple instances with around advices
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

    public String expectedResult = ":bar1:bar2:bar3:bar4:foo";

    public void test()
    {
		System.out.println("-------> ADTest 12: Deploy Multiple Instances with Around : start");

        new DeployA().test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 12: end");
    }

    public static char foo(String s)
    {
        result.append(":foo");
        return 'a';
    }
}

public cclass DeployA
{
    public void test()
    {
		deploy(new AspectA().init(1))
        {
            deploy(new AspectA().init(2))
            {
                deploy(new AspectA().init(3))
                {
                    deploy(new AspectA().init(4))
                    {
                        ADTestCase.foo("string");
                    }
                }
            }
        }
    }
}

public cclass AspectA
{
	public AspectA init(int i)
	{
		this.i = i;
		return this;
	}

	private int i;

	pointcut callFoo(String str) : call(* ADTestCase.foo(..)) && args(str);

	char around(String s) : callFoo(s)
	{
		ADTestCase.result.append(":bar"+i);
		return proceed(s);
	}
}
