package generated.test38;

import junit.framework.*;
import java.util.*;

/**
 * Wrapper test
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public void test() {
		System.out.println("-------> VCTest 38: Wrapper test: start");

		Client c = new Client();
		X x = new X();
		x.doSomethingWithSome(c);

        System.out.println("-------> VCTest 38: end");
	}
}

public class Client {
}

public cclass X {
	public cclass W wraps Client {
		public void print() {		
			System.out.println($wrappee);
		}
	}		
	
	public void doSomethingWithSome(Client c) {
		W(c).print();
	}
}
