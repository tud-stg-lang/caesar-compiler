package generated.test239;

/**
 * Purpose: accessing protected of newly created same class object
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	protected void n() {
	}

	public void m() {
		new OuterA().n();
	}
}