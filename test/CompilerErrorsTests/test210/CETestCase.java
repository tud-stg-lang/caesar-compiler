package generated.test210;

/**
 * Purpose: inner inheritance cycle through different branches of mixin combination
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}

	public cclass InnerB {
	}
}

public cclass OuterB extends OuterA {

	public cclass InnerB extends InnerA {
	}
}

public cclass OuterC extends OuterA {

	public cclass InnerA extends InnerB {
	}
}

public cclass OuterD extends OuterB & OuterC {
}




