package generated.typesystest100;

/**
 * Test field declarations of dependent types.
 * In a class and in a cclass.
 * 
 * @author Ivica Aracic
 */
public class A {
    final G g1 = new G(); 
    g1.N an1 = g1.new N(); 
    
    public class U {
        final G g2 = new G();        
        
        g1.N un1 = an1;
        g2.N un2 = g2.new N();           
        
        public class X {
            final G g3 = new G();        
            
            g1.N xn1 = an1;
            g2.N xn2 = un2;
            g3.N xn3 = g3.new N();
        }
    }
    
//    das hier kann nicht gehen, weil g keine gültige expression ist
//    aber man kann bessere fehlermeldung ausgeben, und nicht g/N cannot be found
//    public static class V {
//        
//        g.N vn; // as a field in a static nested class
//        
//        public static class X {
//            g.N xn; // as a field in an even deeper static nested class 
//        }
//        
//    }
}


public cclass B {
    public final G g = null; 
    public g.N n; // as field in a top-level class
    
    public cclass U {
        public g.N vn; // as a field in a nested class
        
        public cclass X {
            public g.N xn; // as a field in a even deeper nested class
        }
    }       
}

public cclass G {
    public cclass N {}
}