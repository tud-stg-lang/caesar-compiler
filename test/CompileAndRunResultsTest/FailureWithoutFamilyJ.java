package generated;

public class FailureWithoutFamilyJ {
	virtual class Messager {
		public void message() {}
	}
	virtual class MessagerSub extends Messager {
		public int message() { return 0; }
	}
}