package generated.test270;

/**
 * Purpose: applying wrapper function on wrong type
 *
 * @author Vaidas Gasiunas
 */
public class Test {
	public void test() {
		OuterA oa = new OuterA();
		OuterA.InnerA ia = oa.InnerA(new ClassB());
	}
}

public class ClassA {
}

public class ClassB {
}

public cclass OuterA {

	public cclass InnerA wraps ClassA {
	}
}

