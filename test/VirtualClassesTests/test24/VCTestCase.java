package generated.test24;

import junit.framework.*;
import java.util.*;

/**
 * Test arrays on Caesar classes.
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "B.a0, A.a0, B.a1, A.a1, B.a2, A.a2, B.a3, A.a3";


	public void test() {

		System.out.println("-------> VCTest 24: Test Arrays on Caesar Classes: start");

		OuterB ob = new OuterB_Impl(null);

		String result = ob.createDefaultA().queryA();

		System.out.println(result);
		assertEquals(result, expectedResult);

        System.out.println("-------> VCTest 24: end");
	}
}

public cclass OuterA
{
	public cclass InnerA
	{
		InnerA _arr[] = null;
		String _name = null;

		public OuterA.InnerA init(String name)
		{
			_name = name;
			return this;
		}

		public OuterA.InnerA[] getArr()
		{
			return _arr;
		}

		public void setArr(OuterA.InnerA[] arr)
		{
			_arr = arr;
		}

		public String queryA()
		{
			String out = "A." + _name;

			if (_arr != null)
			{
				for (int i1 = 0; i1 < _arr.length; i1++)
				{
					out += ", " + _arr[i1].queryA();
				}
			}

			return out;
		}
	}

	public OuterA.InnerA createDefaultA()
	{
		InnerA[] arr = new InnerA[2];
		arr[0] = $newInnerA().init("a1");
		arr[1] = $newInnerA().init("a2");

		InnerA a = $newInnerA().init("a0");
		a.setArr(arr);
		return a;
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String queryA()
		{
			return "B." + _name + ", " + super.queryA();
		}
	}

	public OuterA.InnerA createDefaultA()
	{
		OuterB.InnerA a = (OuterB.InnerA)super.createDefaultA();
		OuterA.InnerA[] arr = a.getArr();
		OuterA.InnerA[] arrNew = new OuterA.InnerA[arr.length + 1];

		System.arraycopy(arr, 0, arrNew, 0, arr.length);

		arrNew[arr.length] = (OuterB.InnerA)$newInnerA().init("a3");
		a.setArr(arrNew);

		return a;
	}
}