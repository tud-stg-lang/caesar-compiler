package generated.test5b;

import junit.framework.*;
import java.util.*;

/**
 * Test outer class access.
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "abacabaxbxbauu";
	
	public static StringBuffer result = new StringBuffer();

	public void test() {

		System.out.println("-------> VCTest 5b: Outer Class Access: start");

		final A a = new A();
		final a.B b = a.new B();
		b.C c = b.new C();

		a.a();
		b.b();
		c.c();				
		
		System.out.println("result: "+result.toString());

		assertEquals(expectedResult, result.toString());

        System.out.println("-------> VCTest 5b: end");
	}
}


public cclass X {
	public void x() {				
		VCTestCase.result.append("x");
	}
	
	public cclass B {
		public void xb() {
			VCTestCase.result.append("xb");
		}
	}
}

public cclass A extends X {

	public void a() {
		VCTestCase.result.append("a");
	}

	public cclass B {
	
		public void b() {
			VCTestCase.result.append("b");			
			A.this.a();
		}

		public cclass C {

			public cclass D {
				public void d() {}
			}

			public void c() {
				VCTestCase.result.append("c");
				A.this.a();
				A.B.this.b();				
				
				xb();

				x();
				b();
				
				U u;
				u = new U();
				u = A.this.new U();								
			}			
		}	
	}

	public cclass U {
		public U() {
			VCTestCase.result.append("u");	
		}
	}
}
