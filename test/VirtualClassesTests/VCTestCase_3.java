package generated;

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
        System.out.println("-------> VCTestCase_3: start");
        
        TestCase3 testCase = new TestCase3_Impl();
	    TestCase3.G g = testCase.$newCWG(); 	// testCase.G g = testCase.$newCG();
        TestCase3.G.N n1 = g.$newN(); 		// g.N n1 = g.new N();
        TestCase3.G.N n2 = g.$newN(); 		// g.N n2 = g.new N();
        TestCase3.G.E e = g.$newUE(); 	    // g.E = g.new E();

        n1.setName("n1");
        n2.setName("n2");
        e.setEdges(n1, n2);

		g.doSomethingWithEdge(e);
		
		System.out.println("e: "+e);
		System.out.println("connecting: "+e.isConnecting(n1, n2));

        System.out.println("-------> VCTestCase_3: end");
	}       
}

//=========================================================
public cclass TestCase3 {

	public cclass G {
		public void doSomethingWithEdge(TestCase3.G.E _e) {
			TestCase3.G.E e = (TestCase3.G.E)_e;
			// ...			
		}
		
		public cclass E {
	        TestCase3.G.N n1, n2;
		    
	        public void setEdges(TestCase3.G.N n1, TestCase3.G.N n2) {
	            this.n1 = n1;
	            this.n2 = n2;
	        }
	        
			public boolean isConnecting(TestCase3.G.N n1, TestCase3.G.N n2) {
				return this.n1==n1 && this.n2==n2;
			}
			
	        public String toString() {
	        	return n1+"->"+n2;
	        }
		}
		
		public cclass UE extends E {
		    public boolean isConnecting(TestCase3.G.N n1, TestCase3.G.N n2) {
		    	return super.isConnecting(n1,n2) || super.isConnecting(n2, n1);
		    }
		    	
	        public String toString() {
	        	return super.toString()+", "+n2+"->"+n1;
	        }	
		}		
		
		public cclass N {     
			private String name;
			
			public void setName(String name) {this.name = name;}
			public String getName() {return name;}
			
			public String toString() {return getName();}
		}
	}
	
	//=========================================================
	public cclass CG extends G {
		public void doSomethingWithEdge(TestCase3.G.E _e) {
			TestCase3.CG.E e = (TestCase3.CG.E)_e;
			e.setColor("#a1babe");
		}

		public cclass E {
		    String col;
		    
		    public String getColor() {return col;}
		    public void setColor(String col) {this.col = col;}
	        
	        public void someSpecialAlg() {
	            TestCase3.G.N n = this.n1;
	        }	        
	        
	        public String toString() {
	        	return "col:"+col+", "+super.toString();
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
	public cclass CWG extends TestCase3.CG & TestCase3.WG {
	    public cclass E {    
	        public void nowWeHaveItAll() {
	            float w = this.w;
	            String col = this.col;
	            TestCase3.G.N n1 = this.n1;
	            TestCase3.G.N n2 = this.n2;
	        }
	    }    
	}
}
//=========================================================
