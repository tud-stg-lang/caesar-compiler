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
//     Z getZ(){ return z; }
     
        final Z z = new Z(); 
        class Z {
//            gl.Node getN(){ return n; }
            gl.Node n; // ctx(2).gl, Node
        }
    }
    
    
    
    void doSomething() {
        final Y y = this.new Y(); // ctx(1), X$Y
        gl.Node n; // ctx(1).gl, Node
        
        {
	        final y.Z $ = createZ();
	        $.f.X x1
	        $.f.X x2
	        x1 = x2
	        n = $.n; // ctx(0).$.n -> ctx(0).$.ctx(1).gl -> ctx(0).ctx(2).gl
        }
    }
}

public cclass GraphLib {
    public cclass Node{};
};