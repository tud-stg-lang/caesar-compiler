package generated.test227;

/**
 * Purpose: constructing non-existing inner class
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB {
	}

	public void m() {
		OuterB.InnerB a = new OuterA().new InnerB();
	}
}