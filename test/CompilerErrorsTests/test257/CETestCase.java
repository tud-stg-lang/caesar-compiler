package generated.test257;

/**
 * Purpose: mixing incompatible methods
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public int m() {
		return 0;
	}
}

public cclass OuterB {

	public void m() {
	}
}

public cclass OuterC extends OuterA & OuterB {
}