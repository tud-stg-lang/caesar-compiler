package generated.test241;

/**
 * Purpose: access outer field
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public int v;

	public cclass InnerA {

		public void m() {
			int a = $outer.v;
		}
	}
}

