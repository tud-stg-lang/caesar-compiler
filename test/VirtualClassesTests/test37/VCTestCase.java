package generated.test37;

import junit.framework.*;
import java.util.*;
import java.awt.Color;

/**
 * Test automatic casts
 * This unit test fails because of prepareDynamicDeployment rewrites the CClass generated
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> VCTest 37: automatic casts: start");

		{
		final B b = new B();
        final b.CG cg = b.new CG();
        cg.N n1 = cg.new N("n1", Color.BLACK);
        cg.N n2 = cg.new N("n2", Color.RED);
		cg.E e = cg.new E(n1, n2);
		e.doSomethingWithNodes();
		
		System.out.println(n1);
		System.out.println(n2);
		System.out.println(e);
		
		e.getStartNode().getColor();
		e.getEndNode().getColor();
		System.out.println("E connecting same colors? "+e.isConnectingSameColors());
		}
		
		{
		final Y bb = new Y();
		bb.B b = bb.new B();
		b.exec();
		}

		// just compile, no checks

        System.out.println("-------> VCTest 37: end");
	}
}

public cclass A {
	public cclass G {
		public cclass E {
			protected N n1, n2;
			
			public E(N n1, N n2) {
				this.n1 = n1;
				this.n2 = n2;
			}
			
			public N getStartNode() {return n1;}
			public N getEndNode() {return n2;}
			
			public boolean isConnecting(N n1, N n2) {
				return this.n1 == n1 && this.n2 == n2;
			}
			
		     public String toString() {
		     	return "[E:"+n1+"->"+n2+"]";
		     }
		}
		
		public cclass N {
			protected String name;
			
			public N(String name) {
				this.name = name;
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
		    
			public N(String name, Color color) {
				//super(name);
				this.name = name;
				this.col = color;
			}
			
			public String toString() {		
				return "[N:"+name+","+col+"]";
			}
		}
	}
}

public cclass B extends A {
	public cclass CG {
		public cclass E {
			public void doSomethingWithNodes() {
				n1.x();
				n2.x();
			}
		}
		
		public cclass N {
			public void x() {
				System.out.println("B.CG.N.x");
			}
		}
	}
}




public cclass X {
	public cclass A {
		public cclass A {
		}
	}
	
	public cclass B {
		protected A.A a;
	}
}

public cclass Y extends X {
	public cclass A {
		public cclass A {
			public void x() {}
		}
	}
	
	public cclass B {
		public void exec() {
			A ya = Y.this.new A();
			a = ya.new A();
			a.x();
		}
	}
}