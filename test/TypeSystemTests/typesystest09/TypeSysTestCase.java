package generated.typesystest09;

/**
 * dependent types in method signatures
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	final B b = new B();
			
	void foo1() {
	    			m1(b.x1);
	    			m2(b.g1, b.x1);
	    b.g1.X v1 = m3();
	    b.g2.X v2 = m4(b.g2);
	    b.g2.X v3 = m5(b.g2, b.x2);
	    b.g1.X v4 = m6(b.g2, b.x2);
	    			m7(b.g2, b.x2, b.n2);
	    b.g1.X v5 = m8(b.g1, b.x1, b.n1);
	}

	void foo2() {
	    b.x1.N n = b.n1;
	}
	
	void 	m1(b.g1.X x) 		{ }
	void 	m2(final G g, g.X x) 	{ }	
	b.g1.X 	m3() 					{ {{return b.g1.new X();}} }
	g.X 	m4(final G g) 			{ {return g.new X();} }
	g.X 	m5(final G g, g.X x) 	{ return g.new X(); }
	b.g1.X 	m6(final G g, g.X x) 	{ return null; }
	void 	m7(final G g, g.X x, x.N n) {}
	g.X		m8(final G g, g.X x, x.N n) {return null;}
}

public cclass B {
    public final G g1 = null;
    public final G g2 = null;
    
    public final g1.X x1 = null;
    public final g2.X x2 = null;
    
    public x1.N n1 = null;
    public x2.N n2 = null;
}

public cclass G {
    public cclass X {
        public N x() { return new N(); }
        public cclass N {}
    }    
}
