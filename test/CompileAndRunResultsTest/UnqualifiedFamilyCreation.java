package generated;

public class UnqualifiedFamilyCreation {
	
	void receiveB( BeAT b ) {}
	
	virtual class A {
		public void test() {
			receiveB( new BeAT() );
		}
	}
	
	virtual class BeAT {}
}