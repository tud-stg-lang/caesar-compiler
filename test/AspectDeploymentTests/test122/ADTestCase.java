package generated.test122;

import java.rmi.RemoteException;
import org.aspectj.lang.SoftException;

import junit.framework.TestCase;

/**
 * Test privileged access.
 * Does this make sense? 
 * _Impl classes hidden from the programmer -> no private access
 *
 * @author Ivica Aracic
 */

public class ADTestCase extends TestCase
{
    public ADTestCase()
    {
        super("test");
    }

    public static String expectedResult = "foo";
    public static StringBuffer result = new StringBuffer();

    public void test()
    {
		System.out.println("-------> ADTest 22: Test privileged access: start");

        new ClassA().test();

        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 22: end");
    }
}

public cclass ClassA
{
    private String val = ADTestCase.expectedResult;
    
    public void test() {		
	}   
}

public deployed privileged cclass PrivateAccess
{
	before(ClassA a) : execution(* ClassA.test()) && this(a) {
	    ADTestCase.result.append(((ClassA_Impl)a).val);
	} 
}



