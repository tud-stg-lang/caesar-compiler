package generated.typesystest31;

/**
 * Anonymous inner cclasses
 * @author Karl Klose
 */
public class TypeSysTestCase {
}


public cclass GraphLib{
    public cclass Node
    {}
}

public cclass A {

    public void bar(){
        final GraphLib gl = new GraphLib();
        
        gl.Node n1;
        
        
        A a = new A() {
            gl.Node n2;
            
            public void foo(){
                n2 = n1;
            }
            
        };
        
    }
};