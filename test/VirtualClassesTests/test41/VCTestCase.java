package generated.test41;

import junit.framework.*;

/**
 * Accessing field within nested mixin copies
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public static final String expectedResult = "2";

	public void test() {
		System.out.println("-------> VCTest 41: Accessing field within nested mixin copies");

		OuterD.InnerD a = new OuterD().new InnerD();

		String result = "" + a.getF();
		System.out.println(result);

		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 41: end");
	}
}

public cclass OuterA
{
	public cclass InnerA {
	}

	public cclass InnerC extends InnerA {
		protected int _f = 2;
	}

	public cclass InnerD extends InnerC {
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA {
	}
}

public cclass OuterC extends OuterA
{
	public cclass InnerD {

		public int getF() {
			return _f;
		}
	}
}

public cclass OuterD extends OuterC & OuterB {
}
