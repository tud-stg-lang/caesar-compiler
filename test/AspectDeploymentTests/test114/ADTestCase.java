package generated.test114;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test crosscutting outer joins
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
    	":before cutA:A.A-" +
    	":before cutA:A.B-" +
     	":before cutA:B.A-" +
    	":before cutA:A.B-" +
    	":before cutA:before cutC:C.A-" +
    	":before cutA:before cutC:C.B-" +
    	":before cutA:D.A-" +
    	":before cutA:A.B-" +
    	":before cutA:before cutC:before cutE:E.A-" +
    	":before cutA:before cutC:before cutE:E.B-" +
    	":before cutA:before cutC:F.A-" +
    	":before cutA:before cutC:C.B-" +
    	":before cutA:before cutC:before cutE:before cutG:G.A-" +
    	":before cutA:before cutC:before cutE:before cutG:G.B-" +
    	":before cutA:before cutC:before cutE:before cutG:H.A-" +
    	":before cutA:before cutC:before cutE:before cutG:G.B";

    public void test()
    {
		System.out.println("-------> ADTest 14: Crosscutting Outer Joins: start");

        new DeployA_Impl(null).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 14: end");
    }
}

public cclass DeployA
{
    public void test()
    {
		OuterA oa = new OuterA();
		OuterB ob = new OuterB();
		OuterC oc = new OuterC();
		OuterD od = new OuterD();
		OuterE oe = new OuterE();
		OuterF of = new OuterF();
		OuterG og = new OuterG();
		OuterH oh = new OuterH();

		deploy(new AspectA())
        {
        	oa.doA(); ADTestCase.result.append("-");
			oa.doB(); ADTestCase.result.append("-");
			ob.doA(); ADTestCase.result.append("-");
			ob.doB(); ADTestCase.result.append("-");
			oc.doA(); ADTestCase.result.append("-");
			oc.doB(); ADTestCase.result.append("-");
			od.doA(); ADTestCase.result.append("-");
			od.doB(); ADTestCase.result.append("-");
			oe.doA(); ADTestCase.result.append("-");
			oe.doB(); ADTestCase.result.append("-");
			of.doA(); ADTestCase.result.append("-");
			of.doB(); ADTestCase.result.append("-");
			og.doA(); ADTestCase.result.append("-");
			og.doB(); ADTestCase.result.append("-");
			oh.doA(); ADTestCase.result.append("-");
			oh.doB();
        }
    }
}

cclass AspectA
{
	pointcut cutA() : call(* OuterA+.*(..));

	before() : cutA()
	{
		ADTestCase.result.append(":before cutA");
	}

	pointcut cutC() : call(* OuterC+.*(..));

	before() : cutC()
	{
		ADTestCase.result.append(":before cutC");
	}

	pointcut cutE() : call(* OuterE+.*(..));

	before() : cutE()
	{
		ADTestCase.result.append(":before cutE");
	}


	pointcut cutG() : call(* OuterG+.*(..));

	before() : cutG()
	{
		ADTestCase.result.append(":before cutG");
	}
}

public cclass OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":A.A");
	}

	public void doB()
	{
		ADTestCase.result.append(":A.B");
	}
}

public cclass OuterB extends OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":B.A");
	}
}

public cclass OuterC extends OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":C.A");
	}

	public void doB()
	{
		ADTestCase.result.append(":C.B");
	}
}

public cclass OuterD extends OuterA
{
	public void doA()
	{
		ADTestCase.result.append(":D.A");
	}
}

public cclass OuterE extends OuterC
{
	public void doA()
	{
		ADTestCase.result.append(":E.A");
	}

	public void doB()
	{
		ADTestCase.result.append(":E.B");
	}
}

public cclass OuterF extends OuterC
{
	public void doA()
	{
		ADTestCase.result.append(":F.A");
	}
}

public cclass OuterG extends OuterE & OuterD & OuterF
{
	public void doA()
	{
		ADTestCase.result.append(":G.A");
	}

	public void doB()
	{
		ADTestCase.result.append(":G.B");
	}
}

public cclass OuterH extends OuterA & OuterB & OuterC & OuterD & OuterE & OuterF & OuterG
{
	public void doA()
	{
		ADTestCase.result.append(":H.A");
	}
}
