package generated.test30;

import junit.framework.*;
import java.util.*;

/**
 * Test merging class hierarchies.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult =	"A.A.A, A.C.A; B.B.B, B.C.B; A.C.C; A.A.D; B.B.E; A.C.F; A.C.G";


	public void test()
	{

		System.out.println("-------> VCTest 30: Test Merging Class Hierarchies: start");

		OuterD d = new OuterD_Impl(null);

		OuterD.InnerB db = (OuterD.InnerB)d.$newInnerB();
		OuterD.InnerC dc = (OuterD.InnerC)d.$newInnerC();
		OuterD.InnerD dd = (OuterD.InnerD)d.$newInnerD();
		OuterD.InnerF df = (OuterD.InnerF)d.$newInnerF();
		OuterD.InnerG dg = (OuterD.InnerG)d.$newInnerG();
		OuterD.InnerH dh = (OuterD.InnerH)d.$newInnerH();

		String resBA = db.queryA();
		String resBB = db.queryB();
		String resBM = db.queryM();
		String resBN = db.queryN();

		String resCA = dc.queryA();
		String resCB = dc.queryB();
		String resCM = dc.queryM();
		String resCN = dc.queryN();

		String resDA = dd.queryA();
		String resDB = dd.queryB();
		String resDM = dd.queryM();
		String resDO = dd.queryO();

		String resFE = df.queryE();
		String resFM = df.queryM();
		String resFO = df.queryO();

		String resGE = dg.queryE();
		String resGM = dg.queryM();
		String resGO = dg.queryO();
		String resGP = dg.queryP();

		String resHE = dh.queryE();
		String resHO = dh.queryO();
		String resHP = dh.queryP();
		String resHA = dh.queryA();

		String result = resBA + "; " + resBB + "; " + resBM + "; " + resBN
				+ "; " + resCA + "; " + resCB + "; " + resCM + "; " + resCN
				+ "; " + resDA + "; " + resDB + "; " + resDM + "; " + resDO
				+ "; " + resFE + "; " + resFM + "; " + resFO
				+ "; " + resGE + "; " + resGM + "; " + resGO + "; " + resGP
				+ "; " + resHE + "; " + resHO + "; " + resHP + "; " + resHA;

		System.out.println(result);
		//assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 30: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "A.A.A";
		}
	}

	public cclass InnerB extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.B.A";
		}
	}

	public cclass InnerC extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C.A";
		}
	}

	public cclass InnerD extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", A.C.A";
		}
	}

	public cclass InnerE
	{
		public String queryE()
		{
			return "A.E.E";
		}
	}

	public cclass InnerF extends InnerE
	{
		public String queryE()
		{
			return super.queryE() + ", A.F.E";
		}
	}

	public cclass InnerG extends InnerE
	{
		public String queryE()
		{
			return super.queryE() + ", A.G.E";
		}
	}

	public cclass InnerH extends InnerE
	{
		public String queryE()
		{
			return super.queryE() + ", A.H.E";
		}
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerM
	{
		public String queryM()
		{
			return "B.M.M";
		}
	}

	public cclass InnerA extends InnerM
	{
		public String queryM()
		{
			return super.queryM() + ", B.A.M";
		}
	}

	public cclass InnerF extends InnerM
	{
		public String queryM()
		{
			return super.queryM() + ", B.F.M";
		}
	}

	public cclass InnerG extends InnerM
	{
		public String queryM()
		{
			return super.queryM() + ", B.G.M";
		}
	}

	public cclass InnerN extends InnerA
	{
		public String queryA()
		{
			return super.queryA() + ", B.N.A";
		}

		public String queryN()
		{
			return "B.N.N";
		}
	}

	public cclass InnerB extends InnerN
	{
		public String queryN()
		{
			return super.queryN() + ", B.B.N";
		}
	}

	public cclass InnerC extends InnerN
	{
		public String queryN()
		{
			return super.queryN() + ", B.C.N";
		}
	}
}

public cclass OuterC extends OuterA
{
	public cclass InnerO
	{
		public String queryO()
		{
			return "C.O.O";
		}
	}

	public cclass InnerB
	{
		public String queryB()
		{
			return "C.B.B";
		}
	}

	public cclass InnerC extends InnerB
	{
		public String queryB()
		{
			return super.queryB() + ", C.C.B";
		}
	}

	public cclass InnerD extends InnerB & InnerO
	{
		public String queryO()
		{
			return super.queryO() + ", C.D.O";
		}

		public String queryB()
		{
			return super.queryB() + ", C.D.B";
		}
	}

	public cclass InnerE extends InnerO
	{
		public String queryO()
		{
			return super.queryO() + ", C.E.O";
		}
	}

	public cclass InnerP extends InnerO
	{
		public String queryO()
		{
			return super.queryO() + ", C.P.O";
		}

		public String queryP()
		{
			return "C.P.P";
		}
	}

	public cclass InnerG extends InnerP
	{
		public String queryO()
		{
			return super.queryO() + ", C.G.O";
		}

		public String queryP()
		{
			return super.queryP() + ", C.G.P";
		}
	}

	public cclass InnerH extends InnerP & InnerA
	{
		public String queryO()
		{
			return super.queryO() + ", C.H.O";
		}

		public String queryP()
		{
			return super.queryP() + ", C.H.P";
		}

		public String queryA()
		{
			return super.queryA() + ", C.H.A";
		}
	}
}

public cclass OuterD extends OuterB & OuterC
{
}
