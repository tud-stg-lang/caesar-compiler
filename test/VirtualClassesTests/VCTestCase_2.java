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
//        X x = new Y()
	}       
}

public cclass G {
	public cclass E {
        G.N n1, n2;
	    
		public boolean isConnecting() {return false;}
		private void somePrivateEMethod() {}
	}
	
	public cclass UE extends E {
	    public boolean isConnecting() {return false;}
		private void somePrivateUEMethod() {}
	}		
	
	public cclass N {
	}
}

public cclass CG extends G {
	public cclass E {
	    Color col;
	    
	    public Color getColor() {return col;}
	    public void setColor(Color col) {this.col = col;}
	}	
}

public cclass WG extends G {
	public cclass E {
	    float w;
	    
	    public float getW() {return w;}
	    public void setW(float w) {this.w = w;}
	}	
}


public cclass CWG extends CG & WG {
}
