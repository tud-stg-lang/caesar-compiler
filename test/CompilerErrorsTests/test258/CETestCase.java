package generated.test258;

/**
 * Purpose: mixing inner classes with incompatible signatures
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {

		public void m() {
		}
	}
}

public cclass OuterB {

	public cclass InnerA {

		public int m() {
			return 2;
		}
	}
}

public cclass OuterC extends OuterA & OuterB {
}