package generated.test261;

/**
 * Purpose: assigning to more specific virtual class
 *
 * @author Vaidas Gasiunas
 */
public class Test {
	public void test() {
		ClassB.InnerA a = new ClassA().new InnerA();
	}
}

public cclass ClassA {

	public cclass InnerA {
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {
	}
}


