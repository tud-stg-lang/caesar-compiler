package generated.typesystest10;

import java.util.*;

import junit.framework.TestCase;

import org.caesarj.runtime.*;

/**
 * dependent type casts
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase extends TestCase {
    
    final B b = new B();
    
    public TypeSysTestCase() {
		super("test");
	}   
			
	public void test() {
	    System.out.println("TypeSysTestCase: start");
	    final b.g1.X x = b.x1;	    
	    
	    x.N n;	    
	    
	    n = null;
	    n = (x.N)b.n1;
	    
	    n = null;	    
	    //n = (x.N)b.n2; // should fail
	    
	    List l = new LinkedList();
	    l.add(n);
	    n = (x.N)l.get(0);
	    
	    System.out.println("TypeSysTestCase: end");
	}
}

public cclass B {
    public final G g1 = new G();
    public final G g2 = new G();
    
    public final g1.X x1 = g1.new X();
    public final g2.X x2 = g2.new X();
    
    public x1.N n1 = x1.new N();
    public x2.N n2 = x2.new N();
}

public cclass G {
    public cclass X {
        public cclass N {}
    }    
}
