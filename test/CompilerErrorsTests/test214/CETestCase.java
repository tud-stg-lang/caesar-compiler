package generated.test214;

/**
 * Purpose: mixing outer with inner
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA & OuterA.InnerA {
}