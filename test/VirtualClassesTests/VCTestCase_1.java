package generated;

import junit.framework.TestCase;
import java.util.LinkedList;

/**
 * Test & in inner class
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_1 extends TestCase {

	public VCTestCase_1() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        new _VCTestCase_1().test();
	}       
}

public cclass _VCTestCase_1 {
    
    public void test() {
        G g = new ColG();
        G_Impl.UEdge ue = g.createUEdge();
        System.out.println("***"+ue.getClass().getName());
    }
    
    public static cclass G {
        
        public G_Impl.UEdge createUEdge() {
            return new G_Impl.UEdge_Impl();
        }
        
        public static cclass Edge {
            
        }
        
        public static cclass UEdge extends Edge {
            
        }
    }
    
    public static cclass ColG extends G {

        public G_Impl.UEdge createUEdge() {
            return new ColG_Impl.UEdge_Impl();
        }        

        public static cclass Edge extends G_Impl.Edge {
            
        }
        
        public static cclass UEdge extends Edge & G_Impl.UEdge {
            public UEdge() {
                super();
            }
        }
    }
    
}

/*
public cclass _VCTestCase_1 {
    
    public void test() {
        X x = new X();
        X_Impl.D d = new X_Impl.D_Impl();

        System.out.println(d.a());
        System.out.println(d.b());
        System.out.println(d.c());
        System.out.println(d.d());
    }
    
    public static cclass X {
        public static cclass D extends B & C {
            public D() {
                System.out.println("hohoho D");
            }
        
            public String d() {
                return a()+'-'+b()+'-'+c();
            }    
        }
    }
}
*/

