package generated.test220;

import junit.framework.TestCase;

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

	public cclass InnerC {
	}
}


public cclass OuterB extends OuterA {

    public cclass InnerA {}
    
    public cclass InnerB {}
    
    
	public cclass InnerC extends InnerA & InnerB {
	}
}

public cclass OuterC extends OuterA {

    public cclass InnerA {}
    
    public cclass InnerB {}
    
    
    public cclass InnerC extends InnerB & InnerA {
	}
}

public cclass OuterD extends OuterB & OuterC {}