package generated.test233;

/**
 * Purpose: inner inheritance leads to visibility restriction public -> protected
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {

		public void m() {
		}
	}

	public cclass InnerB {

		protected void m() {
		}
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB extends InnerA {
	}
}