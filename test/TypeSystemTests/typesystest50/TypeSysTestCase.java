package generated.typesystest50;

/**
 * simple dependent type test within a cclass
 * 
 * @author Ivica Aracic
 */
public cclass X {
    public final G g = new G();	

    public cclass Y {
        public g.N n = g.new N();
    }
    
	public void foo() {
	    final Y y = new Y();	    
	    g.N n = g.new N();	    
	    n = y.n;
	}
}

public cclass G {
    public cclass N {}
}
