package generated.typesystest01;

/**
 * Test Typesystem
 *
 * @author Ivica Aracic
 */
public class TypeSysTestCase {

	public X x = new X();		
	
	public final G g = new G();	
	
	public g.N n;		
		
	class X {
	    Z z = new Z();
	    class Z {
	    	Y y = new Y();
	        class Y {
	            public g.N n;
			    
			    public void test() {
			        /*
			        TypeSysTestCase.access$000( 
			            X.access$000( X.Z.access$000(this$0) )  
		            ).toString();
		            
		            g.toString();
			        */
			        n = x.z.y.n;       
			    }
		    }
	    }
	}	
	
	public void test() {	    
	    g.N nn = null;	    	    
	    {
	        x.z.y.n = nn;
	    }	 
	}	
}

public cclass G {
    public cclass N {}
}
