package generated.test226;

/**
 * Purpose: qualified new operator inside cclass
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}

	public void m() {
		InnerA a = OuterA.new InnerA();
	}
}