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
		B b = new B();
        B.CG cg = b.new CG();
        B.CG.N n1 = cg.new N().init("n1", Color.BLACK);
        B.CG.N n2 = cg.new N().init("n2", Color.RED);
		B.CG.E e = cg.new E().init(n1, n2);
		e.doSomethingWithNodes();
		
		System.out.println(n1);
		System.out.println(n2);
		System.out.println(e);
		
		e.getStartNode().getColor();
		e.getEndNode().getColor();
		System.out.println("E connecting same colors? "+e.isConnectingSameColors());
		}
		
		{
		Y bb = new Y();
		Y.B b = bb.new B();
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
			
			public E init(N n1, N n2) {
				this.n1 = n1;
				this.n2 = n2;
				return this;
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
			
			public N init(String name) {
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
		    
			public N init(String name, Color color) {
				init(name);
				this.col = color;
				return this;
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
			A ya = $outer.new A();
			a = ya.new A();
			a.x();
		}
	}
}