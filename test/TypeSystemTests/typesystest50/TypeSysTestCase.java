package generated.typesystest50;

/**
 * simple dependent type test within a cclass
 * 
 * @author Ivica Aracic
 */
public cclass X {
    public final G g = new G();	
	
	public void foo() {
	    g.N n1 = null;
	    g.N n2 = null;
	    n1 = n2;
	}
}

public cclass G {
    public cclass N {}
}
