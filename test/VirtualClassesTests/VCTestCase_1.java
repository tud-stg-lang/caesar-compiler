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
    
    
    
    public void test() {
    	// final ColG cg = new this.ColG();
        final ColG cg = $newColG();
        
    	doSomeGraphAlg(cg);
	   	doSomeColGraphAlg(cg);
    }
    
    
    // some G algorithm
    public void doSomeGraphAlg(final G g) {
    	/*** should be *******************
    	g.Node n1 = g.new Node(...);
    	g.Node n2 = g.new Node(...);
    	g.UEdge ue = g.new UEdge(...);
    	**********************************/
    	G.Node n1 = g.$newNode("n1");
    	G.Node n2 = g.$newNode("n2");
		G.UEdge ue = g.$newUEdge("ue1", n1, n2);
		
		System.out.println("n1->n2 ? "+ue.isConnecting(n1, n2));
		System.out.println("n2->n1 ? "+ue.isConnecting(n2, n1));
    }

    // some CG algorithm
    public void doSomeColGraphAlg(final ColG cg) {
    	/*** should be *******************
	    cg.Edge e = cg.getEdge("ue1");
    	**********************************/
    	ColG.Edge e = (ColG.Edge)cg.getEdge("ue1");
    	
    	e.setColor(Color.RED);
    	
    	System.out.println(e);
    }


    /**
     * G
     */
    public cclass G {
    
    	public G() {
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
        			super.isConnecting(n1, n2)
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
    
    	public ColG() {
    		super();
    	}
    
        public cclass Edge {
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
        

        public cclass UEdge extends Edge {
        	public UEdge(String name, G.Node n1, G.Node n2) {
				super(name, n1, n2);
            }
        }
        
        public cclass Node {
        	public Node(String name) {
        		super(name);
        	}
        }
    }
}
