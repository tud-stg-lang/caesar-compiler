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

	public void test() {
		System.out.println("-------> VCTest 39: Wrapper test: start");

		ClassB b = new ClassB();

		ModelD d = new ModelD();

		ModelD.RoleB db = d.RoleB(b);

		String result = db.getId() + "; " + db.getA().getId();
		System.out.println(result);

        System.out.println("-------> VCTest 39: end");
	}
}

public class ClassA
{
	public String getId()
	{
		return "A.A";
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
		return "B.B";
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
			return "A.A.A";
		}
	}

	public cclass RoleB extends RoleA
	{
		public String getId()
		{
			return "A.B.A, " + super.getId();
		}

		public ModelA.RoleA getA()
		{
			return null;
		}
	}
}

public cclass ModelB extends ModelA
{
	public cclass RoleA
	{ }

	public cclass BindAA extends RoleA wraps ClassA
	{
		static int sequence = 0;

		int _id;

		public BindAA()
		{
			_id = sequence++;
		}

		public String getId()
		{
			return super.getId() + ":" + _id + ", " + $wrappee.getId();
		}
	}

	public cclass RoleB wraps ClassB
	{
		public ModelA.RoleA getA()
		{
			return $outer.BindAA($wrappee.getA());
		}
	}
}

public cclass ModelC extends ModelA
{
	public cclass RoleB
	{
		public String getId()
		{
			return super.getId() + ", "
				   + getA().getId();
		}
	}
}

public cclass ModelD extends ModelB & ModelC
{

}
