package generated;

public class FailurePassingCase1 {

	public void test() {
		final Outer1 o = new Outer1();
		o.Inner1 i = o.new Inner1();
		final Outer1 p = new Outer1();
		p.Inner1 j = p.new Inner1();
		
		o.m( i ); // OK
		o.m( j ); // error
	}
}

class Outer1 {
	
	void m( Inner1 i ) {}
	
	virtual class Inner1 {}
}