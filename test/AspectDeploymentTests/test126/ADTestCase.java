package generated.test126;

import junit.framework.TestCase;

/**
 * Test local deployment
 *
 * @author Vaidas Gasiunas
 */

public class ADTestCase extends TestCase
{
	public ADTestCase()
	{
		super("test");
	}

	public static int fooCounter = 0;

	public static StringBuffer result = new StringBuffer();

	public String expectedResult =
		// deploy g1

		":trigger(m1-ta1-ta2-g1):after trigger(ta1):after trigger(ta2):after trigger(g1)" +

		// checkpoint1, deploy g2

		":trigger(m2-tb1-tb2-g1-g2):after trigger(tb1):after trigger(tb2):after trigger(g1):after trigger(g2)" +

		// checkpoint2

		":trigger(m1-ta1-ta2-g1):after trigger(ta1):after trigger(g1):after trigger(g2)" +
		":trigger(m1-ta1-ta2-g1):after trigger(g1):after trigger(g2)" +

		// checkpoint3, undeploy g1

		":trigger(m2-tb1-tb2-g1-g2):after trigger(tb1):after trigger(g2)" +
		":trigger(m2-tb1-tb2-g1-g2):after trigger(g2)";

		// checkpoint4, undeploy g2

	public void test()
	{
		System.out.println("-------> ADTest 26: Test local deployment");

		new DeployA().test();

		System.out.println(result);
		assertEquals(expectedResult, result.toString());

		System.out.println("-------> ADTest 26: end");
	}
}

public cclass DeployA
{
    public void test()
    {
		Runnable anotherThread = new AnotherThread();
        new Thread(anotherThread).start();

		ModelA a = new ModelA();

		AspectA g1 = new AspectA().init("g1");

		deploy(new AspectA().init("ta1"))
        {
			deploy(new AspectA().init("ta2"))
			{
				deploy g1;
				a.setId("m1");
				a.trigger();

				Barrier.getInstance().check(); //Checkpoint 1
				Barrier.getInstance().check(); //Checkpoint 2
			}
			a.trigger();
		}
		a.trigger();
		undeploy g1;

		Barrier.getInstance().check(); //Checkpoint 3
		Barrier.getInstance().check(); //Checkpoint 4
    }
}

public cclass AnotherThread implements Runnable
{
	public void run()
	{
		ModelA a = new ModelA();
		AspectA g2 = new AspectA().init("g2");

		Barrier.getInstance().check(); //Checkpoint 1

		deploy g2;

		deploy(new AspectA().init("tb1"))
		{
			deploy(new AspectA().init("tb2"))
			{
				a.setId("m2");
				a.trigger();


				Barrier.getInstance().check(); //Checkpoint 2
				Barrier.getInstance().check(); //Checkpoint 3
			}
			a.trigger();
		}
		a.trigger();

		undeploy g2;

		Barrier.getInstance().check();  //Checkpoint 4
	}
}

public cclass AspectA
{
    protected String _id;

	public AspectA init(String id)
	{
		_id = id;
		return this;
	}

	after() : execution(void ModelA.trigger())
	{
		ADTestCase.result.append(":after trigger(" + _id + ")");
	}

	void around(String mid) :
		execution(void ModelA.setId(..)) && args(mid)
	{
		proceed(mid + "-" + _id);
	}
}

public cclass ModelA
{
	private String _id;

	public String getId()
	{
		return _id;
	}

	public void setId(String id)
	{
		_id = id;
	}

	public void trigger()
	{
		ADTestCase.result.append(":trigger("+_id+")");
	}
}
