package generated;

import junit.framework.TestCase;

privileged public class PrivilegedAccessTest extends TestCase {
	
	public PrivilegedAccessTest() {
		super("test");
	}

	public void test() {
		PrivateAccessClass privateObject = new PrivateAccessClass();

		privateObject.privateString = "new private String";
			
		privateObject.privateInt = 3;
		
		String s = privateObject.privateString;
		
		int i = privateObject.privateInt;
		
		privateObject.privateMethod();
					
	}
	
	

}

class PrivateAccessClass {

	private int privateInt = 5;
	
	private String privateString = "a private String";
	
	private boolean privateMethod() {
		return true;
	}	

}

