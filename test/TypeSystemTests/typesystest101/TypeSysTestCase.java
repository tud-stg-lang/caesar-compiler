package generated.typesystest101;

/**
 * Test declaration of local dependent type variables.
 * 
 * @author Ivica Aracic
 */
public class A {
    final G g = null; 
    g.N n; // as field       
    
    /*
     * test local declarations
     */
    public void test1() {
        boolean stop = true; 
        
        g.N local1 = n; // as local variable        
        
        { g.N local2 = n; } // as nested local var
        
        if(g == null) {
            g.N local3 = n; // in a if block
        }
        else {
            g.N local4 = n; // in an else block
        }
        
        while(!stop) {
            g.N local5 = n; // in a while block
        }
        
        do {
            g.N local6 = n; // in a do block
        } while(!stop);
        
        for(g.N local7 = n; !stop; ) { // in a for head
            g.N local8 = n; // in a for block
        }
        
        int i = 0;
        switch(i) {
        	case 0: { 
        	    g.N local9 = n; // in a case block        	    
        	    break;
        	}
        	
        	case 1: 
        	    g.N local10 = n; // in a case block not enclosed in {...} 
        	    break;
        	
        	default: {
        	    g.N local11 = n; // in a default block
        	}
        }
    }   
}


public cclass G {
    public cclass N {}
}