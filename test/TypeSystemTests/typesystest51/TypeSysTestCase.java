package generated.typesystest51;

/**
 * simple dependent type test within a cclass
 * 
 * @author Ivica Aracic
 */
public cclass X {    
    public final G g = new G();    
	
	public void foo() {
	    {
		    g.E e = g.new E();
		    g.N n;
		    n = e.n1;
		    n = g.getThisN();
		    //n = g.getOtherN();
		}
	    
	    {
	        g.someOtherG.N n1 = g.someOtherG.new N();
	        g.someOtherG.N n2 = g.someOtherG.new N();
	        n1 = n2;	        
	    }
	}
}

public cclass G {    
    
    public final G someOtherG = new G();
    
    public someOtherG.N getOtherN() {return someOtherG.new N();}
    
    public N getThisN() {return new N();}
    
    public cclass N {}
    public cclass E {      
        public N n1, n2;
        
        public void test1() {            
            n1 = n2;
        }
    }
}
