package generated.test268;

/**
 * Purpose: overriding wraps in overriden class
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
}

public cclass OuterB extends OuterA {

	public cclass InnerA wraps ClassB {
	}
}