package generated.test219;

/**
 * Purpose: extending overrriden classes
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB extends OuterA.InnerA {
	}
}