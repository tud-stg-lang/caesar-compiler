package generated.test50;

import junit.framework.*;

/**
 * Accessing public cclass fields
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

    public VCTestCase() {
		super("test");
	}
    
	public void test() {
		System.out.println("-------> VCTest 50: Accessing field within nested mixin copies");

		OuterA a = new OuterA();
		
		System.out.println(a.i);
		System.out.println(a.x.i);
				
        System.out.println("-------> VCTest 50: end");
	}
}

public class X {
    public int i = 11;
}

public cclass OuterA
{
    public X x = new X();
	public int i = 10;
}

