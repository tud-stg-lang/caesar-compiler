package generated;

public class Y_ extends X_ {
	override class Inner {
		public int baz() {
			System.out.println("Y.Inner.baz()");
			return bar();
		}
		public int bar() {
			System.out.println("Y.Inner.bar");
			return 0;
		}
	}
	public void test(SubInner inner) {
		inner.fjSuper.baz();
	}
}