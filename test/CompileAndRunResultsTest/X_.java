package generated;

public class X_ {
	virtual class Inner {
		public int bar() {
			System.out.println("X.Inner.bar");
			return 0;
		}
		public int baz() {
			return 0;
		}
	}
	
	virtual class SubInner extends Inner {
		public int bar() {
			System.out.println("X.SubInner.bar");
			return 0;
		}

	}

}
