package generated;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * deployment of multiple instances vs. around advices
 */
public class CaesarTestCase_12 extends TestCase
{
    public CaesarTestCase_12()
    {
        super("test");
    }

    public static StringBuffer result = new StringBuffer();

    public String expectedResult = ":bar1:bar2:bar3:bar4:foo";

    public void test()
    {
        new TestCase_12_Impl(null).test();
        assertEquals(expectedResult, result.toString());
    }

    public static char foo(String s)
    {
        result.append(":foo");
        return 'a';
    }
}

public cclass TestCase_12
{
    public void test()
    {
		Aspect_12 asp1 = new Aspect_12_Impl(null);
		asp1.init(1);

		Aspect_12 asp2 = new Aspect_12_Impl(null);
		asp2.init(2);

		Aspect_12 asp3 = new Aspect_12_Impl(null);
		asp3.init(3);

		Aspect_12 asp4 = new Aspect_12_Impl(null);
		asp4.init(4);


		deploy(asp1)
        {
            deploy(asp2)
            {
                deploy(asp3)
                {
                    deploy(asp4)
                    {
                        CaesarTestCase_12.foo("string");
                    }
                }
            }
        }
    }
}

cclass Aspect_12
{
	public Aspect_12 init(int i)
	{
		this.i = i;
		return this;
	}

	private int i;

	pointcut callFoo(String str) : call(* CaesarTestCase_12.foo(..)) && args(str);

	char around(String s) : callFoo(s)
	{
		CaesarTestCase_12.result.append(":bar"+i);
		return proceed(s);
	}
}
