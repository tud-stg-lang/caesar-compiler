package generated.test220;

/**
 * Purpose: changing mixing order
 *
 * @author Vaidas Gasiunas
 */
public cclass OuterA {

	public cclass InnerA {
	}

	public cclass InnerB {
	}

	public cclass InnerC extends InnerA & InnerB {
	}
}


public cclass OuterB extends OuterA {

    public cclass InnerA {}
    
    public cclass InnerB {}
    
	public cclass InnerC extends InnerB & InnerA {
	}
}