package generated;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

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
    
    public G $newG() {
    	return new G();
    }
    
    public ColG $newColG() {
    	return new ColG();
    }
    
    public void test() {
    	// final ColG cg = new this.ColG();
        final ColG cg = $newColG();
        
    	doSomeGraphAlg(cg);
	   	doSomeColGraphAlg(cg);
    }
    
    
    // some G algorithm
    public void doSomeGraphAlg(final G g) {
    	/*** should be *******************
    	g.Node n1 = new g.Node(...);
    	g.Node n2 = new g.Node(...);
    	g.UEdge ue = new g.UEdge(...);
    	**********************************/
    	G.Node n1 = g.$newNode("n1");
    	G.Node n2 = g.$newNode("n2");
		G.UEdge ue = g.$newUEdge("ue1", n1, n2);
		
		System.out.println("n1->n2 ? "+ue.isConnecting(n1, n2));
		System.out.println("n2->n1 ? "+ue.isConnecting(n2, n1));
    }

    // some CG algorithm
    public void doSomeColGraphAlg(final ColG cg) {
    	ColG.Edge e = (ColG.Edge)cg.getEdge("ue1"); // <- cg.Edge e = cg.getEdge("ue1");
    	e.setColor(Color.RED);
    	
    	System.out.println(e);
    }


    /**
     * G
     */
    public cclass G {

        public G.Node $newNode(String name) {
            return new G.Node(name);
        }
        
        public G.Edge $newEdge(String name, G.Node n1, G.Node n2) {
            return new G.Edge(name, n1, n2);
        }
        
        public G.UEdge $newUEdge(String name, G.Node n1, G.Node n2) {
            return new G.UEdge(name, n1, n2);
        }

		public G.Node getNode(String name) {
			return (G.Node)nodeMap.get(name);
		}

		public G.Edge getEdge(String name) {
			return (G.Edge)edgeMap.get(name);
		}
        
        public cclass Edge {
			private String name;
			private G.Node n1, n2;
        
        	public Edge(String name, G.Node n1, G.Node n2) {
        		this.name = name;
        		this.n1 = n1;
        		this.n2 = n2;
        		edgeMap.put(name, this);
        	}
        	
        	public boolean isConnecting(G.Node n1, G.Node n2) {
        		return this.n1==n1 && this.n2==n2;
        	}
        
            public String getName() {
                return name;
            }
        }
        
        public cclass UEdge extends Edge {
        	public UEdge(String name, G.Node n1, G.Node n2) {
        		super(name, n1, n2);
        	}
        	
        	public boolean isConnecting(G.Node n1, G.Node n2) {
        		return 
        			super.isConnecting(n1,n2)
        			|| super.isConnecting(n2, n1);
        	}
        }
        
        public cclass Node {
        	private String name;
        	
        	public Node(String name) {
				this.name = name;
				nodeMap.put(name, this);
        	}
        	
        	public String getName() {
        		return name;
        	}
        }
        
        private HashMap nodeMap = new HashMap();
        private HashMap edgeMap = new HashMap();
    }
    
    
    /**
     * ColG
     */
    public cclass ColG extends G {
    
        public G.Node $newNode(String name) {
            return new ColG.Node(name);
        }
        
        public G.Edge $newEdge(String name, G.Node n1, G.Node n2) {
            return new ColG.Edge(name, n1, n2);
        }
        
        public G.UEdge $newUEdge(String name, G.Node n1, G.Node n2) {
            return new ColG.UEdge(name, n1, n2);
        }
        

        public cclass Edge extends G.Edge {
        	
        	private Color color = null;
        
        	public Edge(String name, G.Node n1, G.Node n2) {
        		super(name, n1, n2);
        	}
        
        	public void setColor(Color color) {
        		this.color = color;
        	}
        
            public Color getColor() {
                return color;
            }
        }
        
        public cclass UEdge extends Edge & G.UEdge {
        	public UEdge(String name, G.Node n1, G.Node n2) {
        		// TODO -> super(name, n1, n2);
				super(_VCTestCase_1.ColG.this, name, n1, n2);
            }
        }
        
        public cclass Node extends G.Node {
        	public Node(String name) {
        		super(name);
        	}
        }
    }
}
