package generated.test234;

/**
 * Purpose:  restricting access in overriden method protected -> private
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	protected void m() {
	}
}

public cclass OuterB extends OuterA {

	private void m() {
	}
}


//KOPI Bug.
//Der Code unten schmeisst auch keinen Fehler.
/*
public class A {

	protected void m() {
	}
}

public class B extends A {

	private void m() {
	}
}
*/