package generated.test37;

import junit.framework.*;
import java.util.*;
import java.awt.Color;

/**
 * Test automatic casts
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> VCTest 37: automatic casts: start");

        CG cg = new CG();
        CG.N n1 = cg.new N().init("n1", Color.BLACK);
        CG.N n2 = cg.new N().init("n2", Color.RED);
		CG.E e = cg.new E().init(n1, n2);
		
		System.out.println(n1);
		System.out.println(n2);
		System.out.println(e);
		
		e.getStartNode().getColor();
		e.getEndNode().getColor();
		System.out.println("E connecting same colors? "+e.isConnectingSameColors());

		// just compile, no checks

        System.out.println("-------> VCTest 37: end");
	}
}


public cclass G {
	public cclass E {
		protected G.N n1, n2;
		
		public G.E init(G.N n1, G.N n2) {
			this.n1 = n1;
			this.n2 = n2;
			return this;
		}
		
		public G.N getStartNode() {return n1;}
		public G.N getEndNode() {return n2;}
		
		public boolean isConnecting(G.N n1, G.N n2) {
			return this.n1 == n1 && this.n2 == n2;
		}
		
	     public String toString() {
	     	return "[E:"+n1+"->"+n2+"]";
	     }
	}
	
	public cclass N {
		protected String name;
		
		public G.N init(String name) {
			this.name = name;
			return this;
		}
 
		public String toString() {
			return "[N:"+name+"]";
		}
	}
}

public cclass CG extends G {	
	public cclass E {
		public boolean isConnectingSameColors() {		
		    return n1.getColor().equals(n2.getColor());
		}
	}
	
	public cclass N {
	    protected Color col;
	    
	    public Color getColor() {return col;}
	    
		public G.N init(String name, Color color) {
			init(name);
			this.col = color;
			return this;
		}
		
		public String toString() {		
			return "[N:"+name+","+col+"]";
		}
	}
}
