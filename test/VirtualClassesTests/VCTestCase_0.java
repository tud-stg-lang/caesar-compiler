package generated;

import junit.framework.TestCase;
import java.util.LinkedList;


public class VCTestCase_0 extends TestCase {

	public VCTestCase_0() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        D d = new D();
        A a = d;
        B b = d;
        
        System.out.println(a.a());
        System.out.println(b.a());
        System.out.println(b.b());
        System.out.println(d.a());
        System.out.println(d.b());
        System.out.println(d.c());
        System.out.println(d.d());
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

public cclass D extends B & C {
    public D() {        
        System.out.println("hohoho D");
    }
    
    public String d() {
        return a()+'-'+b()+'-'+c();
    }    
}

public class X {
    public String toString() {
        return "X";
    }
}
