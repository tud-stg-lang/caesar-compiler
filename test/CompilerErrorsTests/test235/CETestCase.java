package generated.test235;

/**
 * Purpose: accessing private method from subclass
 *
 * @author Vaidas Gasiunas
 */
public cclass ClassA {

	private void m() {
	}
}

public cclass ClassB extends ClassA {

	public void n() {
		m();
	}
}