package generated;
import junit.framework.TestCase;

import org.caesarj.runtime.*;
public class SubTest extends Test {
	
	public Test getThis() {
		return this;
	}
	
	public override class Inner {
		public Inner( String name ) {}
		public Inner() {}
		public int m() {
			return four();
		}
		private int four() { return 4; }			
		public int n() {
			return m();
		}
		
		public override class InnerInner {
			public int m() { return 3; }
		}
	}
}

class SubSubTest extends SubTest {
	public override class NewInner {}
	public override class Inner {
		public int n() {
			return -1 * k();
		}
	}
}