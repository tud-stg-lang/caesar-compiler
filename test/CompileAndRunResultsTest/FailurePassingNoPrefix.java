package generated;

public class FailurePassingNoPrefix {

	public void test() {
	}
}

class Outer3 {
	virtual class Inner {
		public void m( Inner i ) {}
		public void n( Inner i ) {}	
	}	
}
class Outer4 extends Outer3 {	
	override class Inner {
		public void m( Inner i ) {
			// this should work although k is private.
			// wont work anymore, since private accessors
			// have been removed
			//i.k();

			n( i );

			// this will raise an error:
			n( new Outer3().new Inner() );			
		}	
		private void k(){}
	}
}