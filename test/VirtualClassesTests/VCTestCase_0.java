package generated;

import junit.framework.TestCase;
import java.util.LinkedList;


public class VCTestCase_0 extends TestCase {

	public VCTestCase_0() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        B b = new B();
        A a = b;
        
        System.out.println(a.a());
        System.out.println(b.a());
        System.out.println(b.b());   
	}

}

public cclass A {
    public String a() {
        return "A.a";
    }
}

public cclass B extends A {
    public String b() {
        return "B.b";
    }
}

public cclass C extends A {
    public String c() {
        return "C.c";
    }
}

/*
public cclass C extends A&B {
    public void c() {
        a();
        b();
    }
}
*/