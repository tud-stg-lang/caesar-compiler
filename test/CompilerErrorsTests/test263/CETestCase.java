package generated.test263;

/**
 * Purpose: assigning to more specific virtual class inside context of more general class
 *
 * @author Vaidas Gasiunas
 */
public cclass ClassA {

	public cclass InnerA {
		public void m(InnerA a) {
			ClassB.InnerA a1 = a;
		}
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {
	}
}
