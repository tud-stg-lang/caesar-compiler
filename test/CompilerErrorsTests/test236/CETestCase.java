package generated.test236;

/**
 * Purpose: accessing private method from inner class
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	private void m() {
	}

	public cclass InnerA {

		public void n() {
			OuterA.this.m();
		}
	}
}