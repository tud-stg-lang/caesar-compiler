package generated.test254;

/**
 * Purpose:  overriding inner with incompatible signature
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {

		public void m() {

		}
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerA {

		public int m() {

		}
	}
}