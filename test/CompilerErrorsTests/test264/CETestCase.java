package generated.test264;

/**
 * Purpose:  outer class objects not covariant
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public OuterA getA() {
		return this;
	}

	public cclass InnerA {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerA {

		public void m() {
			InnerA a1 = OuterB.this.getA().new InnerA();
		}
	}
}