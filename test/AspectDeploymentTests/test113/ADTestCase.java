package generated.test113;

import java.io.IOException;

import org.caesarj.runtime.CaesarThread;

import junit.framework.TestCase;

/**
 * Test using Caesar type system for crosscutting
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
    	":before cutAA:before cutCA:B.A.A\n" +							// C.A
    	":before cutAA:before cutCA:C.B.A\n"	+						// C.B
    	":before cutAA:before cutCA:C.C.A:after cutAC:after cutCC\n" +	// C.C
    	":before cutAA:before cutCA:B.D.A:after cutAC:after cutCC\n" +	// C.D
    	":before cutAA:before cutCA:C.E.A\n" +							// C.E
    	":before cutAA:before cutCA:C.F.A\n" +							// C.F
    	":before cutAA:B.A.A\n" +										// B.A
    	":before cutAA:B.B.A\n" +										// B.B
    	":before cutAA:A.C.A:after cutAC\n" +							// B.C
    	":before cutAA:B.D.A:after cutAC\n" +							// B.D
    	":before cutAA:A.A.A\n" +										// A.A
    	":before cutAA:A.B.A\n" +										// A.B
    	":before cutAA:C.C.A:after cutAC";								// A.C

    public void test()
    {
		System.out.println("-------> ADTest 13: Crosscutting Virtual Classes: start");

        new DeployA_Impl(null).test();

        System.out.println(result);
        assertEquals(expectedResult, result.toString());

        System.out.println("-------> ADTest 13: end");
    }
}

public cclass DeployA
{
    public void test()
    {
		OuterC oc = new OuterC_Impl(null);
		OuterC.InnerA ca = (OuterC.InnerA)oc.$newInnerA();
		OuterC.InnerB cb = (OuterC.InnerB)oc.$newInnerB();
		OuterC.InnerC cc = (OuterC.InnerC)oc.$newInnerC();
		OuterC.InnerD cd = (OuterC.InnerD)oc.$newInnerD();
		OuterC.InnerE ce = (OuterC.InnerE)oc.$newInnerE();
		OuterC.InnerF cf = (OuterC.InnerF)oc.$newInnerF();

		OuterB ob = new OuterB_Impl(null);
		OuterB.InnerA ba = (OuterB.InnerA)ob.$newInnerA();
		OuterB.InnerB bb = (OuterB.InnerB)ob.$newInnerB();
		OuterB.InnerC bc = (OuterB.InnerC)ob.$newInnerC();
		OuterB.InnerD bd = (OuterB.InnerD)ob.$newInnerD();

		OuterA oa = new OuterA_Impl(null);
		OuterA.InnerA aa = (OuterA.InnerA)oa.$newInnerA();
		OuterA.InnerB ab = (OuterA.InnerB)oa.$newInnerB();
		OuterA.InnerC ac = (OuterA.InnerC)oc.$newInnerC();

		deploy(new AspectA_Impl(null))
        {
        	ca.doA(); ADTestCase.result.append("\n");
        	cb.doA(); ADTestCase.result.append("\n");
        	cc.doA(); ADTestCase.result.append("\n");
        	cd.doA(); ADTestCase.result.append("\n");
        	ce.doA(); ADTestCase.result.append("\n");
        	cf.doA(); ADTestCase.result.append("\n");

        	ba.doA(); ADTestCase.result.append("\n");
        	bb.doA(); ADTestCase.result.append("\n");
        	bc.doA(); ADTestCase.result.append("\n");
        	bd.doA(); ADTestCase.result.append("\n");

        	aa.doA(); ADTestCase.result.append("\n");
			ab.doA(); ADTestCase.result.append("\n");
        	ac.doA();
        }
    }
}

cclass AspectA
{
	pointcut cutAA() : call(* OuterA.InnerA+.doA(..));

	before() : cutAA()
	{
		ADTestCase.result.append(":before cutAA");
	}

	pointcut cutAC() : call(* OuterA.InnerC+.doA(..));

	after() : cutAC()
	{
		ADTestCase.result.append(":after cutAC");
	}

	pointcut cutCA() : call(* OuterC.InnerA+.doA(..));

	before() : cutCA()
	{
		ADTestCase.result.append(":before cutCA");
	}

	pointcut cutCC() : call(* OuterC.InnerC+.doA(..));

	after() : cutCC()
	{
		ADTestCase.result.append(":after cutCC");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public void doA()
		{
			ADTestCase.result.append(":A.A.A");
		}
	}

	public cclass InnerB extends InnerA
	{
		public void doA()
		{
			ADTestCase.result.append(":A.B.A");
		}
	}

	public cclass InnerC extends InnerB
	{
		public void doA()
		{
			ADTestCase.result.append(":A.C.A");
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public void doA()
		{
			ADTestCase.result.append(":B.A.A");
		}
	}

	public cclass InnerB
	{
		public void doA()
		{
			ADTestCase.result.append(":B.B.A");
		}
	}

	// scoping workaround
	public cclass InnerC {}

	public cclass InnerD extends InnerC
	{
		public void doA()
		{
			ADTestCase.result.append(":B.D.A");
		}
	}
}

public cclass OuterC extends OuterB
{
    // scoping workaround
    public cclass InnerA {}

	public cclass InnerB
	{
		public void doA()
		{
			ADTestCase.result.append(":C.B.A");
		}
	}

	public cclass InnerC
	{
		public void doA()
		{
			ADTestCase.result.append(":C.C.A");
		}
	}

	public cclass InnerE extends InnerA
	{
		public void doA()
		{
			ADTestCase.result.append(":C.E.A");
		}
	}

	public cclass InnerF extends InnerE
	{
		public void doA()
		{
			ADTestCase.result.append(":C.F.A");
		}
	}
}