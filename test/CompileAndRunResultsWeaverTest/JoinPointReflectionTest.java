package generated;

import junit.framework.TestCase;

public cclass JoinPointReflectionTest extends TestCase {

	private World world = new World();

	private ReflectionAspect aspect = new ReflectionAspect();

	public JoinPointReflectionTest() {
		super("test");
	}

	public void test() {

		StringBuffer testBuffer = new StringBuffer();
		String expectedTestResult = "";

		deploy(aspect) {

			world.m(testBuffer);

		}

		world.m(testBuffer);
	}

}

class World {

	public void m(StringBuffer testBuffer) {
		testBuffer.append("World: m");

	}

}

cclass ReflectionAspect {
	
	pointcut methodCall(World w, StringBuffer b) : call(* m(..)) && target(w) && args(b);
	
	pointcut errorPC(World w) : call(* m(..)) && target(w);


	void around(World world,StringBuffer testBuffer) : methodCall(world, testBuffer) {
		testBuffer.append(
			"ReflectionAspect: Around: " + thisJoinPoint.toString());
		testBuffer.append(
			"ReflectionAspect: Around: " + thisJoinPointStaticPart.toString());
		testBuffer.append(
			"ReflectionAspect: Around: "
				+ thisEnclosingJoinPointStaticPart.toString());
		proceed(world,testBuffer);
	}

}