package generated;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

/**
 * Test &-Operator and linearization
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_3 extends TestCase {

	public VCTestCase_3() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        System.out.println("-> VCTestCase_3: start");
        TestCase2 testCase = new TestCase2();
	    TestCase2.G g = testCase.$newCG(); 		// testCase.G g = testCase.$newCG();
        TestCase2.G.N n1 = g.$newN(); 		// g.N n1 = g.new N();
        TestCase2.G.N n2 = g.$newN(); 		// g.N n2 = g.new N();
        TestCase2.G.E e = g.$newUE(); 	// g.E = g.new E();
        e.init(n1, n2);

		System.out.println("connecting: "+e.isConnecting(n1, n2));

        System.out.println("-> VCTestCase_3: end");
	}       
}

//=========================================================
public cclass TestCase3 {

	public cclass G {
		public cclass E {
	        TestCase3.G.N n1, n2;
		    
	        public void init(TestCase3.G.N n1, TestCase3.G.N n2) {
	            this.n1 = n1;
	            this.n2 = n2;
	        }
	        
			public boolean isConnecting(TestCase3.G.N n1, TestCase3.G.N n2) {
				return this.n1==n1 && this.n2==n2;
			}
			
			private void somePrivateEMethod() {}
		}
		
		public cclass UE extends E {
		    public boolean isConnecting(TestCase3.G.N n1, TestCase3.G.N n2) {
		    	return super.isConnecting(n1,n2) || super.isConnecting(n2, n1);
		    }
		    	
			private void somePrivateUEMethod() {}
		}		
		
		public cclass N {     
		}
	}
	
	//=========================================================
	public cclass CG extends G {
		public cclass E {
		    Color col;
		    
		    public Color getColor() {return col;}
		    public void setColor(Color col) {this.col = col;}
	        
	        public void someSpecialAlg() {
	            TestCase3.G.N n = this.n1;
	        }
		}	
	}
	
	//=========================================================
	public cclass WG extends G {
	    public cclass E {
	        float w;
	        
	        public float getW() {return w;}
	        public void setW(float w) {this.w = w;}
	    }   
	}
	
	//=========================================================
	public cclass CWG1 extends TestCase3.CG & TestCase3.WG {
	    public cclass E {    
	        public void nowWeHaveItAll() {
	            float w = this.w;
	            Color col = this.col;
	            TestCase3.G.N n1 = this.n1;
	            TestCase3.G.N n2 = this.n2;
	        }
	    }    
	}
}
//=========================================================
