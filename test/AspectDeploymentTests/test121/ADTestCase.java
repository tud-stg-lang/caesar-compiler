package generated.test121;

import java.rmi.RemoteException;
import org.aspectj.lang.SoftException;

import junit.framework.TestCase;

/**
 * Test exception softening
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

	public String expectedResult = ":call1:success:call2:failed";

    public void test()
    {
		System.out.println("-------> ADTest 21: Test exception softening: start");

        new ClassA().test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 21: end");
    }
}

public cclass ClassA
{
    public void test()
    {
		try
		{
			call1();
			ADTestCase.result.append(":success");

			call2();
		}
		catch (SoftException e)
		{
			ADTestCase.result.append(":failed");
		}
	}

    public void call1() throws RemoteException
    {
		ADTestCase.result.append(":call1");
	}

	public void call2() throws RemoteException
	{
		ADTestCase.result.append(":call2");
		throw new RemoteException();
	}
}

public cclass ExceptionSoftening
{
	declare soft : RemoteException : call(* ClassA.call*());
}



