package generated.test237;

/**
 * Purpose: accessing protected method from inner class
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	protected void m() {
	}

	public cclass InnerA {

		public void n() {
			OuterA.this.m();
		}
	}
}