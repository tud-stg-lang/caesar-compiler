package generated.test39;

import junit.framework.*;
import java.util.*;

/**
 * Wrapper test
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public static final String expectedResult = "RoleB<RoleA[RoleA:0(ClassA)]; RoleA:0(ClassA)";

	public void test() {
		System.out.println("-------> VCTest 39: Wrapper test: start");

		final ClassB b = new ClassB();

		final ModelD d = new ModelD();

		d.RoleB db = d.RoleB(b);

		String result = db.getId() + "; " + db.getA().getId();
		System.out.println(result);

		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 39: end");
	}
}

public class ClassA
{
	public String getId()
	{
		return "ClassA";
	}
}

public class ClassB
{
	private ClassA a;

	public ClassB()
	{
		a = new ClassA();
	}

	public String getId()
	{
		return "ClassB";
	}

	public ClassA getA()
	{
		return a;
	}
}

public cclass ModelA
{
	public cclass RoleA
	{
		public String getId()
		{
			return "RoleA";
		}
	}

	public cclass RoleB extends RoleA
	{
		public String getId()
		{
			return "RoleB<" + super.getId();
		}

		public ModelA.RoleA getA()
		{
			return null;
		}
	}
}

public cclass ModelB extends ModelA
{
   public cclass BindAA extends RoleA wraps ClassA
	{
	    protected static int sequence = 0;

	    protected int _id;

		public BindAA()
		{
			_id = sequence++;
		}

		public String getId()
		{
			return super.getId() + ":" + _id + "(" + wrappee.getId() + ")";
		}
	}

	public cclass RoleB wraps ClassB
	{
		public RoleA getA()
		{
			return ModelB.this.BindAA(wrappee.getA());
		}
	}
}

public cclass ModelC extends ModelA
{
	public cclass RoleB
	{
		public String getId()
		{
			return super.getId() + "["
				   + getA().getId() + "]";
		}
	}
}

public cclass ModelD extends ModelB & ModelC
{

}
