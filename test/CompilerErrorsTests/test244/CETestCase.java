package generated.test244;

/**
 * Purpose:  outer this
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public void m() {
	}

	public cclass InnerA {

		public void n() {
			OuterA.this.m();
		}
	}
}