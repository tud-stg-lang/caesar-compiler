package generated.test269;

/**
 * Purpose: overriding wraps in subclass
 *
 * @author Vaidas Gasiunas
 */
public class ClassA {
}

public class ClassB {
}

public cclass OuterA {

	public cclass InnerA wraps ClassA {
	}

	public cclass InnerB extends InnerA wraps ClassB {
	}
}
