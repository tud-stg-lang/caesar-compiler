package generated.test2;

import java.awt.Color;
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
        System.out.println("-> VCTest 2: start");

        TestCase2 testCase = new TestCase2();
	    TestCase2.G g = testCase.new CG(); 		// testCase.G g = testCase.$newCG();
        TestCase2.G.E e = g.new UE(); 	// g.E = g.new E();
        e.init("n1", "n2");

		System.out.println("connecting: "+e.isConnecting("n1", "n2"));
		assertEquals(e.isConnecting("n1", "n2"), false);

        System.out.println("-> VCTest 2: end");
	}
}

//=========================================================
public cclass TestCase2 {

	public cclass G {
		public cclass E {
		    protected String n1, n2;

	        public void init(String n1, String n2) {
	            this.n1 = n1;
	            this.n2 = n2;
	        }

			public boolean isConnecting(String n1, String n2) {
				return this.n1.equals(n1) && this.n2.equals(n1);
			}
		}

		public cclass UE extends E {
		    public boolean isConnecting(String n1, String n2) {
		    	return super.isConnecting(n1,n2) || super.isConnecting(n2, n1);
		    }
		}
	}

	//=========================================================
	public cclass CG extends G {
		public cclass E {
		    protected Color col;

		    public Color getColor() {return col;}
		    public void setColor(Color col) {this.col = col;}
		}
	}
}
//=========================================================
