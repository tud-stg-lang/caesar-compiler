package generated.test262;

/**
 * Purpose: assigning to more specific virtual class inside context of more specific class
 *
 * @author Vaidas Gasiunas
 */
public cclass ClassA {

	public cclass InnerA {
	}
}

public cclass ClassB extends ClassA {

	public cclass InnerA {
		public void m() {
			InnerA a = new ClassA().new InnerA();
		}
	}
}





