package generated.test224;

/**
 * Purpose: constructing external inner class
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB {

	public cclass InnerA {
	}

	public void m() {
		InnerA a = new OuterA.InnerA();
	}
}