package generated.test3;

import junit.framework.*;
import java.util.*;

/**
 * Test &-Operator and linearization
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        System.out.println("-------> VCTest 3: start");

        TestCase3 testCase = new TestCase3();
        testCase.G g = testCase.new CWG();
        g.N n1 = g.new N();
        g.N n2 = g.new N();
        g.E e = g.new UE();

        n1.setName("n1");
        n2.setName("n2");
        e.setEdges(n1, n2);

		g.doSomethingWithEdge(e);

		System.out.println("e: "+e);
		assertEquals(e.toString(), "col:#a1babe, n1->n2, n2->n1");

		System.out.println("connecting: "+e.isConnecting(n1, n2));
		assertEquals(e.isConnecting(n1, n2), true);

        System.out.println("-------> VCTest 3: end");
	}
}

//=========================================================
public cclass TestCase3 {

	public cclass G {
		public void doSomethingWithEdge(E e) {
		    System.out.println("G.doSomethingWithEdge");
		}

		public cclass E {
		    protected N n1, n2;

	        public void setEdges(N n1, N n2) {
	            this.n1 = n1;
	            this.n2 = n2;
	        }

			public boolean isConnecting(N n1, N n2) {
				return this.n1==n1 && this.n2==n2;
			}

	        public String toString() {
	        	return n1+"->"+n2;
	        }
		}

		public cclass UE extends E {
		    public boolean isConnecting(N n1, N n2) {
		    	return super.isConnecting(n1,n2) || super.isConnecting(n2, n1);
		    }

	        public String toString() {
	        	return super.toString()+", "+n2+"->"+n1;
	        }
		}

		public cclass N {
		    protected String name;

			public void setName(String name) {this.name = name;}
			public String getName() {return name;}

			public String toString() {return getName();}
		}
	}

	//=========================================================
	public cclass CG extends G {
	    // test: signature should be same as in G
		public void doSomethingWithEdge(E e) {
		    super.doSomethingWithEdge(e);
			e.setColor("#a1babe");
			System.out.println("CG.doSomethingWithEdge");
		}

		public cclass E {
		    protected String col;

		    public String getColor() {return col;}
		    public void setColor(String col) {this.col = col;}

	        public void someSpecialAlg() {
	            N n = n1;
	        }

	        public String toString() {
	        	return "col:"+col+", "+super.toString();
	        }
		}
	}

	//=========================================================
	public cclass WG extends G {
	    public cclass E {
	        private float w;

	        public float getW() {return w;}
	        public void setW(float w) {this.w = w;}
	    }
	}

	//=========================================================
	public cclass CWG extends CG & WG {
	    public cclass E {
	        public void nowWeHaveItAll() {
	            float w = this.w;
	            String col = this.col;
	            
	            // note that the type of n1,n2 has been bound to the most specific node,
	            // namely CWG.N
	            N n1 = this.n1;
	            N n2 = this.n2;
	        }
	    }
	}
}
//=========================================================
