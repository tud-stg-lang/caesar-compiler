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

    public cclass Y {
  		public cclass Z {
  		    public x.Y y;
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
    	x.y2 = myY;  // this should be ok
    	x.z2 = null; // this should be ok as well
    	x.z2 = x.z; // this should be ok as well
    	//y2 = z.y;    // this should not be ok
    	z.y = x.y;   // this should be ok
    }
  }
