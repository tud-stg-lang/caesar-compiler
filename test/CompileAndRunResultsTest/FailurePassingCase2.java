package generated;

public class FailurePassingCase2 {

	public void test() {
		final Outer2 o = new Outer2();
		o.Inner2 _i_ = o.new Inner2();
		o.Inner3 j = o.new Inner3();
		final Outer2 p = new Outer2();
		p.Inner3 k = p.new Inner3();
		
		_i_.m( j ); // OK
		_i_.m( k ); // error
	}
}

class Outer2 {
	
	virtual class Inner2 {
		public void m( Inner3 _i_ ) {}	
	}
	
	virtual class Inner3 {}
}