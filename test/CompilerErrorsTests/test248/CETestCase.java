package generated.test248;

/**
 * Purpose: accessing outer from outer class
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public void m() {
	    // This shouldn't bee possible at all
		System.out.println(outer().toString());
	}
}
