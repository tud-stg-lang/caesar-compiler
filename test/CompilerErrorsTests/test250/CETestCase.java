package generated.test250;

/**
 * Purpose: private access in overriden class
 *
 * @author Vaidas Gasiunas
 */
public cclass ClassA {

	public cclass InnerA {
		private int a;
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {

		public void m() {
			a = 2;
		}
	}
}


