package generated.typesystest52;

/**
 * Klaus compiler killer test. ;)
 * 
 * @author Klaus Ostermann
 */
public cclass X {
    public final X x = new X();
    public final x.Y y = x.new Y();
    public x.Y y2;
    public final y.Z z = y.new Z();
    public y.Z z2;

    public void setY2(x.Y y) {
        this.y2 = y;
    }
    
    public void setZ2(y.Z z) {
        this.z2 = z;
    }
    
    public cclass Y {
  		public cclass Z {
  		    public x.Y y;
  		    
  		    public void setY(x.Y y) {  		        
  		        this.y = y;
  		    }
  		}

    }
  }

  public class Test {
    public final X x = new X();
    public final x.x.Y myY = x.x.new Y();
    
    public final x.Y y = x.new Y();
    public final y.Z z = y.new Z();
    public x.Y y2;
    public void foo() {
    	x.setY2(myY);  // this should be ok
    	x.setZ2(null); // this should be ok as well
    	x.setZ2(x.z); // this should be ok as well
    	//y2 = z.y;    // this should not be ok
    	z.setY(x.y);   // this should be ok
    }
  }
