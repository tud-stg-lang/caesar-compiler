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
        G g = new _VCTestCase_1_Impl.ColG_Impl();
        G.UEdge ue = g.$newUEdge();
        
        System.out.println("***"+ue.getClass().getName());
        g.doSomethingWithEdge(ue);
        System.out.println("***");
    }
    
    
    public static cclass G {
        public void doSomethingWithEdge(G.UEdge edge) {
            System.out.println(edge.getName());
        }
        
        public G.UEdge $newUEdge() {
            return new G_Impl.UEdge_Impl();
        }
        
        public static cclass Edge {
            public String getName() {
                return "name";
            }
        }
        
        public static cclass UEdge extends Edge {
            
        }
    }
    
    
    public static cclass ColG extends G {
        public void doSomethingWithEdge(G.UEdge edge) {
            ColG.UEdge e = (ColG.UEdge)edge;
            System.out.println(e.getName());
            System.out.println(e.getColor());
        }

        public G.UEdge $newUEdge() {
            return new ColG_Impl.UEdge_Impl();
        }        

        public static cclass Edge extends G.Edge {
            public java.awt.Color getColor() {
                return java.awt.Color.BLACK;
            }
        }
        
        public static cclass UEdge extends Edge & G.UEdge {
            public UEdge() {
                super();
            }
        }
    }
}
