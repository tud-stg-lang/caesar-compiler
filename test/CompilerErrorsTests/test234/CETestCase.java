package generated.test234;

/**
 * Purpose:  restricting access in overriden method protected -> private
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	protected void m() {
	}
}

public cclass OuterB extends OuterA {

	private void m() {
	}
}