package generated.typesystest30;

/**
 *  
 * @author Karl Klose
 */
public class TypeSysTestCase {

	public void test() {
	    new X().doSomething();
	}	
}

class X {
    
    final GraphLib gl = new GraphLib();
    
    class Y {
        class Z {            
            gl.Node n; // ctx(2).gl, Node
        }
    }
    
    void doSomething() {
        final Y y = this.new Y(); 
	    final X.Y.Z z = y.new Z();	    
	    
	    gl.Node n;
	    n = z.n;
	    
	    Y y2 = this.new Y(); 
	    y2.Z z2 = y2.new Z();
	    z = z2; // ?
    }
}

public cclass GraphLib {
    public cclass Node{};
};