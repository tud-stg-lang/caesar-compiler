package generated.typesystest10;

import java.util.*;

import junit.framework.TestCase;

import org.caesarj.runtime.*;

/**
 * dependent types in method signatures
 * 
 * @author Ivica Aracic
 */
public class TypeSysTestCase extends TestCase {
    
    public TypeSysTestCase() {
		super("test");
	}

    /** this method is going to land in caesar's runtime lib */
    public static Object cast(Object o1, CaesarObject o2) {
		if(o1 != null) {
			if(o1 instanceof CaesarObject) {
				if(!((CaesarObject)o1).familyEquals( o2 )) {
					throw new RuntimeException();
				}
			}
			else if(!(o1 instanceof Object)) {
				throw new RuntimeException();
			}
		}
		return o2;
	}	

	final B b = new B();
			
	public void test() {
	    final b.g1.X x = b.x1;
	    
	    x.N n;
	    n = (x.N)b.n1;
	    
	    List l = new LinkedList();
	    l.add(n);
	    n = (x.N)l.get(0);
	    
	}	
	
	
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
        public cclass N {}
    }    
}
