package generated.typesystest32;

/**
 * Return type depends on parameter
 * @author Karl Klose
 */
public class TypeSysTestCase {
}


public cclass GraphLib{
    public cclass Node
    {}
}

public cclass A {
    public g.Node create( final GraphLib g, g.Node n){
        return g.new Node();
    }

    public void use(){
        final GraphLib g = new GraphLib();
        g.Node n;
        
        //n = create( g );
    }
    
};