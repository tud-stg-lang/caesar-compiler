package generated;

import java.awt.Color;
import junit.framework.*;
import java.util.*;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_2 extends TestCase {

	public VCTestCase_2() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        System.out.println("-> VCTestCase_2: start");
	    G g = (CG)new CG_Impl().$ctor(); // G g = new CG();
        G.N n1 = (G.N)g.$newN().$ctor(); // g.N n1 = g.new N();
        G.N n2 = (G.N)g.$newN().$ctor(); // g.N n2 = g.new N();
        G.E e = (G.E)g.$newE().$ctor(n1, n2); // g.E = g.new E(n1, n2);
        //G.UE ue = (G.UE)g.$newUE().$ctor(n1, n2); // g.UE ue = g.new UE(n1, n2);
        System.out.println("-> VCTestCase_2: end");
	}       
}

/*
 * NOTE:
 * all methods starting with $ will be generated later
 */

public cclass G {
    public G() {}
    
    public Object $ctor() {return this;}
    
    public G.N $newN() {return new N_Impl();}
    public G.E $newE() {return new E_Impl();}
    public G.UE $newUE() {return new UE_Impl();}
    
	public cclass E {
        G.N n1, n2;
	    
        public Object $ctor(G.N n1, G.N n2) {
            this.n1 = n1;
            this.n2 = n2;
            return this;
        }
        
		public boolean isConnecting(G.N n1, G.N n2) {return false;}
		private void somePrivateEMethod() {}
	}
	
	public cclass UE extends E {  
        public UE() {}
        
	    public boolean isConnecting(G.N n1, G.N n2) {return false;}
		private void somePrivateUEMethod() {}
	}		
	
	public cclass N {
        public N() {}
        
        public Object $ctor() {return this;}
	}
}

public cclass CG extends G {
    public CG() {}
    
    public G.N $newN() {return new N_Impl();}
    public G.E $newE() {return new E_Impl();}
    public G.UE $newUE() {return new UE_Impl();}
    
	public cclass E {
        public E() {}
        
	    Color col;
	    
	    public Color getColor() {return col;}
	    public void setColor(Color col) {this.col = col;}
	}	
}

public cclass WG extends G {
    public WG() {}
    
    public G.N $newN() {return new N_Impl();}
    public G.E $newE() {return new E_Impl();}
    public G.UE $newUE() {return new UE_Impl();}

	public cclass E {
	    float w;
	    
	    public float getW() {return w;}
	    public void setW(float w) {this.w = w;}
	}	
}


public cclass CWG extends CG & WG {
    public CWG() {}
    
    public G.N $newN() {return new N_Impl();}
    public G.E $newE() {return new E_Impl();}
    public G.UE $newUE() {return new UE_Impl();}
}
