package generated.test51;

import junit.framework.*;

/**
 * Accessing fields defined in enclosing
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

    public VCTestCase() {
		super("test");
	}
    
	public void test() {
		System.out.println("-------> VCTest 51: Accessing fields defined in enclosing");

		OuterA a = new OuterA();
		OuterA.InnerA ia = a.new InnerA();		
		
		System.out.println(ia.getI());		
				
        System.out.println("-------> VCTest 51: end");
	}
}

public cclass OuterA
{
    public int i = 10;    
    public cclass InnerA 
    {
        public int getI() 
        {
            return i;
        }
    }
}

